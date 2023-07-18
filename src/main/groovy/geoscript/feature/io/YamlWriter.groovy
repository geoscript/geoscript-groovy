package geoscript.feature.io

import geoscript.feature.Feature
import geoscript.geom.io.YamlWriter as GeometryYamlWriter
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml

/**
 * Write a Feature to a GeoYaml String
 * @author Jared Erickson
 */
class YamlWriter implements Writer {

    private GeometryYamlWriter geometryYamlWriter = new GeometryYamlWriter()

    @Override
    String write(Feature feature) {
        DumperOptions options = new DumperOptions()
        options.indent = 2
        options.prettyFlow = true
        options.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
        options.explicitStart = true
        Yaml yaml = new Yaml(options)
        Map data = [type: "Feature"]
        data.putAll(build(feature))
        yaml.dump(data)
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
