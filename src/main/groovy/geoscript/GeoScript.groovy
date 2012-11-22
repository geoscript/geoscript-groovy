package geoscript

import geoscript.geom.*
import geoscript.layer.Shapefile
import org.codehaus.groovy.runtime.DefaultGroovyMethods
import geoscript.layer.io.CsvReader
import geoscript.layer.Layer
import geoscript.layer.io.GeoJSONReader
import geoscript.filter.Color
import geoscript.proj.Projection
import geoscript.workspace.PostGIS
import geoscript.workspace.Workspace

/**
 * The GeoScript class contains category methods.
 * <p>You can easily create a Point from a list:</p>
 * <p><blockquote><pre>
 * use(GeoScript) {
 *    Point pt = [1,2] as Point
 * }
 * </pre></blockquote></p>
 * <p>or a LineString from a list or lists:</p>
 * <p><blockquote><pre>
 * use(GeoScript) {
 *    LineString line = [[1,2],[2,3],[3,4]] as LineString
 * }
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class GeoScript {

    static Object asType(List list, Class type) {
        if (type == Point && list.size() == 2) {
            return new Point(list[0], list[1])
        } else if (type == MultiPoint) {
            return new MultiPoint(list)
        } else if (type == LineString) {
            return new LineString(list)
        } else if (type == MultiLineString) {
            return new MultiLineString(list)
        } else if (type == Polygon) {
            return new Polygon(list)
        } else if (type == MultiPolygon) {
            return new MultiPolygon(list)
        } else if (type == Bounds && list.size() == 4) {
            return new Bounds(list[0], list[1], list[2], list[3])
        }
        DefaultGroovyMethods.asType(list, type)
    }

    static Object asType(File file, Class type) {
        if (type == Shapefile && file.name.endsWith(".shp")) {
            return new Shapefile(file)
        } else if (type == Layer && file.name.endsWith(".csv")) {
            return new CsvReader().read(file)
        } else if (type == Layer && file.name.endsWith(".json")) {
            return new GeoJSONReader().read(file)
        }
        DefaultGroovyMethods.asType(file, type)
    }

    static Object asType(String str, Class type) {
        if (type == Color) {
            return new Color(str)
        } else if (type == Projection) {
            return new Projection(str)
        } else if (type == Geometry) {
            return Geometry.fromWKT(str)
        } else if (type == Workspace) {
            return new Workspace(str)
        }
        DefaultGroovyMethods.asType(str, type)
    }

    static Object asType(Map map, Class type) {
        if (type == PostGIS) {
            return new PostGIS(map, map.get("name"))
        } else if (type == Workspace) {
            return new Workspace(map)
        }
        DefaultGroovyMethods.asType(map, type)
    }

}
