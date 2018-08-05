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

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class IdResultSetMetaData implements ResultSetMetaData {
    
    private String schema;
    private String table;
    
    public IdResultSetMetaData(String schema, String table) {
        this.schema = schema;
        this.table = table;
    }

    public String getCatalogName(int column) throws SQLException {
        return null;
    }

    public String getColumnClassName(int column) throws SQLException {
        return Long.class.getName();
    }

    public int getColumnCount() throws SQLException {
        return 1;
    }

    public int getColumnDisplaySize(int column) throws SQLException {
        return 18;
    }

    public String getColumnLabel(int column) throws SQLException {
        return "HATBOX_JOIN_ID";
    }

    public String getColumnName(int column) throws SQLException {
        return "HATBOX_JOIN_ID";
    }

    public int getColumnType(int column) throws SQLException {
        return -5;
    }

    public String getColumnTypeName(int column) throws SQLException {
        return "BIGINT";
    }

    public int getPrecision(int column) throws SQLException {
        return 0;
    }

    public int getScale(int column) throws SQLException {
        return 0;
    }

    public String getSchemaName(int column) throws SQLException {
        return schema;
    }

    public String getTableName(int column) throws SQLException {
        return table;
    }

    public boolean isAutoIncrement(int column) throws SQLException {
        return false;
    }

    public boolean isCaseSensitive(int column) throws SQLException {
        return false;
    }

    public boolean isCurrency(int column) throws SQLException {
        return false;
    }

    public boolean isDefinitelyWritable(int column) throws SQLException {
        return false;
    }

    public int isNullable(int column) throws SQLException {
        return 0;
    }

    public boolean isReadOnly(int column) throws SQLException {
        return true;
    }

    public boolean isSearchable(int column) throws SQLException {
        return false;
    }

    public boolean isSigned(int column) throws SQLException {
        return true;
    }

    public boolean isWritable(int column) throws SQLException {
        return false;
    }
    
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }
}
