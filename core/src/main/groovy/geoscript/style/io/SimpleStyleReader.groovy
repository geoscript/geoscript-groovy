package geoscript.style.io

import geoscript.style.Composite
import geoscript.style.Fill
import geoscript.style.Font
import geoscript.style.Label
import geoscript.style.Shape
import geoscript.style.Stroke
import geoscript.style.Style

/**
 * A Simple StyleReader that can easily create simple Styles using Maps or Strings.
 * @author Jared Erickson
 */
class SimpleStyleReader implements Reader {

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
        Map options = getMap(str)
        if (!options.isEmpty()) {
            read(options)
        } else {
            null
        }
    }

    /**
     * Reade a GeoScript Style from a Map of options.
     * @param options A Map of options
     * @return A GeoScript Style
     */
    Style read(Map options) {
        List parts = []
        if (!options.containsKey('no-fill')) {
            Fill fill = new Fill(
                    color: options.get('fill', "#555555"),
                    opacity: options.get('fill-opacity', 0.6)
            )
            parts.add(fill)
        }
        if (!options.containsKey('no-stroke')) {
            Stroke stroke = new Stroke(
                    color: options.get('stroke', "#555555"),
                    width: options.get('stroke-width', 0.5),
                    opacity: options.get('stroke-opacity', 1.0)
            )
            parts.add(stroke)
        }
        if (['shape','shape-size','shape-type'].any{ options.containsKey(it) }) {
            Shape shape = new Shape(
                    color: options.get('shape', '#7e7e7e'),
                    size: options.get('shape-size', 6),
                    type: options.get('shape-type', 'circle')
            )
            parts.add(shape)
        }
        if (options.containsKey('label')) {
            Font font = new Font(
                    size: options.get('label-size',12),
                    style: options.get('label-style', 'normal'),
                    weight: options.get('label-weight', 'normal'),
                    family: options.get('label-family', 'serif')
            )
            Label label = new Label(
                    property: options.get('label'),
                    font: font
            )
            parts.add(label)
        }
        new Composite(parts)
    }

    private Map getMap(String str) {
        Map params = [:]
        if (str.contains("=")) {
            str.split("[ ]+(?=([^\']*\'[^\']*\')*[^\']*\$)").each {
                def parts = it.split("=")
                def key = parts[0].trim()
                if ((key.startsWith("'") && key.endsWith("'")) ||
                        (key.startsWith("\"") && key.endsWith("\""))) {
                    key = key.substring(1, key.length() - 1)
                }
                def value = parts[1].trim()
                if ((value.startsWith("'") && value.endsWith("'")) ||
                        (value.startsWith("\"") && value.endsWith("\""))) {
                    value = value.substring(1, value.length() - 1)
                }
                params.put(key, value)
            }
        }
        params
    }
}
