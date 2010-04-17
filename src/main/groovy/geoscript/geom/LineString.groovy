package geoscript.geom

import com.vividsolutions.jts.geom.LineString as JtsLineString
import com.vividsolutions.jts.geom.Coordinate

/**
 * A LineString Geometry.
 * <p>You  can create a LineString from a List of List of Doubles or a List of Points.</p>
 * <code>LineString line = new LineString([[1,2],[3,4],[4,5]])</code>
 * <code>LineString line = new LineString([new Point(111.0, -47), new Point(123.0, -48), new Point(110.0, -47)])</code>
 * <p>Or you an create a LineString from a repeated List of Doubles.</p>
 * <code>LineString line = new LineString([1,2],[3,4],[4,5])</code>
 * <p>Or you can create a LineString from a List of repeated Points.</p>
 * <code>LineString line = new LineString(new Point(1,2), new Point(3,4), new Point(4,5))</code>
 * @author Jared Erickson
 */ 
class LineString extends Geometry { 
	
    /**
     * Create a LineString from a JTS LineString.
     * <p><code>LineString line = new LineString(jtsLineString)</code></p>
     * @param line The JTS LineString
     */
    LineString (JtsLineString line) {
        super(line)
    }
	
    /**
     * Create a LineString from a List of List of Doubles or a List of Points.
     * <p><code>LineString line = new LineString([[1,2],[3,4],[4,5]])</code></p>
     * <p><code>LineString line = new LineString([new Point(111.0, -47), new Point(123.0, -48), new Point(110.0, -47)])</code></p>
     * @param coordinates A List of Coordinates as a List of List of Doubles or a List of Points
     */
    LineString(List coordinates) {
        this(create(coordinates))
    }

    /**
     * Create a LineString from a repeated List of Doubles.
     * <p><code>LineString line = new LineString([1,2],[3,4],[4,5])</code></p>
     * @param coordinates A repeated of List of Doubles.
     */
    LineString(List<Double>... coordinates) {
        this(create(coordinates))
    }

    /**
     * Create a LineString from a List of repeated Points.
     * <p><code>LineString line = new LineString(new Point(1,2), new Point(3,4), new Point(4,5))</code></p>
     * @param points A List of repated Points
     */
    LineString(Point... points) {
        this(create(points))
    }

    /**
     * Get the start Point
     * @return The start Point
     */
    Point getStartPoint() {
        Geometry.wrap(g.startPoint)
    }

    /**
     * Get the end Point
     * @return The end Point
     */
    Point getEndPoint() {
        Geometry.wrap(g.endPoint)
    }

    /**
     * Is this LineString closed?
     * @return Whether this LineString is closed
     */
    boolean isClosed() {
        g.isClosed()
    }

    /**
     * Is this LineString a ring?
     * @return Whether this LineString is a ring
     */
    boolean isRing() {
        g.isRing()
    }

    /**
     * Create a new LineString where the coordinates are in reverse order
     * @return A LineString
     */
    LineString reverse() {
        Geometry.wrap(g.reverse())
    }

    /**
     * Add this LineString with another to create a MultiLineString
     * @param line Another LineString
     * @return A new MultiLineString
     */
    MultiLineString plus(LineString line) {
        new MultiLineString([this, line])
    }

    /**
     * Create a LineString from a List of List of Doubles.
     * <p> LineString line = new LineString([[1,2],[3,4],[4,5]])</p>
     * <p> LineString line = new LineString([new Point(111.0, -47), new Point(123.0, -48), new Point(110.0, -47)])</p>
     */
    private static JtsLineString create(List coordinates) {
        Geometry.factory.createLineString(coordinates.collect{ c -> 
                (c instanceof List) ? new Coordinate(c[0], c[1]) : c.g.coordinate
            }.toArray() as Coordinate[])
    }
	
    /**
     * Create a LineString from a List of List of Doubles.
     * <p> LineString line = new LineString([1,2],[3,4],[4,5])</p>
     */
    private static JtsLineString create(List<Double>... coordinates) {
        Geometry.factory.createLineString(coordinates.collect{new Coordinate(it[0], it[1])}.toArray() as Coordinate[])
    }

    /**
     * Create a LineString from a List Points.
     * <p> LineString line = new LineString(new Point(1,2), new Point(3,4), new Point(4,5))</p>
     */
    private static JtsLineString create(Point... points) {
        Geometry.factory.createLineString(points.collect{it.g.coordinate}.toArray() as Coordinate[])
    }
}