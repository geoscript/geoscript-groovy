package geoscript.geom

import com.vividsolutions.jts.geom.LineString as JtsLineString
import com.vividsolutions.jts.geom.Coordinate

/**
 * A LineString Geometry.
 * <p>You  can create a LineString from a List of List of Doubles or a List of {@link Point}s.</p>
 * <p><blockquote><pre>
 * LineString line = new LineString([[1,2],[3,4],[4,5]])
 * LineString line = new LineString([new Point(111.0, -47), new Point(123.0, -48), new Point(110.0, -47)])
 * </pre></blockquote></p>
 * <p>Or you an create a LineString from a repeated List of Doubles.</p>
 * <p><blockquote><pre>
 * LineString line = new LineString([1,2],[3,4],[4,5])
 * </pre></blockquote></p>
 * <p>Or you can create a LineString from a List of repeated {@link Point}s.</p>
 * <p><blockquote><pre>
 * LineString line = new LineString(new Point(1,2), new Point(3,4), new Point(4,5))
 * </pre></blockquote></p>
 * @author Jared Erickson
 */ 
class LineString extends Geometry { 
	
    /**
     * Create a LineString from a JTS LineString.
     * <p><blockquote><pre>
     * LineString line = new LineString(jtsLineString)
     * </pre></blockquote></p>
     * @param line The JTS LineString
     */
    LineString (JtsLineString line) {
        super(line)
    }
	
    /**
     * Create a LineString from a List of List of Doubles or a List of {@link Point}s.
     * <p><blockquote><pre>
     * LineString line = new LineString([[1,2],[3,4],[4,5]])
     * LineString line = new LineString([new Point(111.0, -47), new Point(123.0, -48), new Point(110.0, -47)])
     * </pre></blockquote></p>
     * @param coordinates A List of Coordinates as a List of List of Doubles or a List of Points
     */
    LineString(List coordinates) {
        this(create(coordinates))
    }

    /**
     * Create a LineString from a repeated List of Doubles.
     * <p><blockquote><pre>
     * LineString line = new LineString([1,2],[3,4],[4,5])
     * </pre></blockquote></p>
     * @param coordinates A repeated of List of Doubles.
     */
    LineString(List<Double>... coordinates) {
        this(create(coordinates))
    }

    /**
     * Create a LineString from a List of repeated Points.
     * <p><blockquote><pre>
     * LineString line = new LineString(new Point(1,2), new Point(3,4), new Point(4,5))
     * </pre></blockquote></p>
     * @param points A List of repated Points
     */
    LineString(Point... points) {
        this(create(points))
    }

    /**
     * Get the start {@link Point}
     * @return The start Point
     */
    Point getStartPoint() {
        Geometry.wrap(g.startPoint)
    }

    /**
     * Get the end {@link Point}
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
     * Add this LineString with another to create a {@link MultiLineString}
     * @param line Another LineString
     * @return A new MultiLineString
     */
    MultiLineString plus(LineString line) {
        new MultiLineString([this, line])
    }

    /**
     * Interpolate a {@link Point} on this LineString at the given position from
     * 0 to 1.
     * @param position The position along the LineString from 0 to 1
     * @return A Point on this LineString
     */
    Point interpolatePoint(double position) {
        def indexedLine = new com.vividsolutions.jts.linearref.LengthIndexedLine(g)
        double length = getLength()
        def c = indexedLine.extractPoint(position * length)
        new Point(c.x, c.y)
    }

    /**
     * Locate the position of the {@link Point} along this LineString.
     * @param point The Point
     * @return The position of the Point along this LineString.
     */
    double locatePoint(Point point) {
        def indexedLine = new com.vividsolutions.jts.linearref.LengthIndexedLine(g)
        double position = indexedLine.indexOf(point.g.coordinate)
        double len = getLength()
        double percentAlong = position / len
        percentAlong
    }
    
    /**
     * Place the {@link Point} on the LineString
     * @param point The Point
     * @return A new Point on the LineString
     */
    Point placePoint(Point point) {
        def indexedLine = new com.vividsolutions.jts.linearref.LengthIndexedLine(g)
        double position = indexedLine.indexOf(point.g.coordinate)
        def c = indexedLine.extractPoint(position)
        new Point(c.x, c.y)
    }

    /**
     * Extract a sub LineString from this LineString from the start and end positions.
     * @param start The start position.
     * @param end The end position.
     * @return A sub LineString
     */
    LineString subLine(double start, double end) {
        def indexedLine = new com.vividsolutions.jts.linearref.LengthIndexedLine(g)
        def length = length
        Geometry.wrap(indexedLine.extractLine(start * length, end * length))
    }

    /**
     * Create a LineString from a List of List of Doubles.
     * <p><blockquote><pre>
     * LineString line = new LineString([[1,2],[3,4],[4,5]])
     * LineString line = new LineString([new Point(111.0, -47), new Point(123.0, -48), new Point(110.0, -47)])
     * </pre></blockquote></p>
     */
    private static JtsLineString create(List coordinates) {
        Geometry.factory.createLineString(coordinates.collect{ c -> 
                (c instanceof List) ? new Coordinate(c[0], c[1]) : c.g.coordinate
            }.toArray() as Coordinate[])
    }
	
    /**
     * Create a LineString from a List of List of Doubles.
     * <p><blockquote><pre>
     * LineString line = new LineString([1,2],[3,4],[4,5])
     * </pre></blockquote></p>
     */
    private static JtsLineString create(List<Double>... coordinates) {
        Geometry.factory.createLineString(coordinates.collect{new Coordinate(it[0], it[1])}.toArray() as Coordinate[])
    }

    /**
     * Create a LineString from a List {@link Point}s.
     * <p><blockquote><pre>
     * LineString line = new LineString(new Point(1,2), new Point(3,4), new Point(4,5))
     * </pre></blockquote></p>
     */
    private static JtsLineString create(Point... points) {
        Geometry.factory.createLineString(points.collect{it.g.coordinate}.toArray() as Coordinate[])
    }
}
