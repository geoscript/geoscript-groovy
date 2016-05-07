package geoscript.layer.io

import geoscript.layer.Layer
import com.opencsv.CSVReader
import geoscript.feature.Schema
import geoscript.feature.Field
import geoscript.geom.Point
import geoscript.proj.DecimalDegrees
import geoscript.geom.Geometry
import geoscript.geom.io.WktReader
import geoscript.proj.Projection
import geoscript.workspace.Memory
import geoscript.workspace.Workspace

/**
 * Read a CSV String, File, or InputStream and create a {@link geoscript.layer.Layer Layer}.
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
            throw new IllegalArgumentException("With two columns, Type must be an XY type!")
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
            this.geomReader = geoscript.geom.io.Readers.find("wkb")
        } else if (type == Type.GEOJSON) {
            this.geomReader = geoscript.geom.io.Readers.find("geojson")
        } else if (type == Type.KML) {
            this.geomReader = geoscript.geom.io.Readers.find("kml")
        } else if (type == Type.GML2) {
            this.geomReader = geoscript.geom.io.Readers.find("gml2")
        } else if (type == Type.GML3) {
            this.geomReader = geoscript.geom.io.Readers.find("gml3")
        } else /*if (type == Type.WKT)*/ {
            this.geomReader = geoscript.geom.io.Readers.find("wkt")
        }
    }

    /**
     * Read a GeoScript Layer from an InputStream
     * @param options The optional named parameters:
     * <ul>
     *     <li>workspace: The Workspace used to create the Layer (defaults to Memory)</li>
     *     <li>projection: The Projection assigned to the Layer (defaults to null)</li>
     *     <li>name: The name of the Layer (defaults to csv)</li>
     * </ul>
     * @param input An InputStream
     * @return A GeoScript Layer
     */
    Layer read(Map options = [:], InputStream input) {
        readFromReader(options, new InputStreamReader(input))
    }

    /**
     * Read a GeoScript Layer from a File
     * @param options The optional named parameters:
     * <ul>
     *     <li>workspace: The Workspace used to create the Layer (defaults to Memory)</li>
     *     <li>projection: The Projection assigned to the Layer (defaults to null)</li>
     *     <li>name: The name of the Layer (defaults to csv)</li>
     * </ul>
     * @param file A File
     * @return A GeoScript Layer
     */
    Layer read(Map options = [:], File file) {
        readFromReader(options, new FileReader(file))
    }

    /**
     * Read a GeoScript Layer from a String
     * @param options The optional named parameters:
     * <ul>
     *     <li>workspace: The Workspace used to create the Layer (defaults to Memory)</li>
     *     <li>projection: The Projection assigned to the Layer (defaults to null)</li>
     *     <li>name: The name of the Layer (defaults to csv)</li>
     * </ul>
     * @param str A String
     * @return A GeoScript Layer
     */
    Layer read(Map options = [:], String str) {
        readFromReader(options, new StringReader(str))
    }

    /**
     * Read from java.io.Reader, parse the CSV data, and create a Layer
     * @param options The optional named parameters:
     * <ul>
     *     <li>workspace: The Workspace used to create the Layer (defaults to Memory)</li>
     *     <li>projection: The Projection assigned to the Layer (defaults to null)</li>
     *     <li>name: The name of the Layer (defaults to csv)</li>
     * </ul>
     * @param input The input java.io.Reader
     * @return A Layer
     */
    private Layer readFromReader(Map options = [:], java.io.Reader input) {
        // Default parameters
        Workspace workspace = options.get("workspace", new Memory())
        Projection layerProj = options.get("projection")
        String layerName = options.get("name", "csv")
        // Set up for parsing
        CSVReader reader = new CSVReader(input, separator as char, quote as char)
        def cols = reader.readNext() as List
        Layer layer = null
        int xCol
        int yCol
        boolean isGeom = false
        // Are we splitting the geometry into two fields?
        if (!usingSingleColumn) {
            List fields = cols.collect{name ->
                String type = "String"
                // Try to extract type (name:type)
                if (name.contains(":")) {
                    String[] parts = name.split(":")
                    name = parts[0]
                    type = parts[1]
                }
                new Field(name, type)
            }
            fields.add(new Field("geom", "Point"))
            Schema schema = new Schema(layerName, fields)
            if (!schema.proj) {
                schema = schema.reproject(layerProj, layerName)
            }
            layer = workspace.create(schema)
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
                        def proj = null
                        // Get the type from the header
                        if (c.contains(":")) {
                            String[] parts = c.split(":")
                            c = parts[0]
                            colType = parts[1]
                            if (isGeometry(colType) && !isTypeXY(type)) {
                                column = c
                                isGeom = true
                            }
                            if (parts.length > 2) {
                                String projStr = parts[2..parts.length-1].join(":")
                                proj = new Projection(projStr)
                            }
                        }
                        // Otherwise guess the type
                        else {
                            // The user specified a column but it isn't WKT it's XY
                            if (column && c.equalsIgnoreCase(column) && isTypeXY(type)) {
                                colType = "Point"
                            }
                            // The user either specified a column
                            else if ((column && c.equalsIgnoreCase(column))) {
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
                        }
                        fields.add(new Field(c.trim(), colType.trim(), proj))
                    }
                    Schema schema = new Schema(layerName, fields)
                    if (!schema.proj) {
                        schema = schema.reproject(layerProj, layerName)
                    }
                    layer = workspace.create(schema)
                }
                // Try to turn the CSV values into a Feature, but fail
                // gracefully by logging the error and moving to the next line
                try {
                    Map valueMap = [:]
                    cols.eachWithIndex {c,i ->
                        String colName = getColumnName(c)
                        def v = values[i]
                        if (usingSingleColumn && colName.equalsIgnoreCase(column)) {
                            if (isGeom) {
                                v = geomReader.read(v.trim())
                            } else {
                                // Parse XY values including longitude/latitude in DMS, DDM formats
                                v = new DecimalDegrees(v.trim()).point
                            }
                        }
                        valueMap.put(colName,v)
                    }
                    if (!usingSingleColumn) {
                        // Parse XY values including longitude/latitude in DMS, DDM formats
                        Point p = new DecimalDegrees(values[xCol], values[yCol]).point
                        valueMap.put("geom", p)
                    }
                    layer.add(valueMap)
                } catch(Exception ex) {
                    ex.printStackTrace()
                    System.err.println("Error parsing CSV: ${values} because ${ex.message}")
                }
            }
        }
        // The Layer can still be null if there were no Features
        if (layer == null) {
            boolean hasGeom = false
            List fields = cols.collect {c ->
                String fieldType = "String"
                def proj = null
                if (c.contains(":")) {
                    String[] parts = c.split(":")
                    c = parts[0]
                    fieldType = parts[1]
                    if (parts.length > 2) {
                        String projStr = parts[2..parts.length-1].join(":")
                        proj = new Projection(projStr)
                    }
                }
                // Try to guess the geometry Field
                if (!hasGeom && (isGeometry(fieldType) || c.toLowerCase().contains("geom") || c.toLowerCase().contains("shape"))) {
                    fieldType = fieldType.equalsIgnoreCase("String") ? "Point" : fieldType
                    hasGeom = true
                }
                new Field(c, fieldType, proj)
            }
            Schema schema = new Schema(layerName, fields)
            if (!schema.proj) {
                schema = schema.reproject(layerProj, layerName)
            }
            layer = workspace.create(schema)

        }
        return layer
    }

    /**
     * Determine whether the field type is a geometry field type or nor
     * @param fieldType The field type
     * @return Whether the field type is a geometry field type
     */
    private boolean isGeometry(String fieldType) {
        List geometryNames = [
                "point","linestring","polygon","linearring","geometry","geometrycollection",
                "circularstring","circularring","compoundring","compoundcurve"
        ]
        geometryNames.any{geomName -> fieldType.toLowerCase().endsWith(geomName)}
    }

    /**
     * Get the column name from a column name string that may or may not have type and projection information
     * @param col The column name string
     * @return The column name only
     */
    private String getColumnName(String col) {
        if (col.contains(":")) {
            col.substring(0, col.indexOf(":"))
        } else {
            col
        }
    }

    /**
     * Extract the geometry type from a WKT geometry
     * @param wkt The WKT Geometry
     * @return The geometry type (point, linestring, polygon, ect...)
     */
    private String getGeometryTypeFromWKT(String wkt) {
        int i = wkt.indexOf(" (")
        if (i == -1) {
            i = wkt.indexOf("(")
        }
        wkt.substring(0, i).toLowerCase()
    }

    /**
     * Determine whether the string looks like WKT
     * @param str The string value
     * @return Whether the input value looks like WKT
     */
    private boolean isWKT(String str) {
        if (str.startsWith("POINT (") || str.startsWith("POINT(") ||
                str.startsWith("LINESTRING (") || str.startsWith("LINESTRING(") ||
                str.startsWith("POLYGON") || str.startsWith("POLYGON(") ||
                str.startsWith("MULTIPOINT (") || str.startsWith("MULTIPOINT(") ||
                str.startsWith("MULTILINESTRING (") || str.startsWith("MULTILINESTRING(") ||
                str.startsWith("MULTIPOLYGON") || str.startsWith("MULTIPOLYGON(") ||
                str.startsWith("GEOMETRYCOLLECTION (") || str.startsWith("GEOMETRYCOLLECTION(") ||
                str.startsWith("LINEARRING (") || str.startsWith("LINEARRING(")
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
