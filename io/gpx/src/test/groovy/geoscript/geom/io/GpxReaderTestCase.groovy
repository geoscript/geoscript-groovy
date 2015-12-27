package geoscript.geom.io

import geoscript.geom.Geometry
import geoscript.geom.LineString
import geoscript.geom.MultiLineString
import geoscript.geom.Point
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNull

/**
 * The GpxReader Unit Test
 * @author Jared Erickson
 */
class GpxReaderTestCase {

    @Test void readStrings() {
        GpxReader reader = new GpxReader()
        // Way points
        Geometry g = reader.read("<wpt lat='2.0' lon='1.0'/>")
        assertEquals("POINT (1 2)", g.wkt)
        // Routes
        g = reader.read("<rte><rtept lat='2.0' lon='1.0' />" +
                "<rtept lat='4.0' lon='3.0' />" +
                "<rtept lat='6.0' lon='5.0' /></rte>")
        assertEquals("LINESTRING (1 2, 3 4, 5 6)", g.wkt)
        // Tracks
        g = reader.read("<trk><trkseg><trkpt lat='2.0' lon='1.0'/>" +
                "<trkpt lat='4.0' lon='3.0'/><trkpt lat='6.0' lon='5.0'/></trkseg>" +
                "<trkseg><trkpt lat='6.0' lon='5.0'/><trkpt lat='7.0' lon='6.0'/>" +
                "<trkpt lat='8.0' lon='7.0'/></trkseg></trk>")
        assertEquals("MULTILINESTRING ((1 2, 3 4, 5 6), (5 6, 6 7, 7 8))", g.wkt)
        // Empty
        assertNull(reader.read(""))
        assertNull(reader.read("  "))
        assertNull(reader.read("<test/>"))
    }

    @Test void readFromWriter() {
        GpxWriter writer = new GpxWriter()
        GpxReader reader = new GpxReader()
        // Points
        Geometry expected = new Point(1,2)
        Geometry actual = reader.read(writer.write(expected))
        assertEquals(expected, actual)
        // LineStrings
        expected = new LineString([1,2],[3,4],[5,6])
        actual = reader.read(writer.write(expected))
        assertEquals(expected, actual)
        // MultiLineStrings
        expected = new MultiLineString([[[1, 2], [3, 4], [5, 6]], [[5, 6], [6, 7], [7, 8]]])
        actual = reader.read(writer.write(expected))
        assertEquals(expected, actual)
    }
}
