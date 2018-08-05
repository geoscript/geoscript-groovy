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
 * H2 trigger for adding an entry to the RTree on insert in the spatial table. 
 * 
 * @author Peter Yuill, Justin Deoliveira
 */
public class InsertTrigger extends AbstractTrigger {
    
    private String schema;
    private String table;

    public void fire(Connection con, Object[] oldRow, Object[] newRow) throws SQLException {
        RTreeDml dml = RTreeDml.createDml(con, schema, table);
        RTreeSessionDb session = new RTreeSessionDb(con, dml, true);
        RTree rTree = new RTree(session);
        int pkI = dml.getPkColumnIndex();
        int geomI = dml.getGeomColumnIndex();
        if (newRow[geomI] != null) {
        	WKBEnvelope env = null;
        	try {
        		env = getEnvelope(newRow[geomI]);
        	} catch (Exception e) {
        		throw (SQLException)
        		new SQLException("Failed to obtain geom for " + newRow[pkI]).initCause(e);
        	}
            Entry entry = new Entry(
                env.getMinX(), env.getMaxX(), env.getMinY(), env.getMaxY(), ((Number)newRow[pkI]).longValue());
            rTree.insert(entry);
        }
        session.closeAll();
    }

    public void init(Connection con, String schema, String trigger, String table,
            boolean before, int type) throws SQLException {
        this.schema = schema;
        this.table = table;
    }

}
