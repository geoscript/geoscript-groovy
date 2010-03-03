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
     */
    MultiPolygon(Polygon... polygons) {
        this(create(polygons))
    }
	
    /**
     * Create a MultiPolygon from a List of List of List of Doubles
     */
    MultiPolygon(List<List<List<Double>>>... polygons) {
        this(create(polygons))
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
}
