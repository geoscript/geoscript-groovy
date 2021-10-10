package geoscript.feature.io

import geoscript.AssertUtil
import geoscript.feature.Feature
import geoscript.feature.Field
import geoscript.feature.Schema
import geoscript.filter.Property
import geoscript.geom.LineString
import geoscript.geom.MultiLineString
import geoscript.geom.Point
import org.junit.jupiter.api.Test

/**
 * The GpxWriter Unit Test
 * @author Jared Erickson
 */
class GpxWriterTest {

    @Test
    void writeWpt() {
        Schema schema = new Schema("points", [
                new Field("geom", "Point", "EPSG:4326"),
                new Field("name", "String"),
                new Field("id", "Integer")
        ])
        Feature feature = schema.feature([geom: new Point(0, 0), name: "Point 1", id: 1])
        GpxWriter writer = new GpxWriter(
                name: new Property("id"),
                time: "1/20/14 1:47 PM",
                description: { Feature f -> "This is feature #${f['id']}" },
                type: "Trail"
        )
        String actual = writer.write(feature)
        String expected = "<wpt lat='0.0' lon='0.0' xmlns='http://www.topografix.com/GPX/1/1'>" +
                "<name>1</name>" +
                "<desc>This is feature #1</desc>" +
                "<type>Trail</type><time>1/20/14 1:47 PM</time></wpt>"
        AssertUtil.assertStringsEqual(expected, actual, trim: true)
    }

    @Test void writeRte() {
        Schema schema = new Schema("routes", [
                new Field("geom", "LineString", "EPSG:4326"),
                new Field("name", "String"),
                new Field("id", "Integer")
        ])
        Feature feature = schema.feature([geom: new LineString([1,2], [3,4], [5,6]), name: "LineString 1", id: 1])
        GpxWriter writer = new GpxWriter(
                name: new Property("id"),
                time: "1/20/14 1:47 PM",
                description: { Feature f -> "This is feature #${f['id']}" },
                type: "Trail"
        )
        String actual = writer.write(feature)
        String expected = "<rte xmlns='http://www.topografix.com/GPX/1/1'>" +
                "<name>1</name><desc>This is feature #1</desc>" +
                "<type>Trail</type><rtept lat='2.0' lon='1.0'>" +
                "<time>1/20/14 1:47 PM</time></rtept>" +
                "<rtept lat='4.0' lon='3.0'>" +
                "<time>1/20/14 1:47 PM</time></rtept><rtept lat='6.0' lon='5.0'>" +
                "<time>1/20/14 1:47 PM</time></rtept></rte>"
        AssertUtil.assertStringsEqual(expected, actual, trim: true)
    }

    @Test void writeTrk() {
        Schema schema = new Schema("tracks", [
                new Field("geom", "MultiLineString", "EPSG:4326"),
                new Field("name", "String"),
                new Field("id", "Integer")
        ])
        Feature feature = schema.feature([geom: new MultiLineString([new LineString([1,2], [3,4], [5,6]),
                new LineString([10,20], [30,40], [50,60])]),
                name: "LineString 1", id: 1])
        GpxWriter writer = new GpxWriter(
                name: new Property("id"),
                time: "1/20/14 1:47 PM",
                description: { Feature f -> "This is feature #${f['id']}" },
                type: "Trail"
        )
        String actual = writer.write(feature)
        String expected = "<trk xmlns='http://www.topografix.com/GPX/1/1'>" +
                "<name>1</name><desc>This is feature #1</desc>" +
                "<type>Trail</type><trkseg>" +
                "<trkpt lat='2.0' lon='1.0'><time>1/20/14 1:47 PM</time></trkpt>" +
                "<trkpt lat='4.0' lon='3.0'><time>1/20/14 1:47 PM</time>" +
                "</trkpt><trkpt lat='6.0' lon='5.0'><time>1/20/14 1:47 PM</time></trkpt></trkseg>" +
                "<trkseg><trkpt lat='20.0' lon='10.0'><time>1/20/14 1:47 PM</time></trkpt>" +
                "<trkpt lat='40.0' lon='30.0'><time>1/20/14 1:47 PM</time></trkpt>" +
                "<trkpt lat='60.0' lon='50.0'><time>1/20/14 1:47 PM</time></trkpt></trkseg></trk>"
        AssertUtil.assertStringsEqual(expected, actual, trim: true)
    }

}
