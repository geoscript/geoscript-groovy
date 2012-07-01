package geoscript.layer.io

import geoscript.layer.Layer
import au.com.bytecode.opencsv.CSVReader
import geoscript.feature.Schema
import geoscript.feature.Field
import geoscript.geom.Point

/**
 * Read a CSV String, File, or InputStream and create a Layer
 * @author Jared Erickson
 */
class CsvReader implements Reader {

    /**
     * The type of geometry encoding: WKT or XY
     */
    private String type

    /**
     * The name of the x column if
     * the geometry encoding type is XY
     */
    private String xColumn

    /**
     * The name of the y column if
     * the geometry encoding type is XY
     */
    private String yColumn

    /**
     * The name of the WKT column if
     * the geometry encoding type is WKT
     */
    private String wktColumn

    /**
     * The separator character (the default is comma)
     */
    private String separator

    /**
     * The quote character (the default is double quote)
     */
    private String quote

    /**
     * Read a CSV dataset with the geometry encoded as WKT. The CSV data is inspected in
     * order to discover the name of the geometry column.
     * @param options The CSV reader options (separator and quote)
     */
    CsvReader(Map options = [:]) {
        this.type = "wkt"
        this.separator = options.get("separator", ",")
        this.quote = options.get("quote", "\"")
    }

    /**
     * Read a CSV dataset with the geometry encoded in separate x and y columns.
     * @param options The CSV reader options (separator and quote)
     * @param xColumn The x column name
     * @param yColumn The y column name
     */
    CsvReader(Map options = [:], String xColumn, String yColumn) {
        this(options)
        this.type = "xy"
        this.xColumn = xColumn
        this.yColumn = yColumn
    }

    /**
     * Read a CSV dataset with the geometry encoded as WKT.
     * @param options The CSV reader options (separator and quote)
     * @param wktColumn The name of the WKT column
     */
    CsvReader(Map options = [:], String wktColumn) {
        this(options)
        this.wktColumn = wktColumn
    }

    /**
     * Read a GeoScript Layer from an InputStream
     * @param input An InputStream
     * @return A GeoScript Layer
     */
    Layer read(InputStream input) {
        CSVReader reader = new CSVReader(new InputStreamReader(input), separator as char, quote as char)
        def cols = reader.readNext() as List
        Layer layer = null
        int xCol
        int yCol
        if (type.equalsIgnoreCase("xy")) {
            List fields = cols.collect{name ->
                new Field(name, "String")
            }
            fields.add(new Field("geom", "Point"))
            layer = new Layer("csv", new Schema("csv", fields))
            xCol = cols.indexOf(xColumn)
            yCol = cols.indexOf(yColumn)
        }
        def values
        while((values = reader.readNext()) != null) {
            if (!layer) {
                Schema schema = new Schema("csv", values.collect {v ->
                    new Field(v, isWKT(v) ? getGeometryTypeFromWKT(v) : "String")
                })
                layer = new Layer("csv", schema)
            }
            List v = []
            v.addAll(values)
            if (type.equalsIgnoreCase("xy")) {
                v.add(new Point(values[xCol] as double, values[yCol] as double))
            }
            layer.add(v)
        }
        return layer
    }

    /**
     * Read a GeoScript Layer from a File
     * @param file A File
     * @return A GeoScript Layer
     */
    Layer read(File file) {
        read(new FileInputStream(file))
    }

    /**
     * Read a GeoScript Layer from a String
     * @param str A String
     * @return A GeoScript Layer
     */
    Layer read(String str) {
        read(new ByteArrayInputStream(str.getBytes("UTF-8")))
    }

    /**
     * Extract the geometry type from a WKT geometry
     * @param wkt The WKT Geometry
     * @return The geometry type (point, linestring, polygon, ect...)
     */
    private String getGeometryTypeFromWKT(String wkt) {
        wkt.substring(0, wkt.indexOf(" (")).toLowerCase()
    }

    /**
     * Determine whether the string looks like WKT
     * @param str The string value
     * @return Whether the input value looks like WKT
     */
    private boolean isWKT(String str) {
        if (str.startsWith("POINT (") || str.startsWith("LINESTRING (") || str.startsWith("POLYGON") ||
                str.startsWith("MULTIPOINT (") || str.startsWith("MULTILINESTRING (") || str.startsWith("MULTIPOLYGON") ||
                str.startsWith("GEOMETRYCOLLECTION (") || str.startsWith("LINEARRING (")
        ) {
            return true
        }
        else {
            return false
        }
    }
}
