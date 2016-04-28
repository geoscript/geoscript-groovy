package geoscript.style.io

import geoscript.filter.Color
import geoscript.style.ColorMap
import geoscript.style.Style

/**
 * Read a color table to create a Raster ColorMap Symbolizer
 * @author Jared Erickson
 */
class ColorTableReader implements Reader {

    /**
     * Read a color table from an InputStream to create a ColorMap Symbolizer
     * @param input The InputStream
     * @return A ColorMap Symbolizer
     */
    Style read(InputStream input) {
        read(input.text)
    }

    /**
     * Read a color table from an File to create a ColorMap Symbolizer
     * @param file The File
     * @return A ColorMap Symbolizer
     */
    Style read(File file) {
        read(file.text)
    }

    /**
     * Read a color table from a String to create a ColorMap Symbolizer
     * @param str The String
     * @return A ColorMap Symbolizer
     */
    Style read(String str) {
        List values = []
        str.eachLine {line ->
            def parts = line.replaceAll("\\s+"," ").split(" ") as List
            String value = parts[0]
            Color color
            if (parts.size() == 2) {
                color = new Color(parts[1])
            } else {
                color = new Color(parts.subList(1,parts.size()).join(",") )
            }
            if (value.isNumber() && color?.value) {
                values.add([quantity: value, color: color])
            }
        }
        if (values.size() > 0) {
            new ColorMap(values)
        } else {
            null
        }
    }
}