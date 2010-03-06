package geoscript.geom

import com.vividsolutions.jts.geom.LineString as JtsLineString
import com.vividsolutions.jts.geom.Coordinate

/**
 * A LineString
 */ 
class LineString extends Geometry { 
	
    /**
     * Create a LineString from a JTS LineString.
     * <p>LineString line = new LineString(jtsLineString)</p>
     */
    LineString (JtsLineString line) {
        super(line)
    }
	
    /**
     * Create a LineString from a List of List of Doubles or a List of Points.
     * <p> LineString line = new LineString([[1,2],[3,4],[4,5]])</p>
     * <p> LineString line = new LineString([new Point(111.0, -47), new Point(123.0, -48), new Point(110.0, -47)])</p>
     */
    LineString(List coordinates) {
        this(create(coordinates))
    }

    /**
     * Create a LineString from a List of List of Doubles.
     * <p> LineString line = new LineString([1,2],[3,4],[4,5])</p>
     */
    LineString(List<Double>... coordinates) {
        this(create(coordinates))
    }

    /**
     * Create a LineString from a List of List of Doubles.
     * <p> LineString line = new LineString(new Point(1,2), new Point(3,4), new Point(4,5))</p>
     */
    LineString(Point... points) {
        this(create(points))
    }

    /**
     * Add this LineString with another to create a MultiLineString
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