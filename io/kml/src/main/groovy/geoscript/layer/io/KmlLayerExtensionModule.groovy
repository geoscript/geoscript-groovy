package geoscript.layer.io

import geoscript.layer.Layer
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil

/**
 * A Groovy Extension Module that adds methods to the Layer class.
 * @author Jared Erickson
 */
class KmlLayerExtensionModule {

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

}
