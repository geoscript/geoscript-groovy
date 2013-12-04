package geoscript.feature.io

import geoscript.feature.Feature
import org.geotools.kml.KML
import org.geotools.kml.KMLConfiguration
import org.geotools.xml.Configuration
import org.geotools.xml.Encoder

/**
 * Write a Feature as a KML Placemark.
 * @author Jared Erickson
 */
class KmlWriter implements Writer {

    /**
     * Write a Feature as a KML Placemark
     * @param options The named parameters
     * <ul>
     *     <li>format = Whether to format the KML or not (default = false)</li>
     *     <li>xmldecl = Whether to include the XML declaration (default = false)</li>
     * </ul>
     * @param feature The Feature
     * @return A KML Placemark String
     */
    String write(Map options = [:], Feature feature) {
        boolean format = options.get("format", false)
        boolean xmldecl = options.get("xmldecl", false)
        Configuration config = new KMLConfiguration()
        Encoder encoder = new Encoder(config)
        encoder.indenting = format
        encoder.omitXMLDeclaration = !xmldecl
        encoder.encodeAsString(feature.f, KML.Placemark)
    }

}
