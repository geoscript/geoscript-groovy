package geoscript.feature.io

import geoscript.feature.Feature
import geoscript.filter.Expression
import org.geotools.kml.KML
import org.geotools.kml.KMLConfiguration
import org.geotools.xsd.Configuration
import org.geotools.xsd.Encoder

/**
 * Write a Feature as a KML Placemark.
 * @author Jared Erickson
 */
class KmlWriter implements Writer {

    /**
     * Write a Feature as a KML Placemark
     * @param options The named parameters
     * <ul>
     *     <li>format = Whether to format the KML or not (default = false)</li>
     *     <li>xmldecl = Whether to include the XML declaration (default = false)</li>
     * </ul>
     * @param feature The Feature
     * @return A KML Placemark String
     */
    String write(Map options = [:], Feature feature) {
        boolean format = options.get("format", false)
        boolean xmldecl = options.get("xmldecl", false)
        Configuration config = new KMLConfiguration()
        Encoder encoder = new Encoder(config)
        encoder.indenting = format
        encoder.omitXMLDeclaration = !xmldecl
        encoder.encodeAsString(feature.f, KML.Placemark)
    }

    /**
     * Write a Feature to KML using a Groovy Markup Builder
     * @param options The named parameters
     * <ul>
     *      <li>namespace = The KML namespace prefix (defaults to blank)</li>
     *      <li>name = The name value can be a string value, an Expression, or a Closure</li>
     *      <li>description = The description value can be a string value, an Expression or a Closure</li>
     *      <li>extendedData = Whether to include all attributes as ExtentData</li>
     *      <li>includeStyle = Whether to include a simple style element or not</li>
     *      <li>color = The color of the style element</li>
     * </ul>
     * @param builder The Groovy MarkupBuilder
     * @param feature The Feature
     */
    void write(Map options = [:], def builder, Feature feature) {
        String namespace = options.get("namespace","")
        String ns = namespace.isEmpty() ? "" : "${namespace}:"
        def nameValue = options.get("name", {Feature f -> f.id})
        def descriptionValue = options.get("description")
        boolean extendedData = options.get("extendedData", true)
        boolean includeStyle = options.get("includeStyle", true)
        String color = geoscript.filter.Color.toHex(options.get("color","#ff0000ff")).replace("#","")
        String geometryType = feature.schema.geom.typ.toLowerCase()
        def geometryWriter = new geoscript.geom.io.KmlWriter()
        builder."${ns}Placemark" {
            builder."${ns}name" { mkp.yield(getValue(nameValue, feature)) }
            if (descriptionValue != null) {
                builder."${ns}description" { mkp.yield(getValue(descriptionValue, feature)) }
            }
            if (includeStyle) {
                builder."${ns}Style" {
                    if (geometryType.endsWith("point")) {
                        builder."${ns}IconStyle" {
                            builder."${ns}color"("${color}")
                        }
                    } else {
                        builder."${ns}LineStyle" {
                            builder."${ns}color"("${color}")
                        }
                        if (geometryType.endsWith("Polygon")) {
                            builder."${ns}PolygonStyle" {
                                builder."${ns}Fill"("0")
                            }
                        }
                    }
                }
            }
            if (extendedData) {
                builder."${ns}ExtendedData" {
                    builder."${ns}SchemaData" ("${ns}schemaUrl": "#${feature.schema.name}") {
                        feature.schema.fields.each {fld ->
                            if (!fld.isGeometry()) {
                                builder."${ns}SimpleData" ("${ns}name": fld.name) { mkp.yield(feature.get(fld.name)) }
                            }
                        }
                    }
                }
            }
            geometryWriter.write builder, feature.geom, namespace: namespace
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
}