package geoscript.geom.io

import geoscript.geom.Geometry
import geoscript.geom.GeometryCollection
import geoscript.geom.LineString
import geoscript.geom.MultiLineString
import geoscript.geom.MultiPoint
import geoscript.geom.MultiPolygon
import geoscript.geom.Point
import geoscript.geom.Polygon
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml

/**
 * Write a Geometry to a GeoYaml String.
 * @author Jared Erickson
 */
class YamlWriter implements Writer {

    @Override
    String write(Geometry g) {
        DumperOptions options = new DumperOptions()
        options.indent = 2
        options.prettyFlow = true
        options.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
        options.explicitStart = true
        Yaml yaml = new Yaml(options)
        Map data = [
            geometry: build(g)
        ]
        yaml.dump(data)
    }

    Map build(Geometry g) {
        if (g instanceof Point) {
            Point p = g as Point
            [
                type: "Point",
                coordinates: [p.x, p.y]
            ]
        } else if (g instanceof LineString) {
            LineString line = g as LineString
            [
                type: "LineString",
                coordinates: line.points.collect { Point p -> [p.x, p.y]}
            ]
        } else if (g instanceof Polygon) {
            Polygon polygon = g as Polygon
            List rings = []
            rings.add(polygon.exteriorRing)
            rings.addAll(polygon.interiorRings)
            [
                type: "Polygon",
                coordinates: rings.collect {LineString line ->
                    line.points.collect { Point p -> [p.x, p.y]}
                }
            ]
        } else if (g instanceof MultiPoint) {
            MultiPoint mp = g as MultiPoint
            [
                type: "MultiPoint",
                coordinates: mp.points.collect { Point p -> [p.x, p.y]}
            ]
        } else if (g instanceof MultiLineString) {
            MultiLineString ml = g as MultiLineString
            [
                type: "MultiLineString",
                coordinates: ml.geometries.collect { Geometry line -> line.points.collect {Point p -> [p.x, p.y]} }
            ]
        } else if (g instanceof MultiPolygon) {
            MultiPolygon mp = g as MultiPolygon
            [
                type: "MultiPolygon",
                coordinates: mp.geometries.collect { Geometry geom ->
                    Polygon polygon = geom as Polygon
                    List rings = []
                    rings.add(polygon.exteriorRing)
                    rings.addAll(polygon.interiorRings)
                    rings.collect {LineString line ->
                        line.points.collect { Point p -> [p.x, p.y]}
                    }
                }
            ]
        } else if (g instanceof GeometryCollection) {
            GeometryCollection gc = g as GeometryCollection
            [
                type: "GeometryCollection",
                geometries: gc.geometries.collect { Geometry geom -> build(geom) }
            ]
        }
    }

}
