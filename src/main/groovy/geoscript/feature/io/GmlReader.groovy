package geoscript.feature.io

import geoscript.feature.Feature
import org.geotools.xml.Parser
import org.geotools.gml2.GMLConfiguration as GML2
import org.geotools.gml3.GMLConfiguration as GML3
import org.geotools.gml3.v3_2.GMLConfiguration as GML32
import org.opengis.feature.simple.SimpleFeature

/**
 * Read a Feature from a GML String.
 * <p><blockquote><pre>
 * String gml = """&lt;gml:houses fid="house1" xmlns:gml="http://www.opengis.net/gml" xmlns:xlink="http://www.w3.org/1999/xlink"&gt;
 *    &lt;gml:name&gt;House&lt;/gml:name&gt;
 *    &lt;gml:geom&gt;
 *       &lt;gml:Point&gt;
 *           &lt;gml:coord&gt;
 *               &lt;gml:X&gt;111.0&lt;/gml:X&gt;
 *               &lt;gml:Y&gt;-47.0&lt;/gml:Y&gt;
 *           &lt;/gml:coord&gt;
 *       &lt;/gml:Point&gt;
 *    &lt;/gml:geom&gt;
 *    &lt;gml:price&gt;12.5&lt;/gml:price&gt;
 * &lt;/gml:houses&gt;
 * """
 * GmlReader reader = new GmlReader()
 * {@link geoscript.feature.Feature Feature} actual = reader.read(gml)
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class GmlReader implements Reader {

    /**
     * Read a Feature from a GML String.
     * @param str The GML String
     * @return A Feature
     */
    Feature read(String str) {
        read(str, 2)
    }

    /**
     * Read a Feature from a GML String.
     * @param str The GML String
     * @param version The GML version (2,3,3.2)
     * @return A Feature
     */
    Feature read(String str, double version) {
        def parser = new Parser(getGmlConfig(version))
        def obj = parser.parse(new StringReader(str))
        if (obj instanceof Map) {
            def fid = obj.remove("fid")
            if (!fid) {
                fid = obj.remove("id")
            }
            return new Feature(obj, fid as String)
        } else {
            return new Feature(obj as SimpleFeature)
        }
    }

    private def getGmlConfig(double version) {
        if (version == 2) {
            return new GML2()
        } else if (version == 3) {
            return new GML3()
        } else /*if (version == 3.2)*/ {
            return new GML32()
        }
    }
}
