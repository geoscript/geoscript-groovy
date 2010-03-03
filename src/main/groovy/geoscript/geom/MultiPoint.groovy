package geoscript.geom

import com.vividsolutions.jts.geom.MultiPoint as JtsMultiPoint
import com.vividsolutions.jts.geom.Point as JtsPoint

/**
 * A MultiPoint
 */
class MultiPoint extends Geometry {

    /**
     * Create a MultiPoint that wraps a JTS MultiPoint
     */
    MultiPoint(JtsMultiPoint multiPoint) {
        super(multiPoint)
    }

    /**
     * Create a MultiPoint from a List of Points
     */
    MultiPoint(Point... points) {
        this(create(points))
    }

    /**
     * Create a MultiPoint from a List of List of Doubles
     */
    MultiPoint(List<Double>... points) {
        this(create(points))
    }

    /**
     * Create a MultiPoint from a List of List of Points
     */
    MultiPoint(List points) {
        this(create(points))
    }

    /**
     * Add a Point to this MultiPoint
     */
    public MultiPoint plus(Point point) {
        List<Point> points = []
        (0..numGeometries-1).each{index ->
            points.add(getGeometryN(index))
        }
        points.add(point)
        new MultiPoint(*points)
    }

    /**
     * Create a MultiPoint from a List of List of Doubles
     */
    private static create(List<Double>... points) {
        create(points.collect{pt -> new Point(pt[0], pt[1])})
    }

    /**
     * Create a MultiPoint from a List of Points
     */
    private static create(List points) {
        Geometry.factory.createMultiPoint(points.collect{
                pt -> (pt instanceof Point) ? pt.g : new Point(pt[0], pt[1]).g
            }.toArray() as JtsPoint[])
    }

    /**
     * Create a MultiPoint from a List of Points
     */
    private static create(Point... points) {
        Geometry.factory.createMultiPoint(points.collect{
                pt -> pt.g
            }.toArray() as JtsPoint[])
    }
}
