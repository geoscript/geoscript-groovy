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

/**
 * A pseudo node that contains index meta-data
 * 
 * @author Peter Yuill
 */
public class MetaNode extends AbstractNode {
    private static final int CURRENT_VERSION = 1;
    private static final Algorithm CURRENT_ALGORITHM = Algorithm.RTREE_GUTTMAN_QUADRATIC;
    private static final int META_NODE_SIZE = 500;
    private static final int DEFAULT_ENTRIES_MAX = 49;
    private static final boolean DEFAULT_EXPOSE_PK = false;
    private static final String[] VALID_GEOMETRY =
    {"GEOMETRY","POINT","MULTIPOINT","LINESTRING","MULTILINESTRING",
     "POLYGON","MULTIPOLYGON","GEOMETRYCOLLECTION"};
    public static final int VERSION_OFFSET = 0;
    public static final int ALGORITHM_OFFSET = 1;
    public static final int EXPOSE_PK_OFFSET = 2;
    public static final int GEOM_TYPE_OFFSET = 3;
    public static final int ROOT_ID_OFFSET = 4;
    public static final int INDEX_STATUS_OFFSET = 12;
    public static final int ENTRIES_MAX_OFFSET = 16;
    public static final int SRID_OFFSET = 20;
    public static final int PK_COL_INDEX_OFFSET = 24;
    public static final int GEOM_COL_INDEX_OFFSET = 28;
    public static final int STRINGS_OFFSET = 40;
    
    private String tableName;
    private String pkColName;
    private String geomColName;
    
    public MetaNode(byte[] data) {
        super(RTreeDml.META_NODE_ID, data);
    }
    public MetaNode(
            long rootId, String table, String pkColumn, int pkColumnIndex,
            String geomColumn, int geomColumnIndex, String geomType,
            String srid, String exposePK, String entriesMax) {
    	id = RTreeDml.META_NODE_ID;
    	data = new byte[META_NODE_SIZE];
    	putByte(CURRENT_VERSION, VERSION_OFFSET);
    	putByte(CURRENT_ALGORITHM.ordinal(), ALGORITHM_OFFSET);
    	setIndexStatus(IndexStatus.NO_INDEX);
        setRootId(rootId);
        if ((pkColumn == null) || (geomColumn == null)) {
            throw new InvalidTableException("PK column name and Geom column name must be supplied");
        }
        setNames(table, pkColumn, geomColumn);
        setPkColIndex(pkColumnIndex);
        setGeomColIndex(geomColumnIndex);
        try {
            setSrid(Integer.parseInt(srid));
        } catch (NumberFormatException nfe) {}
        if (exposePK == null) {
        	setExposePk(DEFAULT_EXPOSE_PK);
        } else {
            setExposePk(Boolean.parseBoolean(exposePK));
        }
        if (entriesMax == null) {
        	setEntriesMax(DEFAULT_ENTRIES_MAX);
        } else {
        	setEntriesMax(Integer.parseInt(entriesMax));
        }
        if (geomType == null) {
            geomType = VALID_GEOMETRY[0];
        }
        this.setGeomType(geomType);
    }
    
    public long getRootId() {
    	return getLong(ROOT_ID_OFFSET);
    }
    
    public void setRootId(long id) {
    	putLong(id, ROOT_ID_OFFSET);
    }
    
    public IndexStatus getIndexStatus() {
    	return IndexStatus.values()[getInt(INDEX_STATUS_OFFSET)];
    }
    
    public void setIndexStatus(IndexStatus status) {
    	putInt(status.ordinal(), INDEX_STATUS_OFFSET);
    }
    
    public int getEntriesMax() {
    	return getInt(ENTRIES_MAX_OFFSET);
    }
    
    public void setEntriesMax(int max) {
        if ((max < 2) || (max > 1000)) {
            throw new InvalidTableException("Maximum index entries per node: " + max + " is not valid");
        }
    	putInt(max, ENTRIES_MAX_OFFSET);
    }
    
    public int getSrid() {
    	return getInt(SRID_OFFSET);
    }
    
    public void setSrid(int srid) {
    	if (srid < -1) {
            throw new InvalidTableException("srid: " + srid + " is not valid");
    	}
    	putInt(srid, SRID_OFFSET);
    }
    
    public int getPkColIndex() {
    	return getInt(PK_COL_INDEX_OFFSET);
    }
    
    public void setPkColIndex(int index) {
    	putInt(index, PK_COL_INDEX_OFFSET);
    }
    
    public int getGeomColIndex() {
    	return getInt(GEOM_COL_INDEX_OFFSET);
    }
    
    public void setGeomColIndex(int index) {
    	putInt(index, GEOM_COL_INDEX_OFFSET);
    }
    
    public boolean getExposePk() {
    	return getBoolean(EXPOSE_PK_OFFSET);
    }
    
    public void setExposePk(boolean exposePk) {
    	putBoolean(exposePk, EXPOSE_PK_OFFSET);
    }
    
    public String getGeomType() {
    	return VALID_GEOMETRY[getByte(GEOM_TYPE_OFFSET)];
    }
    
    public void setGeomType(String geomType) {
    	putByte(validateGeomType(geomType), GEOM_TYPE_OFFSET);
    }
    
    public int getVersion() {
    	return getByte(VERSION_OFFSET);
    }
    
    public Algorithm getAlgorithm() {
    	return Algorithm.values()[getByte(ALGORITHM_OFFSET)];
    }
    
    public String getTableName() {
    	if (tableName == null) {
    		populateStrings();
    	}
    	return tableName;
    }
    
    public String getPkColName() {
    	if (pkColName == null) {
    		populateStrings();
    	}
    	return pkColName;
    }
    
    public String getGeomColName() {
    	if (geomColName == null) {
    		populateStrings();
    	}
    	return geomColName;
    }
    
    public void setNames(String table, String pkCol, String geomCol) {
    	this.tableName = table;
    	this.pkColName = pkCol;
    	this.geomColName = geomCol;
    	saveStrings();
    }
    
    private void saveStrings() {
    	String[] strings = new String[3];
    	strings[0] = tableName;
    	strings[1] = pkColName;
    	strings[2] = geomColName;
    	putShort(strings.length, STRINGS_OFFSET);
    	int offset = STRINGS_OFFSET + 2;
    	for (String s : strings) {
    		int len = 0;
    		if (s == null) {
    			len = -1;
    		} else {
    			len = s.length();
    		}
    		putShort(len, offset);
    		offset += 2;
    		for (int i = 0; i < len; i++) {
    			putChar(s.charAt(i), offset);
    			offset += 2;
    		}
    	}
    }
    
    private void populateStrings() {
    	int count = getShort(STRINGS_OFFSET);
    	String[] strings = new String[count];
    	StringBuilder builder = null;
    	int offset = STRINGS_OFFSET + 2;
    	for (int s = 0; s < count; s++) {
    		builder = new StringBuilder();
    		int len = getShort(offset);
    		offset += 2;
    		for (int i = 0; i < len; i++) {
    			builder.append(getChar(offset));
    			offset += 2;
    		}
    		if (len >= 0) {
        		strings[s] = builder.toString();
    		}
    	}
    	tableName = strings[0];
    	pkColName = strings[1];
    	geomColName = strings[2];
    }

    private int validateGeomType(String toValidate) {
        for (int i = 0; i < VALID_GEOMETRY.length; i++) {
            if (VALID_GEOMETRY[i].equalsIgnoreCase(toValidate)) {
                return i;
            }
        }
        throw new InvalidTableException("Geometry Type: " + toValidate + " is not valid");
    }

}
