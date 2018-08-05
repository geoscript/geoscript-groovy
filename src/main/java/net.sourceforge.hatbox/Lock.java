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
package net.sourceforge.hatbox;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;

/**
 * Acquires a read or write lock on the rtree meta node.
 * The meta node is a special node that contains spatial
 * table metadata rather than index entries.
 * 
 * @author Peter Yuill
 */
public class Lock {
    
    private MetaNode meta;
    private ResultSet resultSet;
    private boolean write = false;
    
    /**
     * The read lock constructor. Assumes a constructed index is available.
     * <b/>Acquires a read lock on the Meta Node
     * 
     * @param con The connection to use for acquiring the lock
     * @param dml The database and index specific DML to use
     * @param selectStmt The PreparedStatement to be used to acquire a read lock
     */
    public Lock(Connection con, RTreeDml dml, PreparedStatement selectStmt) {
        this.write = false;
        try {
            selectStmt.setLong(1, RTreeDml.META_NODE_ID);
            resultSet = selectStmt.executeQuery();
            if (!resultSet.next()) {
                throw new RTreeInternalException("Index meta node not found: " + dml.getIndexName());
            }
            byte[] nodeData = resultSet.getBytes(RTreeDml.NODE_DATA_COL);
            meta = new MetaNode(nodeData);
            dml.setMetaNode(meta);
        } catch (SQLException sqle) {
            throw new RTreeInternalException("Unable to select meta node", sqle);
        }
    }
    
    /**
     * The write lock constructor. Assumes a constructed index is available.
     * <b/>Acquires a write lock on the Meta Node
     * 
     * @param con The connection to use for acquiring the lock
     * @param dml The database and index specific DML to use
     */
    public Lock(Connection con, RTreeDml dml) {
        this.write = true;
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(dml.getSelectIndex() + " FOR UPDATE",
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ps.setLong(1, RTreeDml.META_NODE_ID);
            resultSet = ps.executeQuery();
            if (!resultSet.next()) {
                throw new RTreeInternalException("Index meta node not found: " + dml.getIndexName());
            }
            byte[] nodeData = resultSet.getBytes(RTreeDml.NODE_DATA_COL);
            meta = new MetaNode(nodeData);
            if (IndexStatus.BUILDING.equals(meta.getIndexStatus())) {
                throw new RTreeInternalException("Writing is not allowed while index is being constructed");
            }
            dml.setMetaNode(meta);
            resultSet.updateBytes(RTreeDml.NODE_DATA_COL, nodeData);
            resultSet.updateRow();
        } catch (SQLException sqle) {
            throw new RTreeInternalException("Unable to select meta node", sqle);
        }
    }

    /**
     * @return The id (primary key) of the index root node. Defaults to -1 if not set.
     */
    public long getRootId() {
        return meta.getRootId();
    }

    /**
     * Set the id of the index root node. The Lock must be a write lock.
     * 
     * @param rootId The id (primary key) of the index root node.
     * @throws SQLException
     */
    public void setRootId(long rootId) throws SQLException {
        meta.setRootId(rootId);
        if (write && (resultSet != null)) {
            resultSet.updateBytes(RTreeDml.NODE_DATA_COL, meta.getData());
            resultSet.updateRow();
        } else {
            throw new SQLException ("Lock closed or not writable");
        }
    }

    /**
     * @return The ResultSet used to implement this lock.
     */
    public ResultSet getResultSet() {
        return resultSet;
    }

    /**
     * @return true if the lock is a write lock.
     */
    public boolean isWrite() {
        return write;
    }
    
    /**
     * Close the underlying ResultSet of this lock.
     * 
     * @throws SQLException
     */
    public void close() throws SQLException {
        if (resultSet != null) {
        	if (write) {
                resultSet.getStatement().close();
        	} else {
        		resultSet.close();
        	}
            resultSet = null;
        }
    }

    /**
     * @return true if the lock is closed (has no ResultSet).
     */
    public boolean isClosed() {
        return (resultSet == null);
    }
}
