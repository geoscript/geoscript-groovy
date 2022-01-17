package geoscript.geom.io

import geoscript.geom.Geometry
import geoscript.geom.GeometryCollection
import geoscript.geom.LineString
import geoscript.geom.MultiLineString
import geoscript.geom.MultiPoint
import geoscript.geom.MultiPolygon
import geoscript.geom.Point
import geoscript.geom.Polygon
import groovy.yaml.YamlSlurper

/**
 * Read a Geometry from a GeoYaml String
 * @author Jared Erickson
 */
class YamlReader implements Reader {

    @Override
    Geometry read(String str) {
        YamlSlurper yamlSlurper = new YamlSlurper()
        def obj = yamlSlurper.parseText(str)
        readGeometry(obj?.geometry)
    }

    Geometry readGeometry(def obj) {
        String type = obj?.type
        if (type.equalsIgnoreCase("point")) {
            List coords = obj.coordinates as List
            new Point(coords[0] as double, coords[1] as double)
        } else if (type.equalsIgnoreCase("linestring")) {
            List coords = obj.coordinates as List
            new LineString(coords.collect { List xy ->
                new Point(xy[0], xy[1])
            })
        } else if (type.equalsIgnoreCase("polygon")) {
            List coords = obj.coordinates as List
            new Polygon(coords.collect { List rings ->
                rings.collect { List xy ->
                    new Point(xy[0], xy[1])
                }
            })
        } else if (type.equalsIgnoreCase("multipoint")) {
            List coords = obj.coordinates as List
            new MultiPoint(coords.collect { List xy ->
                new Point(xy[0], xy[1])
            })
        } else if (type.equalsIgnoreCase("multilinestring")) {
            List coords = obj.coordinates as List
            new MultiLineString(coords.collect { List lines ->
                new LineString(lines.collect { List xy ->
                    new Point(xy[0], xy[1])
                })
            })
        } else if (type.equalsIgnoreCase("multipolygon")) {
            List coords = obj.coordinates as List
            new MultiPolygon(coords.collect { List poly ->
                new Polygon(poly.collect { List ring ->
                    ring.collect { List xy ->
                        new Point(xy[0], xy[1])
                    }
                })
            })
        } else if (type.equalsIgnoreCase("geometrycollection")) {
            List geoms = obj.geometries as List
            new GeometryCollection(geoms.collect { def geom -> readGeometry(geom)})
        }
    }

}
