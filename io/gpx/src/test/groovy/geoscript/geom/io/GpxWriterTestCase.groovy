package geoscript.geom.io

import geoscript.geom.LineString
import geoscript.geom.MultiLineString
import geoscript.geom.MultiPoint
import geoscript.geom.Point
import groovy.xml.StreamingMarkupBuilder
import junit.framework.Assert
import org.junit.Test

import static junit.framework.Assert.assertEquals
import static junit.framework.Assert.assertNull

/**
 * The GpxWriter Unit Test
 * @author Jared Erickson
 */
class GpxWriterTestCase {

    @Test
    void writeStrings() {
        GpxWriter writer = new GpxWriter()
        Assert.assertEquals("<wpt lat='2.0' lon='1.0'/>",
                writer.write(new Point(1, 2)))
        Assert.assertEquals("<rte><rtept lat='2.0' lon='1.0' />" +
                "<rtept lat='4.0' lon='3.0' />" +
                "<rtept lat='6.0' lon='5.0' /></rte>",
                writer.write(new LineString([1, 2], [3, 4], [5, 6])))
        Assert.assertEquals("<trk><trkseg><trkpt lat='2.0' lon='1.0'/>" +
                "<trkpt lat='4.0' lon='3.0'/><trkpt lat='6.0' lon='5.0'/></trkseg>" +
                "<trkseg><trkpt lat='6.0' lon='5.0'/><trkpt lat='7.0' lon='6.0'/>" +
                "<trkpt lat='8.0' lon='7.0'/></trkseg></trk>",
                writer.write(new MultiLineString([[[1, 2], [3, 4], [5, 6]], [[5, 6], [6, 7], [7, 8]]])))
        assertNull(writer.write(new MultiPoint([1,1],[2,2])))
    }

    @Test void writeUsingMarkupBuilder() {
        StreamingMarkupBuilder builder = new StreamingMarkupBuilder()
        GpxWriter writer = new GpxWriter()
        // Point
        def actual = builder.bind { b ->
            writer.write b, new Point(-71.92, 45.256)
        } as String
        String expected = "<wpt lat='45.256' lon='-71.92'/>"
        assertEquals expected, actual
        // LineString
        actual = builder.bind { b ->
            writer.write b, new LineString([1, 2], [3, 4], [5, 6])
        } as String
        expected = "<rte><rtept lat='2.0' lon='1.0'/><rtept lat='4.0' lon='3.0'/><rtept lat='6.0' lon='5.0'/></rte>"
        assertEquals expected, actual
        // MultiLineString
        actual = builder.bind { b ->
            writer.write b, new MultiLineString([[[1, 2], [3, 4], [5, 6]], [[5, 6], [6, 7], [7, 8]]])
        } as String
        expected = "<trk><trkseg><trkpt lat='2.0' lon='1.0'/><trkpt lat='4.0' lon='3.0'/><trkpt lat='6.0' lon='5.0'/></trkseg>" +
                "<trkseg><trkpt lat='6.0' lon='5.0'/><trkpt lat='7.0' lon='6.0'/><trkpt lat='8.0' lon='7.0'/></trkseg></trk>"
        assertEquals expected, actual
    }
}