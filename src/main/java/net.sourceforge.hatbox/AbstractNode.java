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
 * The core node structure
 *
 * @author Peter Yuill
 */
public class AbstractNode {
	
    public static final int PARENT_OFFSET = 0;
    public static final int LEVEL_OFFSET = 8;
    public static final int COUNT_OFFSET = 10;
    public static final int MAX_OFFSET = 12;
    public static final int BOUNDS_MIN_X_OFFSET = 16;
    public static final int BOUNDS_MAX_X_OFFSET = 24;
    public static final int BOUNDS_MIN_Y_OFFSET = 32;
    public static final int BOUNDS_MAX_Y_OFFSET = 40;
    public static final int ENTRY_LIST_OFFSET = 48;
    public static final int ENTRY_ORDINATE_SIZE = 8;
    public static final int ENTRY_KEY_SIZE = 8;
    
    protected long id = -1;
    
    protected byte[] data;
    
    protected boolean dirty = false;

    public AbstractNode() {
    }
    
    public AbstractNode(long id, byte[] data) {
        this.id = id;
        this.data = data;
    }
    
    public int getIndexNodeSize(int entriesMax) {
    	return ENTRY_LIST_OFFSET + (entriesMax * ENTRY_KEY_SIZE) + (entriesMax * 4 * ENTRY_ORDINATE_SIZE);
    }
    
    public final int getByte(final int offset) {
        return data[offset];
    }
    
    public final void putByte(final int val, final int offset) {
        data[offset] = (byte)val;
        dirty = true;
    }
    
    public final boolean getBoolean(final int offset) {
        return (data[offset] == 1);
    }
    
    public final void putBoolean(final boolean val, final int offset) {
        data[offset] = (byte)(val ? 1 : 0);
        dirty = true;
    }
    
    public final char getChar(final int offset) {
        return (char)(((data[offset] & 0xff) <<  8) | (data[offset + 1] & 0xff));
    }
    
    public final void putChar(final char val, final int offset) {
        data[offset] = (byte)(val >> 8);
        data[offset +1] = (byte)val;
        dirty = true;
    }
    
    public final int getShort(final int offset) {
        return (((data[offset] & 0xff) <<  8) | (data[offset + 1] & 0xff));
    }
    
    public final void putShort(final int val, final int offset) {
        data[offset] = (byte)(val >> 8);
        data[offset +1] = (byte)val;
        dirty = true;
    }
    
    public final int getInt(final int offset) {
        return (((data[offset] & 0xff) << 24) |
                ((data[offset + 1] & 0xff) << 16) |
                ((data[offset + 2] & 0xff) <<  8) |
                ((data[offset + 3] & 0xff)));
    }
    
    public final void putInt(final int val, final int offset) {
        data[offset] = (byte)(val >> 24);
        data[offset + 1] = (byte)(val >> 16);
        data[offset + 2] = (byte)(val >> 8);
        data[offset + 3] = (byte)val;
        dirty = true;
    }
    
    public final long getLong(final int offset) {
        return ((((long)data[offset] & 0xff) << 56) |
                (((long)data[offset + 1] & 0xff) << 48) |
                (((long)data[offset + 2] & 0xff) << 40) |
                (((long)data[offset + 3] & 0xff) << 32) |
                (((long)data[offset + 4] & 0xff) << 24) |
                (((long)data[offset + 5] & 0xff) << 16) |
                (((long)data[offset + 6] & 0xff) <<  8) |
                (((long)data[offset + 7] & 0xff)));
    }
    
    public final void putLong(final long val, final int offset) {
        data[offset] = (byte)(val >> 56);
        data[offset + 1] = (byte)(val >> 48);
        data[offset + 2] = (byte)(val >> 40);
        data[offset + 3] = (byte)(val >> 32);
        data[offset + 4] = (byte)(val >> 24);
        data[offset + 5] = (byte)(val >> 16);
        data[offset + 6] = (byte)(val >> 8);
        data[offset + 7] = (byte)val;
        dirty = true;
    }
    
    public final double getDouble(final int offset) {
        return Double.longBitsToDouble(getLong(offset));
    }
    
    public final void putDouble(final double val, final int offset) {
        putLong(Double.doubleToLongBits(val), offset);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
    public byte[] getData() {
        return data;
    }

    public boolean isDirty() {
        return dirty;
    }
    
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AbstractNode) {
            AbstractNode that = (AbstractNode)obj;
            return this.id == that.id;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (int)id;
    }
}
