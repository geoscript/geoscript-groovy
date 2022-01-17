package geoscript.feature.io

import geoscript.feature.Feature
import groovy.yaml.YamlBuilder
import geoscript.geom.io.YamlWriter as GeometryYamlWriter

/**
 * Write a Feature to a GeoYaml String
 * @author Jared Erickson
 */
class YamlWriter implements Writer {

    private GeometryYamlWriter geometryYamlWriter = new GeometryYamlWriter()

    @Override
    String write(Feature feature) {
        YamlBuilder builder = new YamlBuilder()
        Map yaml = [type: "Feature"]
        yaml.putAll(build(feature))
        builder(yaml)
        builder.toString()
    }

    Map build(Feature feature) {
        Map attributes = [:]
        feature.attributes.each { String name, Object value ->
            if (!feature.schema.geom.name.equalsIgnoreCase(name)) {
                attributes[name] = value
            }
        }
        [
            properties: attributes,
            geometry: geometryYamlWriter.build(feature.geom)
        ]
    }

}
