package geoscript.feature.io

import geoscript.AssertUtil
import geoscript.feature.Feature
import geoscript.feature.Field
import geoscript.feature.Schema
import geoscript.filter.Property
import geoscript.geom.Point
import groovy.xml.StreamingMarkupBuilder
import org.junit.Test

import static org.junit.Assert.assertEquals

/**
 * The KmlWriter UnitTest
 * @author Jared Erickson
 */
class KmlWriterTestCase {

    @Test void write() {
        Schema schema = new Schema("houses", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])
        Feature feature = new Feature([new Point(111,-47), "House", 12.5], "house1", schema)
        KmlWriter writer = new KmlWriter()
        String expected = """<kml:Placemark xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:kml="http://earth.google.com/kml/2.1" id="house1">
<kml:name>House</kml:name>
<kml:Point>
<kml:coordinates>111.0,-47.0</kml:coordinates>
</kml:Point>
</kml:Placemark>"""
        String actual = writer.write(feature)
        AssertUtil.assertStringsEqual(expected, actual, trim: true)
    }

    @Test void writeUsingMarkupBuilder() {
        Schema schema = new Schema("houses", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])
        Feature feature = new Feature([new Point(111,-47), "House", 12.5], "house1", schema)
        StreamingMarkupBuilder builder = new StreamingMarkupBuilder()
        KmlWriter writer = new KmlWriter()
        // name (property)
        def actual = builder.bind { b ->
            mkp.declareNamespace([kml: "http://www.opengis.net/kml/2.2"])
            writer.write b, feature, namespace: "kml", name: new Property("name"), includeStyle: false, extendedData: false
        } as String
        String expected = "<kml:Placemark xmlns:kml='http://www.opengis.net/kml/2.2'>" +
                "<kml:name>House</kml:name>" +
                "<kml:Point><kml:coordinates>111.0,-47.0</kml:coordinates></kml:Point></kml:Placemark>"
        assertEquals expected, actual
        // extended data, style, name (closure), description (value)
        actual = builder.bind { b ->
            mkp.declareNamespace([kml: "http://www.opengis.net/kml/2.2"])
            writer.write b, feature, namespace: "kml", name: {f -> f.get("name")}, description: "The price is ${feature.get('price')}", color: new geoscript.filter.Color("wheat")
        } as String
        expected = "<kml:Placemark xmlns:kml='http://www.opengis.net/kml/2.2'>" +
                "<kml:name>House</kml:name>" +
                "<kml:description>The price is 12.5</kml:description>" +
                "<kml:Style><kml:IconStyle><kml:color>f5deb3</kml:color></kml:IconStyle></kml:Style>" +
                "<kml:ExtendedData>" +
                "<kml:SchemaData kml:schemaUrl='#houses'>" +
                "<kml:SimpleData kml:name='name'>House</kml:SimpleData>" +
                "<kml:SimpleData kml:name='price'>12.5</kml:SimpleData>" +
                "</kml:SchemaData>" +
                "</kml:ExtendedData>" +
                "<kml:Point><kml:coordinates>111.0,-47.0</kml:coordinates></kml:Point></kml:Placemark>"
        assertEquals expected, actual
    }
}
