package geoscript.layer.io

import geoscript.feature.Feature
import geoscript.filter.Expression
import geoscript.layer.Layer
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil

/**
 * Write a Layer as a GeoRSS document.
 * @author Jared Erickson
 */
class GeoRSSWriter implements Writer {

    /**
     * The feed type (atom or rss)
     */
    String feedType = "atom"

    /**
     * The geometry type (simple, gml, w3c)
     */
    String geometryType = "simple"

    /**
     * Whether to include attributes
     */
    boolean includeAttributes = false

    /**
     * The attribute namespace (prefix=url)
     */
    String attributeNamespace = "ogr=http://www.gdal.org/ogr/"

    /**
     * The feed title (Closure, Expression, or String)
     */
    def feedTitle = { Layer layer -> layer.name }

    /**
     * The feed description (Closure, Expression, or String)
     */
    def feedDescription = { Layer layer -> layer.schema.toString() }

    /**
     * The feed link (Closure, Expression, or String)
     */
    def feedLink = { Layer layer -> layer.schema.uri }

    /**
     * The item title (Closure, Expression, or String)
     */
    def itemTitle = { Feature feature -> feature.schema.has("title") ? feature["title"] : feature.id }

    /**
     * The item id (Closure, Expression, or String)
     */
    def itemId

    /**
     * The item description (Closure, Expression, or String)
     */
    def itemDescription = { Feature feature -> feature.schema.has("description") ? feature["description"] : feature.attributes.toString() }

    /**
     * The item date (Closure, Expression, or String)
     */
    def itemDate = new Date()

    /**
     * The item geometry (Closure, Expression, or String)
     */
    def itemGeometry = { Feature feature -> feature.geom }

    /**
     * Write the Layer as a GeoRSS document to an OutputStream
     * @param layer The Layer
     * @param out The OutputStream
     */
    void write(Layer layer, OutputStream out) {

        // Set up XML namespaces
        Map namespaces = [:]
        if (feedType.equalsIgnoreCase("atom")) {
            namespaces = ["": "http://www.w3.org/2005/Atom"]
        }
        if (geometryType.equalsIgnoreCase("simple") || geometryType.equalsIgnoreCase("gml")) {
            namespaces["georss"] = "http://www.georss.org/georss"
        }
        if (geometryType.equalsIgnoreCase("gml")) {
            namespaces["gml"] = "http://www.opengis.net/gml"
        }
        if (geometryType.equalsIgnoreCase("w3c")) {
            namespaces["geo"] = "http://www.w3.org/2003/01/geo/wgs84_pos#"
        }
        if (includeAttributes) {
            String[] parts = attributeNamespace.split("=")
            namespaces[parts[0]] = parts[1]
        }

        def xml
        def markupBuilder = new StreamingMarkupBuilder()
        def featureWriter = new geoscript.feature.io.GeoRSSWriter(
                feedType: feedType,
                geometryType: geometryType,
                itemTitle: itemTitle,
                itemDate: itemDate,
                itemDescription: itemDescription,
                itemGeometry: itemGeometry,
                itemId: itemId,
                includeAttributes: includeAttributes,
                attributeNamespace: attributeNamespace
        )

        // RSS
        if (feedType.equalsIgnoreCase("rss")) {
            xml = markupBuilder.bind { builder ->
                mkp.xmlDeclaration()
                mkp.declareNamespace(namespaces)
                builder.rss(version: "2.0") {
                    builder.channel {
                        title { mkp.yield(getValue(feedTitle, layer)) }
                        description { mkp.yield(getValue(feedDescription, layer)) }
                        link { mkp.yield(getValue(feedLink, layer)) }
                        layer.eachFeature { Feature f ->
                            featureWriter.write builder, f
                        }
                    }
                }
            }
        }
        // ATOM
        else {
            xml = markupBuilder.bind { builder ->
                mkp.xmlDeclaration()
                mkp.declareNamespace(namespaces)
                builder.feed {
                    title { mkp.yield(getValue(feedTitle, layer)) }
                    subtitle { mkp.yield(getValue(feedDescription, layer)) }
                    link { mkp.yield(getValue(feedLink, layer)) }
                    layer.eachFeature { f ->
                        featureWriter.write builder, f
                    }
                }
            }
        }

        XmlUtil.serialize(xml, out)
    }

    /**
     * Write the Layer as a GeoRSS document to a File
     * @param layer The Layer
     * @param file The File
     */
    void write(Layer layer, File file) {
        OutputStream out = new FileOutputStream(file)
        write(layer, out)
        out.close()
    }

    /**
     * Write the Layer as a GeoRSS document to a String
     * @param layer The Layer
     * @return A GeoRSS String
     */
    String write(Layer layer) {
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        write(layer, out);
        out.close()
        out.toString()
    }

    /**
     * Get a String value from a Closure, Expression or value
     * @param template The Closure, Expression, or value
     * @param obj The context Object (Layer, Feature)
     * @return A value
     */
    private String getValue(Object template, Object obj) {
        if (template instanceof Closure) {
            (template as Closure).call(obj)
        } else if (template instanceof Expression) {
            (template as Expression).evaluate(obj)
        } else {
            template as String
        }
    }
}
