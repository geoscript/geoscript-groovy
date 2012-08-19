package geoscript.layer.io

import geoscript.layer.Layer
import au.com.bytecode.opencsv.CSVReader
import geoscript.feature.Schema
import geoscript.feature.Field
import geoscript.geom.Point
import geoscript.proj.DecimalDegrees
import geoscript.geom.Geometry

/**
 * Read a CSV String, File, or InputStream and create a {@geoscript.layer.Layer Layer}.
 * <p><blockquote><pre>
 * String csv = """"geom","name","price"
 * "POINT (111 -47)","House","12.5"
 * "POINT (121 -45)","School","22.7"
 * """
 * CsvReader reader = new CsvReader()
 * Layer layer = reader.read(csv)
 * </pre></blockquote></p>
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
        readFromReader(new InputStreamReader(input))
    }

    /**
     * Read a GeoScript Layer from a File
     * @param file A File
     * @return A GeoScript Layer
     */
    Layer read(File file) {
        readFromReader(new FileReader(file))
    }

    /**
     * Read a GeoScript Layer from a String
     * @param str A String
     * @return A GeoScript Layer
     */
    Layer read(String str) {
        readFromReader(new StringReader(str))
    }

    /**
     * Read from java.io.Reader, parse the CSV data, and create a Layer
     * @param input The input java.io.Reader
     * @return A Layer
     */
    private Layer readFromReader(java.io.Reader input) {
        CSVReader reader = new CSVReader(input, separator as char, quote as char)
        def cols = reader.readNext() as List
        Layer layer = null
        int xCol
        int yCol
        boolean isWkt = false
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
                List fields = []
                cols.eachWithIndex { c, i ->
                    def v = values[i]
                    def type = "String"
                    // The user specified a column but it isn't WKT it's XY
                    if (wktColumn && c.equalsIgnoreCase(wktColumn) && !isWKT(v)) {
                        type = "Point"
                    }
                    // The user either specified a column for WKT or didn't but it
                    // looks like wkt
                    else if((wktColumn && c.equalsIgnoreCase(wktColumn)) || isWKT(v)) {
                        isWkt = true
                        wktColumn = c
                        type = getGeometryTypeFromWKT(v)
                    }
                    fields.add(new Field(c, type))
                }
                Schema schema = new Schema("csv", fields)
                layer = new Layer("csv", schema)
            }
            // Try to turn the CSV values into a Feature, but fail
            // gracefully by logging the error and moving to the next line
            try {
                Map valueMap = [:]
                cols.eachWithIndex {c,i ->
                    def v = values[i]
                    if (type.equalsIgnoreCase("wkt") && c.equalsIgnoreCase(wktColumn)) {
                        if (isWkt) {
                            v = Geometry.fromWKT(v)
                        } else {
                            // Parse XY values including longitude/latitude in DMS, DDM formats
                            v = new DecimalDegrees(v.trim()).point
                        }
                    }
                    valueMap.put(c,v)
                }
                if (type.equalsIgnoreCase("xy")) {
                    // Parse XY values including longitude/latitude in DMS, DDM formats
                    Point p = new DecimalDegrees(values[xCol], values[yCol]).point
                    valueMap.put("geom", p)
                }
                layer.add(valueMap)
            } catch(Exception ex) {
                System.err.println("Error parsing CSV: ${values}")
            }
        }
        return layer
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
