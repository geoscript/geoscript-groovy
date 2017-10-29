package geoscript.style.io

import geoscript.filter.Filter
import geoscript.filter.Color
import geoscript.layer.Layer
import geoscript.render.Draw
import geoscript.style.Composite
import geoscript.style.Style
import geoscript.style.Symbolizer
import geoscript.workspace.GeoPackage

/**
 * Read a Unique Values from a text file.
 *
 * The text file can be in value equals color format (rgb, hex, name):
 * <pre>
 * AHa=#aa0c74
 * AHat=#b83b1f
 * AHcf=#964642
 * AHh=#78092e
 * AHpe=#78092e
 * AHt=#5f025a
 * AHt3=#e76161
 * Aa1=#fcedcd
 * Aa2=#94474b
 * </pre>
 *
 * Or it can be in CSV format where the value is first followed by color format (rgb, hex, name):
 * <pre>
 * AHa,175,0,111
 * AHat,192,54,22
 * AHcf,150,70,72
 * AHh,109,13,60
 * AHpe,232,226,82
 * AHt,99,0,95
 * AHt3,233,94,94
 * </pre>
 * @author Jared Erickson
 */
class UniqueValuesReader implements Reader {

    /**
     * The field name
     */
    private String field

    /**
     * The geometry type (point, linestring, polygon)
     */
    private String geometryType

    /**
     * Create a UniqueValuesReader
     * @param field The field name
     * @param geometryType THe geometry type
     */
    UniqueValuesReader(String field, String geometryType) {
        this.field = field
        this.geometryType = geometryType
    }

    /**
     * Get the field name
     * @return The field name
     */
    String getField() {
        this.field
    }

    /**
     * Get the geometry type
     * @return The geometry type
     */
    String getGeometryType() {
        this.geometryType
    }

    /**
     * Read a GeoScript Style from an InputStream
     * @param input An InputStream
     * @return A GeoScript Style
     */
    @Override
    Style read(InputStream input) {
        read(input.text)
    }

    /**
     * Read a GeoScript Style from a File
     * @param file A File
     * @return A GeoScript Style
     */
    @Override
    Style read(File file) {
        read(file.text)
    }

    /**
     * Read a GeoScript Style from a String
     * @param str A String
     * @return A GeoScript Style
     */
    @Override
    Style read(String str) {
        List styles = []
        str.eachLine {String line ->
            line = line.trim()
            if (!line.isEmpty()) {
                if (line.contains("=")) {
                    List parts = line.split("=")
                    String value = parts[0]
                    String color = Color.toHex(parts[1])
                    if (color) {
                        Filter filter = Filter.equals(this.field, value)
                        styles.add(Symbolizer.getDefault(this.geometryType, color).where(filter))
                    }
                }
                else {
                    List parts = line.split(",")
                    if (parts.size() >= 2) {
                        String value = parts[0]
                        String color = Color.toHex(parts.tail().join(","))
                        if (color) {
                            Filter filter = Filter.equals(this.field, value)
                            styles.add(Symbolizer.getDefault(this.geometryType, color).where(filter))
                        }
                    }
                }
            }
        }
        if (styles.size() > 0) {
            new Composite(styles)
        } else {
            null
        }
    }
}
