package geoscript.layer.io

import geoscript.feature.Feature
import geoscript.filter.Property
import geoscript.layer.Layer
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil

/**
 * Write a Layer to a GPX document.
 * @author Jared Erickson
 */
class GpxWriter implements Writer {

    /**
     * The GPX version (defaults to 1.1)
     */
    String version = "1.1"

    /**
     * Whether to include attributes (defaults to false)
     */
    boolean includeAttributes = false

    /**
     * The attribute namespace (prefix=url)
     */
    String attributeNamespace = "ogr=http://www.gdal.org/ogr/"

    /**
     * The elevation filter, closure, or value
     */
    def elevation = new Property("ele")

    /**
     * The time elevation filter, closure, or value
     */
    def time = new Property("time")

    /**
     * The name filter, closure, or value
     */
    def name = { Feature f -> f.id }

    /**
     * The description filter, closure, or value
     */
    def description = new Property("desc")

    /**
     * The type filter, closure, or value
     */
    def type = new Property("type")

    /**
     * Write the Layer to the OutputStream
     * @param layer The Layer
     * @param out The OutputStream
     */
    @Override
    void write(Layer layer, OutputStream out) {

        // Set up XML namespaces
        Map namespaces = [
                "": "http://www.topografix.com/GPX/1/${version.equals('1.0') ? '0' : '1'}",
        ]
        if (includeAttributes) {
            String[] parts = attributeNamespace.split("=")
            namespaces[parts[0]] = parts[1]
        }

        // Feature Writer
        geoscript.feature.io.GpxWriter featureWriter = new geoscript.feature.io.GpxWriter(
                version: this.version,
                includeAttributes: this.includeAttributes,
                attributeNamespace: this.attributeNamespace,
                elevation: this.elevation,
                time: this.time,
                name: this.name,
                description: this.description,
                type: this.type
        )

        // Build the XML
        def xml
        def markupBuilder = new StreamingMarkupBuilder()
        xml = markupBuilder.bind { builder ->
            mkp.xmlDeclaration()
            mkp.declareNamespace(namespaces)
            builder.gpx(version: version, creator: "geoscript") {
                layer.eachFeature { Feature f ->
                    featureWriter.write builder, f
                }
            }
        }

        XmlUtil.serialize(xml, out)
    }

    /**
     * Write the Layer to the File
     * @param layer The Layer
     * @param file The File
     */
    @Override
    void write(Layer layer, File file) {
        OutputStream out = new FileOutputStream(file)
        write(layer, out)
        out.close()
    }

    /**
     * Write the Layer to a String
     * @param layer The Layer
     * @return A String
     */
    @Override
    String write(Layer layer) {
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        write(layer, out);
        out.close()
        out.toString()
    }

}
