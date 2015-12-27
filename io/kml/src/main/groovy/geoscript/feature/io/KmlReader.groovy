package geoscript.feature.io

import geoscript.feature.Feature
import org.geotools.kml.KMLConfiguration
import org.geotools.xml.Parser

/**
 * Read a Feature from a KML Placemark
 * @author Jared Erickson
 */
class KmlReader implements Reader {

    /**
     * Read a Feature from a KML Placemark
     * @param options The named parameters
     * <ul>
     *     <li>subFields = A List of fields to include (defaults to Geometry, name, and description)</li>
     * </ul>
     * @param str The KML String
     * @return A Feature
     */
    Feature read(Map options = [:], String str) {
        List subFields = options.get("subFields", ["Geometry", "name", "description"])
        Parser parser = new Parser(new KMLConfiguration())
        Feature feature = new Feature(parser.parse(new StringReader(str)))
        feature.schema.includeFields(subFields, feature.schema.name).feature(feature)
    }
}
