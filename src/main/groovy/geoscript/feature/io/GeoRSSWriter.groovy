package geoscript.feature.io

import geoscript.feature.Feature
import geoscript.filter.Expression
import groovy.xml.StreamingMarkupBuilder

/**
 * Write a Feature to a GeoRSS String
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
     * Write a Feature to a String
     * @param feature The Feature
     * @return A String
     */
    String write(Feature feature) {
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
        def markupBuilder = new StreamingMarkupBuilder()
        def xml = markupBuilder.bind { builder ->
            mkp.declareNamespace(namespaces)
            write builder, feature
        }

        xml.toString()
    }

    /**
     * Write a Feature to GeoRSS using Groovy Markup Builder
     * @param builder The Groovy MarkupBuilder
     * @param f The Feature
     */
    void write(def builder, Feature f) {
        def geometryWriter = new geoscript.geom.io.GeoRSSWriter(type: geometryType)
        if (feedType.equalsIgnoreCase("rss")) {
            builder.item {
                buildItemId builder, f
                buildItemDate builder, f
                title { mkp.yield(getValue(itemTitle, f)) }
                description { mkp.yield(getValue(itemDescription, f)) }
                geometryWriter.write builder, itemGeometry.call(f), projId: f.schema.proj?.id
                buildAttributes builder, f
            }
        } else {
            builder.entry {
                buildItemId builder, f
                title { mkp.yield(getValue(itemTitle, f)) }
                summary { mkp.yield(getValue(itemDescription, f)) }
                buildItemDate builder, f
                geometryWriter.write builder, itemGeometry.call(f), projId: f.schema.proj?.id
                buildAttributes builder, f
            }
        }

    }

    /**
     * Write attributes to the XML document
     * @param builder The StreamingMarkupBuilder
     * @param f A Feature
     */
    private void buildAttributes(def builder, Feature f) {
        if (includeAttributes) {
            String defaultPrefix = attributeNamespace.split("=")[0]
            f.attributes.each { String key, Object value ->
                String prefix = defaultPrefix
                String name = key
                // Leads to XML namespace problems
                /*if (key.contains("_")) {
                    int i = key.indexOf("_")
                    prefix = key.substring(0,i)
                    name = key.substring(i + 1)
                }*/
                builder { "${prefix}:${name}" value }
            }
        }
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

    /**
     * Add an item id (id or guid) to the XML document
     * @param builder The StreamingMarkupBuilder
     * @param f A Feature
     */
    private void buildItemId(def builder, Feature f) {
        if (itemId) {
            String value = getValue(itemId, f)
            if (feedType.equalsIgnoreCase("rss")) {
                builder.guid value
            } else {
                builder.id value
            }
        }
    }

    /**
     * Add an item date (pubDate or updated) to an XML document
     * @param builder The StreamMarkupBuilder
     * @param f A Feature
     */
    private void buildItemDate(def builder, Feature f) {
        if (itemDate) {
            String value = getValue(itemDate, f)
            if (feedType.equalsIgnoreCase("rss")) {
                builder.pubDate value
            } else {
                builder.updated value
            }
        }
    }
}
