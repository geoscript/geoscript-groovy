package geoscript.geom.io

import geoscript.geom.Geometry
import org.geotools.geojson.geom.GeometryJSON

/**
 * Read a {@link geoscript.geom.Geometry Geometry} from a GeoJSON String.
 * <p><blockquote><pre>
 * GeoJSONReader reader = new GeoJSONReader()
 * {@link geoscript.geom.Point Point} point = reader.read("""{ "type": "Point", "coordinates": [111.0, -47.0] }""")
 * POINT (111, -47)
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class GeoJSONReader implements Reader {

    /**
     * Read a Geometry from a GeoJSON String
     * @param str The GeoJSON String
     * @return A Geometry
     */
    Geometry read(String str) {
        GeometryJSON geometryJSON = new GeometryJSON()
        StringReader reader = new StringReader(str)
        Geometry geom = Geometry.wrap(geometryJSON.read(reader))
        geom
    }
}

