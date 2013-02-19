package geoscript.layer.io

import geoscript.layer.Layer
import au.com.bytecode.opencsv.CSVReader
import geoscript.feature.Schema
import geoscript.feature.Field
import geoscript.geom.Point
import geoscript.proj.DecimalDegrees
import geoscript.geom.Geometry
import geoscript.geom.io.WktReader
import geoscript.geom.io.WkbReader
import geoscript.geom.io.GeoJSONReader
import geoscript.geom.io.KmlReader
import geoscript.geom.io.Gml2Reader
import geoscript.geom.io.Gml3Reader

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
     * The Type of geometry encoding
     */
    private Type type

    /**
     * The geometry encoding type.
     */
    public static enum Type {
        WKT, WKB, GEOJSON, KML, GML2, GML3, XY, DMS, DMSChar, DDM, DDMChar
    }

    /**
     * The geoscript.geom.io.Writer
     */
    private geoscript.geom.io.Reader geomReader

    /**
     * The name of the single column with WKT or XY data
     */
    private boolean usingSingleColumn = false

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
     * The name of the column that contains the geometry
     */
    private String column

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
        this.type = Type.WKT
        this.usingSingleColumn = true
        this.separator = options.get("separator", ",")
        this.quote = options.get("quote", "\"")
        this.geomReader = new WktReader()
    }

    /**
     * Read a CSV dataset with the geometry encoded in separate x and y columns.
     * @param options The CSV reader options (separator and quote)
     * @param xColumn The x column name
     * @param yColumn The y column name
     */
    CsvReader(Map options = [:], String xColumn, String yColumn, Type type = Type.XY) {
        this(options)
        this.type = type
        if (!isTypeXY(this.type)) {
            throw IllegalArgumentException("With two columns, Type must be an XY type!")
        }
        this.xColumn = xColumn
        this.yColumn = yColumn
        this.usingSingleColumn = false
    }

    /**
     * Read a CSV dataset with the geometry encoded as WKT.
     * @param options The CSV reader options (separator and quote)
     * @param column The name of the geometry column
     */
    CsvReader(Map options = [:], String column) {
        this(options)
        this.column = column
        this.usingSingleColumn = true
    }

    /**
     * Read a CSV dataset with the geometry encoded as WKT.
     * @param options The CSV reader options (separator and quote)
     * @param column The name of the geometry column
     */
    CsvReader(Map options = [:], String column, Type type) {
        this(options, column)
        this.type = type
        this.usingSingleColumn = true
        if (type == Type.WKB) {
            this.geomReader = new WkbReader()
        } else if (type == Type.GEOJSON) {
            this.geomReader = new GeoJSONReader()
        } else if (type == Type.KML) {
            this.geomReader = new KmlReader()
        } else if (type == Type.GML2) {
            this.geomReader = new Gml2Reader()
        } else if (type == Type.GML3) {
            this.geomReader = new Gml3Reader()
        } else /*if (type == Type.WKT)*/ {
            this.geomReader = new WktReader()
        }
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
        boolean isGeom = false
        // Are we splitting the geometry into two fields?
        if (!usingSingleColumn) {
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
            // Skip blank lines
            if (values.length > 0 && values[0] != null) {
                // If there is only one value and it's blank skip it too
                if (values.length == 1 && values[0].trim() == "") {
                    continue
                }
                // The layer is not defined yet, so try to define it from
                // the first row
                if (!layer) {
                    List fields = []
                    cols.eachWithIndex { c, i ->
                        def v = values[i]
                        def colType = "String"
                        // The user specified a column but it isn't WKT it's XY
                        if (column && c.equalsIgnoreCase(column) && isTypeXY(type)) {
                            colType = "Point"
                        }
                        // The user either specified a column
                        else if((column && c.equalsIgnoreCase(column))) {
                            isGeom = true
                            column = c
                            Geometry g = geomReader.read(v)
                            colType = g.getGeometryType()
                        }
                        // The users didn't specify a column but the value looks like WKT
                        else if (!column && isWKT(v)) {
                            isGeom = true
                            column = c
                            colType = getGeometryTypeFromWKT(v)
                        }
                        fields.add(new Field(c, colType))
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
                        if (usingSingleColumn && c.equalsIgnoreCase(column)) {
                            if (isGeom) {
                                v = geomReader.read(v.trim())
                            } else {
                                // Parse XY values including longitude/latitude in DMS, DDM formats
                                v = new DecimalDegrees(v.trim()).point
                            }
                        }
                        valueMap.put(c,v)
                    }
                    if (!usingSingleColumn) {
                        // Parse XY values including longitude/latitude in DMS, DDM formats
                        Point p = new DecimalDegrees(values[xCol], values[yCol]).point
                        valueMap.put("geom", p)
                    }
                    layer.add(valueMap)
                } catch(Exception ex) {
                    System.err.println("Error parsing CSV: ${values} because ${ex.message}")
                }
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

    /**
     * Is the Type an XY/Point only Type?
     * @param t The Type to test
     * @return Whether the Type is an XY/Point only Type
     */
    private boolean isTypeXY(Type t) {
        if (t == Type.XY || t == Type.DMS || t == Type.DMSChar || t == Type.DDM || t == Type.DDMChar) {
            return true
        } else {
            return false
        }
    }
}
