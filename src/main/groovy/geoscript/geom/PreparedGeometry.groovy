package geoscript.geom

import com.vividsolutions.jts.geom.prep.PreparedGeometry as JtsPreparedGeometry
import com.vividsolutions.jts.geom.prep.PreparedGeometryFactory

/**
 * A PreparedGeometry
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
     */
    PreparedGeometry(Geometry geom) {
        prepGeom = factory.create(geom.g)
    }

    /**
     * Whether this PreparedGeometry contains the given Geometry
     */
    boolean contains(Geometry geom) {
        prepGeom.contains(geom.g)
    }

    /**
     * Whether this PreparedGeometry contains the given Geometry.
     */
    boolean containsProperly(Geometry geom) {
        prepGeom.containsProperly(geom.g)
    }

    /**
     * Whether this PreparedGeometry is covered by the given Geometry.
     */
    boolean coveredBy(Geometry geom) {
        prepGeom.coveredBy(geom.g)
    }

    /**
     * Whether this PreparedGeometry covers the given Geometry.
     */
    boolean covers(Geometry geom) {
        prepGeom.covers(geom.g)
    }

    /**
     * Whether this PreparedGeometry crosses the given Geometry.
     */
    boolean crosses(Geometry geom) {
        prepGeom.crosses(geom.g)
    }

    /**
     * Whether this PreparedGeometry is disjoint the given Geometry.
     */
    boolean disjoint(Geometry geom) {
        prepGeom.disjoint(geom.g)
    }

    /**
     * Get the base Geometry of this PreparedGeometry
     */
    Geometry getGeometry() {
        Geometry.wrap(prepGeom.geometry)
    }

    /**
     * Whether this PreparedGeometry intersects the given Geometry.
     */
    boolean intersects(Geometry geom) {
        prepGeom.intersects(geom.g)
    }

    /**
     * Whether this PreparedGeometry overlaps the given Geometry.
     */
    boolean overlaps(Geometry geom) {
        prepGeom.overlaps(geom.g)
    }

    /**
     * Whether this PreparedGeometry touches the given Geometry.
     */
    boolean touches(Geometry geom) {
        prepGeom.touches(geom.g)
    }

    /**
     * Whether this PreparedGeometry is within the given Geometry.
     */
    boolean within(Geometry geom) {
        prepGeom.within(geom.g)
    }

    /**
     * The String representation
     */
    String toString() {
        geometry.toString()
    }

}

