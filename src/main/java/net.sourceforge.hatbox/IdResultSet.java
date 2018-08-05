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

import java.util.List;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * A result set for returning a single Entry Identifier column
 * 
 * @author Peter Yuill
 */
public class IdResultSet extends AbstractResultSet {
    
    protected List<Long> ids;
    protected Long id;
    protected int currentIndex = -1;
    protected int size;
    protected boolean rowAvailable = false;
    private String schema;
    private String table;
    
    public IdResultSet(List<Long> ids, String schema, String table) {
        this.ids = ids;
        size = ids.size();
        this.schema = schema;
        this.table = table;
    }

    @Override
    public void close() throws SQLException {
        ids = null;
    }

    @Override
    public boolean isClosed() throws SQLException {
        return (ids == null);
    }

    @Override
    public int findColumn(String columnLabel) throws SQLException {
        return 1;
    }

    @Override
    public boolean next() throws SQLException {
        if (ids == null) {
            rowAvailable = false;
        } else {
            currentIndex++;
            if (currentIndex < size) {
                id = ids.get(currentIndex);
                rowAvailable = true;
            } else {
                rowAvailable = false;
            }
        }
        return rowAvailable;
    }

    @Override
    public boolean wasNull() throws SQLException {
        return (id == null);
    }

    @Override
    public Object getObject(int columnIndex) throws SQLException {
        check(columnIndex);
        return id;
    }

    @Override
    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        check(columnIndex);
        return type.cast(id);
    }

    @Override
    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        check(findColumn(columnLabel));
        return type.cast(id);
    }
    
    @Override
    public String getString(int columnIndex) throws SQLException {
        check(columnIndex);
        if (id == null) {
            return null;
        } else {
            return id.toString();
        }
    }

    @Override
    public long getLong(int columnIndex) throws SQLException {
        check(columnIndex);
        if (id == null) {
            return 0L;
        } else {
            return id.longValue();
        }
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return new IdResultSetMetaData(schema, table);
    }

    private void check(int columnIndex) throws SQLException {
        if (rowAvailable) {
            if (columnIndex != 1) {
                throw new SQLException("Column index out of range");
            }
        } else {
            throw new SQLException("No row is current");
        }
    }
}
