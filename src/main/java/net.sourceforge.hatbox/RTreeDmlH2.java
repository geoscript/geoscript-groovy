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


public class RTreeDmlH2 extends RTreeDml {
    
    public RTreeDmlH2(String schema, String table) {
        super(schema, table);
    }
    
    public String createSelectAllPk() {
        return "select \"" + getPkColumn() + "\" from " + getFullTableName();
    }
    
    public String createSelectSpatial() {
        return "select \"" + getGeomColumn() + "\" from " + 
        getFullTableName() +
        " where \"" + getPkColumn() + "\" = ?";
    }
    
    public String createSelectAllSpatial() {
        return "select \"" + getGeomColumn() + "\" from " + 
        getFullTableName();        
    }

    public String createCreateIndex(int nodeStorageSize) {
        return "create table " + getIndexName() +
        " (id identity not null," +
        "  node_data binary(" + nodeStorageSize + ") not null)";
    }

    public String createCreateInsTrigger() {
        return "create trigger " +
        getInsTriggerName() +
        " after insert on " + getFullTableName() +
        " for each row " +
        " call \"net.sourceforge.hatbox.jts.InsertTrigger\"";
    }

    public String createCreateBeforeDelTrigger() {
        return "";
    }

    public String createCreateDelTrigger() {
        return "create trigger " +
        getDelTriggerName() +
        " after delete on " + getFullTableName() +
        " for each row " +
        " call \"net.sourceforge.hatbox.jts.DeleteTrigger\"";
    }

    public String createCreateBeforeUpdTrigger() {
        return "";
    }

    public String createCreateUpdTrigger() {
        return "create trigger " +
        getUpdTriggerName() +
        " after update on " + getFullTableName() +
        " for each row " +
        " call \"net.sourceforge.hatbox.jts.UpdateTrigger\"";
    }

    public String createSelectIndex() {
        return "select node_data, id from " +
        getIndexName() + " where id = ?";
    }

    public String createInsertIndex() {
        return "insert into " + getIndexName() + 
        " (node_data) values (?)";
    }
    
    public String createUpdateIndex() {
        return "update " + getIndexName() + 
        " set node_data = ?" +
        " where id = ?";
    }

    public String createDeleteIndex() {
        return "delete from " + getIndexName() + 
        " where id = ?";
    }

    public String createDeleteAllIndex() {
        return "delete from " + getIndexName() + 
        " where id > " + META_NODE_ID;
    }
}
