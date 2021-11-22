package geoscript.geom

import org.locationtech.jts.geom.MultiPoint as JtsMultiPoint
import org.locationtech.jts.geom.Point as JtsPoint

/**
 * A MultiPoint Geometry.
 * <p>You can create a MultiPoint from a variable List of {@link Point}s:</p>
 * <p><blockquote><pre>
 * MultiPoint m = new MultiPoint(new Point(1,2),new Point(3,4))
 * </pre></blockquote></p>
 * <p>Or from a variable List of List of Doubles:</p>
 * <p><blockquote><pre>
 * MultiPoint m = new MultiPoint([1,2],[3,4])
 * </pre></blockquote></p>
 * <p>Or fom a List of {@link Point}s:</p>
 * <p><blockquote><pre>
 * MultiPoint m = new MultiPoint([new Point(1,2),new Point(3,4)])
 * </pre></blockquote></p>
 * <p>Or from a List of List of Doubles:</p>
 * <p><blockquote><pre>
 * MultiPoint m = new MultiPoint([[1,2],[3,4]])
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class MultiPoint extends GeometryCollection {

    /**
     * Create a MultiPoint that wraps a JTS MultiPoint
     * @param multiPoint The JTS MultiPoint
     */
    MultiPoint(JtsMultiPoint multiPoint) {
        super(multiPoint)
    }

    /**
     * Create a MultiPoint from a List of {@link Point}s
     * <p><blockquote><pre>
     * def m = new MultiPoint(new Point(1,2),new Point(3,4))
     * </pre></blockquote></p>
     * @param points A variable List of Points
     */
    MultiPoint(Point... points) {
        this(create(points))
    }

    /**
     * Create a MultiPoint from a List of List of Doubles
     * <p><blockquote><pre>
     * def m = new MultiPoint([1,2],[3,4])
     * </pre></blockquote></p>
     * @param points A variable List of List of Doubles
     */
    MultiPoint(List<Double>... points) {
        this(create(points))
    }

    /**
     * Create a MultiPoint from a List of List of Double or a List of {@link Point}s
     * <p><blockquote><pre>
     * def m = new MultiPoint([new Point(1,2),new Point(3,4)])
     * def m = new MultiPoint([[1,2],[3,4]])
     * </pre></blockquote></p>
     * @param points Either a List of List of Doubles of a List of Points
     */
    MultiPoint(List points) {
        this(create(points))
    }

    /**
     * Add a {@link Point} to this MultiPoint to create another MultiPoint.
     * @param point The other Point
     * @return A new MultiPoint constructed of this Point and the other Point
     */
    MultiPoint plus(Point point) {
        List<Point> points = []
        if (!empty) {
            (0..numGeometries-1).each{index ->
                points.add(getGeometryN(index))
            }
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
