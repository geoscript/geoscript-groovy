package geoscript.feature.io

import geoscript.geom.Geometry
import geoscript.geom.io.YamlReader as GeometryYamlReader
import geoscript.feature.Feature
import org.yaml.snakeyaml.Yaml

/**
 * Read a Feature from a GeoYaml String
 * @author Jared Erickson
 */
class YamlReader implements Reader {

    private GeometryYamlReader geometryYamlReader = new GeometryYamlReader()

    @Override
    Feature read(String str) {
        Yaml yaml = new Yaml()
        def obj = yaml.load(str)
        readFeature(obj)
    }

    Feature readFeature(def obj) {
        Map attributes = obj?.properties ?: [:]
        Geometry geometry = geometryYamlReader.readGeometry(obj?.geometry)
        attributes["geom"] = geometry
        new Feature(attributes, "feature.1")
    }

}
