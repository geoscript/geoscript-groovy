package geoscript.style

import geoscript.feature.Feature
import geoscript.filter.Color as FColor
import geoscript.filter.Filter
import geoscript.layer.Cursor
import geoscript.layer.Layer
import geoscript.feature.Field

/**
 * The UniqueValues Composite creates a Symbolizer
 * for each unique value from a Layer's Field.
 * <p><blockquote><pre>
 * Layer layer = new Shapefile(file)
 * UniqueValues sym = new UniqueValues(layer, "STATE_ABBR", "Greens")
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class UniqueValues extends Composite {

    /**
     * Create a new UniqueValues Composite
     * @param layer The Layer
     * @param field The Field or the Field's name
     * @param colors A Closure (which takes index based on 0 and a value), a Palette name, or a List of Colors
     */
    UniqueValues(Layer layer, def field, def colors = {index, value -> FColor.getRandomPastel()}) {
        super(createSymbolizers(layer, field instanceof Field ? field.name : field as String, colors))
    }

    /**
     * Create a Symbolizer for every unique value of a Layer's Field
     * @param layer The Layer
     * @param field The Field name
     * @param colors A Closure (which takes index based on 0 and a value), a Palette name, or a List of Colors
     * @return A Symbolizer
     */
    private static List createSymbolizers(Layer layer, String field, def colors = {index, value -> FColor.getRandomPastel()}) {

        // Collect the unique values
        Set uniqueValueSet = new HashSet()

        Cursor c = layer.cursor
        while(c.hasNext()) {
            Feature f = c.next()
            uniqueValueSet.add(f.get(field))
        }
        c.close()

        List uniqueValues = new ArrayList(uniqueValueSet)
        Collections.sort(uniqueValues)

        // If the colors argument is a String treat it
        // like a Palette
        if (colors instanceof String) {
            colors = FColor.getPaletteColors(colors)
        }

        // Create the list of Rules
        int i = 0
        uniqueValues.collect{value ->

            // Get the Color
            def color
            if (colors instanceof Closure) {
                color = colors.call(i, value)
            }
            else {
                // Set our counter back to 0 if
                // it exceeds the number of colors
                // in the List
                if (i >= colors.size()) {
                    i = 0
                }
                color = colors[i]
            }

            // Make sure color is a java.awt.Color
            if (color instanceof String) {
                color = new FColor(color)
            }

            // Increment our counter
            i++

            // Create the Symbolizer
            // name: "${field} = ${value}",
            // title: "${field} = ${value}",
            Symbolizer.getDefault(layer.schema.geom.typ, color).where(new Filter(filterFactory.equals(filterFactory.property(field), filterFactory.literal(value))))
        }
    }

}
