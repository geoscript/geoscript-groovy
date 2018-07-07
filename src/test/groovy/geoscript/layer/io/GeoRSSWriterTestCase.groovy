package geoscript.layer.io

import geoscript.AssertUtil
import geoscript.feature.Feature
import geoscript.feature.Schema
import geoscript.filter.Property
import geoscript.layer.Layer
import geoscript.workspace.Memory
import geoscript.workspace.Workspace
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

/**
 * The GeoRSSWriter Unit Test
 * @author Jared Erickson
 */
class GeoRSSWriterTestCase {

    private Layer createLayer() {
        Schema schema = new Schema("points", [
                ["geom", "Point"],
                ["name", "string"],
                ["description", "string"],
                ["id", "int"]
        ])
        Workspace workspace = new Memory()
        Layer layer = workspace.create(schema)
        layer.withWriter { writer ->
            writer.add(schema.feature([geom: "POINT (1 1)", name: "Washington", description: "The state of Washington", id: 1], "state.1"))
            writer.add(schema.feature([geom: "POINT (2 2)", name: "Oregon", description: "The state of Oregon", id: 2], "state.2"))
            writer.add(schema.feature([geom: "POINT (3 3)", name: "California", description: "The state of California", id: 3], "state.3"))
        }
        layer
    }

    @Test
    void writeAtomWithSimpleGeometry() {
        GeoRSSWriter writer = new GeoRSSWriter(
                feedType: "atom",
                geometryType: "simple",
                itemDate: "1/22/1975",
                itemTitle: new Property("name"),
                itemDescription: { Feature f ->
                    f['description']
                }
        )
        String actual = writer.write(createLayer())
        String expected = """<?xml version="1.0" encoding="UTF-8"?><feed xmlns:georss="http://www.georss.org/georss" xmlns="http://www.w3.org/2005/Atom">
<title>points</title>
<subtitle>points geom: Point, name: String, description: String, id: Integer</subtitle>
<link>http://geoscript.org/feature</link>
<entry>
<title>Washington</title>
<summary>The state of Washington</summary>
<updated>1/22/1975</updated>
<georss:point>1.0 1.0</georss:point>
</entry>
<entry>
<title>Oregon</title>
<summary>The state of Oregon</summary>
<updated>1/22/1975</updated>
<georss:point>2.0 2.0</georss:point>
</entry>
<entry>
<title>California</title>
<summary>The state of California</summary>
<updated>1/22/1975</updated>
<georss:point>3.0 3.0</georss:point>
</entry>
</feed>
"""
        AssertUtil.assertStringsEqual(expected, actual, trim: true)
    }

    @Test
    void writeAtomWithW3CGeometry() {
        GeoRSSWriter writer = new GeoRSSWriter(
                feedType: "atom",
                geometryType: "w3c",
                itemDate: "1/22/1975",
                itemTitle: new Property("name"),
                itemDescription: { Feature f ->
                    f['description']
                }
        )
        String actual = writer.write(createLayer())
        String expected = """<?xml version="1.0" encoding="UTF-8"?><feed xmlns:geo="http://www.w3.org/2003/01/geo/wgs84_pos#" xmlns="http://www.w3.org/2005/Atom">
<title>points</title>
<subtitle>points geom: Point, name: String, description: String, id: Integer</subtitle>
<link>http://geoscript.org/feature</link>
<entry>
<title>Washington</title>
<summary>The state of Washington</summary>
<updated>1/22/1975</updated>
<geo:Point>
<geo:lat>1.0</geo:lat>
<geo:long>1.0</geo:long>
</geo:Point>
</entry>
<entry>
<title>Oregon</title>
<summary>The state of Oregon</summary>
<updated>1/22/1975</updated>
<geo:Point>
<geo:lat>2.0</geo:lat>
<geo:long>2.0</geo:long>
</geo:Point>
</entry>
<entry>
<title>California</title>
<summary>The state of California</summary>
<updated>1/22/1975</updated>
<geo:Point>
<geo:lat>3.0</geo:lat>
<geo:long>3.0</geo:long>
</geo:Point>
</entry>
</feed>
"""
        AssertUtil.assertStringsEqual(expected, actual, trim: true)
    }

    @Test
    void writeRssWithGmlGeometry() {
        GeoRSSWriter writer = new GeoRSSWriter(
                feedType: "rss",
                geometryType: "gml",
                itemDate: "1/22/1975",
                itemTitle: new Property("name"),
                itemDescription: { Feature f ->
                    f['description']
                }
        )
        String actual = writer.write(createLayer())
        String expected = """<?xml version="1.0" encoding="UTF-8"?><rss xmlns:georss="http://www.georss.org/georss" xmlns:gml="http://www.opengis.net/gml" version="2.0">
<channel>
<title>points</title>
<description>points geom: Point, name: String, description: String, id: Integer</description>
<link>http://geoscript.org/feature</link>
<item>
<pubDate>1/22/1975</pubDate>
<title>Washington</title>
<description>The state of Washington</description>
<georss:where>
<gml:Point>
<gml:pos>1.0 1.0</gml:pos>
</gml:Point>
</georss:where>
</item>
<item>
<pubDate>1/22/1975</pubDate>
<title>Oregon</title>
<description>The state of Oregon</description>
<georss:where>
<gml:Point>
<gml:pos>2.0 2.0</gml:pos>
</gml:Point>
</georss:where>
</item>
<item>
<pubDate>1/22/1975</pubDate>
<title>California</title>
<description>The state of California</description>
<georss:where>
<gml:Point>
<gml:pos>3.0 3.0</gml:pos>
</gml:Point>
</georss:where>
</item>
</channel>
</rss>
"""
        AssertUtil.assertStringsEqual(expected, actual, trim: true)
    }

    @Test
    void readWriteRssWithSimpleGeometry() {
        GeoRSSWriter writer = new GeoRSSWriter(
                feedType: "rss",
                geometryType: "gml",
                itemDate: "1/22/1975",
                itemTitle: new Property("name"),
                itemDescription: { Feature f ->
                    f['description']
                }
        )
        String xml = writer.write(createLayer())
        GeoRSSReader reader = new GeoRSSReader()
        Layer layer = reader.read(xml)
        assertNotNull layer
        assertEquals 3, layer.count
        List features = layer.features
        // 1
        assertEquals("POINT (1 1)", features[0].geom.wkt)
        assertEquals("Washington", features[0]['title'])
        assertEquals("The state of Washington", features[0]['description'])
        assertEquals("1/22/1975", features[0]['pubDate'])
        // 2
        assertEquals("POINT (2 2)", features[1].geom.wkt)
        assertEquals("Oregon", features[1]['title'])
        assertEquals("The state of Oregon", features[1]['description'])
        assertEquals("1/22/1975", features[1]['pubDate'])
        // 3
        assertEquals("POINT (3 3)", features[2].geom.wkt)
        assertEquals("California", features[2]['title'])
        assertEquals("The state of California", features[2]['description'])
        assertEquals("1/22/1975", features[2]['pubDate'])
    }

    @Test
    void writeAtomWithGmlProjectedGeometry() {
        Schema schema = new Schema("points", [
                ["geom", "Point", "EPSG:2927"],
                ["title", "string"],
                ["description", "string"]
        ])
        Workspace workspace = new Memory()
        Layer layer = workspace.create(schema)
        layer.withWriter { writer ->
            writer.add(schema.feature([geom: "POINT (1200378.25 646954.62)", title: "Point", description: "My amazing point"], "point.1"))
        }
        GeoRSSWriter writer = new GeoRSSWriter(
                feedType: "rss",
                geometryType: "gml",
                itemDate: "11/23/2013"
        )
        String actual = writer.write(layer)
        String expected = """<?xml version="1.0" encoding="UTF-8"?><rss xmlns:georss="http://www.georss.org/georss" xmlns:gml="http://www.opengis.net/gml" version="2.0">
<channel>
<title>points</title>
<description>points geom: Point(EPSG:2927), title: String, description: String</description>
<link>http://geoscript.org/feature</link>
<item>
<pubDate>11/23/2013</pubDate>
<title>Point</title>
<description>My amazing point</description>
<georss:where>
<gml:Point srsName="urn:ogc:def:crs:EPSG:2927">
<gml:pos>646954.62 1200378.25</gml:pos>
</gml:Point>
</georss:where>
</item>
</channel>
</rss>"""
        AssertUtil.assertStringsEqual(expected, actual, trim: true)
    }

}
