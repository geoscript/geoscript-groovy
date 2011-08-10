package geoscript.feature.io

import geoscript.feature.Feature
import javax.xml.namespace.QName
import org.geotools.xml.Encoder
import org.geotools.gml2.GMLConfiguration as GML2
import org.geotools.gml3.GMLConfiguration as GML3
import org.geotools.gml3.v3_2.GMLConfiguration as GML32


/**
 * Write a Feature to GML.
 * @author Jared Erickson
 */
class GmlWriter implements Writer {

    /**
     * Write a Feature to a GML String
     * @param feature The Feature
     * @return A GML String
     */
    String write(Feature feature) {
        write(feature, 2, true, false, false, "gsf")
    }

    /**
     * Write the Feature to a GML String
     * @param feature The Feature
     * @param version The version 2, 3, or 3.2
     * @param format Whether to pretty print or not
     * @param bounds Whether to include Feature Bounds or not
     * @param xmldecl Whether to include XML declaration or not
     * @param nsprefix The XML namespace prefix
     * @return A GML String
     */
    String write(Feature feature, double version, boolean format, boolean bounds, boolean xmldecl, String nsprefix) {
        String nsURI = (version == 3.2) ? new GML32().getNamespaceURI() : new GML3().getNamespaceURI()
        String fName = (version < 3.2) ? "_Feature" : "AbstractFeature"
        QName qName = new QName(nsURI, fName)
        def gmlConfig = getGmlConfig(version)
        if (!bounds) {
            gmlConfig.getProperties().add(GML2.NO_FEATURE_BOUNDS)
        }
        Encoder encoder = new Encoder(gmlConfig)
        if (!xmldecl) {
            encoder.omitXMLDeclaration = true
        }
        if (format) {
            encoder.indenting = true
        }
        encoder.namespaces.declarePrefix(nsprefix, feature.schema.uri)

        encoder.encodeAsString(feature.f, qName)
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
