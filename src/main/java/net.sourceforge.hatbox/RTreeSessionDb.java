/*
 *    HatBox : A user-space spatial add-on for Java databases
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
package net.sourceforge.hatbox;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.nio.ByteBuffer;

/**
 * 
 * @author Peter Yuill
 */
public class RTreeSessionDb implements RTreeSession {
    private Connection con;
    private PreparedStatement selectSpatialStmt;
    private PreparedStatement selectStmt;
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement deleteStmt;
    private RTreeDml dml;
    private Lock lock;
    private int commitInterval = 0;
    private int dmlCount = 0;
    
    /**
     * A special purpose constructor to support index creation.
     * <p>
     * Assumes that the connection is *not* nested
     * (ie it can be committed explicitly). and a lock
     * is not required because the index status has
     * already been set to 'BUILDING'
     * 
     * @param con The JDBC Connection to use for the session
     * @param dml The RTreeDml describing the dataset
     * @param commitInterval The interval at which commit should be issued during an index build
     * @throws SQLException
     */
    public RTreeSessionDb(Connection con, RTreeDml dml, int commitInterval) throws SQLException {
        this.con = con;
        this.dml = dml;
        this.commitInterval = commitInterval;
    }
    
    /**
     * The general use constructor for short transactions.
     * <p>
     * This constructor creates a Lock which acts on the index meta node.
     * 
     * @param con The database connection to use
     * @param dml The database and index specific DML to use
     * @param write Is the session going to write to the index
     * @throws SQLException
     */
    public RTreeSessionDb(Connection con, RTreeDml dml, boolean write) throws SQLException {
        this.con = con;
        this.dml = dml;
        if (write) {
            lock = new Lock(con, dml);
        } else {
            selectStmt = con.prepareStatement(dml.getSelectIndex());
            lock = new Lock(con, dml, selectStmt);
        }
    }
    
    public long getRootId() throws SQLException {
        return dml.getMetaNode().getRootId();
    }

    public void setRootId(long id) throws SQLException {
        dml.getMetaNode().setRootId(id);
        if (lock == null) { // index build mode
            if (updateStmt == null) {
                updateStmt = con.prepareStatement(dml.getUpdateIndex());
            }
            updateStmt.setBytes(RTreeDml.NODE_DATA_COL, dml.getMetaNode().getData());
            updateStmt.setLong(RTreeDml.UPDATE_ID_COL, RTreeDml.META_NODE_ID);
            updateStmt.executeUpdate();
        } else { // normal mode
            lock.setRootId(id);
        }
    }

    public Connection getCon() {
        return con;
    }

    public void setCon(Connection con) {
        this.con = con;
    }
    
    public void closeAll() {
        if (lock != null) {
            try {
                lock.close();
            } catch (SQLException e) {}
            lock = null;
        }
        if (selectStmt != null) {
            synchronized(selectStmt) {
                try {
                    selectStmt.close();
                } catch (SQLException e) {}
                selectStmt = null;
            }
        }
        if (selectSpatialStmt != null) {
            synchronized(selectSpatialStmt) {
                try {
                    selectSpatialStmt.close();
                } catch (SQLException e) {}
                selectSpatialStmt = null;
            }
        }
        if (insertStmt != null) {
            synchronized(insertStmt) {
                try {
                    insertStmt.close();
                } catch (SQLException e) {}
                insertStmt = null;
            }
        }
        if (updateStmt != null) {
            synchronized(updateStmt) {
                try {
                    updateStmt.close();
                } catch (SQLException e) {}
                updateStmt = null;
            }
        }
        if (deleteStmt != null) {
            synchronized(deleteStmt) {
                try {
                    deleteStmt.close();
                } catch (SQLException e) {}
                deleteStmt = null;
            }
        }
    }
    
    public Node getNode(long id) throws SQLException {
        Node node = null;
        if (selectStmt == null) {
            selectStmt = con.prepareStatement(dml.getSelectIndex());
        }
        synchronized(selectStmt) {
            try {
                selectStmt.setLong(RTreeDml.ID_COL, id);
                ResultSet rs = selectStmt.executeQuery();
                if (rs.next()) {
                    byte[] data = rs.getBytes(RTreeDml.NODE_DATA_COL);
                    node = new Node(id, data);
                } else {
                    throw new RTreeInternalException(id + " not found in database");
                }
            } catch (SQLException sqle) {
                try {
                    selectStmt.close();
                } catch (SQLException e) {}
                selectStmt = null;
                throw sqle;
            }
        }
        return node;
    }
    
    public long insertNode(Node node) throws SQLException {
        int id = 0;
        
        if (insertStmt == null) {
            insertStmt = con.prepareStatement(dml.getInsertIndex(), Statement.RETURN_GENERATED_KEYS);
        }
        synchronized(insertStmt) {
            try {
                insertStmt.setBytes(RTreeDml.NODE_DATA_COL, node.getData());
                int rows = insertStmt.executeUpdate();
                if (rows == 1) {
                    ResultSet keyRs = insertStmt.getGeneratedKeys();
                    if (keyRs.next()) {
                        id = keyRs.getInt(1);
                        node.setId(id);
                    } else {
                        throw new RTreeInternalException("No generated key returned from insert");
                    }
                } else {
                    throw new RTreeInternalException("No index node inserted");
                }
                if (commitInterval > 0) {
                    // a user connection
                    dmlCount++;
                    if ((dmlCount % commitInterval) == 0) {
                        con.commit();
                    }
                }
            } catch (SQLException sqle) {
                try {
                    insertStmt.close();
                } catch (SQLException e) {}
                insertStmt = null;
                throw sqle;
            }
        }
        node.setDirty(false);
        return id;
    }
    
    public void updateNode(Node node) throws SQLException {
        if (updateStmt == null) {
            updateStmt = con.prepareStatement(dml.getUpdateIndex());
        }
        synchronized(updateStmt) {
            try {
                updateStmt.setBytes(RTreeDml.NODE_DATA_COL, node.getData());
                updateStmt.setLong(RTreeDml.UPDATE_ID_COL, node.getId());
                int rows = updateStmt.executeUpdate();
                if (rows != 1) {
                    throw new RTreeInternalException("No rows updated for key: " + node.getId());
                }
                if (commitInterval > 0) {
                    // a user connection
                    dmlCount++;
                    if ((dmlCount % commitInterval) == 0) {
                        con.commit();
                    }
                }
                // No need to update the node cache because this node was recently retrieved
            } catch (SQLException sqle) {
                try {
                    updateStmt.close();
                } catch (SQLException e) {}
                updateStmt = null;
                throw sqle;
            }
        }
        node.setDirty(false);
    }
    
    public void deleteNode(Node node) throws SQLException {
        if (deleteStmt == null) {
            deleteStmt = con.prepareStatement(dml.getDeleteIndex());
        }
        synchronized(deleteStmt) {
            try {
                deleteStmt.setLong(RTreeDml.ID_COL, node.getId());
            } catch (SQLException sqle) {
                try {
                    deleteStmt.close();
                } catch (SQLException e) {}
                deleteStmt = null;
                throw sqle;                
            }
        }
    }
    
    public byte[] getWKB(long id) throws SQLException {
        byte[] bytes = null;
        if (selectSpatialStmt == null) {
            selectSpatialStmt = con.prepareStatement(dml.getSelectSpatial());
        }
        synchronized (selectSpatialStmt) {
            selectSpatialStmt.setLong(1, id);
            ResultSet rs = null;
            try {
                rs = selectSpatialStmt.executeQuery();
                if (rs.next()) {
                    bytes = rs.getBytes(1);
                }
            } finally {
                if (rs != null) {
                    try { rs.close(); } catch (SQLException sqle) {}
                }
            }
            
        }
        return bytes;
    }
}
