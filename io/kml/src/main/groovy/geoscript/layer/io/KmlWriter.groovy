package geoscript.layer.io

import geoscript.layer.Layer
import org.geotools.kml.KML
import org.geotools.kml.KMLConfiguration
import org.geotools.xml.Encoder

/**
 * Write a {@link geoscript.layer.Layer Layer}  to a KML InputStream, File, or String.
 * @author Jared Erickson
 */
class KmlWriter implements Writer {

    /**
     * Write the Layer to the KML OutputStream
     * @param layer The Layer
     * @param out The KML OutputStream
     */
    void write(Layer layer, OutputStream out) {
        Encoder encoder = new Encoder(new KMLConfiguration());
        encoder.indenting = true
        encoder.omitXMLDeclaration = true
        encoder.encode(layer.fs.features, KML.kml, out);
    }

    /**
     * Write the Layer to the KML File
     * @param layer The Layer
     * @param file The KML File
     */
    void write(Layer layer, File file) {
        FileOutputStream out = new FileOutputStream(file)
        write(layer, out)
        out.close()
    }

    /**
     * Write the Layer to a KML String
     * @param layer The Layer
     * @return A KML String
     */
    String write(Layer layer) {
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        write(layer, out);
        out.close()
        return out.toString()
    }
}