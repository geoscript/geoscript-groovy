package geoscript.geom

import com.vividsolutions.jts.geom.LinearRing as JtsLinearRing
import com.vividsolutions.jts.geom.Coordinate

/**
 * A LinearRing Geometry is a LineString whose first and last coordinates are the same forming a closed ring.
 * <p>You can create a LinearRing with a List of List of Doubles:</p>
 * <p><blockquote><pre>
 * LinearRing l = new LinearRing([[111.0, -47],[123.0, -48],[110.0, -47], [111.0, -47]])
 * </pre></blockquote></p>
 * <p>Or you can create a LinearRing wih a List of {@link Point}s:</p>
 * <p><blockquote><pre>
 * LinearRing l = new LinearRing([new Point(111.0, -47), new Point(123.0, -48), new Point(110.0, -47), new Point(111.0, -47)])
 * </pre></blockquote></p>
 * <p>Or you can create a LinearRing with a repeated List of Doubles:</p>
 * <p><blockquote><pre>
 * LinearRing l =  new LinearRing([111.0, -47],[123.0, -48],[110.0, -47], [111.0, -47])
 * </pre></blockquote></p>
 * <p>Or you can create a LinearRing with a repated List of {@link Point}s:</p>
 * <p><blockquote><pre>
 * LinearRing l = new LinearRing(new Point(111.0, -47), new Point(123.0, -48), new Point(110.0, -47), new Point(111.0, -47))
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class LinearRing extends LineString {
	
    /**
     * Create a LinearRing by wrapping a JTS LinearRing
     * @param ring A JTS LinearRing
     */
    LinearRing (JtsLinearRing ring) {
        super(ring)
    }
	
    /**
     * Create a new LinearRing with a List of List of Doubles.
     * <p><blockquote><pre>
     * LinearRing ring = new LinearRing([[1,2],[3,4],[4,5]])
     * LinearRing ring = new LinearRing([new Point(111.0, -47), new Point(123.0, -48), new Point(110.0, -47), new Point(111.0, -47)])
     * </pre></blockquote></p>
     * @param coordinate A List of List Double or a List of Points
     */
    LinearRing(List coordinates) {
        this(create(coordinates))
    }

    /**
     * Create a new LinearRing with a List of List of Doubles.
     * <p><blockquote><pre>
     * LinearRing ring = new LinearRing([1,2],[3,4],[4,5])
     * </pre></blockquote></p>
     * @param coordinates A repeated List of List of Doubles
     *
     */
    LinearRing(List<Double>... coordinates) {
        this(create(coordinates))
    }

    /**
     * Create a new LinearRing with a repeated List {@link Point}s.
     * <p><blockquote><pre>
     * LinearRing ring = new LinearRing(new Point(1,2), new Point(3,4), new Point(4,5))
     * </pre></blockquote></p>
     * @param points A repeated List of Points
     */
    LinearRing(Point... points) {
        this(create(points))
    }

    /**
     * Create a new LinearRing with a List of List of Doubles.
     * <p><blockquote><pre>
     * LinearRing ring = new LinearRing([[1,2],[3,4],[4,5]])
     * </pre></blockquote></p>
     * @param coordinate A List of List of Doubles or a List of Points
     */
    private static JtsLinearRing create(List coordinates) {
        Geometry.factory.createLinearRing(coordinates.collect{c ->
                (c instanceof Point) ? c.g.coordinate : new Coordinate(c[0], c[1])
        }.toArray() as Coordinate[])
    }
	
    /**
     * Create a new LinearRing with a List of List of Doubles.
     * <p><blockquote><pre>
     * LinearRing ring = new LinearRing([1,2],[3,4],[4,5])
     * </pre></blockquote></p>
     * @param coordinates A repeated List of Doubles
     */
    private static JtsLinearRing create(List<Double>... coordinates) {
        Geometry.factory.createLinearRing(coordinates.collect{new Coordinate(it[0], it[1])}.toArray() as Coordinate[])
    }

    /**
     * Create a new LinearRing with a repeated List {@link Point}s.
     * <p><blockquote><pre>
     * LinearRing ring = new LinearRing(new Point(1,2), new Point(3,4), new Point(4,5))
     * </pre></blockquote></p>
     * @param points A repeated List of Points
     */
    private static JtsLinearRing create(Point... points) {
        Geometry.factory.createLinearRing(points.collect{it.g.coordinate}.toArray() as Coordinate[])
    }
	
}