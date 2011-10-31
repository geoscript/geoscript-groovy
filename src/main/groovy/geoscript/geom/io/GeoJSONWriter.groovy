package geoscript.geom.io

import geoscript.geom.*
import com.vividsolutions.jts.geom.Coordinate

/**
 * Write a Geometry to a GeoJSON String.
 * <p><code>GeoJSONWriter writer = new GeoJSONWriter()</code></p>
 * <p><code>writer.write(new Point(111,-47))</code></p>
 * <p><code>{ "type": "Point", "coordinates": [111.0, -47.0] }</code></p>
 * @author Jared Erickson
 */
class GeoJSONWriter implements Writer {

    /**
     * Write the Geometry to a GeoJSON String
     * @param geom The Geometry
     * @return A GeoJSON String
     */
    String write(Geometry geom) {

        if (geom instanceof Point) {
            return """{ "type": "Point", "coordinates": ${geometryToCoordinateString(geom)} }"""
        }
        else if (geom instanceof LinearRing) {
            return """{ "type": "Polygon", "coordinates": [${geometryToCoordinateString(geom)}] }"""
        }
        else if (geom instanceof LineString) {
            return """{ "type": "LineString", "coordinates": [${geometryToCoordinateString(geom)}] }"""
        }
        else if (geom instanceof Polygon) {
            return """{ "type": "Polygon", "coordinates": [${polygonToCoordinateString(geom as Polygon)}] }"""
        }
        if (geom instanceof MultiPoint) {
            return """{ "type": "MultiPoint", "coordinates": [${geom.geometries.collect{g-> geometryToCoordinateString(g)}.join(', ')}] }"""
        }
        else if (geom instanceof MultiLineString) {
            return """{ "type": "MultiLineString", "coordinates": [${geom.geometries.collect{g->'[' + geometryToCoordinateString(g) + ']'}.join(', ')}] }"""
        }
        else if (geom instanceof MultiPolygon) {
            return """{ "type": "MultiPolygon", "coordinates": [${geom.geometries.collect{g-> '[' + polygonToCoordinateString(g as Polygon) + ']'}.join(', ')}] }"""
        }
        else {
            return """{ "type": "GeometryCollection", "geometries": [${geom.geometries.collect{g->write(g)}.join(', ')}] }"""
        }
    }

    /**
     * Write an Array of Coordinates to a KML String
     * @param coords The Array of Coordinates
     * @return A KML String (x1,y1 x2,y2)
     */
    private String geometryToCoordinateString(Geometry geom) {
        geom.coordinates.collect{c -> "[${c.x}, ${c.y}]"}.join(", ")
    }

    /**
     * Write a Polygon to a String
     * @param poly The Polygon
     * @return A JSON String
     */
    private String polygonToCoordinateString(Polygon poly) {
        String str = "[${geometryToCoordinateString(poly.exteriorRing)}]"
        str += poly.interiorRings.collect{g-> ", [" + geometryToCoordinateString(g) + "]"}.join('')
        str
    }

}

