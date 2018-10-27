package geoscript.layer.io

import geoscript.layer.Layer
import net.opengis.wfs.WfsFactory
import javax.xml.namespace.QName
import org.geotools.xml.Encoder
import org.geotools.gml2.GMLConfiguration as GML2
import org.geotools.wfs.v1_0.WFSConfiguration_1_0 as WFS10
import org.geotools.wfs.v1_1.WFSConfiguration as WFS11
import org.geotools.wfs.v2_0.WFSConfiguration as WFS20
import net.opengis.wfs.FeatureCollectionType

/**
 * Write a {@link geoscript.layer.Layer Layer} to a GML InputStream, File, or String.
 * <p><blockquote><pre>
 * def layer = new Shapefile("states.shp")
 * GmlWriter writer = new GmlWriter()
 * String gml = writer.write(layer)
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class GmlWriter implements Writer {

    /**
     * Write the Layer to the OutputStream
     * @param layer The Layer
     * @param out The OutputStream
     */
    void write(Layer layer, OutputStream out) {
       write(layer, out, 2, true, false, false, "gsf")
    }

    /**
     * Write the Layer to the OutputStream
     * @param layer The Layer
     * @param out The OutputStream
     * @param version The version 2, 3, or 3.2
     * @param format Whether to pretty print or not
     * @param bounds Whether to include Feature Bounds or not
     * @param xmldecl Whether to include XML declaration or not
     * @param nsprefix The XML namespace prefix
     */
    void write(Layer layer, OutputStream out, double version, boolean format, boolean bounds, boolean xmldecl, String nsprefix) {

        FeatureCollectionType fct = WfsFactory.eINSTANCE.createFeatureCollectionType()
        fct.feature.add(layer.fs.features)

        String nsURI = (version == 3.2) ? new WFS20().getNamespaceURI() : new WFS11().getNamespaceURI()
        String fName = "FeatureCollection"
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
        encoder.namespaces.declarePrefix(nsprefix, layer.schema.uri)

        encoder.encode(fct, qName, out)
    }

    /**
     * Write the Layer to the File
     * @param layer The Layer
     * @param file The File
     */
    void write(Layer layer, File file) {
        FileOutputStream out = new FileOutputStream(file)
        write(layer, out)
        out.close()
    }

    /**
     * Write the Layer to the File
     * @param layer The Layer
     * @param file The File
     * @param version The version 2, 3, or 3.2
     * @param format Whether to pretty print or not
     * @param bounds Whether to include Feature Bounds or not
     * @param xmldecl Whether to include XML declaration or not
     * @param nsprefix The XML namespace prefix
     */
    void write(Layer layer, File file, double version, boolean format, boolean bounds, boolean xmldecl, String nsprefix) {
        FileOutputStream out = new FileOutputStream(file)
        write(layer, out, version, format, bounds, xmldecl, nsprefix)
        out.close()
    }

    /**
     * Write the Layer to a String
     * @param layer The Layer
     * @return A String
     */
    String write(Layer layer) {
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        write(layer, out);
        out.close()
        return out.toString()
    }

    /**
     * Write the Layer to a String
     * @param layer The Layer
     * @param version The version 2, 3, or 3.2
     * @param format Whether to pretty print or not
     * @param bounds Whether to include Feature Bounds or not
     * @param xmldecl Whether to include XML declaration or not
     * @param nsprefix The XML namespace prefix
     * @return A String
     */
    String write(Layer layer, double version, boolean format, boolean bounds, boolean xmldecl, String nsprefix) {
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        write(layer, out, version, format, bounds, xmldecl, nsprefix);
        out.close()
        return out.toString()
    }

    /**
     * Get the correct GML Configuration based on the version
     * @param version The version number (2, 3, or 3.2)
     * @return  A GeoTools GML Configuration
     */
    private def getGmlConfig(double version) {
        if (version == 2) {
            return new WFS10()
        } else if (version == 3) {
            return new WFS11()
        } else /*if (version == 3.2)*/ {
            return new WFS20()
        }
    }
}
