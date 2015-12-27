package geoscript.feature.io

import geoscript.feature.Feature
import geoscript.filter.Expression
import geoscript.filter.Property
import geoscript.geom.Geometry
import geoscript.geom.LineString
import geoscript.geom.MultiLineString
import geoscript.geom.Point
import groovy.xml.StreamingMarkupBuilder

/**
 * Write a Feature to a GPX document
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
     * Write a Feature to a String
     * @param feature The Feature
     * @return A String
     */
    @Override
    String write(Feature feature) {
        // Set up XML namespaces
        Map namespaces = [
                "": "http://www.topografix.com/GPX/1/${version.equals('1.0') ? '0' : '1'}",
        ]
        if (includeAttributes) {
            String[] parts = attributeNamespace.split("=")
            namespaces[parts[0]] = parts[1]
        }

        // Build XML
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
        Geometry g = f.geom
        if (g instanceof Point) {
            Point pt = g as Point
            builder.wpt(lat: pt.y, lon: pt.x) {
                addElement builder, f, name, "name"
                addElement builder, f, description, "desc"
                addElement builder, f, type, "type"
                addElement builder, f, elevation, "ele"
                addElement builder, f, time, "time"
                buildAttributes builder, f
            }
        } else if (g instanceof LineString) {
            LineString line = g as LineString
            builder.rte {
                addElement builder, f, name, "name"
                addElement builder, f, description, "desc"
                addElement builder, f, type, "type"
                buildAttributes builder, f
                line.points.each { Point p ->
                    builder.rtept(lat: p.y, lon: p.x) {
                        addElement builder, f, elevation, "ele"
                        addElement builder, f, time, "time"
                    }
                }
            }
        } else if (g instanceof MultiLineString) {
            MultiLineString multiLine = g as MultiLineString
            builder.trk {
                addElement builder, f, name, "name"
                addElement builder, f, description, "desc"
                addElement builder, f, type, "type"
                buildAttributes builder, f
                multiLine.geometries.each { LineString line ->
                    builder.trkseg {
                        line.points.each { Point p ->
                            builder.trkpt(lat: p.y, lon: p.x) {
                                addElement builder, f, elevation, "ele"
                                addElement builder, f, time, "time"
                            }
                        }
                    }
                }
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
            builder.extensions {
                f.attributes.each { String key, Object value ->
                    if (!f.schema.get(key).isGeometry()) {
                        String prefix = defaultPrefix
                        String name = key
                        builder."${prefix}:${name}" value
                    }
                }
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
     * Add an ele (elevation) element to the XML document
     * @param builder The StreamingMarkupBuilder
     * @param f A Feature
     */
    private void addElement(def builder, Feature f, def template, String element) {
        if (template) {
            String value = getValue(template, f)
            if (value) {
                builder."${element}" value
            }
        }
    }
}