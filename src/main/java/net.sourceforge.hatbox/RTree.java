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
import java.util.ArrayList;
import java.util.LinkedList;
import java.sql.SQLException;

/**
 * An r-tree implementation based on Antonin Guttman 1984
 * "R-TREES A DYNAMIC INDEX STRUCTURE FOR SPATIAL SEARCHING"
 * 
 * @author Peter Yuill
 */
public class RTree {
    
    private static final double DEFAULT_MIN_NODE_SPLIT = 0.34;
    private double minNodeSplit = DEFAULT_MIN_NODE_SPLIT;
   
    private RTreeSession session;
    
    public RTree(RTreeSession session) {
        this.session = session;
    }
    
    /**
     * Search the index for entries that intersect the search envelope
     * <p>
     * (ref Guttman84 p48)
     * 
     * @param searchEnv The search envelope to use
     * @return A list of spatial table ids for rows where geometry intersects the search envelope
     * @throws SQLException
     */
    public List<Long> search(Envelope searchEnv) throws SQLException {
        List<Long> matchIds = new ArrayList<Long>();
        search(session.getRootId(), searchEnv, matchIds);
        return matchIds;
    }
    
    private void search(long id, Envelope searchEnv, List<Long> matchIds) throws SQLException {
        Node node = session.getNode(id);
        boolean leaf = node.isLeaf();
        for (int i = 0; i < node.getEntriesCount(); i++) {
            if (node.intersects(searchEnv, i)) {
                if (leaf) {
                    matchIds.add(node.getEntryId(i));
                } else {
                    search(node.getEntryId(i), searchEnv, matchIds);
                }
            }
        }
    }
    
    /**
     * Insert a new index entry
     * <p>
     * (ref Guttman84 p49)
     * 
     * @param entry The Entry to insert
     * @throws SQLException
     */
    public void insert(Entry entry) throws SQLException {
        Node node = chooseLeaf(session.getRootId(), entry);
        if (entry.getLevel() > 0) {
            // reinserting a non-leaf node
            Node entryNode = session.getNode(entry.getId());
            entryNode.setParentId(node.getId());
            session.updateNode(entryNode);
        }
        if (node.getEntriesCount() < node.getEntriesMax()) {
            node.addEntry(entry);
            session.updateNode(node);
            adjustTree(node, null, null);
        } else {
            List<Entry> split1 = new ArrayList<Entry>(node.getEntriesCount());
            List<Entry> split2 = new ArrayList<Entry>(node.getEntriesCount());
            splitNode(node, entry, split1, split2);
            adjustTree(node, split1, split2);
        }
    }
    
    /**
     * Choose the best node to insert an entry.
     * <p>
     * Kept the name 'chooseLeaf' for compatibility with Guttman's paper.
     * However this method serves both inserting new leaf entries and
     * reinserting non-leaf entries during 'condenseTree'. Consequently
     * the node returned will not be a leaf node for non-leaf entries.
     * <p>
     * (ref Guttman84 p50)
     * 
     * @param id
     * @param newEnv
     * @return The chosen Node
     * @throws SQLException
     */
    private Node chooseLeaf(long id, Entry entry) throws SQLException {
        Node node = session.getNode(id);
        if (node.getLevel() == entry.getLevel()) {
            // for new entries this is a leaf
            // for reinserted entries it may be higher
            return node;
        }
        int entryI = 0;
        Envelope env = new Envelope();
        double smallestArea = Double.POSITIVE_INFINITY;
        double smallestDelta = Double.POSITIVE_INFINITY;
        double area = 0.0;
        double delta = 0.0;
        for (int i = 0; i < node.getEntriesCount(); i++) {
            area = node.populateEnvelope(env, i);
            delta = env.expandToFit(entry) - area;
            if (delta < smallestDelta) {
                smallestArea = area;
                smallestDelta = delta;
                entryI = i;
            } else if (delta == smallestDelta) {
                if (area < smallestArea) {
                    smallestArea = area;
                    entryI = i;
                }
            }
        }
        return chooseLeaf(node.getEntryId(entryI), entry);
    }
    
    /**
     * Adjust the rtree upwards from a modified node. If the root node
     * is split, add another level.
     * <p>
     * (ref Guttman84 p50)
     * <p>
     * Reorganised the algorithm to focus this method on management of the
     * nodes.
     * 
     * @param n The node to be adjusted
     * @param split1 The list of entries to remain in the original node.
     * @param split2 The list of entries to go into the new node.
     * @throws SQLException
     */
    private void adjustTree(Node n, List<Entry> split1, List<Entry> split2) throws SQLException {
        // Are we at the root?
        if (n.getId() == session.getRootId()) {
            if (split1 != null) { // The root node has been split, create a new level
                Node root = n.split();
                root.setLevel(n.getLevel() + 1);
                session.setRootId(session.insertNode(root));
                
                n.resetEntries();
                n.setParentId(session.getRootId());
                for (int i = 0; i < split1.size(); i++) {
                    Entry e = split1.get(i);
                    n.addEntry(e);
                }
                session.updateNode(n);
                
                Node n2 = n.split();
                n2.setParentId(session.getRootId());
                long n2Id = session.insertNode(n2);
                for (int i = 0; i < split2.size(); i++) {
                    Entry e = split2.get(i);
                    n2.addEntry(e);
                    if (!n2.isLeaf()) {
                        Node child = session.getNode(e.getId());
                        child.setParentId(n2Id);
                        session.updateNode(child);
                    }
                }
                session.updateNode(n2);
                       
                root.addEntry(n.getMyEntry());
                root.addEntry(n2.getMyEntry());
                session.updateNode(root);
            }
            return;
        }
        // Update the parent
        Node p = session.getNode(n.getParentId());
        if (split1 == null) {
            p.changeEntryEnvelope(n.getMyEntry());
            session.updateNode(p);
            adjustTree(p, null, null);
        } else {
            n.resetEntries();
            for (int i = 0; i < split1.size(); i++) {
                Entry e = split1.get(i);
                n.addEntry(e);
            }
            session.updateNode(n);
            p.changeEntryEnvelope(n.getMyEntry());
            
            Node n2 = n.split();
            long n2Id = session.insertNode(n2);
            for (int i = 0; i < split2.size(); i++) {
                Entry e = split2.get(i);
                n2.addEntry(e);
                if (!n2.isLeaf()) {
                    Node child = session.getNode(e.getId());
                    child.setParentId(n2Id);
                    session.updateNode(child);
                }
            }
            session.updateNode(n2);
            
            if (p.getEntriesCount() < p.getEntriesMax()) {
                p.addEntry(n2.getMyEntry());  
                session.updateNode(p);
                adjustTree(p, null, null);
            } else {
                List<Entry> pe1 = new ArrayList<Entry>(n.getEntriesCount());
                List<Entry> pe2 = new ArrayList<Entry>(n.getEntriesCount());
                splitNode(p, n2.getMyEntry(), pe1, pe2);
                adjustTree(p, pe1, pe2);
            }
        }
    }
    
    /**
     * Split a node that is full and needs another entry added.
     * <p>
     * (ref Guttman84 p51)
     * <p>
     * Reorganised the algorithm to focus this method on the actual
     * splitting of the entries, not the construction of nodes.
     * 
     * @param n The node that has overflowed.
     * @param entry The entry that caused the split.
     * @param split1 The entries to go into the first split.
     * @param split2 The entries to go into the second split.
     * @return
     */
    private void splitNode(Node n, Entry entry, List<Entry> split1, List<Entry> split2) throws SQLException {
        int minEntries = (int)(n.getEntriesMax() * getMinNodeSplit());
        Envelope e1 = new Envelope();
        Envelope e2 = new Envelope();
        // Create an entry list with all node entries and the entry that caused the split
        List<Entry> entryList = new ArrayList<Entry>(n.getEntriesCount() + 1);
        for (int i = 0; i < n.getEntriesCount(); i++) {
            Entry e = new Entry();
            n.populateEnvelope(e, i);
            e.setId(n.getEntryId(i));
            entryList.add(e);
        }
        entryList.add(entry);
        
        // Pick the seed entries
        Entry[] seeds = pickSeeds(entryList);
        e1.populate(seeds[0]);
        split1.add(seeds[0]);
        entryList.remove(seeds[0]);
        e2.populate(seeds[1]);
        split2.add(seeds[1]);
        entryList.remove(seeds[1]);
        
        while (entryList.size() > 0) {
            if ((split1.size() + entryList.size()) == minEntries) {
                split1.addAll(entryList);
                entryList.clear();
            } else if ((split2.size() + entryList.size()) == minEntries) {
                split2.addAll(entryList);
                entryList.clear();
            } else {
                Entry next = pickNext(entryList, e1, e2);
                if (next.getD1() == next.getD2()) { // tie
                    if (e1.getArea() == e2.getArea()) { // another tie
                        if (split1.size() < split2.size()) { // then smallest; list
                            split1.add(next);
                            e1.expandToFit(next);
                        } else {
                            split2.add(next);
                            e2.expandToFit(next);
                        }
                    } else if (e1.getArea() < e2.getArea()) { // then smallest area
                        split1.add(next);
                        e1.expandToFit(next);
                    } else {
                        split2.add(next);                        
                        e2.expandToFit(next);
                    }
                } else if (next.getD1() < next.getD2()) { // then smallest delta
                    split1.add(next);
                    e1.expandToFit(next);
                } else {
                    split2.add(next);                                            
                    e2.expandToFit(next);
                }
            }
        }
    }
    
    /**
     * Choose the most wasteful pair of entries to have in the same node
     * and make them the seeds for each of the split nodes.
     * <p>
     * (ref Guttman84 p52)
     * <p>
     * There is some doubt in my mind about the detail of the algorithm
     * with respect to intersecting candidates. Guttman appears to
     * 'double count' the area of intersection, giving rise to artificially
     * low deltas.
     * 
     * @param entryList
     * @return An array of two Entries to use as seed for a split
     */
    protected Entry[] pickSeeds(List<Entry> entryList) {
        int size = entryList.size();
        double maxD = Double.NEGATIVE_INFINITY;
        double d;
        Entry j = new Entry();
        Entry maxE1 = null;
        Entry maxE2 = null;
        Entry e1 = null;
        Entry e2 = null;
        for (int x = 0; x < (size - 1); x++) {
            e1 = entryList.get(x);
            for (int y = (x + 1); y < size; y++) {
                e2 = entryList.get(y);
                j.reset();
                j.expandToFit(e1);
                j.expandToFit(e2);
                d = j.getArea() - e1.getArea() - e2.getArea();
                if (d > maxD) {
                    maxD = d;
                    maxE1 = e1;
                    maxE2 = e2;
                }
            }
        }
        return new Entry[] {maxE1, maxE2};
    }
    
    /**
     * Pick the entry that has the biggest difference in affinity for the two groups
     * <p>
     * (ref Guttman84 p52)
     * 
     * @param entryList The remaining entries.
     * @param e1 Envelope of the first group.
     * @param e2 Envelope of the second group.
     * @return The selected entry.
     */
    protected Entry pickNext(List<Entry> entryList, Envelope e1, Envelope e2) {
        int size = entryList.size();
        Entry bestEntry = null;
        int bestEntryIndex = -1;
        Entry e = null;
        double maxDD = Double.NEGATIVE_INFINITY;
        double dd;
        Envelope temp = new Envelope();
        for (int i = 0; i < size; i++) {
            e = entryList.get(i);
            temp.populate(e1);
            temp.expandToFit(e);
            e.setD1(temp.getArea() - e1.getArea()); // affinity for e1
            temp.populate(e2);
            temp.expandToFit(e);
            e.setD2(temp.getArea() - e2.getArea()); // affinity for e2
            dd = Math.abs(e.getD1() - e.getD2());
            if (dd > maxDD) {
                maxDD = dd;
                bestEntry = e;
                bestEntryIndex = i;
            }
            
        }
        entryList.remove(bestEntryIndex);
        return bestEntry;
    }

    public double getMinNodeSplit() {
        return minNodeSplit;
    }

    public void setMinNodeSplit(double minNodeSplit) {
//        if ((minNodeSplit < 0.0) || (minNodeSplit > 0.5)) {
//            throw new IllegalArgumentException("MinNodeSplit must be between 0.0 and 0.5");
//        } else {
            this.minNodeSplit = minNodeSplit;
//        }
    }
    
    /**
     * Delete an entry from the index.
     * <p>
     * (ref Guttman84 p50)
     * 
     * @param entry The Entry to delete
     * @throws SQLException
     */
    public void delete(Entry entry) throws SQLException {
        Node leaf = findLeaf(session.getRootId(), entry);
        if (leaf == null) {
            return;
        }
        leaf.removeEntry(entry.getId());
        LinkedList<Entry> orphans = new LinkedList<Entry>();
        condenseTree(leaf, orphans);
        int orphanCount = orphans.size();
        for (int i = 0; i < orphanCount; i++) {
            // reinsert orphans in reverse order, highest level first
            insert(orphans.removeLast());
        }
        
        Node root = session.getNode(session.getRootId());
        if (!root.isLeaf() && (root.getEntriesCount() == 1)) {
            Node newRoot = session.getNode(root.getEntryId(0));
            newRoot.setParentId(-1L);
            session.updateNode(newRoot);
            session.setRootId(newRoot.getId());
            session.deleteNode(root);
        }
    }
    
    /**
     * Find the leaf node that contains the leaf entry.
     * <p>
     * (ref Guttman84 p50)
     * 
     * @param id
     * @param entryToFind
     * @return The containing Node
     * @throws SQLException
     */
    private Node findLeaf(long id, Entry entryToFind) throws SQLException {
        Node node = session.getNode(id);
        if (node.isLeaf()) {
            for (int i = 0; i < node.getEntriesCount(); i++) {
                if (node.getEntryId(i) == entryToFind.getId()) {
                    return node;
                }
            }
        } else {
            for (int i = 0; i < node.getEntriesCount(); i++) {
                if (node.intersects(entryToFind, i)) {
                    Node leaf = findLeaf(node.getEntryId(i), entryToFind);
                    if (leaf != null) {
                        return leaf;
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * Condense the tree for deleted entries
     * <p>
     * (ref Guttman84 p50)
     * 
     * @param node The Node to be deleted or added to.
     * @param orphans List of entries which need to be re-inserted
     * @throws SQLException
     */
    private void condenseTree(Node node, List<Entry> orphans) throws SQLException {
        if (session.getRootId() == node.getId()) {
            // we are at the root node
            session.updateNode(node);
            return;
        }
        Node parent = session.getNode(node.getParentId());
        int minEntries = (int)(node.getEntriesMax() * getMinNodeSplit());
        if (node.getEntriesCount() < minEntries) {
            // eliminate under-full node
            for (int i = 0; i < node.getEntriesCount(); i++) {
                orphans.add(node.getEntry(i));
            }
            session.deleteNode(node);
            parent.removeEntry(node.getId());
        } else {
            session.updateNode(node);
            parent.changeEntryEnvelope(node.getMyEntry());
        }
        condenseTree(parent, orphans);
    }
    
}