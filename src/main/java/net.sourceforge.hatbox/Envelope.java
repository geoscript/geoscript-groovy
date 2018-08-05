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

public class Envelope {
    
    protected double minX = Double.POSITIVE_INFINITY;
    protected double maxX = Double.NEGATIVE_INFINITY;
    protected double minY = Double.POSITIVE_INFINITY;
    protected double maxY = Double.NEGATIVE_INFINITY;
    protected double area = 0.0;
    
    public Envelope() {}
    
    public Envelope(final double minX, final double maxX, final double minY, final double maxY) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        area = (maxX - minX) * (maxY - minY);
    }
    
    public final void reset() {
        this.minX = Double.POSITIVE_INFINITY;
        this.maxX = Double.NEGATIVE_INFINITY;
        this.minY = Double.POSITIVE_INFINITY;
        this.maxY = Double.NEGATIVE_INFINITY;
        this.area = 0.0;
    }
    
    public final double populate(final double minX, final double maxX, final double minY, final double maxY) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        return area = (maxX - minX) * (maxY - minY);
    }
    
    public final double populate(final Envelope e) {
        return populate(e.minX, e.maxX, e.minY, e.maxY);
    }
    
    public final double expandToFit(final double minX, final double maxX, final double minY, final double maxY) {
        if (minX < this.minX) {
            this.minX = minX;
        }
        if (maxX > this.maxX) {
            this.maxX = maxX;
        }
        if (minY < this.minY) {
            this.minY = minY;
        }
        if (maxY > this.maxY) {
            this.maxY = maxY;
        }
        return area = (this.maxX - this.minX) * (this.maxY - this.minY);
    }
    
    public final double expandToFit(final Envelope e) {
        return expandToFit(e.minX, e.maxX, e.minY, e.maxY);
    }

    public final double getMinX() {
        return minX;
    }

    public final double getMaxX() {
        return maxX;
    }

    public final double getMinY() {
        return minY;
    }

    public final double getMaxY() {
        return maxY;
    }

    public final double getArea() {
        return area;
    }
    
    public boolean isNullGeometry() {
        return (minX == Double.POSITIVE_INFINITY) &&
               (maxX == Double.NEGATIVE_INFINITY) &&
               (minY == Double.POSITIVE_INFINITY) &&
               (maxY == Double.NEGATIVE_INFINITY);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Envelope) {
            Envelope that = (Envelope)obj;
            if ((this.minX == that.minX) &&
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
        return (int)minX;
    }
    
    @Override
    public String toString() {
        return minX + ":" + maxX + ":" + minY + ":" + maxY;
    }

}
