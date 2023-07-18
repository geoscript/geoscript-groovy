package geoscript.layer.io

import geoscript.feature.Feature
import geoscript.feature.Field
import geoscript.feature.Schema
import geoscript.layer.Layer
import geoscript.feature.io.YamlReader as FeatureYamlReader
import org.yaml.snakeyaml.Yaml

/**
 * Read a Layer from a GeoYaml String, File, or InputStream
 * @author Jared Erickson
 */
class YamlReader implements Reader {

    private FeatureYamlReader featureYamlReader = new FeatureYamlReader()

    @Override
    Layer read(InputStream input) {
        Yaml yaml = new Yaml()
        readLayer(yaml.load(input))
    }

    @Override
    Layer read(File file) {
        Layer layer = null
        file.withInputStream { InputStream inputStream ->
            layer = read(inputStream)
        }
        layer
    }

    @Override
    Layer read(String str) {
        Yaml yaml = new Yaml()
        readLayer(yaml.load(str))
    }

    private Layer readLayer(def obj) {
        Layer layer = null
        obj?.features.eachWithIndex { def f, int i ->
            Feature feature = featureYamlReader.readFeature(f)
            if (i == 0) {
                List fields = []
                feature.attributes.each { String name, Object value ->
                    fields.add(new Field(name, value.class.simpleName))
                }
                Schema schema = new Schema("layer", fields)
                layer = new Layer("layer", schema)
            }
            layer.add(feature)
        }
        layer
    }

}
