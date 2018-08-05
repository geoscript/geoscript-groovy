/*
 *    HatBox : A user-space spatial add-on for the Java databases
 *    
 *    Copyright (C) 2007 - 2009 Peter Yuill
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.sourceforge.hatbox.jts;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.hatbox.Entry;
import net.sourceforge.hatbox.Envelope;
import net.sourceforge.hatbox.IdResultSet;
import net.sourceforge.hatbox.Lock;
import net.sourceforge.hatbox.Node;
import net.sourceforge.hatbox.RTree;
import net.sourceforge.hatbox.RTreeDml;
import net.sourceforge.hatbox.RTreeInternalException;
import net.sourceforge.hatbox.RTreeSessionDb;
import net.sourceforge.hatbox.SpatialPredicate;
import net.sourceforge.hatbox.InvalidTableException;
import net.sourceforge.hatbox.MetaNode;
import net.sourceforge.hatbox.IndexStatus;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.impl.CoordinateArraySequenceFactory;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;
import org.locationtech.jts.io.WKBReader;
import org.locationtech.jts.io.WKBWriter;
import org.locationtech.jts.io.ParseException;

/**
 * Implementation of server side procedures
 * 
 * @author Peter Yuill
 */
public class Proc {
    
    private static ThreadLocal<Entry> savedEntry = new ThreadLocal<Entry>();
            
    private Proc() {}
    
    public static String getDefaultSchema(Connection con) throws SQLException {
        DatabaseMetaData meta = con.getMetaData();
        return meta.getUserName().toUpperCase();
    }
    
    public static MetaNode spatialMetaData(Connection con, String schema, String table)
            throws SQLException {
        if (schema == null) {
            schema = getDefaultSchema(con);
        }
        RTreeDml dml = RTreeDml.createDml(con, schema, table);
        PreparedStatement ps = con.prepareStatement(dml.getSelectIndex());
        ps.setLong(1, RTreeDml.META_NODE_ID);
        ResultSet rs = ps.executeQuery();
        rs.next();
        MetaNode meta = new MetaNode(rs.getBytes(RTreeDml.NODE_DATA_COL));
        rs.close();
        ps.close();
        return meta;
    }
    
    public static void spatializeProc(
            String schema, String table,
            String geomColumn, String geomType, String srid,
            String exposePK, String entriesMax)
            throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:default:connection");
        spatialize(con, schema, table, geomColumn, geomType, srid, exposePK, entriesMax);
    }
    
    /**
     * Spatialize a currently non-spatialized table. This creates the Hatbox table
     * in 'NO_INDEX' mode. The index must be created separately if required.
     * <p>There are no triggers created at this time, so bulk data loading can
     * proceed efficiently.
     * <p>The case of database object names must be specified exactly as recorded
     * in database meta data. Both derby and H2 will convert un-escaped object names
     * to upper case when it records them in the database meta data.
     * 
     * @param spatialSchema
     * @param spatialTable
     * @param entriesMax The maximum number of entries per node (defaults to 98)
     * @param fidColumn The feature Id column in the spatial table
     * @param geomColumn The geometry column in the spatial table
     * @param geomType The geometry type (refer WKT types)
     * @throws SQLException
     */
    public static void spatialize(
            Connection con,
            String schema, String table,
            String geomColumn, String geomType, String srid,
            String exposePK, String entriesMax)
            throws SQLException {
        if (schema == null) {
            schema = getDefaultSchema(con);
        }
        int pkColumnIndex = -1;
        int geomColumnIndex = -1;
        
        //find PK column
        List<String> pkColList = new ArrayList<String>();
        DatabaseMetaData dbMetaData = con.getMetaData();
        ResultSet pkRs = dbMetaData.getPrimaryKeys(null, schema, table);
        while(pkRs.next()) {
            pkColList.add(pkRs.getString(4));
        }
        pkRs.close();
        if (pkColList.size() != 1) {
            throw new InvalidTableException("Table " + schema + "." + table + " has " + pkColList.size() + " PK columns - only allowed 1");
        }
        ResultSet colRs = dbMetaData.getColumns(null, schema, table, pkColList.get(0));
        int colType = Types.NULL;
        if (colRs.next()) {
            colType = colRs.getInt(5);
            pkColumnIndex = (colRs.getInt(17) - 1); // column ordinal start at 1
        }
        colRs.close();
        switch (colType) {
        case Types.BIGINT :
        case Types.INTEGER :
        case Types.SMALLINT :
        case Types.TINYINT :
            break;
        default :
            throw new InvalidTableException("Table " + schema + "." + table + " must have PK of SMALLINT, INTEGER or BIGINT");
        }
        
        // Validate geometry column
        colRs = dbMetaData.getColumns(null, schema, table, geomColumn);
        colType = Types.NULL;
        if (colRs.next()) {
            colType = colRs.getInt(5);
            geomColumnIndex = (colRs.getInt(17) - 1); // column ordinal start at 1
        }
        colRs.close();
        switch (colType) {
        case Types.BLOB :
        case Types.VARBINARY :
        case Types.BINARY :
            break;
        default :
            throw new InvalidTableException("Table " + schema + "." + table + " must have geometry column of BLOB, BINARY or VARBINARY");
        }
        
        Statement stmt = con.createStatement();
        RTreeDml dml = RTreeDml.createDml(con, schema, table);
        MetaNode meta = new MetaNode(
                -1, table, pkColList.get(0), pkColumnIndex, geomColumn, geomColumnIndex, geomType,
                srid, exposePK, entriesMax);
        dml.setMetaNode(meta);
        //TODO check to see if the index table or triggers exist
        
        // create the index table
        stmt.executeUpdate(dml.getCreateIndex(meta.getIndexNodeSize(meta.getEntriesMax())));
        stmt.close();
        
        // insert the meta node
        PreparedStatement ps = con.prepareStatement(dml.getInsertIndex());
        ps.setBytes(RTreeDml.NODE_DATA_COL, meta.getData());
        ps.execute();
        ps.close();
    }
    
    public static void buildIndexProc(String schema, String table, int commitInterval, ProgressMonitor progressMonitor)
            throws SQLException {
        Connection nested = DriverManager.getConnection("jdbc:default:connection");
        if (schema == null) {
            schema = getDefaultSchema(nested);
        }
        DatabaseMetaData dbMeta = nested.getMetaData();
        Connection con = DriverManager.getConnection(dbMeta.getURL());
        buildIndex(con, schema, table, commitInterval, progressMonitor);
    }
        
    /**
     * Build the index for a spatial table. The Hatbox table must already be created
     * (ie the target table is spatialized). This procedure will create the index
     * maintenance triggers, change the status to 'BUILDING', build the index, then
     * change the status to 'INDEXED'.
     * <p>This procedure uses a non-nested connection that is committed independently
     * of the connection that started the procedure.
     * 
     * @param spatialSchema
     * @param spatialTable
     * @param progressMonitor
     * @throws SQLException
     */
    public static void buildIndex(Connection con, String schema, String table, int commitInterval, ProgressMonitor progressMonitor)
        	throws SQLException {
        con.setAutoCommit(false);
        
        Statement stmt = con.createStatement();
        RTreeDml dml = RTreeDml.createDml(con, schema, table);
        
        PreparedStatement ps = con.prepareStatement(dml.getSelectIndex());
        ps.setLong(1, RTreeDml.META_NODE_ID);
        ResultSet rs = ps.executeQuery();
        rs.next();
        MetaNode meta = new MetaNode(rs.getBytes(RTreeDml.NODE_DATA_COL));
        rs.close();
        ps.close();
        
        dml.setMetaNode(meta);
        
        // delete all index nodes
        stmt.executeUpdate(dml.getDeleteAllIndex());
        
        // insert the root node
        long rootId = -1L;
        ps = con.prepareStatement(dml.getInsertIndex(), Statement.RETURN_GENERATED_KEYS);
        Node rootNode = new Node(0, -1L, meta.getEntriesMax());
        ps.setBytes(RTreeDml.NODE_DATA_COL, rootNode.getData());
        ps.execute();
        ResultSet keyRs = ps.getGeneratedKeys();
        if (keyRs.next()) {
            rootId = keyRs.getInt(1);
        } else {
            throw new RTreeInternalException("No generated key returned from insert");
        }
        ps.close();
        
        meta.setRootId(rootId);
    	updateStatus(IndexStatus.BUILDING, dml, meta, con);
        
        if (progressMonitor != null) {
        	progressMonitor.setRowsProcessed(0);
        	progressMonitor.setCurrentIndexStatus(IndexStatus.BUILDING);
        }
        
        // drop and create the triggers
        // Any attempt to use these triggers will fail until
        // the index status has been set to 'INDEXED'
        
        int rowsExpected = 0;
        try {
            try {
                stmt.executeUpdate("drop trigger " + dml.getInsTriggerName());
            } catch (SQLException sqle) {}
            stmt.executeUpdate(dml.getCreateInsTrigger());
            try {
                stmt.executeUpdate("drop trigger " + dml.getBeforeUpdTriggerName());
            } catch (SQLException sqle) {}
            if (dml.getCreateBeforeUpdTrigger().length() > 0) {
                stmt.executeUpdate(dml.getCreateBeforeUpdTrigger());
            }
            try {
                stmt.executeUpdate("drop trigger " + dml.getUpdTriggerName());
            } catch (SQLException sqle) {}
            stmt.executeUpdate(dml.getCreateUpdTrigger());
            try {
                stmt.executeUpdate("drop trigger " + dml.getBeforeDelTriggerName());
            } catch (SQLException sqle) {}
            if (dml.getCreateBeforeDelTrigger().length() > 0) {
                stmt.executeUpdate(dml.getCreateBeforeDelTrigger());
            }
            try {
                stmt.executeUpdate("drop trigger " + dml.getDelTriggerName());
            } catch (SQLException sqle) {}
            stmt.executeUpdate(dml.getCreateDelTrigger());

            con.commit();
            
            //build the index
            RTreeSessionDb session = new RTreeSessionDb(con, dml, commitInterval);
            RTree rtree = new RTree(session);
            String selectCount =
                "select count(*) from " + 
                dml.getFullTableName();
            ResultSet countRs = stmt.executeQuery(selectCount);
            if (countRs.next()) {
            	rowsExpected = countRs.getInt(1);
            }
            countRs.close();
            if (progressMonitor != null) {
            	progressMonitor.setRowsExpected(rowsExpected);
            }
            String select =
            "select \"" + dml.getPkColumn() +
            "\", \"" + dml.getGeomColumn() + "\" from " + 
            dml.getFullTableName();
            ResultSet dataRs = stmt.executeQuery(select);

            int count = 0;
            WKBReader reader = new WKBReader();
            while (dataRs.next()) {
                count++;
                if ((progressMonitor != null) && ((count % 1000) == 0)) {
                	progressMonitor.setRowsProcessed(count);
                }
                long newId = dataRs.getLong(1);
                byte[] data = dataRs.getBytes(2);
                if ((newId >= 0) && (data != null)) {
                    Geometry geom = null;
                    try {
                        geom = reader.read(data);
                    } catch (ParseException pe) {
                        throw new SQLException("Failed to parse geom for " + newId);
                    }
                    org.locationtech.jts.geom.Envelope e = geom.getEnvelopeInternal();
                    Entry entry = new Entry(e.getMinX(), e.getMaxX(), e.getMinY(), e.getMaxY(), newId);
                    rtree.insert(entry);
                }
            }
            stmt.close();
            rootId = session.getRootId();
            meta.setRootId(rootId);
        	updateStatus(IndexStatus.INDEXED, dml, meta, con);
        } catch (SQLException sqle) {
        	updateStatus(IndexStatus.BUILD_FAILED, dml, meta, con);
        	throw sqle;
        } catch (RuntimeException re) {
        	updateStatus(IndexStatus.BUILD_FAILED, dml, meta, con);
        	throw re;
        }
          	
        if (progressMonitor != null) {
        	progressMonitor.setRowsProcessed(rowsExpected);
        	progressMonitor.setCurrentIndexStatus(IndexStatus.INDEXED);
        }
    }
    
    private static void updateStatus(IndexStatus status, RTreeDml dml, MetaNode meta, Connection con) throws SQLException {
        meta.setIndexStatus(status);
        PreparedStatement ps = con.prepareStatement(dml.getUpdateIndex());
        ps.setBytes(RTreeDml.NODE_DATA_COL, meta.getData());
        ps.setLong(RTreeDml.UPDATE_ID_COL, RTreeDml.META_NODE_ID);
        ps.execute();
        ps.close();
        con.commit();
    }
    
    public static void deSpatializeProc(String schema, String table) throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:default:connection");
        deSpatialize(con, schema, table);
    }
    
    /**
     * Despatialize a currently spatialized table. This amounts to removing the triggers
     * on the spatial table and dropping the index table.
     * 
     * @param spatialSchema
     * @param spatialTable
     * @throws SQLException
     */
    public static void deSpatialize(Connection con, String schema, String table) throws SQLException {
        if (schema == null) {
            schema = getDefaultSchema(con);
        }
        DatabaseMetaData meta = con.getMetaData();
        ResultSet rs = meta.getTables(null, schema, table, new String[] {"TABLE"});
        if (!rs.next()) {
            throw new SQLException("Table " + schema + "." + table + " not found");
        }
        Statement stmt = con.createStatement();
        RTreeDml dml = RTreeDml.createDml(con, schema, table);
        // index table and triggers might not exist
        try {
            stmt.executeUpdate("drop table " + dml.getIndexName());
        } catch (SQLException sqle) {}
        try {
            stmt.executeUpdate("drop trigger " + dml.getInsTriggerName());
        } catch (SQLException sqle) {}
        try {
            stmt.executeUpdate("drop trigger " + dml.getBeforeUpdTriggerName());
        } catch (SQLException sqle) {}
        try {
            stmt.executeUpdate("drop trigger " + dml.getUpdTriggerName());
        } catch (SQLException sqle) {}
        try {
            stmt.executeUpdate("drop trigger " + dml.getBeforeDelTriggerName());
        } catch (SQLException sqle) {}
        try {
            stmt.executeUpdate("drop trigger " + dml.getDelTriggerName());
        } catch (SQLException sqle) {}
    }
    
    /**
     * Insert an entry into the index for an existing row in the spatial table.
     * This method is used by the post-insert trigger on the spatial table.
     * 
     * @param schema The spatial schema
     * @param table The spatial table
     * @param id The id of the row in the spatial table
     * @throws SQLException
     */
    public static void insSpatial(String schema, String table, long id) throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:default:connection");
        RTreeDml dml = RTreeDml.createDml(con, schema, table);
        RTreeSessionDb session = new RTreeSessionDb(con, dml, true);
        RTree rTree = new RTree(session);
        PreparedStatement ps = con.prepareStatement(dml.getSelectSpatial());
        ps.setLong(1, id);
        byte[] data = null;
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            data = rs.getBytes(1);
        }
        if (data != null) {
            WKBReader reader = new WKBReader();
            Geometry geom = null;
            try {
                geom = reader.read(data);
            } catch (ParseException pe) {
                throw new SQLException("Failed to parse geom for " + id);
            }
            org.locationtech.jts.geom.Envelope e = geom.getEnvelopeInternal();
            Entry entry = new Entry(e.getMinX(), e.getMaxX(), e.getMinY(), e.getMaxY(), id);
            rTree.insert(entry);
        }
        ps.close();
    }
    
    /**
     * Update an entry in the index for an existing row in the spatial table.
     * This method is used by the post-update trigger on the spatial table.
     * 
     * @param schema The spatial schema
     * @param table The spatial table
     * @param id The id of the row in the spatial table
     * @throws SQLException
     */
    public static void updSpatial(String schema, String table, long id) throws SQLException {
        Entry oldEntry = savedEntry.get();
        Entry newEntry = new Entry(id);
        if (oldEntry == null) {
            throw new SQLException("Found no saved entry for " + id);
        }
        if (oldEntry.getId() != newEntry.getId()) {
            throw new SQLException("Saved entry not found for " + id + " - found " + oldEntry.getId() + " instead");
        }
        Connection con = DriverManager.getConnection("jdbc:default:connection");
        RTreeDml dml = RTreeDml.createDml(con, schema, table);
        RTreeSessionDb session = new RTreeSessionDb(con, dml, true);
        RTree rTree = new RTree(session);
        PreparedStatement ps = con.prepareStatement(dml.getSelectSpatial());
        ps.setLong(1, id);
        byte[] data = null;
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            data = rs.getBytes(1);
        }
        if (data != null) {
            WKBReader reader = new WKBReader();
            Geometry geom = null;
            try {
                geom = reader.read(data);
            } catch (ParseException pe) {
                throw new SQLException("Failed to parse geom for " + id);
            }
            org.locationtech.jts.geom.Envelope e = geom.getEnvelopeInternal();
            newEntry = new Entry(e.getMinX(), e.getMaxX(), e.getMinY(), e.getMaxY(), id);
        }
        if (oldEntry.equals(newEntry)) {
            // no need for any change
        } else {
            if (!oldEntry.isNullGeometry()) {
                rTree.delete(oldEntry);
            }
            if (!newEntry.isNullGeometry()) {
                rTree.insert(newEntry);
            }
        }
        ps.close();
    }
    
    /**
     * Delete an entry from the index for a deleted row in the spatial table.
     * This method is used by the post-delete trigger on the spatial table.
     * 
     * @param schema The spatial schema
     * @param table The spatial table
     * @param id The id of the row in the spatial table
     * @throws SQLException
     */
    public static void delSpatial(String schema, String table, long id) throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:default:connection");
        RTreeDml dml = RTreeDml.createDml(con, schema, table);
        RTreeSessionDb session = new RTreeSessionDb(con, dml, true);
        RTree rTree = new RTree(session);
        Entry entry = savedEntry.get();
        if (entry == null) {
            throw new SQLException("Found no saved entry for " + id);
        } else {
            if (id != entry.getId()) {
                throw new SQLException("Saved entry not found for " + id + " - found " + entry.getId() + " instead");
            }
            if (!entry.isNullGeometry()) {
                rTree.delete(entry);
            }
        }
    }
    
    /**
     * This procedure is invoked from 'before delete' and 'before update' triggers.
     * Its purpose is to store the entry of the 'old' state of a feature in the
     * ThreadLocal savedEntry to be used by the corresponding 'after delete'
     * and 'after update' triggers. This is necessary because Derby does not
     * allow large objects (eg BLOB) to be passed into procedures as parameters.
     * Derby also does not allow before triggers to do any database updates.
     * 
     * @param schema The spatial schema
     * @param table The spatial table
     * @param id The id of the row in the spatial table
     * @throws SQLException
     */
    public static void saveEntry(String schema, String table, long id) throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:default:connection");
        RTreeDml dml = RTreeDml.createDml(con, schema, table);
        PreparedStatement select = con.prepareStatement(dml.getSelectIndex());
        Lock lock = new Lock(con, dml, select); // sets meta data on RTreeDml
        lock.close();
        select.close();
        PreparedStatement ps = con.prepareStatement(dml.getSelectSpatial());
        ps.setLong(1, id);
        byte[] data = null;
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            data = rs.getBytes(1);
        }
        if (data == null) {
            savedEntry.set(new Entry(id));
        } else {
            WKBReader reader = new WKBReader();
            Geometry geom = null;
            try {
                geom = reader.read(data);
            } catch (ParseException pe) {
                throw new SQLException("Failed to parse geom for " + id);
            }
            org.locationtech.jts.geom.Envelope e = geom.getEnvelopeInternal();
            Entry entry = new Entry(e.getMinX(), e.getMaxX(), e.getMinY(), e.getMaxY(), id);
            savedEntry.set(entry);
        }
        ps.close();
    }
    
    /**
     * Convert Well Known Text string to Well Known Binary byte array.
     * <p><b>Note</b> the Derby limitation on function arguments and
     * return values that limits the size of WKT strings to 32,672 char
     * and WKB arrays to 32,672 bytes.
     * 
     * @param WKT
     * @return WKB
     * @throws ParseException
     */
    public static byte[] wktToWkb(String wkt) throws ParseException {
        WKTReader reader = new WKTReader();
        Geometry geom = reader.read(wkt);
        WKBWriter writer = new WKBWriter();
        return writer.write(geom);
    }
    
    /**
     * Convert Well Known Binary byte array to Well Known Text string.
     * <p><b>Note</b> the Derby limitation on function arguments and
     * return values that limits the size of WKT strings to 32,672 char
     * and WKB arrays to 32,672 bytes.
     * 
     * @param WKB
     * @return WKT
     * @throws ParseException
     */
    public static String wkbToWkt(byte[] wkb) throws ParseException {
        WKBReader reader = new WKBReader();
        Geometry geom = reader.read(wkb);
        WKTWriter writer = new WKTWriter();
        return writer.write(geom);
    }
    
    public static ResultSet mbrIntersectsEnvFunc(String schema, String table, double minx, double maxx, double miny, double maxy) throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:default:connection");
        return mbrIntersectsEnv(con, schema, table, minx, maxx, miny, maxy);
    }
    /**
     * A Table Function designed to provide the fastest possible intersection search
     * at the cost of spatial inaccuracy ie an id is returned for each feature whose
     * minimum bounding rectangle (MBR) intersects a rectangular search envelope.
     * This search employs only the spatial index, never retrieving the feature itself
     * and never applying a secondary spatial filter.
     * 
     * @param con
     * @param schema
     * @param table
     * @param minx
     * @param maxx
     * @param miny
     * @param maxy
     * @return
     * @throws SQLException
     */
    public static ResultSet mbrIntersectsEnv(Connection con, String schema, String table, double minx, double maxx, double miny, double maxy) throws SQLException {
        if (schema == null) {
            schema = getDefaultSchema(con);
        }
        RTreeDml dml = RTreeDml.createDml(con, schema, table);
        RTreeSessionDb session = new RTreeSessionDb(con, dml, false);
        List<Long> ids = null;
        if (dml.getMetaNode().getIndexStatus().equals(IndexStatus.INDEXED)) {
            RTree rTree = new RTree(session);
            ids = rTree.search(new Envelope(minx, maxx, miny, maxy));
            return new IdResultSet(ids, schema, table);
        } else { // get all ids and apply the secondary filter
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(dml.getSelectAllPk());
            ids = new ArrayList<Long>();
            while (rs.next()) {
                ids.add(rs.getLong(1));
            }
            rs.close();
            stmt.close();
            GeometryFactory geomFactory = new GeometryFactory();
            CoordinateArraySequenceFactory factory = CoordinateArraySequenceFactory.instance();
            Coordinate[] coord = new Coordinate[5];
            coord[0] = new Coordinate(minx, miny);
            coord[1] = new Coordinate(maxx, miny);
            coord[2] = new Coordinate(maxx, maxy);
            coord[3] = new Coordinate(minx, maxy);
            coord[4] = new Coordinate(minx, miny);
            Geometry queryGeom = new Polygon(new LinearRing(factory.create(coord), geomFactory), null, geomFactory);
            return new FilteredResultSet(ids, session, queryGeom, SpatialPredicate.INTERSECTS, schema, table);
        }
    }
    
    public static ResultSet queryIntersectsWkbFunc(String schema, String table, byte[] wkb) throws SQLException, ParseException {
        Connection con = DriverManager.getConnection("jdbc:default:connection");
        WKBReader reader = new WKBReader();
        Geometry queryGeom = reader.read(wkb);
        return queryWithPredicate(con, schema, table, SpatialPredicate.INTERSECTS, queryGeom);
    }
        
    public static ResultSet queryIntersectsWkb(Connection con, String schema, String table, byte[] wkb) throws SQLException, ParseException {
        WKBReader reader = new WKBReader();
        Geometry queryGeom = reader.read(wkb);
        return queryWithPredicate(con, schema, table, SpatialPredicate.INTERSECTS, queryGeom);
    }
    
    public static ResultSet queryIntersectsWktFunc(String schema, String table, String wkt) throws SQLException, ParseException {
        Connection con = DriverManager.getConnection("jdbc:default:connection");
        WKTReader reader = new WKTReader();
        Geometry queryGeom = reader.read(wkt);
        return queryWithPredicate(con, schema, table, SpatialPredicate.INTERSECTS, queryGeom);
    }
    
    public static ResultSet queryIntersectsWkt(Connection con, String schema, String table, String wkt) throws SQLException, ParseException {
        WKTReader reader = new WKTReader();
        Geometry queryGeom = reader.read(wkt);
        return queryWithPredicate(con, schema, table, SpatialPredicate.INTERSECTS, queryGeom);
    }
    
    public static ResultSet queryWithPredicateWkbFunc(String schema, String table, String predicate, byte[] wkb) throws SQLException, ParseException {
        Connection con = DriverManager.getConnection("jdbc:default:connection");
        WKBReader reader = new WKBReader();
        Geometry queryGeom = reader.read(wkb);
        return queryWithPredicate(con, schema, table, SpatialPredicate.valueOf(predicate.toUpperCase()), queryGeom);
    }
    
    public static ResultSet queryWithPredicateWkb(Connection con, String schema, String table, String predicate, byte[] wkb) throws SQLException, ParseException {
        WKBReader reader = new WKBReader();
        Geometry queryGeom = reader.read(wkb);
        return queryWithPredicate(con, schema, table, SpatialPredicate.valueOf(predicate.toUpperCase()), queryGeom);
    }
    
    public static ResultSet queryWithPredicateWktFunc(String schema, String table, String predicate, String wkt) throws SQLException, ParseException {
        Connection con = DriverManager.getConnection("jdbc:default:connection");
        WKTReader reader = new WKTReader();
        Geometry queryGeom = reader.read(wkt);
        return queryWithPredicate(con, schema, table, SpatialPredicate.valueOf(predicate.toUpperCase()), queryGeom);
    }
    
    public static ResultSet queryWithPredicateWkt(Connection con, String schema, String table, String predicate, String wkt) throws SQLException, ParseException {
        WKTReader reader = new WKTReader();
        Geometry queryGeom = reader.read(wkt);
        return queryWithPredicate(con, schema, table, SpatialPredicate.valueOf(predicate.toUpperCase()), queryGeom);
    }
        
    private static ResultSet queryWithPredicate(Connection con, String schema, String table,
            SpatialPredicate predicate, Geometry queryGeom) throws SQLException {
        if (schema == null) {
            schema = getDefaultSchema(con);
        }
        RTreeDml dml = RTreeDml.createDml(con, schema, table);
        RTreeSessionDb session = new RTreeSessionDb(con, dml, false);
        List<Long> ids = null;
        if (dml.getMetaNode().getIndexStatus().equals(IndexStatus.INDEXED)) {
            RTree rTree = new RTree(session);
            Envelope queryEnv = null;
            // All the predicates except DISJOINT will find their candidates by MBR intersects query envelope
            // DISJOINT needs to examine the whole spatial table.
            if (SpatialPredicate.DISJOINT.equals(predicate)) {
                queryEnv = new Envelope(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
            } else {
                org.locationtech.jts.geom.Envelope e = queryGeom.getEnvelopeInternal();
                queryEnv = new Envelope(e.getMinX(), e.getMaxX(), e.getMinY(), e.getMaxY());
            }
            ids = rTree.search(queryEnv);
        } else { // get all ids and apply the secondary filter
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(dml.getSelectAllPk());
            ids = new ArrayList<Long>();
            while (rs.next()) {
                ids.add(rs.getLong(1));
            }
            rs.close();
            stmt.close();
        }
        return new FilteredResultSet(ids, session, queryGeom, predicate, schema, table);
    }
    
    /**
     * Determine the bounds of a dataset by the most economical means available. If an index is built then
     * use the root node, otherwise do a full scan of the spatial table.
     * 
     * @param con A JDBC conection to the database
     * @param schema The dataset schema
     * @param table The table name of the dataset
     */
    public static org.locationtech.jts.geom.Envelope getDatasetBounds(Connection con, String schema, String table) throws SQLException {
        if (schema == null) {
            schema = getDefaultSchema(con);
        }
        RTreeDml dml = RTreeDml.createDml(con, schema, table);
        RTreeSessionDb session = null;
        try {
            session = new RTreeSessionDb(con, dml, false);
        } catch (SQLException sqle) { // no index table, return null Envelope
        	return new org.locationtech.jts.geom.Envelope();
        }
        long rootId = session.getRootId();
        if (rootId < 0) { // index not built
    		//TODO investigate more efficient solution
        	org.locationtech.jts.geom.Envelope jtsEnv = new org.locationtech.jts.geom.Envelope();
        	WKBReader reader = new WKBReader();
            Statement stmt = con.createStatement();
        	ResultSet rs = stmt.executeQuery(dml.getSelectAllSpatial());
            while (rs.next()) {
            	try {
                    jtsEnv.expandToInclude(reader.read(rs.getBytes(1)).getEnvelopeInternal());
            	} catch (ParseException pe) {
            		// if the wkb is invalid then ignore it
            	}
            }
            rs.close();
            stmt.close();
            return jtsEnv;
        } else {
        	Envelope env = session.getNode(rootId).getBounds();
            return new org.locationtech.jts.geom.Envelope(env.getMinX(), env.getMaxX(), env.getMinY(), env.getMaxY());
        }
    }
}
