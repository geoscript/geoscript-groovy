package geoscript

import geoscript.feature.Feature
import geoscript.feature.Schema
import geoscript.filter.Expression
import geoscript.filter.Filter
import geoscript.geom.*
import geoscript.layer.Cursor
import geoscript.layer.io.Readers
import geoscript.process.Process
import geoscript.proj.Geodetic
import geoscript.layer.Format
import geoscript.layer.Raster
import org.codehaus.groovy.runtime.DefaultGroovyMethods
import geoscript.layer.Layer
import geoscript.filter.Color
import geoscript.proj.Projection
import geoscript.workspace.Workspace

import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

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

    /**
     * Get the GeoScript version
     * @return The GeoScript version
     */
    static String getVersion() {
        Properties properties = new Properties()
        properties.load(this.getClassLoader().getResourceAsStream("application.properties"))
        properties.getProperty("geoscript.version")
    }

    /**
     * Convert a List of numbers to a GeoScript Geometry.
     * @param list A List of numbers
     * @param type The Geometry Class
     * @return A Geometry
     */
    static Object asType(List list, Class type) {
        // Point pt = [1,2] as Point
        if (type == Point && list.size() == 2) {
            return new Point(list[0], list[1])
        }
        // MultiPoint p = [[1,1],[2,2]] as MultiPoint
        else if (type == MultiPoint) {
            return new MultiPoint(list)
        }
        // LineString line = [[1,2],[2,3],[3,4]] as LineString
        else if (type == LineString) {
            return new LineString(list)
        }
        // MultiLineString line = [[[1,2],[3,4]], [[5,6],[7,8]]] as MultiLineString
        else if (type == MultiLineString) {
            return new MultiLineString(list)
        }
        // Polygon p = [[[1,2],[3,4],[5,6],[1,2]]] as Polygon
        else if (type == Polygon) {
            return new Polygon(list)
        }
        // MultiPolygon p = [[[[1,2],[3,4],[5,6],[1,2]]], [[[7,8],[9,10],[11,12],[7,8]]]] as MultiPolygon
        else if (type == MultiPolygon) {
            return new MultiPolygon(list)
        }
        // Bounds b = [1,3,2,4] as Bounds
        else if (type == Bounds && list.size() == 4) {
            return new Bounds(list[0], list[1], list[2], list[3])
        }
        DefaultGroovyMethods.asType(list, type)
    }

    /**
     * Convert a File to GeoScript Layer
     * @param file The File
     * @param type The GeoScript Layer class
     * @return A GeoScript Layer
     */
    static Object asType(File file, Class type) {
        // Shapefile shp = file as Shapefile
        if (type == Layer && file.name.endsWith(".shp")) {
            return Workspace.getWorkspace(file.absolutePath).get(file.name)
        }
        // Property prop = file as Property
        else if (type == Layer && file.name.endsWith(".properties")) {
            return Workspace.getWorkspace(file.absolutePath).get(file.name)
        }
        // Layer layer = csvFile as Layer
        else if (type == Layer && file.name.endsWith(".csv") && Readers.find("csv")) {
            return Readers.find("csv").read(file)
        }
        // Layer layer = jsonFile as Layer
        else if (type == Layer && file.name.endsWith(".json") && Readers.find("geojson")) {
            return Readers.find("geojson").read(file)
        }
        DefaultGroovyMethods.asType(file, type)
    }

    /**
     * Convert a String to a GeoScript object (Color, Projection, Geometry, Workspace, Expression, Geodetic)
     * @param str The String
     * @param type The GeoScript class
     * @return A GeoScript object
     */
    static Object asType(String str, Class type) {
        // Color c = "255,255,255" as Color
        if (type == Color) {
            return new Color(str)
        }
        // Projection p = "EPSG:2927" as Projection
        else if (type == Projection) {
            return new Projection(str)
        }
        // Geometry g = "POINT (1 1)" as Geometry
        else if (type == Geometry) {
            return Geometry.fromWKT(str)
        }
        // Workspace w = "url='states.shp' 'create spatial index'=true" as Workspace
        else if (type == Workspace) {
            return Workspace.getWorkspace(str)
        }
        // Expression expr = "max(2,4)" as Expression
        else if (type == Expression) {
            return Expression.fromCQL(str)
        }
        // Geodetic geod = "clrk66" as Geodetic
        else if (type == Geodetic) {
            return new Geodetic(str)
        }
        DefaultGroovyMethods.asType(str, type)
    }

    /**
     * Create a GeoScript Workspace from a Map
     * @param map The Map
     * @param type The GeoScript Workspace class
     * @return A GeoScript Workspace
     */
    static Object asType(Map map, Class type) {
        // Workspace w = ['url': 'states.shp', 'create spatial index': true] as Workspace
        if (type == Workspace) {
            return Workspace.getWorkspace(map)
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
        else if (obj instanceof org.locationtech.jts.geom.Geometry) {
            return Geometry.wrap(obj as org.locationtech.jts.geom.Geometry)
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
            return new Format(gridFormat, null)
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

    /**
     * Zip a List of Files
     * @param files The List of Files
     * @param zipFile The Zip File
     * @return The Zip File
     */
    static File zip(List<File> files, File zipFile) {
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile))
        try {
            files.each { File file ->
                if (file.exists()) {
                    zipIt(file, file, out)
                }
            }
        } finally {
            out.close()
        }
        zipFile
    }

    /**
     * Recursively add files and directories to the zip file
     * @param root The File root used to calculate relatives paths
     * @param file The File being zipped
     * @param out The ZipOutputStream
     */
    private static void zipIt(File root, File file, ZipOutputStream out) {
        if (file.isDirectory()) {
            file.listFiles().each { File f ->
                zipIt(root, f, out)
            }
        } else {
            String path = root.toURI().relativize(file.toURI()).path
            if (!path) {
                path = file.name
            }
            out.putNextEntry(new ZipEntry(path))
            file.withInputStream { InputStream inputStream ->
                out << inputStream
            }
            out.closeEntry()
        }
    }

    /**
     * Unzip the Zip File into a Directory
     * @param zipFile The Zip File
     * @param dir The output Directory
     * @return The output Directory
     */
    static File unzip(File zipFile, File dir = zipFile.parentFile) {
        if (!dir.exists()) dir.mkdir()
        ZipFile zip = new ZipFile(zipFile)
        zip.entries().each { ZipEntry entry ->
            File f = new File(dir, entry.name)
            if (entry.directory) {
                f.mkdirs()
            } else {
                if (!f.parentFile.exists()) {
                    f.parentFile.mkdirs()
                }
                f.withOutputStream { OutputStream out ->
                    out << zip.getInputStream(entry)
                }
            }
        }
        dir
    }

    /**
     * Download a URL to a File
     * @param options Optional named parameters:
     * <ul>
     *     <li>overwrite = Whether to overwrite the existing file or not (defaults to true) </li>
     * </ul>
     * @param url The URL
     * @param file The File
     * @return The downloaded File
     */
    static File download(Map options = [:], URL url, File file) {
        boolean overwrite = options.get('overwrite', true) as boolean
        if (overwrite == true || (overwrite == false && !file.exists())) {
            url.withInputStream { InputStream inputStream ->
                file.withOutputStream { OutputStream outputStream ->
                    new BufferedOutputStream(outputStream) << inputStream
                }
            }
        }
        file
    }
}
