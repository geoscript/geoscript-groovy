package geoscript.layer.io

import geoscript.layer.Layer

/**
 * Write a {@link geoscript.layer.Layer Layer} to an InputStream, File, or String.
 * @author Jared Erickson
 */
interface Writer {

    /**
     * Write the Layer to the OutputStream
     * @param layer The Layer
     * @param out The OutputStream
     */
    void write(Layer layer, OutputStream out)

    /**
     * Write the Layer to the File
     * @param layer The Layer
     * @param file The File
     */
    void write(Layer layer, File file)

    /**
     * Write the Layer to a String
     * @param layer The Layer
     * @return A String
     */
    String write(Layer layer)

}
