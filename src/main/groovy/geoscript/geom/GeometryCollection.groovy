package geoscript.geom

import org.locationtech.jts.geom.GeometryCollection as JtsGeometryCollection
import org.locationtech.jts.geom.Geometry as JtsGeometry

/**
 * A GeometryCollection is a heterogenerous collection of other Geometries.
 * <p>You can create a GeometryCollection by passing in a variable number of Geometries:</p>
 * <p><blockquote><pre>
 * def gc = new GeometryCollection(new Point(1,2),new Point(3,4))
 * </pre></blockquote></p>
 * <p>Or you can pass in a List of Geometries:</p>
 * <p><blockquote><pre>
 * def pts = [new Point(1,2),new Point(3,4)]
 * def gc2 = new GeometryCollection(pts)
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class GeometryCollection extends Geometry {

    /**
     * Create a GeometryCollection that wraps a JTS GeometryCollection
     * @param geomCollection The JTS GeometryCollection
     */
    GeometryCollection(JtsGeometryCollection geomCollection) {
        super(geomCollection)
    }

    /**
     * Create a GeometryCollection from a List of Geometries.
     * <p><blockquote><pre>
     * def gc = new GeometryCollection(new Point(1,2),new Point(3,4))
     * </pre></blockquote></p>
     * @param points A variable List of Points
     */
    GeometryCollection(Geometry... geometries) {
        this(create(geometries))
    }

    /**
     * Create a GeometryCollection from a List of Geometries.
     * <p><blockquote><pre>
     * def pts = [new Point(1,2),new Point(3,4)]
     * def gc2 = new GeometryCollection(pts)
     * </pre></blockquote></p>
     * @param geometries A List of Geometries
     */
    GeometryCollection(List<Geometry> geometries) {
        this(create(geometries))
    }

    /**
     * Add a Geometry to this GeometryCollection to create another GeometryCollection.
     * @param geometry The Geometry
     * @return A new GeometryCollection constructed of the Geometries in this
     * GeometryCollection and the new Geometry
     */
    GeometryCollection plus(Geometry geometry) {
        List<Geometry> geometries = []
        if (!empty) {
            (0..numGeometries-1).each{index ->
                geometries.add(getGeometryN(index))
            }
        }
        geometries.add(geometry)
        new GeometryCollection(geometries)
    }

    /**
     * Get a subset of Geometries in the GeometryCollection.
     * @param start The start index (can be positive or negative)
     * @param end The end index (can be positive or negative)
     * @return The new Geometry
     */
    Geometry slice(Integer start = 0, Integer end = null) {
        int len = getNumGeometries()
        if (start < 0) {
            start = len + start
        }
        if (Math.abs(start) > len) {
            throw new IllegalArgumentException("Start index can not be more than the number of items!")
        }
        end = end == null ? len : end
        if (end < 0) {
            end = len + end
        }
        if (Math.abs(end) > len) {
            throw new IllegalArgumentException("End index can not be more than the number of items!")
        }
        List geoms = []
        (start..<end).each {int i ->
           geoms.add(getGeometryN(i).g);
        }
        Geometry.wrap(factory.buildGeometry(geoms))
    }

    /**
     * Get a Geometry from this GeometryCollection that is the most type specific as possible.
     * If all of the Geometries are Points, a MultiPoint is returned.
     * @return A Geometry
     */
    Geometry narrow() {
        Geometry.wrap(this.g.factory.buildGeometry(this.geometries.collect{it.g}))
    }

    /**
     * Create a GeometryCollection from a List of Geometries
     */
    private static create(List geometries) {
        Geometry.factory.createGeometryCollection(geometries.collect{geom -> geom.g}.toArray() as JtsGeometry[])
    }

    /**
     * Create a GeometryCollection from a variable List of Geometries
     */
    private static create(Geometry... geometries) {
        Geometry.factory.createGeometryCollection(geometries.collect{geom -> geom.g}.toArray() as JtsGeometry[])
    }
}