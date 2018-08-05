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

import java.sql.Connection;
import java.sql.SQLException;

import net.sourceforge.hatbox.Entry;
import net.sourceforge.hatbox.RTree;
import net.sourceforge.hatbox.RTreeDml;
import net.sourceforge.hatbox.RTreeSessionDb;
import net.sourceforge.hatbox.wk.WKBEnvelope;

/**
 * H2 trigger for updating an entry in the RTree on update of the spatial table. 
 * 
 * @author Peter Yuill, Justin Deoliveira
 */
public class UpdateTrigger extends AbstractTrigger {
    
    private String schema;
    private String table;

    public void fire(Connection con, Object[] oldRow, Object[] newRow) throws SQLException {
        RTreeDml dml = RTreeDml.createDml(con, schema, table);
        RTreeSessionDb session = new RTreeSessionDb(con, dml, true);
        RTree rTree = new RTree(session);
        int pkI = dml.getPkColumnIndex();
        int geomI = dml.getGeomColumnIndex();
        Entry oldEntry = null;
        Entry newEntry = null;
        if (oldRow[geomI] != null) {
        	WKBEnvelope env = null;
        	try {
        		env = getEnvelope(oldRow[geomI]);
        	} catch (Exception e) {
        		throw (SQLException)
        		new SQLException("Failed to obtain geom for " + oldRow[pkI]).initCause(e);
        	}
            oldEntry = new Entry(
                env.getMinX(), env.getMaxX(), env.getMinY(), env.getMaxY(), ((Number)oldRow[pkI]).longValue());
        }
        if (newRow[geomI] != null) {
        	WKBEnvelope env = null;
        	try {
        		env = getEnvelope(newRow[geomI]);
        	} catch (Exception e) {
        		throw (SQLException)
        		new SQLException("Failed to obtain geom for " + newRow[pkI]).initCause(e);
        	}
            newEntry = new Entry(
                env.getMinX(), env.getMaxX(), env.getMinY(), env.getMaxY(), ((Number)newRow[pkI]).longValue());
        }
        if ((newEntry != null) && newEntry.equals(oldEntry)) {
            // no change, do nothing
        } else {
            if (oldEntry != null) {
                rTree.delete(oldEntry);
            }
            if (newEntry != null) {
                rTree.insert(newEntry);
            }
        }
        session.closeAll();
    }

    public void init(Connection con, String schema, String trigger, String table,
            boolean before, int type) throws SQLException {
        this.schema = schema;
        this.table = table;
    }

}
