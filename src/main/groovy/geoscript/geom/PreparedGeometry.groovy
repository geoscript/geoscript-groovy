package geoscript.geom

import org.locationtech.jts.geom.prep.PreparedGeometry as JtsPreparedGeometry
import org.locationtech.jts.geom.prep.PreparedGeometryFactory

/**
 * A PreparedGeometry makes repeated spatial operations more efficient.
 * <p>You can create a PreparedGeometry by wrapping an existing {@link Geometry}:</p>
 * <p><blockquote><pre>
 * def p1 = new PreparedGeometry(new Point(1,4))
 * </pre></blockquote></p>
 * <p>Or by calling the prepare() method on a {@link Geometry}:<p>
 * <p><blockquote><pre>
 * def prep = Geometry.fromWKT('POLYGON ((0 0, 5 0, 5 5, 0 5, 0 0))').prepare()
 * </pre></blockquote></p>
 * <p>Or by using the Geometry.prepare() static method:</p>
 * <p><blockquote><pre>
 * def prep = Geometry.prepared(Geometry.fromWKT('POLYGON ((0 0, 5 0, 5 5, 0 5, 0 0))'))
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class PreparedGeometry {

    /**
     * The wrapped JTS PreparedGeometry
     */
    JtsPreparedGeometry prepGeom

    /**
     * The JTS PreparedGeometryFactory
     */
    static PreparedGeometryFactory factory = new PreparedGeometryFactory()
    
    /**
     * Create a PreparedGeometry from a Geometry
     * @param A Geometry
     */
    PreparedGeometry(Geometry geom) {
        prepGeom = factory.create(geom.g)
    }

    /**
     * Whether this PreparedGeometry contains the given Geometry
     * @param The other Geometry
     * @return Whether this PreparedGeometry contains the other Geometry
     */
    boolean contains(Geometry geom) {
        prepGeom.contains(geom.g)
    }

    /**
     * Whether this PreparedGeometry contains the given Geometry.
     * @param geom The other Geometry
     * @return Whether this PreparedGeometry contains the given Geometry.
     */
    boolean containsProperly(Geometry geom) {
        prepGeom.containsProperly(geom.g)
    }

    /**
     * Whether this PreparedGeometry is covered by the given Geometry.
     * @param geom The other Geometry
     * @return Whether this PreparedGeometry is covered by the given Geometry.
     */
    boolean coveredBy(Geometry geom) {
        prepGeom.coveredBy(geom.g)
    }

    /**
     * Whether this PreparedGeometry covers the given Geometry.
     * @param geom The other Geometry
     * @return Whether this PreparedGeometry covers the given Geometry.
     */
    boolean covers(Geometry geom) {
        prepGeom.covers(geom.g)
    }

    /**
     * Whether this PreparedGeometry crosses the given Geometry.
     * @param geom The other Geometry
     * @return Whether this PreparedGeometry crosses the given Geometry.
     */
    boolean crosses(Geometry geom) {
        prepGeom.crosses(geom.g)
    }

    /**
     * Whether this PreparedGeometry is disjoint the given Geometry.
     * @param geom The other Geometry
     * @return Whether this PreparedGeometry is disjoint the given Geometry.
     */
    boolean disjoint(Geometry geom) {
        prepGeom.disjoint(geom.g)
    }

    /**
     * Get the base Geometry of this PreparedGeometry
     * @return The Geometry
     */
    Geometry getGeometry() {
        Geometry.wrap(prepGeom.geometry)
    }

    /**
     * Whether this PreparedGeometry intersects the given Geometry.
     * @param geom The other Geometry
     * @return Whether this PreparedGeometry intersects the given Geometry.
     */
    boolean intersects(Geometry geom) {
        prepGeom.intersects(geom.g)
    }

    /**
     * Whether this PreparedGeometry overlaps the given Geometry.
     * @param geom The other Geometry
     * @return Whether this PreparedGeometry overlaps the given Geometry.
     */
    boolean overlaps(Geometry geom) {
        prepGeom.overlaps(geom.g)
    }

    /**
     * Whether this PreparedGeometry touches the given Geometry.
     * @param geom The other Geometry
     * @return Whether this PreparedGeometry touches the given Geometry.
     */
    boolean touches(Geometry geom) {
        prepGeom.touches(geom.g)
    }

    /**
     * Whether this PreparedGeometry is within the given Geometry.
     * @param geom The other Geometry
     * @return Whether this PreparedGeometry is within the given Geometry.
     */
    boolean within(Geometry geom) {
        prepGeom.within(geom.g)
    }

    /**
     * The String representation
     * @return The String representation
     */
    String toString() {
        geometry.toString()
    }
}

