package geoscript.style

import geoscript.filter.Filter

/**
 * A Composite is a Symbolizer that contains one of more Symbolizers.
 * You can create a Composite by combining two Symbolizers:
 * <p><code>def composite = new Fill("wheat") + new Stroke("brown")</code></p>
 * Or you can create a Composite from a List of Symbolizers:
 * <p><code>def composite = new Composite([
 *   new Fill("wheat"),
 *   new Stroke("brown")
 * ])</code></p>
 * @author Jared Erickson
 */
class Composite extends Symbolizer {

    /**
     * A List of Symbolizers
     */
    List parts = new ArrayList()

    /**
     * Create a new Composite with a Symbolizer
     * @param symbolizer A Symbolizer
     */
    Composite(Symbolizer symbolizer) {
        this([symbolizer])
    }

    /**
     * Create a new Composite with a List of Symbolizers
     * @param parts The List of Symbolizers
     */
    Composite(List parts) {
        super()
        this.parts.addAll(parts)
    }

    /**
     * Apply the Filter or CQL statement to the Composite
     * @param filter A Filter of CQL statement
     * @return This Composite
     */
    Symbolizer where(def filter) {
        super.where(filter)
        parts.each{part -> part.where(filter)}
        this
    }

    /**
     * Apply the min and max scale to this Composite
     * @param min The min scale (defaults to -1)
     * @param max The max scale (defaults to -1)
     * @return This Composite
     */
    Symbolizer range(double min = -1, double max = -1) {
        super.range(min, max)
        parts.each{part -> part.range(min, max)}
        this
    }

    /**
     * Apply the zindex to this Composite
     * @param z The zoom level
     * @return This Composite
     */
    Symbolizer zindex(int z) {
        super.zindex(z)
        parts.each{part -> part.zindex(z)}
        this
    }

    /**
     * The String representation
     * @return The string representation
     */
    String toString() {
        "Composite (${parts.join(', ')})${filter != Filter.PASS ? filter : ''}"
    }
}
