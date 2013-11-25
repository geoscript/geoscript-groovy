package geoscript.layer.io

import com.vividsolutions.jts.geom.Coordinate
import geoscript.feature.Feature
import geoscript.filter.Expression
import geoscript.geom.Geometry
import geoscript.geom.LineString
import geoscript.geom.Point
import geoscript.geom.Polygon
import geoscript.layer.Layer
import geoscript.proj.Projection
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

        // RSS
        if (feedType.equalsIgnoreCase("rss")) {
            xml = markupBuilder.bind { builder ->
                mkp.xmlDeclaration()
                mkp.declareNamespace(namespaces)
                rss(version: "2.0") {
                    channel {
                        title { mkp.yield(getValue(feedTitle, layer)) }
                        description { mkp.yield(getValue(feedDescription, layer)) }
                        link { mkp.yield(getValue(feedLink, layer)) }
                        layer.eachFeature { Feature f ->
                            item {
                                buildItemId builder, f
                                buildItemDate builder, f
                                title { mkp.yield(getValue(itemTitle, f)) }
                                description { mkp.yield(getValue(itemDescription, f)) }
                                buildGeometry builder, f
                                buildAttributes builder, f
                            }
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
                feed {
                    title { mkp.yield(getValue(feedTitle, layer)) }
                    subtitle { mkp.yield(getValue(feedDescription, layer)) }
                    link { mkp.yield(getValue(feedLink, layer)) }
                    layer.eachFeature { f ->
                        entry {
                            buildItemId builder, f
                            title { mkp.yield(getValue(itemTitle, f)) }
                            summary { mkp.yield(getValue(itemDescription, f)) }
                            buildItemDate builder, f
                            buildGeometry builder, f
                            buildAttributes builder, f
                        }
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

    /**
     * Add a geometry element to the XML document
     * @param builder The StreamingMarkupBuilder
     * @param f A Feature
     */
    private void buildGeometry(def builder, Feature f) {
        Projection proj = f.schema.proj
        Geometry geom = itemGeometry.call(f)
        if (geometryType.equalsIgnoreCase("simple")) {
            if (geom instanceof Point) {
                Point pt = geom as Point
                builder.georss.point "${pt.y} ${pt.x}"
            } else if (geom instanceof LineString) {
                LineString line = geom as LineString
                builder.georss.line "${getCoordinatesAsString(line.coordinates)}"
            } else if (geom instanceof Polygon) {
                Polygon poly = geom as Polygon
                builder.georss.polygon "${getCoordinatesAsString(poly.exteriorRing.coordinates)}"
            }
        } else if (geometryType.equalsIgnoreCase("gml")) {
            if (geom instanceof Point) {
                Point pt = geom as Point
                builder.georss.where {
                    gml.Point(proj && !proj.id.equals("4326") ? [srsName: "urn:ogc:def:crs:${proj.id}"] : [:]) {
                        gml.pos "${pt.y} ${pt.x}"
                    }
                }
            } else if (geom instanceof LineString) {
                LineString line = geom as LineString
                builder.georss.where {
                    gml.LineString(proj && !proj.id.equals("4326") ? [srsName: "urn:ogc:def:crs:${proj.id}"] : [:]) {
                        gml.posList "${getCoordinatesAsString(line.coordinates)}"
                    }
                }
            } else if (geom instanceof Polygon) {
                Polygon poly = geom as Polygon
                builder.georss.where {
                    gml.Polygon(proj && !proj.id.equals("4326") ? [srsName: "urn:ogc:def:crs:${proj.id}"] : [:]) {
                        gml.LinearRing {
                            gml.posList "${getCoordinatesAsString(poly.exteriorRing.coordinates)}"
                        }
                    }
                }
            }
        } else if (geometryType.equalsIgnoreCase("w3c")) {
            if (geom instanceof Point) {
                Point pt = geom as Point
                builder.geo.Point {
                    geo.lat pt.y
                    geo.long pt.x
                }
            }
        }
    }

    /**
     * Write an Array of Coordinates to a GML String
     * @param coords The Array of Coordinates
     * @return A GML String (x1,y1 x2,y2)
     */
    private String getCoordinatesAsString(Coordinate[] coords) {
        coords.collect { c -> "${c.y} ${c.x}" }.join(" ")
    }
}
