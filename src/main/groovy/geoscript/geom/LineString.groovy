package geoscript.geom

import com.vividsolutions.jts.geom.LineString as JtsLineString
import com.vividsolutions.jts.geom.Coordinate

/**
 * A LineString
 */ 
class LineString extends Geometry { 
	
    /**
     * Create a LineString from a JTS LineString.
     * <p>LineString line = new LineString()</p>
     */
    LineString (JtsLineString line) {
        super(line)
    }
	
    /**
     * Create a LineString from a List of List of Doubles.
     * <p> LineString line = new LineString([[1,2],[3,4],[4,5]])</p>
     */
    LineString(List<List<Double>> coordinates) {
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
     * <p> LineString line = new LineString([[1,2],[3,4],[4,5]])</p>
     */
    private static JtsLineString create(List<List<Double>> coordinates) {
        Geometry.factory.createLineString(coordinates.collect{new Coordinate(it[0], it[1])}.toArray() as Coordinate[])
    }
	
    /**
     * Create a LineString from a List of List of Doubles.
     * <p> LineString line = new LineString([1,2],[3,4],[4,5])</p>
     */
    private static JtsLineString create(List<Double>... coordinates) {
        Geometry.factory.createLineString(coordinates.collect{new Coordinate(it[0], it[1])}.toArray() as Coordinate[])
    }
	
}