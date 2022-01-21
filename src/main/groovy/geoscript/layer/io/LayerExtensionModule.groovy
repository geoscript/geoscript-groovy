package geoscript.layer.io

import geoscript.layer.Layer
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil

/**
 * A Groovy Extension Module that adds methods to the Layer class.
 * @author Jared Erickson
 */
class LayerExtensionModule {

    /**
     * Write the Layer as GML to an OutputStream
     * @param layer The Layer
     * @param out The OutputStream (defaults to System.out)
     */
    static void toGML(Layer layer, OutputStream out = System.out) {
        GmlWriter gmlWriter = new GmlWriter()
        gmlWriter.write(layer, out)
    }

    /**
     * Write the Layer as GML to a File
     * @param layer The Layer
     * @param file The File
     */
    static void toGMLFile(Layer layer, File file) {
        GmlWriter gmlWriter = new GmlWriter()
        gmlWriter.write(layer, file)
    }

    /**
     * Write the Layer as GML to a String
     * @param layer The Layer
     * @param out A GML String
     */
    static String toGMLString(Layer layer) {
        GmlWriter gmlWriter = new GmlWriter()
        gmlWriter.write(layer)
    }

    /**
     * Write the Layer as GeoJSON to an OutputStream
     * @param layer The Layer
     * @param out The OutputStream (defaults to System.out)
     */
    static void toJSON(Layer layer, OutputStream out = System.out) {
        GeoJSONWriter geoJSONWriter = new GeoJSONWriter()
        geoJSONWriter.write([:], layer, out)
    }

    /**
     * Write the Layer as GeoJSON to an OutputStream
     * @param layer The Layer
     * @param options Optional named parameters:
     * <ol>
     *     <li> decimals = The number of decimals (defaults to 4) </li>
     *     <li> encodeFeatureBounds = Whether to encode Feature Bounds (default is false) </li>
     *     <li> encodeFeatureCollectionBounds = Whether to encode FeatureCollection Bounds (default is false) </li>
     *     <li> encodeFeatureCollectionCRS = Whether to encode FeatureCollection CRS (default is false) </li>
     *     <li> encodeFeatureCRS = Whether to encode Feature CRS (default is false) </li>
     *     <li> encodeNullValues = Whether to encode null values (default is false) </li>
     * </ol>
     * @param out The OutputStream (defaults to System.out)
     */
    static void toJSON(Layer layer, Map options, OutputStream out = System.out) {
        GeoJSONWriter geoJSONWriter = new GeoJSONWriter()
        geoJSONWriter.write(options, layer, out)
    }

    /**
     * Write the Layer as GeoJSON to a File
     * @param layer The Layer
     * @param options Optional named parameters:
     * <ol>
     *     <li> decimals = The number of decimals (defaults to 4) </li>
     *     <li> encodeFeatureBounds = Whether to encode Feature Bounds (default is false) </li>
     *     <li> encodeFeatureCollectionBounds = Whether to encode FeatureCollection Bounds (default is false) </li>
     *     <li> encodeFeatureCollectionCRS = Whether to encode FeatureCollection CRS (default is false) </li>
     *     <li> encodeFeatureCRS = Whether to encode Feature CRS (default is false) </li>
     *     <li> encodeNullValues = Whether to encode null values (default is false) </li>
     * </ol>
     * @param file The File
     */
    static void toJSONFile(Layer layer, Map options = [:], File file) {
        GeoJSONWriter geoJSONWriter = new GeoJSONWriter()
        geoJSONWriter.write(options, layer, file)
    }

    /**
     * Write the Layer as GeoJSON to a String
     * @param layer The Layer
     * @param options Optional named parameters:
     * <ol>
     *     <li> decimals = The number of decimals (defaults to 4) </li>
     *     <li> encodeFeatureBounds = Whether to encode Feature Bounds (default is false) </li>
     *     <li> encodeFeatureCollectionBounds = Whether to encode FeatureCollection Bounds (default is false) </li>
     *     <li> encodeFeatureCollectionCRS = Whether to encode FeatureCollection CRS (default is false) </li>
     *     <li> encodeFeatureCRS = Whether to encode Feature CRS (default is false) </li>
     *     <li> encodeNullValues = Whether to encode null values (default is false) </li>
     * </ol>
     * @param out A GeoJSON String
     */
    static String toJSONString(Layer layer, Map options = [:]) {
        GeoJSONWriter geoJSONWriter = new GeoJSONWriter()
        geoJSONWriter.write(options, layer)
    }

    /**
     * Write the Layer as Geobuf to an OutputStream
     * @param layer The Layer
     * @param out The OutputStream (defaults to System.out)
     */
    static void toGeobuf(Layer layer, OutputStream out = System.out) {
        GeobufWriter writer = new GeobufWriter()
        writer.write(layer, out)
    }

    /**
     * Write the Layer as Geobuf to a File
     * @param layer The Layer
     * @param file The File
     */
    static void toGeobufFile(Layer layer, File file) {
        GeobufWriter writer = new GeobufWriter()
        writer.write(layer, file)
    }

    /**
     * Write the Layer as Geobuf to a String
     * @param layer The Layer
     * @param out A Geobuf Hex String
     */
    static String toGeobufString(Layer layer) {
        GeobufWriter writer = new GeobufWriter()
        writer.write(layer)
    }

    /**
     * Write the Layer as Geobuf to a byte array
     * @param layer The Layer
     * @param out A Geobuf byte array
     */
    static byte[] toGeobufBytes(Layer layer) {
        GeobufWriter writer = new GeobufWriter()
        writer.writeBytes(layer)
    }

    /**
     * Write the Layer as KML to an OutputStream.
     * @param layer The Layer
     * @param out The OutputStream (defaults to System.out)
     * @param nameClosure A Closure that takes a Feature and returns a value
     * used as the Placemark's name.  Default to the Feature's ID
     * @param descriptionClosure A Closure that takes a Feature and returns a value
     * used as the Placemark's description. Defaults to null which means no description
     * is created
     */
    static void toKML(Layer layer, OutputStream out = System.out, Closure nameClosure = {f -> f.id}, Closure descriptionClosure = null) {
        def xml
        def markupBuilder = new StreamingMarkupBuilder()
        def featureWriter = new geoscript.feature.io.KmlWriter()
        xml = markupBuilder.bind { builder ->
            mkp.xmlDeclaration()
            mkp.declareNamespace([kml: "http://www.opengis.net/kml/2.2"])
            kml.kml {
                kml.Document {
                    kml.Folder {
                        kml.name layer.name
                        kml.Schema ("kml:name": layer.name, "kml:id": layer.name) {
                            layer.schema.fields.each {fld ->
                                if (!fld.isGeometry()) {
                                    kml.SimpleField("kml:name": fld.name, "kml:type": fld.typ)
                                }
                            }
                        }
                        layer.eachFeature {f ->
                            featureWriter.write builder, f, namespace: "kml", name: nameClosure, description: descriptionClosure
                        }
                    }
                }
            }
        }

        XmlUtil.serialize(xml, out)
    }

    /**
     * Write the Layer as KML to a String.
     * @param layer The Layer
     * @param nameClosure A Closure that takes a Feature and returns a value
     * used as the Placemark's name.  Default to the Feature's ID
     * @param descriptionClosure A Closure that takes a Feature and returns a value
     * used as the Placemark's description. Defaults to null which means no description
     * is created
     */
    static String toKMLString(Layer layer, Closure nameClosure = {f -> f.id}, Closure descriptionClosure = null) {
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        toKML(layer, out, nameClosure, descriptionClosure)
        out.toString()
    }

    /**
     * Write the Layer as KML to a File.
     * @param layer The Layer
     * @param file The File we are writing
     * @param nameClosure A Closure that takes a Feature and returns a value
     * used as the Placemark's name.  Default to the Feature's ID
     * @param descriptionClosure A Closure that takes a Feature and returns a value
     * used as the Placemark's description. Defaults to null which means no description
     * is created
     */
    static void toKMLFile(Layer layer, File file, Closure nameClosure = {f -> f.id}, Closure descriptionClosure = null) {
        FileOutputStream out = new FileOutputStream(file)
        toKML(layer, out, nameClosure, descriptionClosure)
        out.close()
    }

    /**
     * Write the Layer as a YAML String
     * @param layer The Layer
     * @return The GeoYaml String
     */
    static String toYamlString(Layer layer) {
        YamlWriter yamlWriter = new YamlWriter()
        yamlWriter.write(layer)
    }

    /**
     * Write the Layer as YAML to an OutputStream
     * @param layer The Layer
     * @param out The OutputStream (defaults to System.out)
     */
    static void toYaml(Layer layer, OutputStream out = System.out) {
        YamlWriter yamlWriter = new YamlWriter()
        yamlWriter.write(layer, out)
    }

    /**
     * Write the Layer as YAML to a File
     * @param layer The Layer
     * @param file The File
     */
    static void toYamlFile(Layer layer, File file) {
        YamlWriter yamlWriter = new YamlWriter()
        yamlWriter.write(layer, file)
    }

}
