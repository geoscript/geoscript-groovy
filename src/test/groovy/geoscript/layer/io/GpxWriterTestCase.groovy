package geoscript.layer.io

import geoscript.AssertUtil
import geoscript.feature.Feature
import geoscript.feature.Field
import geoscript.feature.Schema
import geoscript.filter.Property
import geoscript.geom.LineString
import geoscript.geom.MultiLineString
import geoscript.geom.Point
import geoscript.layer.Layer
import geoscript.workspace.Memory
import org.junit.Test

/**
 * The GpxWriter Unit Test
 * @author Jared Erickson
 */
class GpxWriterTestCase {

    @Test void write() {
        Schema schema = new Schema("points",[
                new Field("geom","Geometry","EPSG:4326"),
                new Field("name","String"),
                new Field("ele","Double"),
                new Field("id","Integer")
        ])
        Layer layer = new Memory().create(schema)
        layer.add([
                [geom: new Point(0,0), name: "Point 1", ele: 45.2, id: 1],
                [geom: new Point(1,1), name: "Point 2", ele: 46.3, id: 2],
                [geom: new Point(2,2), name: "Point 3", ele: 44.1, id:3],
                [geom: new LineString([0,0],[1,1],[2,2]), name: "LineString 1", id:4],
                [geom: new MultiLineString([[[0,0],[1,1],[2,2]]]), name: "MultiLineString 1", id:5]
        ])
        GpxWriter writer = new GpxWriter(
                name: new Property("id"),
                time: "1/20/14 1:47 PM",
                description: {Feature f -> "This is feature # ${f['id']}"},
                type: "Trail"
        )
        String actual = writer.write(layer)
        String expected = """<?xml version="1.0" encoding="UTF-8"?>
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
        AssertUtil.assertStringsEqual(expected, actual, trim: true)
    }

    @Test void writeWithAttributes() {
        Schema schema = new Schema("points",[
                new Field("geom","Point","EPSG:4326"),
                new Field("name","String"),
                new Field("id","Integer")
        ])
        Layer layer = new Memory().create(schema)
        layer.add([
                [geom: new Point(0,0), name: "Point 1", id: 1],
                [geom: new Point(1,1), name: "Point 2", id: 2],
                [geom: new Point(2,2), name: "Point 3", id:3]
        ])
        GpxWriter writer = new GpxWriter(
                name: new Property("id"),
                includeAttributes: true
        )
        String actual = writer.write(layer)
        String expected = """<?xml version="1.0" encoding="UTF-8"?>
<gpx xmlns:ogr="http://www.gdal.org/ogr/" xmlns="http://www.topografix.com/GPX/1/1" version="1.1" creator="geoscript">
<wpt lat="0.0" lon="0.0">
<name>1</name>
<extensions>
<ogr:name>Point 1</ogr:name>
<ogr:id>1</ogr:id>
</extensions>
</wpt>
<wpt lat="1.0" lon="1.0">
<name>2</name>
<extensions>
<ogr:name>Point 2</ogr:name>
<ogr:id>2</ogr:id>
</extensions>
</wpt>
<wpt lat="2.0" lon="2.0">
<name>3</name>
<extensions>
<ogr:name>Point 3</ogr:name>
<ogr:id>3</ogr:id>
</extensions>
</wpt>
</gpx>
"""
        AssertUtil.assertStringsEqual(expected, actual, trim: true)
    }
}