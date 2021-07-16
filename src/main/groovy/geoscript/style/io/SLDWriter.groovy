package geoscript.style.io

import geoscript.style.Style
import org.geotools.factory.CommonFactoryFinder
import org.geotools.styling.NamedLayer
import org.geotools.xml.styling.SLDTransformer
import org.geotools.styling.StyleFactory
import org.geotools.styling.StyledLayerDescriptor
import org.geotools.styling.UserLayer

/**
 * Write a Style to an SLD document
 * <p><blockquote><pre>
 * def sym = new Fill("#ff00FF") + new Stroke("#ffff00", 0.25")
 * def writer = new SLDWriter()
 * writer.write(sym)
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class SLDWriter implements Writer {

    /**
     * Whether to format the SLD or not
     */
    boolean format = true
   
    /**
     * Write the Style to the OutputStream
     * @param options Optional named parameters
     * <ol>
     *     <li>type = Whether to export UserLayer (default) or NamedLayer </li>
     *     <li>exportDefaultValues = Whether to export default values or not (defaults to false) </li>
     *     <li>indentation = The number of spaces to use when indenting (defaults to 2) </li>
     * </ol>
     * @param style The Style
     * @param out The OutputStream
     */
    void write(Map options = [:], Style style, OutputStream out) {
        StyleFactory sf = CommonFactoryFinder.getStyleFactory(null)
        StyledLayerDescriptor sld = sf.createStyledLayerDescriptor()
        if (options.get("type", "UserLayer").equalsIgnoreCase("UserLayer")) {
            UserLayer userLayer = sf.createUserLayer()
            userLayer.addUserStyle(style.gtStyle)
            sld.addStyledLayer(userLayer)
        } else {
            NamedLayer namedLayer = sf.createNamedLayer()
            namedLayer.addStyle(style.gtStyle)
            sld.addStyledLayer(namedLayer)
        }
        def transformer = new SLDTransformer()
        transformer.exportDefaultValues = options.get("exportDefaultValues", false)
        if (format) {
            transformer.indentation = options.get("indentation", 2)
        }
        transformer.transform(sld, out)
    }

    /**
     * Write the Style to the File
     * @param options Optional named parameters
     * <ol>
     *     <li>exportDefaultValues = Whether to export default values or not (defaults to false) </li>
     *     <li>indentation = The number of spaces to use when indenting (defaults to 2) </li>
     * </ol>
     * @param style The Style
     * @param file The File
     */
    void write(Map options = [:], Style style, File file) {
        FileOutputStream out = new FileOutputStream(file)
        write(options, style, out)
        out.close()
    }

    /**
     * Write the Style to a String
     * @param options Optional named parameters
     * <ol>
     *     <li>exportDefaultValues = Whether to export default values or not (defaults to false) </li>
     *     <li>indentation = The number of spaces to use when indenting (defaults to 2) </li>
     * </ol>
     * @param The Style
     * @return A String
     */
    String write(Map options = [:], Style style) {
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        write(options, style, out);
        out.close()
        return out.toString()
    }
}