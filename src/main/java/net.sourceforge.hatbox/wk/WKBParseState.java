/*
 *    HatBox : A user-space spatial add-on for the Java databases
 *    
 *    Copyright (C) 2011 Peter Yuill
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
package net.sourceforge.hatbox.wk;

public class WKBParseState {
	
	private byte[] bytes = new byte[8];
    private int endian;
    private boolean hasZ;
    private WKBEnvelope envelope;
    private StringBuilder stringBuilder;
    private int coordCount;
    private int typeCount;
    private int ringCount;
    private WKBParseState parent;
    private WKBGeometryType type;
    
	public int getEndian() {
		return endian;
	}
	
	public void setEndian(int endian) {
		this.endian = endian;
	}
	
	public boolean hasZ() {
		return hasZ;
	}
	
	public void setHasZ(boolean z) {
		this.hasZ = z;
	}
	
	public WKBEnvelope getEnvelope() {
		return envelope;
	}

	public void setEnvelope(WKBEnvelope envelope) {
		this.envelope = envelope;
	}

	public StringBuilder getStringBuilder() {
		return stringBuilder;
	}

	public void setStringBuilder(StringBuilder stringBuilder) {
		this.stringBuilder = stringBuilder;
	}

	public WKBParseState getParent() {
		return parent;
	}

	public void setParent(WKBParseState parent) {
		this.parent = parent;
		this.envelope = parent.envelope;
		this.stringBuilder = parent.stringBuilder;
	}

	public WKBGeometryType getType() {
		return type;
	}

	public void setType(WKBGeometryType type) {
		this.type = type;
	}

	public int getTypeCount() {
		return typeCount;
	}

	public void setTypeCount(int typeCount) {
		this.typeCount = typeCount;
	}
	
	public byte[] getBytes() {
		return bytes;
	}
	
    public final void addCoord(final double x, final double y) {
    	if (envelope != null) {
        	envelope.expandToFit(x, y);
    	}
    	
    	if (stringBuilder != null) {
    		if (coordCount > 0) {
        		stringBuilder.append(", ");
    		}
    		stringBuilder.append(x);
    		stringBuilder.append(' ');
    		stringBuilder.append(y);
    	}
    	coordCount++;    	
    }
    
    public final boolean isBuildingString() {
    	return (stringBuilder != null);
    }
    
    public final void openType(String name) {
    	coordCount = 0;
    	ringCount = 0;
    	int parentTypeCount = 0;
    	if (parent != null) {
    		parentTypeCount = parent.getTypeCount();
    	}
    	if (parentTypeCount > 0) {
    		stringBuilder.append(", ");
    	}
    	if (name != null) {
    		stringBuilder.append(name);
    		stringBuilder.append(' ');
    	}
    	stringBuilder.append('(');
    }
    
    public final void closeType() {
    	stringBuilder.append(')');
    }
    
    public final void openRing() {
    	coordCount = 0;
    	if (ringCount > 0) {
    		stringBuilder.append(", ");
    	}
    	stringBuilder.append('(');
    	ringCount++;
    }
    
    public final void closeRing() {
    	stringBuilder.append(')');
    }

}
