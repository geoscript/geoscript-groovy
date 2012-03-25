package geoscript.style

import geoscript.filter.Expression
import geoscript.filter.Filter
import geoscript.filter.Color
import geoscript.layer.Layer
import geoscript.feature.Field

/**
 * The Gradient Composite Symbolizer creates gradients between a series of values and symbolizers or from
 * values from Layer.
 * @author Jared Erickson
 */
class Gradient extends Composite {

    /**
     * Create a new Gradient by interpolating between a List of values and styles.
     * @param expression An Expression or a String expression.
     * @param values A List of values
     * @param styles A List of Styles
     * @param classes The number of classes
     * @param method The interpolation method (linear, exponential, logarithmic)
     * @param inclusive Whether to include the last value of not
     */
    Gradient(def expression, List values, List styles, int classes = 5, String method="linear", boolean inclusive = true) {
        super(createGradient(new Expression(expression), values, styles, classes, method, inclusive))
    }

    /**
     * Create a new Gradient where the interpolation is based on a classification method based on values from the Layer's
     * Field.
     * @param layer The Layer
     * @param field The Field or Field's name
     * @param method The classification method (Quantile or EqualInterval)
     * @param number The number of categories
     * @param colors A Color Brewer palette name, or a List of Colors
     * @param elseMode The else mode (ignore, min, max)
     */
    Gradient(Layer layer, def field, String method, int number, def colors, String elseMode = "ignore") {
        super(createGraduatedSymbolizer(layer, field instanceof Field ? field.name : field as String, method, number, colors, elseMode))
    }

    /**
     * Create a graduated Symbolizer
     * @param layer The Layer
     * @param field The Field name
     * @param method The classification method (Quantile or EqualInterval)
     * @param number The number of categories
     * @param colors A Palette name, or a List of Colors
     * @param elseMode The else mode (ignore, min, max)
     * @return The graduated Symbolizer
     */
    static List createGraduatedSymbolizer(Layer layer, String field, String method, int number, def colors, String elseMode = "ignore") {

        org.opengis.filter.FilterFactory2 ff = org.geotools.factory.CommonFactoryFinder.getFilterFactory2(null)
        org.opengis.filter.expression.Function function = ff.function(method, ff.property(field), ff.literal(number))
        org.geotools.filter.function.Classifier classifier = (org.geotools.filter.function.Classifier) function.evaluate(layer.fs.features)

        int elseModeInt
        if (elseMode.equalsIgnoreCase("min")) {
            elseModeInt = org.geotools.brewer.color.StyleGenerator.ELSEMODE_INCLUDEASMIN
        } else if (elseMode.equalsIgnoreCase("max")) {
            elseModeInt = org.geotools.brewer.color.StyleGenerator.ELSEMODE_INCLUDEASMAX
        } else {
            elseModeInt = org.geotools.brewer.color.StyleGenerator.ELSEMODE_IGNORE
        }

        // If the colors argument is a String treat it
        // like a Palette
        if (colors instanceof String) {
            colors = Color.getPaletteColors(colors).collect{c -> c.asColor()} as java.awt.Color[]
        }
        else {
            colors = colors.collect{c ->
                new Color(c).asColor()
            } as java.awt.Color[]
        }

        // Generate the FeatureTypeStyle
        org.geotools.styling.FeatureTypeStyle featureTypeStyle = org.geotools.brewer.color.StyleGenerator.createFeatureTypeStyle(
            classifier,
            (org.geotools.filter.Expression) ff.property(field),
            colors,
            "test",
            layer.fs.schema.geometryDescriptor,
            elseModeInt,
            0.5,
            null);

        String geometryType = layer.schema.geom.typ
        List symbolizers = featureTypeStyle.rules().collect{rule ->
            def sym = rule.symbolizers()[0]
            if (geometryType.equalsIgnoreCase("polygon") || geometryType.equalsIgnoreCase("multipolygon")) {
                return (new Fill(sym.fill.color.value) + new Stroke(sym.stroke.color.value)).where(new Filter(rule.filter))
            } else if (geometryType.equalsIgnoreCase("line") || geometryType.equalsIgnoreCase("multiline")) {
                return new Stroke(sym.stroke.color.value).where(new Filter(rule.filter))
            }  else if (geometryType.equalsIgnoreCase("point") || geometryType.equalsIgnoreCase("multipoint")) {
                return new Shape(sym.graphic.mark.fill.color.value, sym.graphic.size.value, sym.graphic.mark.wellKnownName.vallue).where(new Filter(rule.filter))
            } else {
                null
            }
        }

        return symbolizers
    }

    /**
     * Create a new Gradient by interpolating between a List of values and styles.
     * @param expression An Expression
     * @param values A List of values
     * @param styles A List of Styles
     * @param classes The number of classes
     * @param method The interpolation method (linear, exponential, logarithmic)
     * @param inclusive Whether to include the last value of not
     */
    private static List createGradient(Expression expression, List values, List styles, int classes = 5, String method="linear", boolean inclusive = true) {
        List parts = []
        Map styleMap = [:]
        styles.eachWithIndex {style,i ->
            styleMap[values[i]] = style
        }
        0.upto(values.size() - 2) {i ->
            def startValue = values[i]
            def endValue = values[i+1]
            def delta = endValue - startValue
            Symbolizer startStyle = styleMap[startValue]
            Symbolizer endStyle = styleMap[endValue]
            0.upto(classes - 1) {j ->
                def v0 = startValue + interpolatedValue(method, delta, j / classes)
                def v1 = startValue + interpolatedValue(method, delta, (j + 1) / classes)
                Filter filter = new Filter("${expression.value} >= ${v0} and ${expression.value} < ${v1}")
                def sym = interpolatedStyle(startStyle, endStyle, (double) (j / (classes - 1))).where(filter)
                parts.add(sym)
            }
        }
        if (inclusive) {
            def endValue = values[values.size()-1]
            parts.add(styleMap[endValue].where("${expression.value} = ${endValue}"))
        }
        return parts
    }

    /**
     * Calculate the interpolated value
     * @param method The interpolation method (linear, exponential, logarithmic)
     * @param delta The delta
     * @param fraction The fraction
     * @return The interpolated value
     */
    private static def interpolatedValue(String method, double delta, double fraction) {
        if (method.equalsIgnoreCase("linear")) {
            return fraction * delta
        } else if (method.equalsIgnoreCase("exponential")) {
            return (float) (Math.exp(fraction * Math.log(1 + delta)).round(8) - 1)
        } else if (method.equalsIgnoreCase("logarithmic")) {
            return delta * (float) ((Math.log(fraction + 1) / Math.log(2)).round(8))
        } else {
            throw new Exception("Unsupported interpolation method: ${method}")
        }
    }

    /**
     * Calculate an interpolate Style between the start and end Symbolizers
     * @param start The start Symbolizer
     * @param end The end Symbolizer
     * @param fraction The fraction
     * @return A Composite Symbolizer
     */
    private static Composite interpolatedStyle(Symbolizer start, Symbolizer end, double fraction) {
        List parts = []
        if (!(start instanceof Composite)) {
            start = new Composite(start)
            end = new Composite(end)
        }
        0.upto(start.parts.size() - 1){i ->
            def s = start.parts[i]
            def e = end.parts[i]
            if (!e) {
                throw new Exception("Start and end Composites must have equal number of parts!")
            }
            parts.add(interpolatedSymbolizer(s, e, fraction))
        }
        new Composite(parts)
    }

    /**
     * Calculate an interpolated Symbolizer between the start and end Symbolizer
     * @param start The start Symbolizer
     * @param end The end Symbolizer
     * @param fraction The fractions
     * @return The interpolated Symbolizer
     */
    private static Symbolizer interpolatedSymbolizer(Symbolizer start, Symbolizer end, double fraction) {
        Symbolizer sym = start.clone()
        if (start instanceof Fill && end instanceof Fill) {

            // color
            sym.color = interpolatedColor(start.color, end.color, fraction)

            // opacity
            sym.opacity = interpolatedLiteral(start.opacity.value as float, end.opacity.value as float, fraction)

        } else if (start instanceof Shape && end instanceof Shape) {

            // color
            sym.color = interpolatedColor(start.color, end.color, fraction)

            // opacity
            sym.opacity = interpolatedLiteral(start.opacity.value as float, end.opacity.value as float, fraction)

            // size
            sym.size = interpolatedLiteral(start.size.value as float, end.size.value as float, fraction)

        } else if (start instanceof Stroke && end instanceof Stroke) {

            // color
            sym.color = interpolatedColor(start.color, end.color, fraction)

            // opacity
            sym.opacity = interpolatedLiteral(start.opacity.value as float, end.opacity.value as float, fraction)

            // width
            sym.width = interpolatedLiteral(start.width.value as float, end.width.value as float, fraction)
        }
        sym
    }

    /**
     * Calculate an interpolated Color between the start and end Color
     * @param start The start Color
     * @param end The end Color
     * @param fraction The fraction
     * @return An interpolated Color
     */
    private static Color interpolatedColor(Color start, Color end, double fraction) {
        List startHsl = start.hsl
        List endHsl = end.hsl
        int index = 0
        List hsl = startHsl.collect{v->
            def part = v + (fraction * (endHsl[index] - startHsl[index]))
            index++
            return part
        }
        new Color([h: hsl[0], s: hsl[1], l: hsl[2]])
    }

    /**
     * Calculate an interpolated value between the start and end values
     * @param start The start value
     * @param end The end value
     * @param fraction The fraction
     * @return An interpolated value
     */
    private static float interpolatedLiteral(float start, float end, double fraction) {
        start + (fraction * (end - start))
    }
}
