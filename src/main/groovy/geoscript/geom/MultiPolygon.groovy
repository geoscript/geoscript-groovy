package geoscript.geom

import com.vividsolutions.jts.geom.MultiPolygon as JtsMultiPolygon
import com.vividsolutions.jts.geom.Polygon as JtsPolygon

/**
 * A MultiPolygon
 */ 
class MultiPolygon extends Geometry {
	
    /**
     * Create a MultiPolygon that wraps a JTS MultiPolygon
     */
    MultiPolygon(JtsMultiPolygon multiPolygon) {
        super(multiPolygon)
    }
	
    /**
     * Create a MultiPolygon from a List of Polygons
     * <p>MultiPolygon mp = new MultiPolygon(new Polygon([1,2],[3,4],[5,6],[1,2]), new Polygon([7,8],[9,10],[11,12],[7,8]))</p>
     */
    MultiPolygon(Polygon... polygons) {
        this(create(polygons))
    }
	
    /**
     * Create a MultiPolygon from a List of List of List of Doubles
     * <p>MultiPolygon mp = new MultiPolygon([[[1,2],[3,4],[5,6],[1,2]]], [[[7,8],[9,10],[11,12],[7,8]]])</p>
     */
    MultiPolygon(List<List<List<Double>>>... polygons) {
        this(create(polygons))
    }
    
    /**
     * Create a MultiPolygon from a List of Polygons or a List of List of Doubles
     * <p>MultiPolygon mp = new MultiPolygon([new Polygon([1,2],[3,4],[5,6],[1,2]), new Polygon([7,8],[9,10],[11,12],[7,8])])</p>
     * <p>MultiPolygon mp = new MultiPolygon([[[[1,2],[3,4],[5,6],[1,2]]], [[[7,8],[9,10],[11,12],[7,8]]]])</p>
     */
    MultiPolygon(List polygons) {
        this(create(polygons))
    }

    /**
     * Add a Polygon to this MultiPolygon to create another MultiPolygon
     * <p>def mp1 = new MultiPolygon(new Polygon([1,2],[3,4],[5,6],[1,2]), new Polygon([7,8],[9,10],[11,12],[7,8]))</p>
     * <p>def mp2 = mp1 + new Polygon([11,12],[13,14],[15,16],[11,12])</p>
     * <p>MULTIPOLYGON (((1 2, 3 4, 5 6, 1 2)), ((7 8, 9 10, 11 12, 7 8)), ((11 12, 13 14, 15 16, 11 12)))</p>
     */
    MultiPolygon plus(Polygon poly) {
        List<Polygon> polygons = []
        (0..numGeometries-1).each{index ->
            polygons.add(getGeometryN(index))
        }
        polygons.add(poly)
        new MultiPolygon(polygons)
    }

    /**
     * Create a JTS MultiPolygon from a List of Polygons 
     */
    private static create(Polygon... polygons) {
        Geometry.factory.createMultiPolygon(polygons.collect{
                polygon -> polygon.g
            }.toArray() as JtsPolygon[])
    }

    /**
     * Create a JTS MultiPolygon from a List of List of List of Doubles
     */
    private static create(List<List<List<Double>>>... polygons) {
        List<Polygon> p = polygons.collect{
            poly -> new Polygon(poly)
        }
        create(*p)
    }

    /**
     * Create a JTS MultiPolygon from a List of Polygons or a List of List of Doubles
     */
    private static create(List polygons) {
        List<Polygon> p = polygons.collect{poly ->
            (poly instanceof Polygon) ? poly : new Polygon(poly)
        }
        create(*p)
    }
}
