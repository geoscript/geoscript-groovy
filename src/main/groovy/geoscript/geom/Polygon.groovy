package geoscript.geom

import com.vividsolutions.jts.geom.Polygon as JtsPolygon
import com.vividsolutions.jts.geom.LinearRing as JtsLinearRing
import com.vividsolutions.jts.geom.Coordinate

/**
 * A Polygon Geometry.
 *
 * <p>You can create a Polygon with no holes by providing a LinearRing:</p>
 * <code>Polygon p = new Polygon(new LinearRing([[1,1],[4,1],[4,4],[1,1]]))</code>
 *
 * <p>Or you can create a Polygon with an exterior LinearRing and a List of hole LinearRings:</p>
 * <code>Polygon p = new Polygon(new LinearRing([1,1], [10,1], [10,10], [1,10], [1,1]), [new LinearRing([2,2], [4,2], [4,4], [2,4], [2,2]), new LinearRing([5,5], [6,5], [6,6], [5,6], [5,5])])</code>
 *
 * <p>Or you can create a Polygon with no holes by providing a variable List of Doubles:<p>
 * <code>Polygon p = new Polygon([1,2],[3,4],[5,6],[1,2])</code>
 *
 * <p>Or you can create a Polygon a List of a List of a List of Doubles. The first List of
 * List of Doubles is the exterion ring.  Others are holes.</p>
 * <code>Polygon p = new Polygon([[[1,2],[3,4],[5,6],[1,2]]])</code>
 *
 * <p>Or you can create a Polygon a List LinearRings. The first List of
 * List of Doubles is the exterion ring.  Others are holes.</p>
 * <code>Polygon p = new Polygon([new LinearRing([1,1], [10,1], [10,10], [1,10], [1,1]), new LinearRing([2,2], [4,2], [4,4], [2,4], [2,2]), new LinearRing([5,5], [6,5], [6,6], [5,6], [5,5])])</code>
 * @author Jared Erickson
 */
class Polygon extends Geometry {
	
    /**
     * Create a new Polygon by wrapping a JTS Polygon.
     * @param poly A JTS Polygon
     */
    Polygon(JtsPolygon poly) {
        super(poly)
    }
	
    /**
     * Create a Polygon with no holes.
     * <p><code>Polygon p = new Polygon(new LinearRing([[1,1],[4,1],[4,4],[1,1]]))</code></p>
     * @param ring A LinearRing
     */
    Polygon(LinearRing ring) {
        this(create(ring, [] as List<LinearRing>))
    }
	
    /**
     * Create a new Polygon with an exterior ring and a List
     * of holes.
     * <p><code>Polygon p = new Polygon(new LinearRing([1,1], [10,1], [10,10], [1,10], [1,1]), [new LinearRing([2,2], [4,2], [4,4], [2,4], [2,2]), new LinearRing([5,5], [6,5], [6,6], [5,6], [5,5])])</code></p>
     * @param ring A LinearRing for the exterior shell
     * @param holes A List of LinearRings for the holes
     */
    Polygon(LinearRing ring, List<LinearRing> holes) {
        this(create(ring, holes))
    }
	
    /**
     * Create a new Polygon with an exterion ring as a List of List of Doubles
     * <p><code>Polygon p = new Polygon([1,2],[3,4],[5,6],[1,2])</code></p>
     * @param ring A variable List of List of Doubles
     */
    Polygon(List<Double>... ring) {
        this(create(new LinearRing(ring), [] as List<LinearRing>))
    }

    /**
     * Create a new Polygon from a List of a List of a List of Doubles or LinearRings. The
     * first List of List of Doubles is the exterion ring.  Others are holes.
     * <p><code>Polygon p = new Polygon([[[1,2],[3,4],[5,6],[1,2]]])</code></p>
     * <p><code>Polygon p = new Polygon([new LinearRing([1,1], [10,1], [10,10], [1,10], [1,1]), new LinearRing([2,2], [4,2], [4,4], [2,4], [2,2]), new LinearRing([5,5], [6,5], [6,6], [5,6], [5,5])])</code></p>
     * @param rings A List of LinearRings or a List of List of Doubles
     */
    Polygon(List rings) {
        this(create(rings))
    }

    /**
     * Get the exterior ring or shell 
     * @return The exterion ring or shell
     */
    LineString getExteriorRing() {
        Geometry.wrap(g.exteriorRing)
    }

    /**
     * Get the number of interior rings or holes
     * @return The number of interior rings or holes
     */
    int getNumInteriorRing() {
        g.numInteriorRing
    }

    /**
     * Get the nth interior ring or hole
     * @param n The index of a interior ring or hole
     * @return The nth interior ring or hole
     */
    LineString getInteriorRingN(int n) {
        Geometry.wrap(g.getInteriorRingN(n))
    }

    /**
     * Get a List of all interior rings
     * @return A List of all interior rings
     */
    List<LineString> getInteriorRings() {
        List<LineString> lines = []
        if (numInteriorRing > 0) {
            (0..numInteriorRing-1).each{index ->
                lines.add(getInteriorRingN(index))
            }
        }
        lines
    }

    /**
     * Add this Polygon with another to create a MultiPolygon.
     * @param poly The other Polygon
     * @return A new MultiPolygon containing this Polygon and the other Polygon
     */
    MultiPolygon plus(Polygon poly) {
        new MultiPolygon([this, poly])
    }

    /**
     * Create a JTS Polygon from a List of List of List of Doubles
     */
    private static JtsPolygon create(List rings) {
        JtsLinearRing shell =  (rings[0] instanceof LinearRing) ? rings[0].g : new LinearRing(rings[0]).g
        JtsLinearRing[] holes = [];
        if (rings.size() > 1) {
            holes = rings[1..-1].collect{ring ->
                (ring instanceof LinearRing) ? ring.g : new LinearRing(ring).g
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