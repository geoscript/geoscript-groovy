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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;

public abstract class RTreeDml {
    
    public static final long META_NODE_ID = 1;

    public static final int NODE_DATA_COL = 1;
    public static final int ID_COL = 1;
    public static final int UPDATE_ID_COL = 2;
    
    private MetaNode metaNode;
    protected String schema;
    protected String table;
    private String selectAllPk;
    private String selectSpatial;
    private String selectAllSpatial;
    private String createInsTrigger;
    private String createBeforeUpdTrigger;
    private String createUpdTrigger;
    private String createBeforeDelTrigger;
    private String createDelTrigger;
    private String selectIndex;
    private String insertIndex;
    private String updateIndex;
    private String deleteIndex;
    private String deleteAllIndex;
    
    protected RTreeDml(String schema, String table) {
        this.schema = schema;
        this.table = table;
    }
    
    public static RTreeDml createDml(Connection con, String schema, String table) throws SQLException {
        String product = con.getMetaData().getDatabaseProductName();
        if (product.equals("Apache Derby")) {
            return new RTreeDmlDerby(schema, table);
        } else if (product.equals("H2")) {
            return new RTreeDmlH2(schema, table);
        } else {
            throw new IllegalArgumentException("Not a Derby or H2 Connection: " + product);
        }
    }
    
    public String getSchema() {
        return schema;
    }

    public String getTable() {
        return table;
    }

    public String getPkColumn() {
        return metaNode.getPkColName();
    }

    public int getPkColumnIndex() {
        return metaNode.getPkColIndex();
    }

    public String getGeomColumn() {
        return metaNode.getGeomColName();
    }

    public int getGeomColumnIndex() {
        return metaNode.getGeomColIndex();
    }

    public MetaNode getMetaNode() {
        return metaNode;
    }

    public void setMetaNode(MetaNode metaNode) {
        this.metaNode = metaNode;
    }

    public String getFullTableName() {
        StringBuilder buf = new StringBuilder();
        buf.append('"').append(schema).append('"').append('.').append('"').append(table).append('"');
        return buf.toString();
    }
    
    public String getIndexName() {
        StringBuilder buf = new StringBuilder();
        buf.append('"').append(schema).append('"').append('.').append('"').append(table).append("_HATBOX").append('"');
        return buf.toString();
    }
    
    public String getInsTriggerName() {
        StringBuilder buf = new StringBuilder();
        buf.append('"').append(schema).append('"').append('.').append('"').append(table).append("_INSTRG").append('"');
        return buf.toString();
    }
    
    public String getUpdTriggerName() {
        StringBuilder buf = new StringBuilder();
        buf.append('"').append(schema).append('"').append('.').append('"').append(table).append("_UPDTRG").append('"');
        return buf.toString();
    }
    
    public String getBeforeUpdTriggerName() {
        StringBuilder buf = new StringBuilder();
        buf.append('"').append(schema).append('"').append('.').append('"').append(table).append("_UPD_BT").append('"');
        return buf.toString();
    }
    
    public String getDelTriggerName() {
        StringBuilder buf = new StringBuilder();
        buf.append('"').append(schema).append('"').append('.').append('"').append(table).append("_DELTRG").append('"');
        return buf.toString();
    }
    
    public String getBeforeDelTriggerName() {
        StringBuilder buf = new StringBuilder();
        buf.append('"').append(schema).append('"').append('.').append('"').append(table).append("_DEL_BT").append('"');
        return buf.toString();
    }
    
    public String getSelectAllPk() {
        if (selectAllPk == null) {
            selectAllPk = createSelectAllPk();
        }
        return selectAllPk;
    }
    
    public abstract String createSelectAllPk();
    
    public String getSelectSpatial() {
        if (selectSpatial == null) {
            selectSpatial = createSelectSpatial();
        }
        return selectSpatial;
    }
    
    public abstract String createSelectSpatial();
    
    public String getSelectAllSpatial() {
        if (selectAllSpatial == null) {
            selectAllSpatial = createSelectAllSpatial();
        }
        return selectAllSpatial;
    }
    
    public abstract String createSelectAllSpatial();

    public String getCreateIndex(int nodeStorageSize) {
        return createCreateIndex(nodeStorageSize);
    }

    public abstract String createCreateIndex(int nodeStorageSize);

    public String getCreateInsTrigger() {
        if (createInsTrigger == null) {
            createInsTrigger = createCreateInsTrigger();
        }
        return createInsTrigger;
    }

    public abstract String createCreateInsTrigger();

    public String getCreateDelTrigger() {
        if (createDelTrigger == null) {
            createDelTrigger = createCreateDelTrigger();
        }
        return createDelTrigger;
    }

    public abstract String createCreateDelTrigger();

    public String getCreateBeforeDelTrigger() {
        if (createBeforeDelTrigger == null) {
            createBeforeDelTrigger = createCreateBeforeDelTrigger();
        }
        return createBeforeDelTrigger;
    }

    public abstract String createCreateBeforeDelTrigger();

    public String getCreateUpdTrigger() {
        if (createUpdTrigger == null) {
            createUpdTrigger = createCreateUpdTrigger();
        }
        return createUpdTrigger;
    }

    public abstract String createCreateUpdTrigger();

    public String getCreateBeforeUpdTrigger() {
        if (createBeforeUpdTrigger == null) {
            createBeforeUpdTrigger = createCreateBeforeUpdTrigger();
        }
        return createBeforeUpdTrigger;
    }

    public abstract String createCreateBeforeUpdTrigger();

    public String getSelectIndex() {
        if (selectIndex == null) {
            selectIndex = createSelectIndex();
        }
        return selectIndex;
    }

    public abstract String createSelectIndex();

    public String getInsertIndex() {
        if (insertIndex == null) {
            insertIndex = createInsertIndex();
        }
        return insertIndex;
    }

    public abstract String createInsertIndex();

    public String getUpdateIndex() {
        if (updateIndex == null) {
            updateIndex = createUpdateIndex();
        }
        return updateIndex;
    }
    
    public abstract String createUpdateIndex();

    public String getDeleteIndex() {
        if (deleteIndex == null) {
            deleteIndex = createDeleteIndex();
        }
        return deleteIndex;
    }

    public abstract String createDeleteIndex();

    public String getDeleteAllIndex() {
        if (deleteAllIndex == null) {
            deleteAllIndex = createDeleteAllIndex();
        }
        return deleteAllIndex;
    }

    public abstract String createDeleteAllIndex();
}
