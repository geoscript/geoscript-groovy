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

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

public abstract class AbstractResultSet implements ResultSet {

    public boolean absolute(int row) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void afterLast() throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void beforeFirst() throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void cancelRowUpdates() throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void clearWarnings() throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public abstract void close() throws SQLException;

    public void deleteRow() throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public abstract int findColumn(String columnLabel) throws SQLException;

    public boolean first() throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public Array getArray(int columnIndex) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public Array getArray(String columnLabel) throws SQLException {
        return getArray(findColumn(columnLabel));
    }

    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public InputStream getAsciiStream(String columnLabel) throws SQLException {
        return getAsciiStream(findColumn(columnLabel));
    }

    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        return getBigDecimal(findColumn(columnLabel));
    }

    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        return getBigDecimal(findColumn(columnLabel), scale);
    }

    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public InputStream getBinaryStream(String columnLabel) throws SQLException {
        return getBinaryStream(findColumn(columnLabel));
    }

    public Blob getBlob(int columnIndex) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public Blob getBlob(String columnLabel) throws SQLException {
        return getBlob(findColumn(columnLabel));
    }

    public boolean getBoolean(int columnIndex) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public boolean getBoolean(String columnLabel) throws SQLException {
        return getBoolean(findColumn(columnLabel));
    }

    public byte getByte(int columnIndex) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public byte getByte(String columnLabel) throws SQLException {
        return getByte(findColumn(columnLabel));
    }

    public byte[] getBytes(int columnIndex) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public byte[] getBytes(String columnLabel) throws SQLException {
        return getBytes(findColumn(columnLabel));
    }

    public Reader getCharacterStream(int columnIndex) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public Reader getCharacterStream(String columnLabel) throws SQLException {
        return getCharacterStream(findColumn(columnLabel));
    }

    public Clob getClob(int columnIndex) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public Clob getClob(String columnLabel) throws SQLException {
        return getClob(findColumn(columnLabel));
    }

    public int getConcurrency() throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public String getCursorName() throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public Date getDate(int columnIndex) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public Date getDate(String columnLabel) throws SQLException {
        return getDate(findColumn(columnLabel));
    }

    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public Date getDate(String columnLabel, Calendar cal) throws SQLException {
        return getDate(findColumn(columnLabel), cal);
    }

    public double getDouble(int columnIndex) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public double getDouble(String columnLabel) throws SQLException {
        return getDouble(findColumn(columnLabel));
    }

    public int getFetchDirection() throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public int getFetchSize() throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public float getFloat(int columnIndex) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public float getFloat(String columnLabel) throws SQLException {
        return getFloat(findColumn(columnLabel));
    }

    public int getHoldability() throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public int getInt(int columnIndex) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public int getInt(String columnLabel) throws SQLException {
        return getInt(findColumn(columnLabel));
    }

    public long getLong(int columnIndex) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public long getLong(String columnLabel) throws SQLException {
        return getLong(findColumn(columnLabel));
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public Reader getNCharacterStream(int columnIndex) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public Reader getNCharacterStream(String columnLabel) throws SQLException {
        return getNCharacterStream(findColumn(columnLabel));
    }

    public NClob getNClob(int columnIndex) throws SQLException {
        throw new SQLException("Unimplemented");
    }
    
    public NClob getNClob(String columnLabel) throws SQLException {
        return getNClob(findColumn(columnLabel));
    };

    public String getNString(int columnIndex) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public String getNString(String columnLabel) throws SQLException {
        return getNString(findColumn(columnLabel));
    }

    public Object getObject(int columnIndex) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public Object getObject(String columnLabel) throws SQLException {
        return getObject(findColumn(columnLabel));
    }

    public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
        return getObject(findColumn(columnLabel), map);
    }

    public Ref getRef(int columnIndex) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public Ref getRef(String columnLabel) throws SQLException {
        return getRef(findColumn(columnLabel));
    }

    public int getRow() throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public RowId getRowId(int columnIndex) throws SQLException {
        throw new SQLException("Unimplemented");
    }
    
    public RowId getRowId(String columnLabel) throws SQLException {
        return getRowId(findColumn(columnLabel));
    }
    
    public short getShort(int columnIndex) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public short getShort(String columnLabel) throws SQLException {
        return getShort(findColumn(columnLabel));
    }

    public Statement getStatement() throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public String getString(int columnIndex) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public String getString(String columnLabel) throws SQLException {
        return getString(findColumn(columnLabel));
    }

    public SQLXML getSQLXML(int columnIndex) throws SQLException {
        throw new SQLException("Unimplemented");
    }
    
    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        return getSQLXML(findColumn(columnLabel));
    }

    public Time getTime(int columnIndex) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public Time getTime(String columnLabel) throws SQLException {
        return getTime(findColumn(columnLabel));
    }

    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public Time getTime(String columnLabel, Calendar cal) throws SQLException {
        return getTime(findColumn(columnLabel), cal);
    }

    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        return getTimestamp(findColumn(columnLabel));
    }

    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        return getTimestamp(findColumn(columnLabel), cal);
    }

    public int getType() throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public URL getURL(int columnIndex) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public URL getURL(String columnLabel) throws SQLException {
        return getURL(findColumn(columnLabel));
    }

    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public InputStream getUnicodeStream(String columnLabel) throws SQLException {
        return getUnicodeStream(findColumn(columnLabel));
    }

    public SQLWarning getWarnings() throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void insertRow() throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public boolean isAfterLast() throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public boolean isBeforeFirst() throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public boolean isClosed() throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public boolean isFirst() throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public boolean isLast() throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public boolean last() throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void moveToCurrentRow() throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void moveToInsertRow() throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public abstract boolean next() throws SQLException;

    public boolean previous() throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void refreshRow() throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public boolean relative(int rows) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public boolean rowDeleted() throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public boolean rowInserted() throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public boolean rowUpdated() throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void setFetchDirection(int direction) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void setFetchSize(int rows) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void updateArray(int columnIndex, Array x) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void updateArray(String columnLabel, Array x) throws SQLException {
        updateArray(findColumn(columnLabel), x);
    }

    public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
        updateAsciiStream(findColumn(columnLabel), x);
    }

    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
        updateAsciiStream(findColumn(columnLabel), x, length);
    }

    public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
        updateAsciiStream(findColumn(columnLabel), x, length);
    }

    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
        updateBigDecimal(findColumn(columnLabel), x);
    }

    public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
        updateBinaryStream(findColumn(columnLabel), x);
    }

    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
        updateBinaryStream(findColumn(columnLabel), x, length);
    }

    public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
        updateBinaryStream(findColumn(columnLabel), x, length);
    }

    public void updateBlob(int columnIndex, Blob x) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void updateBlob(String columnLabel, Blob x) throws SQLException {
        updateBlob(findColumn(columnLabel), x);
    }

    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
        updateBlob(findColumn(columnLabel), inputStream);
    }

    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
        updateBlob(findColumn(columnLabel), inputStream, length);
    }

    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void updateBoolean(String columnLabel, boolean x) throws SQLException {
        updateBoolean(findColumn(columnLabel), x);
    }

    public void updateByte(int columnIndex, byte x) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void updateByte(String columnLabel, byte x) throws SQLException {
        updateByte(findColumn(columnLabel), x);
    }

    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void updateBytes(String columnLabel, byte[] x) throws SQLException {
        updateBytes(findColumn(columnLabel), x);
    }

    public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
        updateCharacterStream(findColumn(columnLabel), reader);
    }

    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
        updateCharacterStream(findColumn(columnLabel), reader, length);
    }

    public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        updateCharacterStream(findColumn(columnLabel), reader, length);
    }

    public void updateClob(int columnIndex, Clob x) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void updateClob(String columnLabel, Clob x) throws SQLException {
        updateClob(findColumn(columnLabel), x);
    }

    public void updateClob(int columnIndex, Reader reader) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void updateClob(String columnLabel, Reader reader) throws SQLException {
        updateClob(findColumn(columnLabel), reader);
    }

    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
        updateClob(findColumn(columnLabel), reader, length);
    }

    public void updateDate(int columnIndex, Date x) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void updateDate(String columnLabel, Date x) throws SQLException {
        updateDate(findColumn(columnLabel), x);
    }

    public void updateDouble(int columnIndex, double x) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void updateDouble(String columnLabel, double x) throws SQLException {
        updateDouble(findColumn(columnLabel), x);
    }

    public void updateFloat(int columnIndex, float x) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void updateFloat(String columnLabel, float x) throws SQLException {
        updateFloat(findColumn(columnLabel), x);
    }

    public void updateInt(int columnIndex, int x) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void updateInt(String columnLabel, int x) throws SQLException {
        updateInt(findColumn(columnLabel), x);
    }

    public void updateLong(int columnIndex, long x) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void updateLong(String columnLabel, long x) throws SQLException {
        updateLong(findColumn(columnLabel), x);
    }

    public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
        updateNCharacterStream(findColumn(columnLabel), reader);
    }

    public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        updateNCharacterStream(findColumn(columnLabel), reader, length);
    }

    public void updateNClob(int columnIndex, Reader reader) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void updateNClob(String columnLabel, Reader reader) throws SQLException {
        updateNClob(findColumn(columnLabel), reader);
    }

    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
        updateNClob(findColumn(columnLabel), reader, length);
    }

    public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
        throw new SQLException("Unimplemented");
    }
    
    public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
        updateNClob(findColumn(columnLabel), nClob);
    }
    
    public void updateNString(int columnIndex, String string) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void updateNString(String columnLabel, String string) throws SQLException {
        updateNString(findColumn(columnLabel), string);
    }

    public void updateNull(int columnIndex) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void updateNull(String columnLabel) throws SQLException {
        updateNull(findColumn(columnLabel));
    }

    public void updateObject(int columnIndex, Object x) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void updateObject(String columnLabel, Object x) throws SQLException {
        updateObject(findColumn(columnLabel), x);
    }

    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
        updateObject(findColumn(columnLabel), x, scaleOrLength);
    }

    public void updateRef(int columnIndex, Ref x) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void updateRef(String columnLabel, Ref x) throws SQLException {
        updateRef(findColumn(columnLabel), x);
    }

    public void updateRow() throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void updateRowId(int columnIndex, RowId x) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void updateRowId(String columnLabel, RowId x) throws SQLException {
        updateRowId(findColumn(columnLabel), x);
    }

    public void updateShort(int columnIndex, short x) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void updateShort(String columnLabel, short x) throws SQLException {
        updateShort(findColumn(columnLabel), x);
    }

    public void updateString(int columnIndex, String x) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void updateString(String columnLabel, String x) throws SQLException {
        updateString(findColumn(columnLabel), x);
    }

    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
        throw new SQLException("Unimplemented");
    }
    
    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
        updateSQLXML(findColumn(columnLabel), xmlObject);
    }
    
    public void updateTime(int columnIndex, Time x) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void updateTime(String columnLabel, Time x) throws SQLException {
        updateTime(findColumn(columnLabel), x);
    }

    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
        updateTimestamp(findColumn(columnLabel), x);
    }

    public abstract boolean wasNull() throws SQLException;

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new SQLException("Unimplemented");
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException("Unimplemented");
    }

}
