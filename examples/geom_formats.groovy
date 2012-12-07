import geoscript.geom.*
import geoscript.geom.io.*

def formats = [
    wkt: new WktWriter() ,
    wkb: new WkbWriter(),
    geojson: new GeoJSONWriter(),
    georss: new GeoRSSWriter(),
    gml2: new Gml2Writer(),
    gml3: new Gml3Writer(),
    kml: new KmlWriter()
]

def geometries = [
    new Point(111, -47),
    new LineString([[111.0, -47],[123.0, -48],[110.0, -47]]),
    new LinearRing([[111.0, -47],[123.0, -48],[110.0, -47],[111.0, -47]]),
    new Polygon(new LinearRing([1,1], [10,1], [10,10], [1,10], [1,1]),
        [
            new LinearRing([2,2], [4,2], [4,4], [2,4], [2,2]),
            new LinearRing([5,5], [6,5], [6,6], [5,6], [5,5])
        ]
    ),
    new MultiPoint([111,-47],[110,-46.5]),
    new MultiLineString(new LineString([1,2],[3,4]), new LineString([5,6],[7,8])),
    new MultiPolygon([[[[1,2],[3,4],[5,6],[1,2]]], [[[7,8],[9,10],[11,12],[7,8]]]]),
    new GeometryCollection(new Point(100.0, 0.0), new LineString([101.0, 0.0], [102.0,1.0]))
]

geometries.each{g ->
    formats.each{format ->
        println "${format.key} = ${format.value.write(g)}"
    }
    println ""
}