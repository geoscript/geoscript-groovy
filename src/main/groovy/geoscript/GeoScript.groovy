package geoscript

import geoscript.feature.Feature
import geoscript.feature.Schema
import geoscript.filter.Expression
import geoscript.filter.Filter
import geoscript.geom.*
import geoscript.layer.Cursor
import geoscript.layer.Shapefile
import geoscript.process.Process
import geoscript.proj.Geodetic
import geoscript.layer.Format
import geoscript.layer.Raster
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
        } else if (type == Expression) {
            return Expression.fromCQL(str)
        } else if (type == Geodetic) {
            return new Geodetic(str)
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

    /**
     * Convert a GeoTools Object to a GeoScript Object if possible
     * @param obj The Object
     * @return A GeoScript Object wrapping the GeoTools Object if possible
     */
    static Object wrap(Object obj) {
        // SimpleFeature -> Feature
        if (obj instanceof org.opengis.feature.simple.SimpleFeature) {
            return new Feature(obj as org.opengis.feature.simple.SimpleFeature)
        }
        // SimpleFeatureType -> Schema
        else if (obj instanceof org.opengis.feature.simple.SimpleFeatureType) {
            return new Schema(obj as org.opengis.feature.simple.SimpleFeatureType)
        }
        // JTS Geometry -> Geometry
        else if (obj instanceof com.vividsolutions.jts.geom.Geometry) {
            return Geometry.wrap(obj as com.vividsolutions.jts.geom.Geometry)
        }
        // ReferencedEnvelope -> Bounds
        else if (obj instanceof org.geotools.geometry.jts.ReferencedEnvelope) {
            return new Bounds(obj as org.geotools.geometry.jts.ReferencedEnvelope)
        }
        // GeoTools Expression -> Expression
        else if (obj instanceof org.opengis.filter.expression.Expression) {
            return new Expression(obj as org.opengis.filter.expression.Expression)
        }
        // GeoTools Filter -> Filter
        else if (obj instanceof org.opengis.filter.Filter) {
            return new Filter(obj as org.opengis.filter.Filter)
        }
        // FeatureCollection -> Cursor
        else if (obj instanceof org.geotools.feature.FeatureCollection) {
            return new Cursor(obj as org.geotools.feature.FeatureCollection)
        }
        // FeatureSource -> Layer
        else if (obj instanceof org.geotools.data.FeatureSource) {
            return new Layer(obj as org.geotools.data.FeatureSource)
        }
        /*// GeoTools Process -> Process
        else if (obj instanceof org.geotools.process.Process) {
            return new Process((obj as org.geotools.process.Process)
        }*/
        // CoordinateReferenceSystem -> Projection
        else if (obj instanceof org.opengis.referencing.crs.CoordinateReferenceSystem) {
            return new Projection(obj as org.opengis.referencing.crs.CoordinateReferenceSystem)
        }
        // DefaultEllipsoid -> Geodetic
        else if (obj instanceof org.geotools.referencing.datum.DefaultEllipsoid) {
            return new Geodetic(obj as org.geotools.referencing.datum.DefaultEllipsoid)
        }
        // DataStore -> Workspace
        else if (obj instanceof org.geotools.data.DataStore) {
            return new Workspace(obj as org.geotools.data.DataStore)
        }
        // GridCoverage -> Raster
        else if (obj instanceof org.opengis.coverage.grid.GridCoverage) {
            def grid = obj as org.opengis.coverage.grid.GridCoverage
            return new Raster(grid)
        }
        // GridFormat -> Format
        else if (obj instanceof org.geotools.coverage.grid.io.AbstractGridFormat) {
            def gridFormat = obj as org.geotools.coverage.grid.io.AbstractGridFormat
            return new Format(gridFormat)
        }
        // Not a wrapped GeoTools object just just return
        else {
            return obj
        }
    }

    /**
     * Convert a GeoScript object to a GeoTools object if possible
     * @param obj The potential GeoScript object
     * @return A GeoTools object backing the GeoScript object if possible
     */
    static Object unwrap(Object obj) {
        // Feature -> SimpleFeature
        if (obj instanceof Feature) {
            return (obj as Feature).f
        }
        // Schema -> SimpleFeatureType
        else if (obj instanceof Schema) {
            return (obj as Schema).featureType
        }
        // Geometry -> JTS Geometry
        else if (obj instanceof Geometry) {
            return (obj as Geometry).g
        }
        // Bounds -> ReferencedEnvelope
        else if (obj instanceof Bounds) {
            return (obj as Bounds).env
        }
        // Expression -> GeoTools Expression
        else if (obj instanceof Expression) {
            return (obj as Expression).expr
        }
        // Filter -> GeoTools Filter
        else if (obj instanceof Filter) {
            return (obj as Filter).filter
        }
        // Cursor -> FeatureCollection
        else if (obj instanceof Cursor) {
            return (obj as Cursor).col
        }
        // Layer -> FeatureSource
        else if (obj instanceof Layer) {
            return (obj as Layer).fs
        }
        // Process -> GeoTools Process
        else if (obj instanceof Process) {
            return (obj as Process).process
        }
        // Projection -> CoordinateReferenceSystem
        else if (obj instanceof Projection) {
            return (obj as Projection).crs
        }
        // Geodetic -> DefaultEllipsoid
        else if (obj instanceof Geodetic) {
            return (obj as Geodetic).ellipsoid
        }
        // Workspace -> DataStore
        else if (obj instanceof Workspace) {
            return (obj as Workspace).ds
        }
        // Raster -> GridCoverage
        else if (obj instanceof Raster) {
            return (obj as Raster).coverage
        }
        // Format -> AbstractGridFormat
        else if (obj instanceof Format) {
            return (obj as Format).gridFormat
        }
        // Not a supported GeoScript object
        else {
            return obj
        }
    }
}
