package geoscript.layer.io

import geoscript.layer.Layer
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNull

/**
 * The GpxReader Unit Test
 * @author Jared Erickson
 */
class GpxReaderTestCase {

    private String gpx = """<?xml version="1.0" encoding="UTF-8"?>
<gpx xmlns="http://www.topografix.com/GPX/1/1" version="1.1" creator="geoscript">
<wpt lat="0.0" lon="0.0">
<name>1</name>
<desc>This is feature # 1</desc>
<type>Trail</type>
<ele>45.2</ele>
<time>1/20/14 1:47 PM</time>
</wpt>
<wpt lat="1.0" lon="1.0">
<name>2</name>
<desc>This is feature # 2</desc>
<type>Trail</type>
<ele>46.3</ele>
<time>1/20/14 1:47 PM</time>
</wpt>
<wpt lat="2.0" lon="2.0">
<name>3</name>
<desc>This is feature # 3</desc>
<type>Trail</type>
<ele>44.1</ele>
<time>1/20/14 1:47 PM</time>
</wpt>
<rte>
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
</rte>
<trk>
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
</trk>
</gpx>
"""

    @Test
    void readWayPoints() {
        GpxReader reader = new GpxReader(type: GpxReader.Type.WayPoints)
        Layer layer = reader.read(gpx)
        assertEquals("Point", layer.schema.geom.typ)
        assertEquals(3, layer.count)
        assertEquals "gpx geom: Point(EPSG:4326), name: String, desc: String, type: String, ele: String, time: String",
                layer.schema.toString()
        List features = layer.features
        // 1
        assertEquals("POINT (0 0)", features[0].geom.wkt)
        assertEquals("1", features[0]['name'])
        assertEquals("This is feature # 1", features[0]['desc'])
        assertEquals("45.2", features[0]['ele'])
        assertEquals("1/20/14 1:47 PM", features[0]['time'])
        // 2
        assertEquals("POINT (1 1)", features[1].geom.wkt)
        assertEquals("2", features[1]['name'])
        assertEquals("This is feature # 2", features[1]['desc'])
        assertEquals("46.3", features[1]['ele'])
        assertEquals("1/20/14 1:47 PM", features[1]['time'])
        // 3
        assertEquals("POINT (2 2)", features[2].geom.wkt)
        assertEquals("3", features[2]['name'])
        assertEquals("This is feature # 3", features[2]['desc'])
        assertEquals("44.1", features[2]['ele'])
        assertEquals("1/20/14 1:47 PM", features[2]['time'])
    }

    @Test
    void readWayPointsRoutesTracks() {
        GpxReader reader = new GpxReader(type: GpxReader.Type.WayPointsRoutesTracks)
        Layer layer = reader.read(gpx)
        assertEquals("Geometry", layer.schema.geom.typ)
        assertEquals(5, layer.count)
        assertEquals "gpx geom: Geometry(EPSG:4326), name: String, desc: String, type: String, ele: String, time: String",
                layer.schema.toString()
        List features = layer.features
        // 1
        assertEquals("POINT (0 0)", features[0].geom.wkt)
        assertEquals("1", features[0]['name'])
        assertEquals("This is feature # 1", features[0]['desc'])
        assertEquals("45.2", features[0]['ele'])
        assertEquals("1/20/14 1:47 PM", features[0]['time'])
        // 2
        assertEquals("POINT (1 1)", features[1].geom.wkt)
        assertEquals("2", features[1]['name'])
        assertEquals("This is feature # 2", features[1]['desc'])
        assertEquals("46.3", features[1]['ele'])
        assertEquals("1/20/14 1:47 PM", features[1]['time'])
        // 3
        assertEquals("POINT (2 2)", features[2].geom.wkt)
        assertEquals("3", features[2]['name'])
        assertEquals("This is feature # 3", features[2]['desc'])
        assertEquals("44.1", features[2]['ele'])
        assertEquals("1/20/14 1:47 PM", features[2]['time'])
        // 4
        assertEquals("LINESTRING (0 0, 1 1, 2 2)", features[3].geom.wkt)
        assertEquals("4", features[3]['name'])
        assertEquals("This is feature # 4", features[3]['desc'])
        assertNull(features[3]['ele'])
        assertNull(features[3]['time'])
        // 5
        assertEquals("MULTILINESTRING ((0 0, 1 1, 2 2))", features[4].geom.wkt)
        assertEquals("5", features[4]['name'])
        assertEquals("This is feature # 5", features[4]['desc'])
        assertNull(features[4]['ele'])
        assertNull(features[4]['time'])
    }

    @Test
    void readRoutes() {
        GpxReader reader = new GpxReader(type: GpxReader.Type.Routes)
        Layer layer = reader.read(gpx)
        assertEquals("LineString", layer.schema.geom.typ)
        assertEquals(1, layer.count)
        assertEquals "gpx geom: LineString(EPSG:4326), name: String, desc: String, type: String",
                layer.schema.toString()
        List features = layer.features
        assertEquals("LINESTRING (0 0, 1 1, 2 2)", features[0].geom.wkt)
        assertEquals("4", features[0]['name'])
        assertEquals("This is feature # 4", features[0]['desc'])
        assertNull(features[0]['ele'])
        assertNull(features[0]['time'])
    }

    @Test
    void readTracks() {
        GpxReader reader = new GpxReader(type: GpxReader.Type.Tracks)
        Layer layer = reader.read(gpx)
        assertEquals("MultiLineString", layer.schema.geom.typ)
        assertEquals(1, layer.count)
        assertEquals "gpx geom: MultiLineString(EPSG:4326), name: String, desc: String, type: String",
                layer.schema.toString()
        List features = layer.features
        assertEquals("MULTILINESTRING ((0 0, 1 1, 2 2))", features[0].geom.wkt)
        assertEquals("5", features[0]['name'])
        assertEquals("This is feature # 5", features[0]['desc'])
        assertNull(features[0]['ele'])
        assertNull(features[0]['time'])
    }

    @Test
    void readRoutePoints() {
        GpxReader reader = new GpxReader(type: GpxReader.Type.RoutePoints)
        Layer layer = reader.read(gpx)
        assertEquals("Point", layer.schema.geom.typ)
        assertEquals(3, layer.count)
        assertEquals "gpx geom: Point(EPSG:4326), route_fid: Integer, route_point_id: Integer, name: String, " +
                "desc: String, type: String, time: String",
                layer.schema.toString()
        List features = layer.features
        // 1
        assertEquals("POINT (0 0)", features[0].geom.wkt)
        assertEquals(1, features[0]['route_fid'])
        assertEquals(1, features[0]['route_point_id'])
        assertEquals("4", features[0]['name'])
        assertEquals("This is feature # 4", features[0]['desc'])
        assertEquals("Trail", features[0]['type'])
        assertEquals("1/20/14 1:47 PM", features[0]['time'])
        // 2
        assertEquals("POINT (1 1)", features[1].geom.wkt)
        assertEquals(1, features[1]['route_fid'])
        assertEquals(2, features[1]['route_point_id'])
        assertEquals("4", features[1]['name'])
        assertEquals("This is feature # 4", features[1]['desc'])
        assertEquals("Trail", features[1]['type'])
        assertEquals("1/20/14 1:47 PM", features[1]['time'])
        // 3
        assertEquals("POINT (2 2)", features[2].geom.wkt)
        assertEquals(1, features[2]['route_fid'])
        assertEquals(3, features[2]['route_point_id'])
        assertEquals("4", features[2]['name'])
        assertEquals("This is feature # 4", features[2]['desc'])
        assertEquals("Trail", features[2]['type'])
        assertEquals("1/20/14 1:47 PM", features[2]['time'])
    }

    @Test
    void readTrackPoints() {
        GpxReader reader = new GpxReader(type: GpxReader.Type.TrackPoints)
        Layer layer = reader.read(gpx)
        assertEquals("Point", layer.schema.geom.typ)
        assertEquals(3, layer.count)
        assertEquals "gpx geom: Point(EPSG:4326), track_fid: Integer, track_seg_id: Integer, track_seg_point_id: Integer, " +
                "name: String, desc: String, type: String, time: String",
                layer.schema.toString()
        List features = layer.features
        // 1
        assertEquals("POINT (0 0)", features[0].geom.wkt)
        assertEquals(1, features[0]['track_fid'])
        assertEquals(1, features[0]['track_seg_id'])
        assertEquals(1, features[0]['track_seg_point_id'])
        assertEquals("5", features[0]['name'])
        assertEquals("This is feature # 5", features[0]['desc'])
        assertEquals("Trail", features[0]['type'])
        assertEquals("1/20/14 1:47 PM", features[0]['time'])
        // 2
        assertEquals("POINT (1 1)", features[1].geom.wkt)
        assertEquals(1, features[1]['track_fid'])
        assertEquals(1, features[1]['track_seg_id'])
        assertEquals(2, features[1]['track_seg_point_id'])
        assertEquals("5", features[1]['name'])
        assertEquals("This is feature # 5", features[1]['desc'])
        assertEquals("Trail", features[1]['type'])
        assertEquals("1/20/14 1:47 PM", features[1]['time'])
        // 3
        assertEquals("POINT (2 2)", features[2].geom.wkt)
        assertEquals(1, features[2]['track_fid'])
        assertEquals(1, features[2]['track_seg_id'])
        assertEquals(3, features[2]['track_seg_point_id'])
        assertEquals("5", features[2]['name'])
        assertEquals("This is feature # 5", features[2]['desc'])
        assertEquals("Trail", features[2]['type'])
        assertEquals("1/20/14 1:47 PM", features[2]['time'])
    }
}