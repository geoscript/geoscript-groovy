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

public class Entry extends Envelope {
    
    private long id = -1;
    private int level = 0;
    private double d1;
    private double d2;
    
    public Entry() {}
    
    public Entry(long id) {
        super();
        this.id = id;
    }
    
    public Entry(final double minX, final double maxX, final double minY, final double maxY, final long id) {
        super(minX, maxX, minY, maxY);
        this.id = id;
    }
    
    public Entry(final double minX, final double maxX, final double minY, final double maxY, final long id, final int level) {
        super(minX, maxX, minY, maxY);
        this.id = id;
        this.level = level;
    }

    public double getD1() {
        return d1;
    }

    public void setD1(final double d1) {
        this.d1 = d1;
    }

    public double getD2() {
        return d2;
    }

    public void setD2(final double d2) {
        this.d2 = d2;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }
    
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isIdAllocated() {
        return id >= 0;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof Entry) {
            Entry that = (Entry)obj;
            if ((this.id == that.id) &&
                (this.minX == that.minX) &&
                (this.maxX == that.maxX) &&
                (this.minY == that.minY) &&
                (this.maxY == that.maxY)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (int)id;
    }

    @Override
    public String toString() {
        return Long.toString(id) + "*" + minX + ":" + maxX + ":" + minY + ":" + maxY;
    }
        
}
