package geoscript.layer.io

import geoscript.layer.Layer
import au.com.bytecode.opencsv.CSVWriter

/**
 * Write a Layer to a CSV String.
 * @author Jared Erickson
 */
class CsvWriter implements Writer {

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
     * The separator character (the default is comma)
     */
    private String separator

    /**
     * The quote character (the default is double quote)
     */
    private String quote

    /**
     * Create a CsvWriter that encodes geometry as WKT
     * @param options The CSV writer options  (separator and quote)
     */
    CsvWriter(Map options = [:]) {
        this.type = "wkt"
        this.separator = options.get("separator", ",")
        this.quote = options.get("quote", "\"")
    }

    /**
     * Create a CsvWriter that encodes geometry in separator x and y values
     * @param xColumn The x column name
     * @param yColumn The y column name
     * @param options The CSV writer options  (separator and quote)
     */
    CsvWriter(Map options = [:], String xColumn, String yColumn) {
        this(options)
        this.type = "xy"
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
        def columns = []
        fields.each {fld ->
            if (type.equalsIgnoreCase("xy") && fld.isGeometry()) {
                columns.add(xColumn)
                columns.add(yColumn)
            } else {
                columns.add(fld.name)
            }
        }
        writer.writeNext(columns as String[])
        layer.eachFeature{f ->
            def values = []
            columns.each { fld ->
                if (type.equalsIgnoreCase("xy") && fld.equals(xColumn)) {
                    values.add(f.geom.centroid.x)
                } else if (type.equalsIgnoreCase("xy") &&  fld.equals(yColumn)) {
                    values.add(f.geom.centroid.y)
                } else {
                    values.add(f.get(fld))
                }
            }
            writer.writeNext(values as String[])
        }
        writer.flush()
        writer.close()
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
