package geoscript.geom.io

import geoscript.geom.Geometry
import org.geotools.kml.KMLConfiguration
import org.geotools.xml.Parser

/**
 * Read a {@link geoscript.geom.Geometry Geometry} from a KML String.
 * <p><blockquote><pre>
 * KmlReader reader = new KmlReader()
 * {@link geoscript.geom.Point Point} point = reader.read("&lt;Point&gt;&lt;coordinates&gt;111.0,-47.0&lt;/coordinates&gt;&lt;/Point&gt;")
 *
 * POINT (111 -47)
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class KmlReader implements Reader {

    /**
     * Read a Geometry from a KML String
     * @param str The KML String
     * @return A Geometry
     */
    Geometry read(String str) {
        Parser parser = new Parser(new KMLConfiguration())
        Geometry.wrap(parser.parse(new StringReader(str)))
    }
}