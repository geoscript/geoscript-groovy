package geoscript.geom.io

import geoscript.geom.Geometry
import org.geotools.geojson.geom.GeometryJSON

/**
 * Write a {@link geoscript.geom.Geometry Geometry} to a GeoJSON String.
 * <p><blockquote><pre>
 * GeoJSONWriter writer = new GeoJSONWriter()
 * writer.write(new {@link geoscript.geom.Point Point}(111,-47))
 *
 * { "type": "Point", "coordinates": [111.0, -47.0] }
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class GeoJSONWriter implements Writer {

    /**
     * Write the Geometry to a GeoJSON String
     * @param geom The Geometry
     * @return A GeoJSON String
     */
    String write(Geometry geom) {
        GeometryJSON geometryJSON = new GeometryJSON()
        StringWriter writer = new StringWriter()
        geometryJSON.write(geom.g, writer)
        writer.toString()
    }
}

