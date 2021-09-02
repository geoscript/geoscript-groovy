package geoscript.geom

import org.locationtech.jts.geom.MultiPolygon as JtsMultiPolygon
import org.locationtech.jts.geom.Polygon as JtsPolygon

/**
 * A MultiPolygon Geometry.
 * <p>You can create a MultiPolygon from a variable List of {@link Polygon}s:</p>
 * <p><blockquote><pre>
 * MultiPolygon mp = new MultiPolygon(new Polygon([1,2],[3,4],[5,6],[1,2]), new Polygon([7,8],[9,10],[11,12],[7,8]))
 * </pre></blockquote></p>
 * <p>Or from a variable List of List of Doubles:</p>
 * <p><blockquote><pre>
 * MultiPolygon mp = new MultiPolygon([[[1,2],[3,4],[5,6],[1,2]]], [[[7,8],[9,10],[11,12],[7,8]]])
 * </pre></blockquote></p>
 * <p>Or from a List of {@link Polygon}s: </p>
 * <p><blockquote><pre>
 * MultiPolygon mp = new MultiPolygon([new Polygon([1,2],[3,4],[5,6],[1,2]), new Polygon([7,8],[9,10],[11,12],[7,8])])
 * </pre></blockquote></p>
 * <p>Or form a List of List of List of Doubles:</p>
 * <p><blockquote><pre>
 * MultiPolygon mp = new MultiPolygon([[[[1,2],[3,4],[5,6],[1,2]]], [[[7,8],[9,10],[11,12],[7,8]]]])
 * </pre></blockquote></p>
 * @author Jared Erickson
 */ 
class MultiPolygon extends GeometryCollection {
	
    /**
     * Create a MultiPolygon that wraps a JTS MultiPolygon
     * @param multiPolygon The JTS MultiPolygon
     */
    MultiPolygon(JtsMultiPolygon multiPolygon) {
        super(multiPolygon)
    }
	
    /**
     * Create a MultiPolygon from a variable List of {@link Polygon}s
     * <p><blockquote><pre>
     * MultiPolygon mp = new MultiPolygon(new Polygon([1,2],[3,4],[5,6],[1,2]), new Polygon([7,8],[9,10],[11,12],[7,8]))
     * </pre></blockquote></p>
     * @param polygons A variable List of Polygons
     */
    MultiPolygon(Polygon... polygons) {
        this(create(polygons))
    }
	
    /**
     * Create a MultiPolygon from a variable List of List of List of Doubles
     * <p><blockquote><pre>
     * MultiPolygon mp = new MultiPolygon([[[1,2],[3,4],[5,6],[1,2]]], [[[7,8],[9,10],[11,12],[7,8]]])
     * </pre></blockquote></p>
     * @param polygons A variable List of List of Doubles
     */
    MultiPolygon(List<List<List<Double>>>... polygons) {
        this(create(polygons))
    }
    
    /**
     * Create a MultiPolygon from a List of {@link Polygon}s or a List of List of List of Doubles
     * <p><blockquote><pre>
     * MultiPolygon mp = new MultiPolygon([new Polygon([1,2],[3,4],[5,6],[1,2]), new Polygon([7,8],[9,10],[11,12],[7,8])])
     * MultiPolygon mp = new MultiPolygon([[[[1,2],[3,4],[5,6],[1,2]]], [[[7,8],[9,10],[11,12],[7,8]]]])
     * </pre></blockquote></p>
     * @param polygons A List of Polygons or a List of List of List of Doubles
     */
    MultiPolygon(List polygons) {
        this(create(polygons))
    }

    /**
     * Add a {@link Polygon} to this MultiPolygon to create another MultiPolygon
     * <p><blockquote><pre>
     * def mp1 = new MultiPolygon(new Polygon([1,2],[3,4],[5,6],[1,2]), new Polygon([7,8],[9,10],[11,12],[7,8]))
     * def mp2 = mp1 + new Polygon([11,12],[13,14],[15,16],[11,12])
     *
     * MULTIPOLYGON (((1 2, 3 4, 5 6, 1 2)), ((7 8, 9 10, 11 12, 7 8)), ((11 12, 13 14, 15 16, 11 12)))
     * </pre></blockquote></p>
     * @param poly The Polygon to add to this Polygon
     * @return A new MultiPolygon containing this Polygon and the other Polygon
     */
    MultiPolygon plus(Polygon poly) {
        List<Polygon> polygons = []
        if(!empty) {
            (0..numGeometries-1).each{index ->
                polygons.add(getGeometryN(index))
            }
        }
        polygons.add(poly)
        new MultiPolygon(polygons)
    }

    /**
     * Split a Polygon with a LineString
     * @param lineString The LineString
     * @return The split Geometry
     */
    Geometry split(LineString lineString) {
        split(new MultiLineString([lineString]))
    }

    /**
     * Split a Polygon with a MultiLineString
     * @param multiLineString The MultiLineString
     * @return The split Geometry
     */
    Geometry split(MultiLineString multiLineString) {
        Geometry polygons = this.boundary.union(multiLineString).polygonize()
        List polygonsToKeep = []
        polygons.geometries.each{p ->
            if (polygons.contains(p.interiorPoint)) {
                polygonsToKeep.add(p)
            }
        }
        polygonsToKeep.size() > 1 ? new MultiPolygon(polygonsToKeep) : polygonsToKeep[0]
    }

    /**
     * Create a JTS MultiPolygon from a List of Polygons 
     */
    private static create(Polygon... polygons) {
        if (polygons.size() == 0) {
            Geometry.factory.createMultiPolygon()
        } else {
            Geometry.factory.createMultiPolygon(polygons.collect {
                polygon -> polygon.g
            }.toArray() as JtsPolygon[])
        }
    }

    /**
     * Create a JTS MultiPolygon from a List of List of List of Doubles
     */
    private static create(List<List<List<Double>>>... polygons) {
        List<Polygon> p = polygons.collect{
            poly -> new Polygon(poly)
        }
        if (p.size() > 0) {
            create(*p)
        } else {
            Geometry.factory.createMultiPolygon()
        }
    }

    /**
     * Create a JTS MultiPolygon from a List of Polygons or a List of List of Doubles
     */
    private static create(List polygons) {
        if (polygons.isEmpty()) {
            Geometry.factory.createMultiPolygon()
        } else {
            List<Polygon> p = polygons.collect { poly ->
                (poly instanceof Polygon) ? poly : new Polygon(poly)
            }
            create(*p)
        }
    }
}
