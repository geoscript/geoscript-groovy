package geoscript.feature.io

import geoscript.feature.Feature
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

/**
 * The GpxReader Unit Test
 * @author Jared Erickson
 */
class GpxReaderTestCase {

    @Test void readWpt() {
        String gpx = """<wpt lat="0.0" lon="0.0">
<name>1</name>
<desc>This is feature # 1</desc>
<type>Trail</type>
<ele>45.2</ele>
<time>1/20/14 1:47 PM</time>
</wpt>"""
        GpxReader reader = new GpxReader()
        Feature feature = reader.read(gpx)
        assertNotNull(feature)
        assertEquals("POINT (0 0)", feature.geom.wkt)
        assertEquals("1", feature['name'])
        assertEquals("This is feature # 1", feature['desc'])
        assertEquals("Trail", feature['type'])
        assertEquals("45.2", feature['ele'])
        assertEquals("1/20/14 1:47 PM", feature['time'])
    }

    @Test void readWptWithExtensions() {
        String gpx = """<wpt lat="0.0" lon="0.0">
<name>1</name>
<extensions>
<ogr:name>Point 1</ogr:name>
<ogr:id>1</ogr:id>
</extensions>
</wpt>"""
        GpxReader reader = new GpxReader()
        Feature feature = reader.read(gpx)
        assertNotNull(feature)
        assertEquals("POINT (0 0)", feature.geom.wkt)
        assertEquals("1", feature['name'])
        assertEquals("Point 1", feature['ogr_name'])
        assertEquals("1", feature['ogr_id'])
    }

    @Test void readRpt() {
        String gpx = """<rte>
<name>4</name>
<desc>This is feature # 4</desc>
<type>Trail</type>
<rtept lat="0.0" lon="0.0">
<time>1/20/14 1:47 PM</time>
</rtept>
<rtept lat="1.0" lon="1.0">
<time>1/20/14 1:47 PM</time>
</rtept>
<rtept lat="2.0" lon="2.0">
<time>1/20/14 1:47 PM</time>
</rtept>
</rte>"""
        GpxReader reader = new GpxReader()
        Feature feature = reader.read(gpx)
        assertNotNull(feature)
        assertEquals("LINESTRING (0 0, 1 1, 2 2)", feature.geom.wkt)
        assertEquals("4", feature['name'])
        assertEquals("This is feature # 4", feature['desc'])
        assertEquals("Trail", feature['type'])
    }

    @Test void readTrk() {
        String gpx = """<trk>
<name>5</name>
<desc>This is feature # 5</desc>
<type>Trail</type>
<trkseg>
<trkpt lat="0.0" lon="0.0">
<time>1/20/14 1:47 PM</time>
</trkpt>
<trkpt lat="1.0" lon="1.0">
<time>1/20/14 1:47 PM</time>
</trkpt>
<trkpt lat="2.0" lon="2.0">
<time>1/20/14 1:47 PM</time>
</trkpt>
</trkseg>
</trk>"""
        GpxReader reader = new GpxReader()
        Feature feature = reader.read(gpx)
        assertNotNull(feature)
        assertEquals("MULTILINESTRING ((0 0, 1 1, 2 2))", feature.geom.wkt)
        assertEquals("5", feature['name'])
        assertEquals("This is feature # 5", feature['desc'])
        assertEquals("Trail", feature['type'])
    }
}
