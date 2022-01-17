package geoscript.layer.io

import geoscript.feature.Feature
import geoscript.layer.Layer
import geoscript.feature.io.YamlWriter as FeatureYamlWriter
import groovy.yaml.YamlBuilder

/**
 * Write a Layer to a GeoYaml String, File, or OutputStream
 * @author Jared Erickson
 */
class YamlWriter implements Writer {

    private FeatureYamlWriter featureYamlWriter = new FeatureYamlWriter()

    @Override
    void write(Layer layer, OutputStream out) {
        out.withWriter { java.io.Writer writer ->
            writer.write(write(layer))
        }
    }

    @Override
    void write(Layer layer, File file) {
        file.text = write(layer)
    }

    @Override
    String write(Layer layer) {
        YamlBuilder builder = new YamlBuilder()
        Map yaml = [
                type: "FeatureCollection",
                features: layer.collectFromFeature { Feature feature ->
                    featureYamlWriter.build(feature)
                }
        ]
        builder(yaml)
        builder.toString()
    }

}
