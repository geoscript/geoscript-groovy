package geoscript.feature.io

import geoscript.AssertUtil
import geoscript.feature.Feature
import geoscript.feature.Field
import geoscript.feature.Schema
import geoscript.geom.Point
import org.junit.Test

/**
 * The KmlWriter UnitTest
 * @author Jared Erickson
 */
class KmlWriterTestCase {

    @Test void write() {
        Schema schema = new Schema("houses", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])
        Feature feature = new Feature([new Point(111,-47), "House", 12.5], "house1", schema)
        KmlWriter writer = new KmlWriter()
        String expected = """<kml:Placemark xmlns:kml="http://earth.google.com/kml/2.1" id="house1">
<kml:name>House</kml:name>
<kml:Point>
<kml:coordinates>111.0,-47.0</kml:coordinates>
</kml:Point>
</kml:Placemark>"""
        String actual = writer.write(feature)
        AssertUtil.assertStringsEqual(expected, actual, trim: true)
    }

}
