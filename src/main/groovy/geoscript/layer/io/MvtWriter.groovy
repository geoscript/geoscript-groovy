package geoscript.layer.io

import geoscript.layer.Layer
import org.apache.commons.codec.binary.Base64

/**
 * A Mapnik Vector Tile Writer
 * @author Jared Erickson
 */
class MvtWriter implements Writer {

    /**
     * Write the Layer to the OutputStream
     * @param layer The Layer
     * @param out The OutputStream
     */
    @Override
    void write(Layer layer, OutputStream out) {
        Mvt.write(layer, out)
    }

    /**
     * Write the Layer to the File
     * @param layer The Layer
     * @param file The File
     */
    @Override
    void write(Layer layer, File file) {
        Mvt.write(layer, file)
    }

    /**
     * Write the Layer to a String
     * @param layer The Layer
     * @return A String
     */
    @Override
    String write(Layer layer) {
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        Mvt.write(layer, out)
        new String(Base64.encodeBase64(out.toByteArray()), "UTF-8")
    }
}
