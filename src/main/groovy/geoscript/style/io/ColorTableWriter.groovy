package geoscript.style.io

import geoscript.filter.Color as FilterColor
import geoscript.style.ColorMap
import geoscript.style.Style

/**
 * Write a ColorMap Symbolizer to a color table
 * @author Jared Erickson
 */
class ColorTableWriter implements Writer {

    /**
     * Cross platform new line
     */
    private static final String NEW_LINE = System.getProperty("line.separator")

    /**
     * Write the ColorMap Symbolizer as a color table to an OutputStream
     * @param style The ColorMap Symbolizer, otherwise an IllegalArgumentException is thrown
     * @param out The OutputStream
     */
    void write(Style style, OutputStream out) {
        def printStream = new PrintStream(out)
        printStream.print(write(style))
        printStream.close()
    }

    /**
     * Write the ColorMap Symbolizer as a color table to a File
     * @param style The ColorMap Symbolizer, otherwise an IllegalArgumentException is thrown
     * @param file The File
     */
    void write(Style style, File file) {
        file.write(write(style))
    }

    /**
     * Write the ColorMap Symbolizer as a color table to a String
     * @param style The ColorMap Symbolizer, otherwise an IllegalArgumentException is thrown
     * @return The String
     */
    String write(Style style) {
        if (!(style instanceof ColorMap)) {
            throw new IllegalArgumentException("The ColorTableWriter only works with ColorMaps!")
        }
        ColorMap colorMap = style as ColorMap
        StringWriter writer = new StringWriter()
        colorMap.values.eachWithIndex{value,i ->
            if (i > 0) writer.write(NEW_LINE)
            writer.write("${value.quantity} ${new FilterColor(value.color).rgb.join(' ')}")
        }
        writer.toString()
    }
}
