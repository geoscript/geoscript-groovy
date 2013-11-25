package geoscript.layer.io

import geoscript.feature.Feature
import geoscript.geom.Point
import geoscript.geom.Polygon
import geoscript.layer.Layer
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.*

/**
 * The GeoRSSReader Unit Test
 * @author Jared Erickson
 */
class GeoRSSReaderTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Test
    void readRssWithGml() {
        String xml = """<?xml version="1.0" encoding="UTF-8"?>
  <rss version="2.0"
       xmlns:georss="http://www.georss.org/georss"
       xmlns:gml="http://www.opengis.net/gml">
    <channel>
    <link>http://maps.google.com</link>
    <title>Cambridge Neighborhoods</title>
    <description>One guy's view of Cambridge, MA</description>
    <item>
      <guid isPermaLink="false">00000111c36421c1321d3</guid>
      <pubDate>Thu, 05 Apr 2007 20:16:31 +0000</pubDate>
      <title>Central Square</title>
      <description>The heart and soul of the "old" Cambridge. Depending on where you
               stand, you can feel like you're in the 1970s or 2020.</description>
      <author>rajrsingh</author>
      <georss:where>
        <gml:Polygon>
          <gml:exterior>
            <gml:LinearRing>
              <gml:posList>
                -71.106216 42.366661
                -71.105576 42.367104
                -71.104378 42.367134
                -71.103729 42.366249
                -71.098793 42.363331
                -71.101028 42.362541
                -71.106865 42.366123
                -71.106216 42.366661
              </gml:posList>
            </gml:LinearRing>
          </gml:exterior>
        </gml:Polygon>
      </georss:where>
    </item>
   </channel>
 </rss>"""
        GeoRSSReader reader = new GeoRSSReader()
        Layer layer = reader.read(xml)
        assertNotNull layer
        assertEquals 1, layer.count
        Feature feature = layer.first()
        assertNotNull feature
        assertNotNull feature.geom
        assertTrue feature.geom instanceof Polygon
    }

    @Test
    void readAtomWithRss() {
        String xml = """<?xml version="1.0" encoding="utf-8"?>
 <feed xmlns="http://www.w3.org/2005/Atom"
       xmlns:georss="http://www.georss.org/georss">
   <title>Earthquakes</title>
   <subtitle>International earthquake observation labs</subtitle>
   <link href="http://example.org/"/>
   <updated>2005-12-13T18:30:02Z</updated>
   <author>
      <name>Dr. Thaddeus Remor</name>
      <email>tremor@quakelab.edu</email>
   </author>
   <id>urn:uuid:60a76c80-d399-11d9-b93C-0003939e0af6</id>
   <entry>
      <title>M 3.2, Mona Passage</title>
      <link href="http://example.org/2005/09/09/atom01"/>
      <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>
      <updated>2005-08-17T07:02:32Z</updated>
      <summary>We just had a big one.</summary>
      <georss:point>45.256 -71.92</georss:point>
   </entry>
 </feed>"""
        GeoRSSReader reader = new GeoRSSReader()
        Layer layer = reader.read(xml)
        assertNotNull layer
        assertEquals 1, layer.count
        Feature feature = layer.first()
        assertNotNull feature
        assertNotNull feature.geom
        assertTrue feature.geom instanceof Point
    }

    @Test
    void readAtomWithSimpleBoxGeometry() {
        String xml = """<?xml version="1.0" encoding="utf-8"?>
 <feed xmlns="http://www.w3.org/2005/Atom"
       xmlns:georss="http://www.georss.org/georss">
   <title>Earthquakes</title>
   <subtitle>International earthquake observation labs</subtitle>
   <link href="http://example.org/"/>
   <updated>2005-12-13T18:30:02Z</updated>
   <author>
      <name>Dr. Thaddeus Remor</name>
      <email>tremor@quakelab.edu</email>
   </author>
   <id>urn:uuid:60a76c80-d399-11d9-b93C-0003939e0af6</id>
   <entry>
      <title>M 3.2, Mona Passage</title>
      <link href="http://example.org/2005/09/09/atom01"/>
      <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>
      <updated>2005-08-17T07:02:32Z</updated>
      <summary>We just had a big one.</summary>
      <georss:box>42.943 -71.032 43.039 -69.856</georss:box>
   </entry>
 </feed>"""
        GeoRSSReader reader = new GeoRSSReader()
        Layer layer = reader.read(xml)
        assertNotNull layer
        assertEquals 1, layer.count
        Feature feature = layer.first()
        assertNotNull feature
        assertNotNull feature.geom
        assertTrue feature.geom instanceof Polygon
    }

    @Test
    void readGeoRSSWithW3c() {
        String xml = """<?xml version="1.0"?>
 <?xml-stylesheet href="/eqcenter/catalogs/rssxsl.php?feed=eqs7day-M5.xml" type="text/xsl"
                  media="screen"?>
 <rss version="2.0"
      xmlns:geo="http://www.w3.org/2003/01/geo/wgs84_pos#"
      xmlns:dc="http://purl.org/dc/elements/1.1/">
  <channel>
     <title>USGS M5+ Earthquakes</title>
     <description>Real-time, worldwide earthquake list for the past 7 days</description>
     <link>http://earthquake.usgs.gov/eqcenter/</link>
     <dc:publisher>U.S. Geological Survey</dc:publisher>
     <pubDate>Thu, 27 Dec 2007 23:56:15 PST</pubDate>
     <item>
       <pubDate>Fri, 28 Dec 2007 05:24:17 GMT</pubDate>
       <title>M 5.3, northern Sumatra, Indonesia</title>
       <description>December 28, 2007 05:24:17 GMT</description>
       <link>http://earthquake.usgs.gov/eqcenter/recenteqsww/Quakes/us2007llai.php</link>
       <geo:lat>5.5319</geo:lat>
       <geo:long>95.8972</geo:long>
     </item>
   </channel>
 </rss>"""
        GeoRSSReader reader = new GeoRSSReader()
        Layer layer = reader.read(xml)
        assertNotNull layer
        assertEquals 1, layer.count
        Feature feature = layer.first()
        assertNotNull feature
        assertNotNull feature.geom
        assertTrue feature.geom instanceof Point
    }

    @Test
    void readComplicatedRssSchema() {
        String xml = """<rss version="2.0"
xmlns:geo="http://www.w3.org/2003/01/geo/wgs84_pos#"
xmlns:dc="http://purl.org/dc/elements/1.1/">
    <channel>
         <title>USGS M5+ Earthquakes</title>
         <description>Real-time, worldwide earthquake list for the past 7 days</description>
         <link>http://earthquake.usgs.gov/eqcenter/</link>
         <dc:publisher>U.S. Geological Survey</dc:publisher>
         <pubDate>Thu, 27 Dec 2007 23:56:15 PST</pubDate>
         <item>
            <title>My tile</title>
            <link>http://www.mylink.org</link>
            <description>Cool descriprion !</description>
            <pubDate>Wed, 11 Jul 2007 15:39:21 GMT</pubDate>
            <guid>http://www.mylink.org/2007/07/11</guid>
            <category>Computer Science</category>
            <category>Open Source Software</category>
            <category>GIS</category>
            <georss:point>49 2</georss:point>
            <myns:name type="my_type">My Name</myns:name>
            <myns:complexcontent>
                <myns:subelement>Subelement</myns:subelement>
            </myns:complexcontent>
        </item>
    </channel>
 </rss>
    """
        GeoRSSReader reader = new GeoRSSReader()
        Layer layer = reader.read(xml)
        assertNotNull layer
        assertEquals 1, layer.count
        Feature feature = layer.first()
        assertNotNull feature
        assertNotNull feature.geom
        assertTrue feature.geom instanceof Point
        assertEquals("My tile", feature["title"])
        assertEquals("http://www.mylink.org", feature["link"])
        assertEquals("Cool descriprion !", feature["description"])
        assertEquals("Wed, 11 Jul 2007 15:39:21 GMT", feature["pubDate"])
        assertEquals("http://www.mylink.org/2007/07/11", feature["guid"])
        assertEquals("Computer Science", feature["category"])
        assertEquals("Open Source Software", feature["category1"])
        assertEquals("GIS", feature["category2"])
        assertEquals("My Name", feature["myns_name"])
        assertEquals("my_type", feature["myns_name_type"])
        assertEquals("<myns:subelement>Subelement</myns:subelement>", feature["myns_complexcontent"])
    }

    @Test
    void readComplicatedAtomSchema() {
        String xml = """<?xml version="1.0" encoding="utf-8"?>
 <feed xmlns="http://www.w3.org/2005/Atom"
       xmlns:georss="http://www.georss.org/georss">
   <title>Earthquakes</title>
   <subtitle>International earthquake observation labs</subtitle>
   <entry>
      <title>My tile</title>
        <link>http://www.mylink.org</link>
        <description>Cool descriprion !</description>
        <pubDate>Wed, 11 Jul 2007 15:39:21 GMT</pubDate>
        <guid>http://www.mylink.org/2007/07/11</guid>
        <category>Computer Science</category>
        <category>Open Source Software</category>
        <category>GIS</category>
        <georss:point>49 2</georss:point>
        <myns:name type="my_type">My Name</myns:name>
        <myns:complexcontent>
            <myns:subelement>Subelement</myns:subelement>
        </myns:complexcontent>
   </entry>
 </feed>"""
        GeoRSSReader reader = new GeoRSSReader()
        Layer layer = reader.read(xml)
        assertNotNull layer
        assertEquals 1, layer.count
        Feature feature = layer.first()
        assertNotNull feature
        assertNotNull feature.geom
        assertTrue feature.geom instanceof Point
        assertEquals("My tile", feature["title"])
        assertEquals("http://www.mylink.org", feature["link"])
        assertEquals("Cool descriprion !", feature["description"])
        assertEquals("Wed, 11 Jul 2007 15:39:21 GMT", feature["pubDate"])
        assertEquals("http://www.mylink.org/2007/07/11", feature["guid"])
        assertEquals("Computer Science", feature["category"])
        assertEquals("Open Source Software", feature["category1"])
        assertEquals("GIS", feature["category2"])
        assertEquals("My Name", feature["myns_name"])
        assertEquals("my_type", feature["myns_name_type"])
        assertEquals("<myns:subelement>Subelement</myns:subelement>", feature["myns_complexcontent"])
    }

    @Test
    void readUsgsEarthquakes() {
        String xml = """<?xml version="1.0" encoding="UTF-8"?>
<feed xmlns="http://www.w3.org/2005/Atom" xmlns:georss="http://www.georss.org/georss"><title>USGS Significant Earthquakes, Past Month</title><updated>2013-11-24T23:40:01Z</updated><author><name>U.S. Geological Survey</name><uri>http://earthquake.usgs.gov/</uri></author><id>http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/significant_month.atom</id><link rel="self" href="http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/significant_month.atom"/><icon>http://earthquake.usgs.gov/favicon.ico</icon>
<entry>
<id>urn:earthquake-usgs-gov:us:b000l51g</id>
<title>M 6.5 - Fiji region</title>
<updated>2013-11-24T03:56:19.000Z</updated>
<link rel="alternate" type="text/html" href="http://earthquake.usgs.gov/earthquakes/eventpage/usb000l51g"/>
<link rel="alternate" type="application/cap+xml" href="http://earthquake.usgs.gov/earthquakes/eventpage/usb000l51g.cap"/>
<summary type="html"><![CDATA[<p class="quicksummary"><a href="http://earthquake.usgs.gov/earthquakes/eventpage/usb000l51g#pager" title="PAGER estimated impact alert level" class="pager-green">PAGER - <strong class="roman">GREEN</strong></a> <a href="http://earthquake.usgs.gov/earthquakes/eventpage/usb000l51g#shakemap" title="ShakeMap maximum estimated intensity" class="mmi-IV">ShakeMap - <strong class="roman">IV</strong></a> <a href="http://earthquake.usgs.gov/earthquakes/eventpage/usb000l51g#dyfi" class="mmi-III" title="Did You Feel It? maximum reported intensity (3 reports)">DYFI? - <strong class="roman">III</strong></a></p><dl><dt>Time</dt><dd>2013-11-23 07:48:32 UTC</dd><dd>2013-11-22 19:48:32 -12:00 at epicenter</dd><dt>Location</dt><dd>17.097&deg;S 176.562&deg;W</dd><dt>Depth</dt><dd>377.07 km (234.30 mi)</dd></dl>]]></summary>
<georss:point>-17.0971 -176.5618</georss:point>
<georss:elev>-377070</georss:elev>
<category label="Age" term="Past Week"/>
<category label="Magnitude" term="Magnitude 6"/>
</entry>
<entry>
<id>urn:earthquake-usgs-gov:us:b000l0gq</id>
<title>M 7.7 - Scotia Sea</title>
<updated>2013-11-24T10:26:04.821Z</updated>
<link rel="alternate" type="text/html" href="http://earthquake.usgs.gov/earthquakes/eventpage/usb000l0gq"/>
<link rel="alternate" type="application/cap+xml" href="http://earthquake.usgs.gov/earthquakes/eventpage/usb000l0gq.cap"/>
<summary type="html"><![CDATA[<p class="quicksummary"><a href="http://earthquake.usgs.gov/earthquakes/eventpage/usb000l0gq#pager" title="PAGER estimated impact alert level" class="pager-green">PAGER - <strong class="roman">GREEN</strong></a> <a href="http://earthquake.usgs.gov/earthquakes/eventpage/usb000l0gq#shakemap" title="ShakeMap maximum estimated intensity" class="mmi-VII">ShakeMap - <strong class="roman">VII</strong></a> <a class="tsunamilogo" href="http://www.tsunami.gov/" title="Tsunami Warning Center"> <img src="http://earthquake.usgs.gov/earthquakes/feed/v1.0/images/tsunami-wave-warning.jpg" alt="Tsunami Warning Center"/></a></p><dl><dt>Time</dt><dd>2013-11-17 09:04:55 UTC</dd><dd>2013-11-17 06:04:55 -03:00 at epicenter</dd><dt>Location</dt><dd>60.274&deg;S 46.401&deg;W</dd><dt>Depth</dt><dd>10.00 km (6.21 mi)</dd></dl>]]></summary>
<georss:point>-60.2739 -46.4012</georss:point>
<georss:elev>-10000</georss:elev>
<category label="Age" term="Past Month"/>
<category label="Magnitude" term="Magnitude 7"/>
</entry>
</feed>"""
        GeoRSSReader reader = new GeoRSSReader()
        Layer layer = reader.read(xml)
        assertNotNull layer
        assertEquals 2, layer.count
        List features = layer.features
        // 1
        assertEquals("urn:earthquake-usgs-gov:us:b000l51g", features[0]["id"])
        assertEquals("M 6.5 - Fiji region", features[0]["title"])
        assertEquals("2013-11-24T03:56:19.000Z", features[0]["updated"])
        assertEquals("alternate", features[0]["link_rel"])
        assertEquals("text/html", features[0]["link_type"])
        assertEquals("http://earthquake.usgs.gov/earthquakes/eventpage/usb000l51g", features[0]["link_href"])
        assertEquals("alternate", features[0]["link_rel1"])
        assertEquals("application/cap+xml", features[0]["link_type1"])
        assertEquals("http://earthquake.usgs.gov/earthquakes/eventpage/usb000l51g.cap", features[0]["link_href1"])
        assertEquals("POINT (-176.5618 -17.0971)", features[0].geom.wkt)
        assertEquals("-377070", features[0]["georss_elev"])
        assertEquals("Age", features[0]["category_label"])
        assertEquals("Past Week", features[0]["category_term"])
        assertEquals("Magnitude", features[0]["category_label1"])
        assertEquals("Magnitude 6", features[0]["category_term1"])
        assertTrue(features[0]["summary"].toString().startsWith("<p class=\"quicksummary\">"))
        assertTrue(features[0]["summary"].toString().endsWith("<dd>377.07 km (234.30 mi)</dd></dl>"))
        // 2
        assertEquals("urn:earthquake-usgs-gov:us:b000l0gq", features[1]["id"])
        assertEquals("M 7.7 - Scotia Sea", features[1]["title"])
        assertEquals("2013-11-24T10:26:04.821Z", features[1]["updated"])
        assertEquals("alternate", features[1]["link_rel"])
        assertEquals("text/html", features[1]["link_type"])
        assertEquals("http://earthquake.usgs.gov/earthquakes/eventpage/usb000l0gq", features[1]["link_href"])
        assertEquals("alternate", features[1]["link_rel1"])
        assertEquals("application/cap+xml", features[1]["link_type1"])
        assertEquals("http://earthquake.usgs.gov/earthquakes/eventpage/usb000l0gq.cap", features[1]["link_href1"])
        assertEquals("POINT (-46.4012 -60.2739)", features[1].geom.wkt)
        assertEquals("-10000", features[1]["georss_elev"])
        assertEquals("Age", features[1]["category_label"])
        assertEquals("Past Month", features[1]["category_term"])
        assertEquals("Magnitude", features[1]["category_label1"])
        assertEquals("Magnitude 7", features[1]["category_term1"])
        assertTrue(features[1]["summary"].toString().startsWith("<p class=\"quicksummary\">"))
        assertTrue(features[1]["summary"].toString().endsWith("<dd>10.00 km (6.21 mi)</dd></dl>"))
    }

}