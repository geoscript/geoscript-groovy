package geoscript.geom

import com.vividsolutions.jts.geom.LinearRing as JtsLinearRing
import com.vividsolutions.jts.geom.Coordinate

/**
 * A LinearRing
 */
class LinearRing extends Geometry { 
	
    /**
     * Create a LinearRing by wrapping a JTS LinearRing
     */
    LinearRing (JtsLinearRing ring) {
        super(ring)
    }
	
    /**
     * Create a new LinearRing with a List of List of Doubles.
     * <p>LinearRing ring = new LinearRing([[1,2],[3,4],[4,5]])</p>
     */
    LinearRing(List<List<Double>> coordinates) {
        this(create(coordinates))
    }

    /**
     * Create a new LinearRing with a List of List of Doubles.
     * <p>LinearRing ring = new LinearRing([1,2],[3,4],[4,5])</p>
     */
    LinearRing(List<Double>... coordinates) {
        this(create(coordinates))
    }

    /**
     * Create a new LinearRing with a List of List of Doubles.
     * <p>LinearRing ring = new LinearRing([[1,2],[3,4],[4,5]])</p>
     */
    private static JtsLinearRing create(List<List<Double>> coordinates) {
        Geometry.factory.createLinearRing(coordinates.collect{new Coordinate(it[0], it[1])}.toArray() as Coordinate[])
    }
	
    /**
     * Create a new LinearRing with a List of List of Doubles.
     * <p>LinearRing ring = new LinearRing([1,2],[3,4],[4,5])</p>
     */
    private static JtsLinearRing create(List<Double>... coordinates) {
        Geometry.factory.createLinearRing(coordinates.collect{new Coordinate(it[0], it[1])}.toArray() as Coordinate[])
    }
	
}