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
package net.sourceforge.hatbox.jts;

import java.util.List;
import java.sql.SQLException;

import net.sourceforge.hatbox.IdResultSet;
import net.sourceforge.hatbox.RTreeSessionDb;
import net.sourceforge.hatbox.SpatialPredicate;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKBReader;
import org.locationtech.jts.io.ParseException;

/**
 * A result set that filters results based on a spatial predicate
 * 
 * @author Peter Yuill
 */
public class FilteredResultSet extends IdResultSet {
    
    private Geometry query;
    private SpatialPredicate predicate;
    private WKBReader reader = new WKBReader();
    private RTreeSessionDb session;

    
    public FilteredResultSet(List<Long> ids, RTreeSessionDb session, Geometry query,
            SpatialPredicate predicate, String schema, String table) throws SQLException {
        super(ids, schema, table);
        this.session = session;
        this.query = query;
        this.predicate = predicate;
    }

    @Override
    public void close() throws SQLException {
        super.close();
        query = null;
    }

    @Override
    /**
     * This result set is created with a list of ids for the spatial table
     * that came from a spatial index query. For each id we need to execute
     * the specified query sql and return only those rows where the detailed
     * spatial predicate is satisfied (ie the secondary filter).
     */
    public boolean next() throws SQLException {
        rowAvailable = false;
        byte[] candidateWKB = null;
        while ((ids != null) && (!rowAvailable)) {
            currentIndex++;
            if (currentIndex < size) {
                id = ids.get(currentIndex);
                if (id != null) {
                    candidateWKB = session.getWKB(id);
                    if (candidateWKB != null) {
                        try {
                            rowAvailable = testPredicate(reader.read(candidateWKB));
                        } catch (ParseException pe) {
                            throw new SQLException("Failed to parse WKB: " + pe.getMessage());
                        }
                    }
                }
            } else {
                ids = null;
            }
        }
        return rowAvailable;
    }
    
    /**
     * Does this candidate (created from WKB) relate to the query geometry
     * by the specified predicate. The relationship reads:
     * <p>Candidate predicate Query eg candidate overlaps query
     * 
     * @param wkb
     * @return
     * @throws SQLException
     */
    private boolean testPredicate(Geometry candidate) throws SQLException {
        boolean test = false;
        try {
            switch (predicate) {
            case CONTAINS :
                test = candidate.contains(query);
                break;
            case COVERS :
                test = candidate.covers(query);
                break;
            case COVEREDBY :
                test = candidate.coveredBy(query);
                break;
            case CROSSES :
                test = candidate.crosses(query);
                break;
            case DISJOINT :
                test = candidate.disjoint(query);
                break;
            case EQUALS :
                test = candidate.equals(query);
                break;
            case INTERSECTS :
                test = candidate.intersects(query);
                break;
            case OVERLAPS :
                test = candidate.overlaps(query);
                break;
            case TOUCHES :
                test = candidate.touches(query);
                break;
            case WITHIN :
                test = candidate.within(query);
                break;
            }
        } catch (Exception e) {
            throw new SQLException("Failed spatial relation: " + predicate + " " + e.getMessage());
        }
        
        return test;
    }
}
