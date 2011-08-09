package geoscript.feature.io

import geoscript.feature.Feature
import javax.xml.namespace.QName
import org.geotools.xml.Parser
import org.geotools.xml.Encoder
import org.geotools.xml.Configuration
import org.geotools.gml2.GMLConfiguration as GML2
import org.geotools.gml3.GMLConfiguration as GML3
import org.geotools.gml3.v3_2.GMLConfiguration as GML32

/**
 * Read a Feature from a GML String.
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
            return new Feature(obj, fid)
        } else {
            return new Feature(obj)
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
