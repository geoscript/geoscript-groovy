package geoscript.feature.io

import geoscript.AssertUtil
import geoscript.feature.Feature
import geoscript.feature.Field
import geoscript.feature.Schema
import geoscript.geom.Point
import org.junit.Test

import static org.junit.Assert.assertEquals

/**
 * The GeoRSSWriter Unit Test
 * @author Jared Erickson
 */
class GeoRSSWriterTestCase {

    private Feature getFeature() {
        Schema schema = new Schema("houses", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])
        new Feature([new Point(111,-47), "House", 12.5], "house1", schema)
    }

    @Test void writeDefaults() {
        Feature feature = getFeature()
        GeoRSSWriter writer = new GeoRSSWriter(itemDate: "12/7/2013")
        def xml = new XmlSlurper().parseText(writer.write(feature)).declareNamespace(["georss": 'http://www.georss.org/georss'])
        assertEquals "entry", xml.name()
        assertEquals "http://www.georss.org/georss", xml.lookupNamespace("georss")
        assertEquals "http://www.w3.org/2005/Atom", xml.lookupNamespace("")
        assertEquals "house1", xml.title.text()
        assertEquals "[geom:POINT (111 -47), name:House, price:12.5]", xml.summary.text()
        assertEquals "12/7/2013", xml.updated.text()
        assertEquals "-47.0 111.0", xml["georss:point"].text()
    }

    @Test void writeAtomSimple() {
        Feature feature = getFeature()
        GeoRSSWriter writer = new GeoRSSWriter(feedType: "atom", geometryType: "simple", itemDate: "12/7/2013")
        def xml = new XmlSlurper().parseText(writer.write(feature)).declareNamespace(["georss": 'http://www.georss.org/georss'])
        assertEquals "entry", xml.name()
        assertEquals "http://www.georss.org/georss", xml.lookupNamespace("georss")
        assertEquals "http://www.w3.org/2005/Atom", xml.lookupNamespace("")
        assertEquals "house1", xml.title.text()
        assertEquals "[geom:POINT (111 -47), name:House, price:12.5]", xml.summary.text()
        assertEquals "12/7/2013", xml.updated.text()
        assertEquals "-47.0 111.0", xml["georss:point"].text()
    }

    @Test void writeAtomGml() {
        Feature feature = getFeature()
        GeoRSSWriter writer = new GeoRSSWriter(feedType: "atom", geometryType: "gml", itemDate: "12/7/2013")
        def xml = new XmlSlurper().parseText(writer.write(feature)).declareNamespace([
                "georss": 'http://www.georss.org/georss',
                "gml": 'http://www.opengis.net/gml'
        ])
        assertEquals "entry", xml.name()
        assertEquals "http://www.georss.org/georss", xml.lookupNamespace("georss")
        assertEquals "http://www.opengis.net/gml", xml.lookupNamespace("gml")
        assertEquals "http://www.w3.org/2005/Atom", xml.lookupNamespace("")
        assertEquals "house1", xml.title.text()
        assertEquals "[geom:POINT (111 -47), name:House, price:12.5]", xml.summary.text()
        assertEquals "12/7/2013", xml.updated.text()
        assertEquals "-47.0 111.0", xml["georss:where"].text()
    }

    @Test void writeAtomW3C() {
        Feature feature = getFeature()
        GeoRSSWriter writer = new GeoRSSWriter(feedType: "atom", geometryType: "w3c", itemDate: "12/7/2013")
        String expected = "<entry xmlns:geo='http://www.w3.org/2003/01/geo/wgs84_pos#' xmlns='http://www.w3.org/2005/Atom'>" +
                "<title>house1</title>" +
                "<summary>[geom:POINT (111 -47), name:House, price:12.5]</summary>" +
                "<updated>12/7/2013</updated>" +
                "<geo:Point><geo:lat>-47.0</geo:lat><geo:long>111.0</geo:long></geo:Point>" +
                "</entry>"
        String actual = writer.write(feature)
        AssertUtil.assertStringsEqual expected, actual, removeXmlNS: true
    }

    @Test void writeRssSimple() {
        Feature feature = getFeature()
        GeoRSSWriter writer = new GeoRSSWriter(feedType: "rss", geometryType: "simple", itemDate: "12/7/2013")
        String expected = "<item xmlns:georss='http://www.georss.org/georss'>" +
                "<pubDate>12/7/2013</pubDate>" +
                "<title>house1</title>" +
                "<description>[geom:POINT (111 -47), name:House, price:12.5]</description>" +
                "<georss:point>-47.0 111.0</georss:point>" +
                "</item>"
        String actual = writer.write(feature)
        AssertUtil.assertStringsEqual expected, actual, removeXmlNS: true
    }

    @Test void writeRssGml() {
        Feature feature = getFeature()
        GeoRSSWriter writer = new GeoRSSWriter(feedType: "rss", geometryType: "gml", itemDate: "12/7/2013")
        String expected = "<item xmlns:georss='http://www.georss.org/georss' xmlns:gml='http://www.opengis.net/gml'>" +
                "<pubDate>12/7/2013</pubDate>" +
                "<title>house1</title>" +
                "<description>[geom:POINT (111 -47), name:House, price:12.5]</description>" +
                "<georss:where><gml:Point><gml:pos>-47.0 111.0</gml:pos></gml:Point></georss:where>" +
                "</item>"
        String actual = writer.write(feature)
        AssertUtil.assertStringsEqual expected, actual, removeXmlNS: true
    }

    @Test void writeRssW3C() {
        Feature feature = getFeature()
        GeoRSSWriter writer = new GeoRSSWriter(feedType: "rss", geometryType: "w3c", itemDate: "12/7/2013")
        String expected = "<item xmlns:geo='http://www.w3.org/2003/01/geo/wgs84_pos#'>" +
                "<pubDate>12/7/2013</pubDate>" +
                "<title>house1</title>" +
                "<description>[geom:POINT (111 -47), name:House, price:12.5]</description>" +
                "<geo:Point><geo:lat>-47.0</geo:lat><geo:long>111.0</geo:long></geo:Point>" +
                "</item>"
        String actual = writer.write(feature)
        AssertUtil.assertStringsEqual expected, actual, removeXmlNS: true
    }
}
