package geoscript.layer.io

import geoscript.geom.Point
import geoscript.layer.Layer
import au.com.bytecode.opencsv.CSVWriter
import geoscript.proj.DecimalDegrees
import geoscript.geom.io.WkbWriter
import geoscript.geom.io.WktWriter
import geoscript.geom.Geometry
import geoscript.geom.io.GeoJSONWriter
import geoscript.geom.io.KmlWriter
import geoscript.geom.io.Gml2Writer
import geoscript.geom.io.Gml3Writer

/**
 * Write a {@geoscript.layer.Layer Layer} to a CSV String.
 * <p><blockquote><pre>
 * def layer = new Shapefile("states.shp")
 * CsvWriter writer = new CsvWriter()
 * String csv = writer.write(layer)
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class CsvWriter implements Writer {

    /**
     * The Type of geometry encoding
     */
    private Type type

    /**
     * How to encode the geometry
     */
    public static enum Type {
        WKT, WKB, GEOJSON, KML, GML2, GML3, XY, DMS, DMSChar, DDM, DDMChar
    }

    /**
     * The geoscript.geom.io.Writer
     */
    private geoscript.geom.io.Writer geomWriter

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
     * The separator character (the default is comma)
     */
    private String separator

    /**
     * The quote character (the default is double quote)
     */
    private String quote

    /**
     * Create a CsvWriter that encodes geometry in a single column as WKT
     * @param options The CSV writer options  (separator and quote)
     */
    CsvWriter(Map options = [:]) {
        this(options, Type.WKT)
    }

    /**
     * Create a CsvWriter that encodes geometry in single column
     * @param type The Type
     * @param options The CSV writer options  (separator and quote)
     */
    CsvWriter(Map options = [:], Type type) {
        this.type = type
        this.usingSingleColumn = true
        this.separator = options.get("separator", ",")
        this.quote = options.get("quote", "\"")
        if (this.type == Type.WKB) {
            this.geomWriter = new WkbWriter()
        } else if (this.type == Type.GEOJSON) {
            this.geomWriter = new GeoJSONWriter()
        } else if (this.type == Type.KML) {
            this.geomWriter = new KmlWriter()
        } else if (type == Type.GML2) {
            this.geomWriter = new Gml2Writer()
        } else if (type == Type.GML3) {
            this.geomWriter = new Gml3Writer()
        } else /*if (this.type == Type.WKT)*/ {
            this.geomWriter = new WktWriter()
        }
    }

    /**
     * Create a CsvWriter that encodes geometry in separator x and y values
     * @param xColumn The x column name
     * @param yColumn The y column name
     * @param options The CSV writer options  (separator and quote)
     */
    CsvWriter(Map options = [:], String xColumn, String yColumn) {
        this(options, xColumn, yColumn, Type.XY)
    }

    /**
     * Create a CsvWriter that encodes geometry in separator x and y values
     * @param xColumn The x column name
     * @param yColumn The y column name
     * @param type The Type
     * @param options The CSV writer options  (separator and quote)
     */
    CsvWriter(Map options = [:], String xColumn, String yColumn, Type type) {
        this(options)
        this.type = type
        this.usingSingleColumn = false
        this.xColumn = xColumn
        this.yColumn = yColumn
    }

    /**
     * Write the Layer to the OutputStream
     * @param layer The Layer
     * @param out The OutputStream
     */
    void write(Layer layer, OutputStream out) {
         writeToWriter(layer, new OutputStreamWriter(out))
    }

    /**
     * Write the Layer to the java.io.Writer
     * @param layer The Layer
     * @param out The java.io.Writer
     */
    private void writeToWriter(Layer layer, java.io.Writer out) {
        CSVWriter writer = new CSVWriter(out, separator as char, quote as char)
        List fields = layer.schema.fields
        String geomFldName = layer.schema.geom.name
        def columns = []
        fields.each {fld ->
            if (!usingSingleColumn && isXY(type) && fld.isGeometry()) {
                columns.add(xColumn)
                columns.add(yColumn)
            } else {
                columns.add(fld.name)
            }
        }
        writer.writeNext(columns as String[])
        layer.eachFeature{f ->
            Point pt = f.geom.centroid
            DecimalDegrees dd = new DecimalDegrees(pt.x, pt.y)
            def values = []
            columns.each { fld ->
                if (!usingSingleColumn && isXY(type) && fld.equals(xColumn)) {
                    if (type == Type.XY) {
                        values.add(pt.x)
                    } else if (type == Type.DMS) {
                        values.add(dd.toDms(true).split(",")[0])
                    } else if (type == Type.DMSChar) {
                        values.add(dd.toDms(false).split(",")[0])
                    } else if (type == Type.DDM) {
                        values.add(dd.toDdm(true).split(",")[0])
                    } else if (type == Type.DDMChar) {
                        values.add(dd.toDdm(false).split(",")[0])
                    }
                } else if (!usingSingleColumn && isXY(type) &&  fld.equals(yColumn)) {
                    if (type == Type.XY) {
                        values.add(pt.y)
                    } else if (type == Type.DMS) {
                        values.add(dd.toDms(true).split(",")[1])
                    } else if (type == Type.DMSChar) {
                        values.add(dd.toDms(false).split(",")[1])
                    } else if (type == Type.DDM) {
                        values.add(dd.toDdm(true).split(",")[1])
                    } else if (type == Type.DDMChar) {
                        values.add(dd.toDdm(false).split(",")[1])
                    }
                } else if (usingSingleColumn && isXY(type) && fld.equals(geomFldName)) {
                    if (type == Type.XY) {
                        values.add(pt.x + "," + pt.y)
                    } else if (type == Type.DMS) {
                        values.add(dd.toDms(true))
                    } else if (type == Type.DMSChar) {
                        values.add(dd.toDms(false))
                    } else if (type == Type.DDM) {
                        values.add(dd.toDdm(true))
                    } else if (type == Type.DDMChar) {
                        values.add(dd.toDdm(false))
                    }
                } else {
                    if (fld.equals(geomFldName)) {
                        values.add(geomWriter.write(f.get(fld) as Geometry))
                    }
                    else {
                        values.add(f.get(fld))
                    }
                }
            }
            writer.writeNext(values as String[])
        }
        writer.flush()
        writer.close()
    }

    /**
     * Is the Type an XY type or is it WKT?
     * @param type The Type
     * @return Whether the Type is XY or WTK
     */
    private boolean isXY(Type type) {
        if (type == Type.XY
            || type == Type.DMS || type == Type.DMSChar
            || type == Type.DDM || type == Type.DDMChar) {
            return true
        }
        else {
            return false
        }
    }

    /**
     * Write the Layer to the File
     * @param layer The Layer
     * @param file The File
     */
    void write(Layer layer, File file) {
        FileWriter writer = new FileWriter(file)
        writeToWriter(layer, writer)
        writer.close()
    }

    /**
     * Write the Layer to a String
     * @param layer The Layer
     * @return A String
     */
    String write(Layer layer) {
        StringWriter writer = new StringWriter()
        writeToWriter(layer, writer);
        writer.close()
        return writer.toString()
    }
}
