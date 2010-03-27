package geoscript.geom

import com.vividsolutions.jts.geom.MultiPoint as JtsMultiPoint
import com.vividsolutions.jts.geom.Point as JtsPoint

/**
 * A MultiPoint Geometry.
 * <p>You can create a MultiPoint from a variable List of Points:</p>
 * <code>MultiPoint m = new MultiPoint(new Point(1,2),new Point(3,4))</code>
 * <p>Or from a variable List of List of Doubles:</p>
 * <code>MultiPoint m = new MultiPoint([1,2],[3,4])</code>
 * <p>Or fom a List of Points:</p>
 * <code>MultiPoint m = new MultiPoint([new Point(1,2),new Point(3,4)])</code>
 * <p>Or from a List of List of Doubles:</p>
 * <code>MultiPoint m = new MultiPoint([[1,2],[3,4]])</code>
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
     * Create a MultiPoint from a List of Points
     * <p><code>def m = new MultiPoint(new Point(1,2),new Point(3,4))</code></p>
     * @param points A variable List of Points
     */
    MultiPoint(Point... points) {
        this(create(points))
    }

    /**
     * Create a MultiPoint from a List of List of Doubles
     * <p><code>def m = new MultiPoint([1,2],[3,4])</code></p>
     * @param points A variable List of List of Doubles
     */
    MultiPoint(List<Double>... points) {
        this(create(points))
    }

    /**
     * Create a MultiPoint from a List of List of Doble or a List of Points
     * <p><code>def m = new MultiPoint([new Point(1,2),new Point(3,4)])</code></p>
     * <p><code>def m = new MultiPoint([[1,2],[3,4]])</code></p>
     * @param points Either a List of List of Doubles of a List of Points
     */
    MultiPoint(List points) {
        this(create(points))
    }

    /**
     * Add a Point to this MultiPoint to create another MultiPoint.
     * @param point The other Point
     * @return A new MultiPoint constructed of this Point and the other Point
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
