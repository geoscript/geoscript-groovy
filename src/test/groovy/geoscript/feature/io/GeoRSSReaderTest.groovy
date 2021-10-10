package geoscript.feature.io

import geoscript.feature.Feature
import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*

/**
 * The GeoRSSReader Unit Test
 * @author Jared Erickson
 */
class GeoRSSReaderTest {

    private void testAtomFeature(Feature feature) {
        assertNotNull(feature)
        assertEquals("house1", feature["title"])
        assertEquals("[geom:POINT (111 -47), name:House, price:12.5]", feature["summary"])
        assertEquals("12/7/2013", feature["updated"])
        assertEquals("POINT (111 -47)", feature.geom.wkt)
    }

    @Test
    void readAtomSimple() {
        GeoRSSReader reader = new GeoRSSReader()
        String str = "<entry xmlns:georss='http://www.georss.org/georss' xmlns='http://www.w3.org/2005/Atom'>" +
                "<title>house1</title>" +
                "<summary>[geom:POINT (111 -47), name:House, price:12.5]</summary>" +
                "<updated>12/7/2013</updated>" +
                "<georss:point>-47.0 111.0</georss:point>" +
                "</entry>"
        Feature feature = reader.read(str)
        testAtomFeature(feature)
    }

    @Test
    void readAtomGml() {
        GeoRSSReader reader = new GeoRSSReader()
        String str = "<entry xmlns:georss='http://www.georss.org/georss' xmlns='http://www.w3.org/2005/Atom' " +
                "xmlns:gml='http://www.opengis.net/gml'>" +
                "<title>house1</title>" +
                "<summary>[geom:POINT (111 -47), name:House, price:12.5]</summary>" +
                "<updated>12/7/2013</updated>" +
                "<georss:where><gml:Point><gml:pos>-47.0 111.0</gml:pos></gml:Point></georss:where>" +
                "</entry>"
        Feature feature = reader.read(str)
        testAtomFeature(feature)
    }

    @Test
    void readAtomW3C() {
        GeoRSSReader reader = new GeoRSSReader()
        String str = "<entry xmlns:geo='http://www.w3.org/2003/01/geo/wgs84_pos#' xmlns='http://www.w3.org/2005/Atom'>" +
                "<title>house1</title>" +
                "<summary>[geom:POINT (111 -47), name:House, price:12.5]</summary>" +
                "<updated>12/7/2013</updated>" +
                "<geo:Point><geo:lat>-47.0</geo:lat><geo:long>111.0</geo:long></geo:Point>" +
                "</entry>"
        Feature feature = reader.read(str)
        testAtomFeature(feature)
    }

    private void testRssFeature(Feature feature) {
        assertNotNull(feature)
        assertEquals("house1", feature["title"])
        assertEquals("[geom:POINT (111 -47), name:House, price:12.5]", feature["description"])
        assertEquals("12/7/2013", feature["pubDate"])
        assertEquals("POINT (111 -47)", feature.geom.wkt)
    }

    @Test void readRssSimple() {
        GeoRSSReader reader = new GeoRSSReader()
        String str = "<item xmlns:georss='http://www.georss.org/georss'>" +
                "<pubDate>12/7/2013</pubDate>" +
                "<title>house1</title>" +
                "<description>[geom:POINT (111 -47), name:House, price:12.5]</description>" +
                "<georss:point>-47.0 111.0</georss:point>" +
                "</item>"
        Feature feature = reader.read(str)
        testRssFeature(feature)
    }

    @Test void readRssGml() {
        GeoRSSReader reader = new GeoRSSReader()
        String str = "<item xmlns:georss='http://www.georss.org/georss' xmlns:gml='http://www.opengis.net/gml'>" +
                "<pubDate>12/7/2013</pubDate>" +
                "<title>house1</title>" +
                "<description>[geom:POINT (111 -47), name:House, price:12.5]</description>" +
                "<georss:where><gml:Point><gml:pos>-47.0 111.0</gml:pos></gml:Point></georss:where>" +
                "</item>"
        Feature feature = reader.read(str)
        testRssFeature(feature)
    }

    @Test void readRssW3C() {
        GeoRSSReader reader = new GeoRSSReader()
        String str = "<item xmlns:geo='http://www.w3.org/2003/01/geo/wgs84_pos#'>" +
                "<pubDate>12/7/2013</pubDate>" +
                "<title>house1</title>" +
                "<description>[geom:POINT (111 -47), name:House, price:12.5]</description>" +
                "<geo:Point><geo:lat>-47.0</geo:lat><geo:long>111.0</geo:long></geo:Point>" +
                "</item>"
        Feature feature = reader.read(str)
        testRssFeature(feature)
    }
}
