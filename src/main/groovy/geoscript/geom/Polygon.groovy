package geoscript.geom

import com.vividsolutions.jts.geom.Polygon as JtsPolygon
import com.vividsolutions.jts.geom.LinearRing as JtsLinearRing
import com.vividsolutions.jts.geom.Coordinate

/**
 * A Polygon
 */
class Polygon extends Geometry {
	
    /**
     * Create a new Polygon by wrapping a JTS Polygon
     */
    Polygon(JtsPolygon poly) {
        super(poly)
    }
	
    /**
     * Create a Polygon with no holes.
     * <p>Polygon p = new Polygon(new LinearRing([[1,1],[4,1],[4,4],[1,1]]))</p>
     */
    Polygon(LinearRing ring) {
        this(create(ring, [] as List<LinearRing>))
    }
	
    /**
     * Create a new Polygon with an exterior ring and a List
     * of holes.
     */
    Polygon(LinearRing ring, List<LinearRing> holes) {
        this(create(ring, holes))
    }
	
    /**
     * Create a new Polygon with an exterion ring as a List of List of Doubles
     * <p>Polygon p = new Polygon([1,2],[3,4],[5,6],[1,2])</p>
     */
    Polygon(List<Double>... ring) {
        this(create(new LinearRing(ring), [] as List<LinearRing>))
    }

    /**
     * Create a new Polygon with an exterion ring as a List of List of Doubles
     * <p>Polygon p = new Polygon([[1,2],[3,4],[5,6],[1,2]])</p>
     */
    /*Polygon(List<List<Double>>... rings) {
    this(create(rings))
    }*/
	
    /**
     * Create a new Polygon from a List of a List of a List of Doubles. The
     * first List of List of Doubles is the exterion ring.  Others are holes.
     */
    Polygon(List<List<List<Double>>> rings) {
        this(create(rings))
    }
	
    /**
     * Create a JTS Polygon from a List of List of List of Doubles
     */
    private static JtsPolygon create(List<List<List<Double>>> rings) {
        JtsLinearRing shell =  new LinearRing(rings[0]).g
        JtsLinearRing[] holes = [];
        if (rings.size() > 1) {
            holes = rings[1..-1].collect{
                ring -> new LinearRing(ring).g
            }
        }
        Geometry.factory.createPolygon(shell, holes)
    }
	
    /**
     * Create a JTS Polygon from a LinearRing exterion ring and a List
     * of LinearRing holes
     */
    private static JtsPolygon create(LinearRing ring, List<LinearRing> holes) {
        Geometry.factory.createPolygon(ring.g, holes.collect{
                hole -> hole.g
            }.toArray() as JtsLinearRing[])
    }
}