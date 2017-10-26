package geoscript.style.io

import geoscript.style.Composite
import geoscript.style.Fill
import geoscript.style.Font
import geoscript.style.Icon
import geoscript.style.Label
import geoscript.style.Shape
import geoscript.style.Stroke
import geoscript.style.Style

/**
 * A Simple StyleReader that can easily create simple Styles using Maps or Strings.
 * Fill properties: fill and fill-opacity
 * Stroke properties: stroke, stroke-width, stroke-opacity
 * Shape properties: shape, shape-size, shape-type
 * Label properties: label-size, label-style, label-weight, label-family
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
        Shape shape = null
        if (['shape','shape-size','shape-type'].any{ options.containsKey(it) }) {
            shape = new Shape(
                    color: options.get('shape', '#7e7e7e'),
                    size: options.get('shape-size', 6),
                    type: options.get('shape-type', 'circle')
            )
            parts.add(shape)
        }
        if (['fill','fill-opacity'].any{ options.containsKey(it) }) {
            Fill fill = new Fill(
                    color: options.get('fill', "#555555"),
                    opacity: options.get('fill-opacity', 0.6)
            )
            if (shape) {
                shape.color = fill.color
                shape.opacity = fill.opacity
            } else {
                parts.add(fill)
            }
        }
        if (['stroke','stroke-width','stroke-opacity'].any{ options.containsKey(it) }) {
            Stroke stroke = new Stroke(
                    color: options.get('stroke', "#555555"),
                    width: options.get('stroke-width', 0.5),
                    opacity: options.get('stroke-opacity', 1.0)
            )
            if (shape) {
                shape.setStroke(stroke)
            } else {
                parts.add(stroke)
            }
        }

        if (['icon'].any{ options.containsKey(it) }) {
            Icon icon = new Icon(
                    url: options.get('icon'),
                    size: options.get('icon-size', -1) as double
            )
            parts.add(icon)
        }
        if (['label-size','label-style','label-weight','label-family'].any{ options.containsKey(it) }) {
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
