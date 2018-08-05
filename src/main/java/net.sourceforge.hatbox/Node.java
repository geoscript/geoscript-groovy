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
 * An rtree node composed of entries with an envelope and an id.
 * A node is either an index node (the entries point to other
 * rtree nodes) or a leaf node (the entries point to rows in a
 * spatial table).
 * <p>Nodes are stored in binary database columns.
 * This implementation is focused on keeping the object count low.
 *
 * @author Peter Yuill
 */
public class Node extends AbstractNode {
    private int MIN_X_OFFSET;
    private int MAX_X_OFFSET;
    private int MIN_Y_OFFSET;
    private int MAX_Y_OFFSET;
    
    public Node(long id, byte[] data) {
        super(id, data);
        int entriesMax = getEntriesMax();
        MIN_X_OFFSET = ENTRY_LIST_OFFSET + (entriesMax * ENTRY_KEY_SIZE);
        MAX_X_OFFSET = MIN_X_OFFSET + (entriesMax * ENTRY_ORDINATE_SIZE);
        MIN_Y_OFFSET = MAX_X_OFFSET + (entriesMax * ENTRY_ORDINATE_SIZE);
        MAX_Y_OFFSET = MIN_Y_OFFSET + (entriesMax * ENTRY_ORDINATE_SIZE);
    }
       
    public Node(int level, long parentId, int entriesMax) {
        MIN_X_OFFSET = ENTRY_LIST_OFFSET + (entriesMax * ENTRY_KEY_SIZE);
        MAX_X_OFFSET = MIN_X_OFFSET + (entriesMax * ENTRY_ORDINATE_SIZE);
        MIN_Y_OFFSET = MAX_X_OFFSET + (entriesMax * ENTRY_ORDINATE_SIZE);
        MAX_Y_OFFSET = MIN_Y_OFFSET + (entriesMax * ENTRY_ORDINATE_SIZE);
        data = new byte[getIndexNodeSize(entriesMax)];
        setParentId(parentId);
        setLevel(level);
        setEntriesCount(0);
        setEntriesMax(entriesMax);
        putDouble(Double.POSITIVE_INFINITY, BOUNDS_MIN_X_OFFSET);
        putDouble(Double.NEGATIVE_INFINITY, BOUNDS_MAX_X_OFFSET);
        putDouble(Double.POSITIVE_INFINITY, BOUNDS_MIN_Y_OFFSET);
        putDouble(Double.NEGATIVE_INFINITY, BOUNDS_MAX_Y_OFFSET);
    }
    
    public Node split() {
        return new Node(getLevel(), getParentId(), getEntriesMax());
    }

    public long getParentId() {
        return getLong(PARENT_OFFSET);
    }

    public void setParentId(long parentId) {
        putLong(parentId, PARENT_OFFSET);
        dirty = true;
    }

    public boolean isIdAllocated() {
        return (id >= 0);
    }

    public Envelope getBounds() {
        return new Envelope(getDouble(BOUNDS_MIN_X_OFFSET), getDouble(BOUNDS_MAX_X_OFFSET), getDouble(BOUNDS_MIN_Y_OFFSET), getDouble(BOUNDS_MAX_Y_OFFSET));
    }

    public Entry getMyEntry() {
        return new Entry(getDouble(BOUNDS_MIN_X_OFFSET), getDouble(BOUNDS_MAX_X_OFFSET), getDouble(BOUNDS_MIN_Y_OFFSET), getDouble(BOUNDS_MAX_Y_OFFSET), id, getShort(LEVEL_OFFSET) + 1);
    }
    
    public void resetEntries() {
        setEntriesCount(0);
        putDouble(Double.POSITIVE_INFINITY, BOUNDS_MIN_X_OFFSET);
        putDouble(Double.NEGATIVE_INFINITY, BOUNDS_MAX_X_OFFSET);
        putDouble(Double.POSITIVE_INFINITY, BOUNDS_MIN_Y_OFFSET);
        putDouble(Double.NEGATIVE_INFINITY, BOUNDS_MAX_Y_OFFSET);
        dirty = true;
    }

    public void addEntry(Entry entry) {
        int entriesCount = getEntriesCount();
        putEntryId(entry.getId(), entriesCount);
        putEntryMinX(entry.getMinX(), entriesCount);
        putEntryMaxX(entry.getMaxX(), entriesCount);
        putEntryMinY(entry.getMinY(), entriesCount);
        putEntryMaxY(entry.getMaxY(), entriesCount);
        setEntriesCount(entriesCount + 1);
        Envelope bounds = getBounds();
        bounds.expandToFit(entry);
        putDouble(bounds.getMinX(), BOUNDS_MIN_X_OFFSET);
        putDouble(bounds.getMaxX(), BOUNDS_MAX_X_OFFSET);
        putDouble(bounds.getMinY(), BOUNDS_MIN_Y_OFFSET);
        putDouble(bounds.getMaxY(), BOUNDS_MAX_Y_OFFSET);
        dirty = true;
    }

    /**
     * Change the bounds of this node to that of the supplied Entry
     * 
     * @param entry The new bounds
     */
    public void changeEntryEnvelope(Entry entry) {
        int idIndex = getEntryIndex(entry.getId());
        putEntryMinX(entry.getMinX(), idIndex);
        putEntryMaxX(entry.getMaxX(), idIndex);
        putEntryMinY(entry.getMinY(), idIndex);
        putEntryMaxY(entry.getMaxY(), idIndex);
        recalcBounds();
        dirty = true;
    }

    public void removeEntry(long entryId) {
    	int idIndex = getEntryIndex(entryId);
		int last = getEntriesMax() - 1;
		if (idIndex < last) {
            System.arraycopy(
                    data,
                    ENTRY_LIST_OFFSET + ((idIndex + 1) * ENTRY_KEY_SIZE),
                    data,
                    ENTRY_LIST_OFFSET + (idIndex * ENTRY_KEY_SIZE),
                    (last - idIndex) * ENTRY_KEY_SIZE);
            System.arraycopy(
                    data,
                    MIN_X_OFFSET + ((idIndex + 1) * ENTRY_ORDINATE_SIZE),
                    data,
                    MIN_X_OFFSET + (idIndex * ENTRY_ORDINATE_SIZE),
                    (last - idIndex) * ENTRY_ORDINATE_SIZE);
            System.arraycopy(
                    data,
                    MAX_X_OFFSET + ((idIndex + 1) * ENTRY_ORDINATE_SIZE),
                    data,
                    MAX_X_OFFSET + (idIndex * ENTRY_ORDINATE_SIZE),
                    (last - idIndex) * ENTRY_ORDINATE_SIZE);
            System.arraycopy(
                    data,
                    MIN_Y_OFFSET + ((idIndex + 1) * ENTRY_ORDINATE_SIZE),
                    data,
                    MIN_Y_OFFSET + (idIndex * ENTRY_ORDINATE_SIZE),
                    (last - idIndex) * ENTRY_ORDINATE_SIZE);
            System.arraycopy(
                    data,
                    MAX_Y_OFFSET + ((idIndex + 1) * ENTRY_ORDINATE_SIZE),
                    data,
                    MAX_Y_OFFSET + (idIndex * ENTRY_ORDINATE_SIZE),
                    (last - idIndex) * ENTRY_ORDINATE_SIZE);
		}
		setEntriesCount(getEntriesCount() - 1);
		recalcBounds();
		dirty = true;
    }
    
    private int getEntryIndex(long id) {
        int entriesCount = getEntriesCount();
        for (int i = 0; i < entriesCount; i++) {
            if (getEntryId(i) == id) {
                return i;
            }
        }
        throw new RTreeInternalException("Node " + this.id + " does not contain entry id " + id);
    }
    
    private void recalcBounds() {
        Envelope bounds = new Envelope();
        int entriesCount = getEntriesCount();
        for (int i = 0; i < entriesCount; i++) {
            double minx = getEntryMinX(i);
            double maxx = getEntryMaxX(i);
            double miny = getEntryMinY(i);
            double maxy = getEntryMaxY(i);
            bounds.expandToFit(minx, maxx, miny, maxy);
        }
        putDouble(bounds.getMinX(), BOUNDS_MIN_X_OFFSET);
        putDouble(bounds.getMaxX(), BOUNDS_MAX_X_OFFSET);
        putDouble(bounds.getMinY(), BOUNDS_MIN_Y_OFFSET);
        putDouble(bounds.getMaxY(), BOUNDS_MAX_Y_OFFSET);
    }

    public boolean isLeaf() {
        return (getLevel() == 0);
    }
    
    public int getLevel() {
        return getShort(LEVEL_OFFSET);
    }

    public void setLevel(int level) {
        putShort(level, LEVEL_OFFSET);
    }

    public int getEntriesCount() {
        return getShort(COUNT_OFFSET);
    }

    private void setEntriesCount(int count) {
        putShort(count, COUNT_OFFSET);
    }
    
    public int getEntriesMax() {
        return getShort(MAX_OFFSET);
    }

    private void setEntriesMax(int entriesMax) {
        putShort(entriesMax, MAX_OFFSET);
    }
    
    public double populateEnvelope(Envelope envelope, int entryIndex) {
        return envelope.populate(
                getEntryMinX(entryIndex), getEntryMaxX(entryIndex),
                getEntryMinY(entryIndex), getEntryMaxY(entryIndex));
    }
    
    public final boolean intersects(final Envelope search, final int entryIndex) {
        return !(
            search.getMaxX() < getEntryMinX(entryIndex) ||
            search.getMinX() > getEntryMaxX(entryIndex) ||
            search.getMaxY() < getEntryMinY(entryIndex) ||
            search.getMinY() > getEntryMaxY(entryIndex)
            );
    }
    
    public final long getEntryId(int i) {
        return getLong(ENTRY_LIST_OFFSET + (i * ENTRY_KEY_SIZE));
    }
    
    public final double getEntryMinX(int i) {
        return getDouble(MIN_X_OFFSET + (i * ENTRY_ORDINATE_SIZE));
    }
    
    public final double getEntryMaxX(int i) {
        return getDouble(MAX_X_OFFSET + (i * ENTRY_ORDINATE_SIZE));
    }
    
    public final double getEntryMinY(int i) {
        return getDouble(MIN_Y_OFFSET + (i * ENTRY_ORDINATE_SIZE));
    }
    
    public final double getEntryMaxY(int i) {
        return getDouble(MAX_Y_OFFSET + (i * ENTRY_ORDINATE_SIZE));
    }
    
    public final void putEntryId(long id, int i) {
        putLong(id, ENTRY_LIST_OFFSET + (i * ENTRY_KEY_SIZE));
    }
    
    public final void putEntryMinX(double minX, int i) {
        putDouble(minX, MIN_X_OFFSET + (i * ENTRY_ORDINATE_SIZE));
    }
    
    public final void putEntryMaxX(double maxX, int i) {
        putDouble(maxX, MAX_X_OFFSET + (i * ENTRY_ORDINATE_SIZE));
    }
    
    public final void putEntryMinY(double minY, int i) {
        putDouble(minY, MIN_Y_OFFSET + (i * ENTRY_ORDINATE_SIZE));
    }
    
    public final void putEntryMaxY(double maxY, int i) {
        putDouble(maxY, MAX_Y_OFFSET + (i * ENTRY_ORDINATE_SIZE));
    }
    
    public Entry getEntry(int i) {
        return new Entry(getEntryMinX(i), getEntryMaxX(i), getEntryMinY(i), getEntryMaxY(i), getEntryId(i), getLevel());
    }
	
	public int getMinNodeEntries(double minNodeSplit) {
	    return (int)(getEntriesMax() * minNodeSplit);
	}
    
    public boolean isDirty() {
        return dirty;
    }
    
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
    
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("Id:");
        buf.append(id);
        buf.append(" Parent:");
        buf.append(getParentId());
        buf.append((getLevel() == 0) ? " Leaf " : " Index-" + getLevel() + " ");
        int entriesCount = getEntriesCount();
        buf.append("Entries:");
        buf.append(entriesCount);
        buf.append(" Max:");
        buf.append(getEntriesMax());
        buf.append(" {");
        for (int i = 0; i < entriesCount; i++) {
            if (i != 0) {
                buf.append(", ");
            }
            Entry entry = getEntry(i);
            buf.append(entry.getId());
            buf.append("*");
            buf.append(entry.getMinX());
            buf.append(":");
            buf.append(entry.getMaxX());
            buf.append(":");
            buf.append(entry.getMinY());
            buf.append(":");
            buf.append(entry.getMaxY());
        }
        buf.append("}");
        return buf.toString();
    }
}
