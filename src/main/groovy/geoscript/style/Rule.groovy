package geoscript.style

import geoscript.filter.Filter
import org.geotools.styling.Rule as GtRule

/**
 * A Style Rule
 * @author Jared Erickson
 */
class Rule {

    /**
     * The GeoTools Rule
     */
    GtRule rule

    /**
     * Create a new Rule wrapping a GeoTools Rule
     * @param gtRule The GeoTools Rule
     */
    Rule(GtRule gtRule) {
        this.rule = gtRule
    }

    /**
     * Create a new Rule with a Symbolizer
     * @param symbolizer The Symbolizer
     */
    Rule(Symbolizer symbolizer) {
        this(Style.builder.createRule(symbolizer.symbolizer))
    }

    /**
     * Create a new Rule with a Symbolizer
     * @param symbolizer The Symbolizer
     */
    Rule(Symbolizer symbolizer, Filter filter) {
        this(symbolizer)
        setFilter(filter)
    }


    /**
     * Create a new Rule with a List of Symbolizers
     * @param symbolizers A List of Symbolizers
     */
    Rule(List<Symbolizer> symbolizers) {
        this(createGtRule(symbolizers))
    }

    /**
     * Create a new Rule with a List of Symbolizers
     * @param symbolizers A List of Symbolizers
     */
    Rule(List<Symbolizer> symbolizers, Filter filter) {
        this(symbolizers)
        setFilter(filter)
    }

    /**
     * Create a GeoTools Rule from a List of Symbolizers
     * @param symbolizers A List of Symbolizers
     * @return a GeoTools Rule
     */
    private static GtRule createGtRule(List<Symbolizer> symbolizers) {
        Style.builder.createRule(symbolizers.collect{sym->
            sym.symbolizer
        }.toArray() as org.geotools.styling.Symbolizer[])
    }

    /**
     * Get the Filter
     * @return The Filter
     */
    Filter getFilter() {
        new Filter(rule.filter)
    }

    /**
     * Set the Filter
     * @param filter The new Filter
     */
    void setFilter(Filter filter) {
        rule.filter = filter.filter
    }

    /**
     * Get the List of Symbolizers
     * @return The List of Symbolizers
     */
    List<Symbolizer> getSymbolizers() {
        rule.symbolizers().collect{sym -> new Symbolizer(sym)}
    }

    /**
     * Get the max scale
     * @return The max scale
     */
    double getMaxScale() {
        rule.maxScaleDenominator
    }

    /**
     * Set the max scale
     * @param maxScale The new max scale
     */
    void setMaxScale(double maxScale) {
        rule.maxScaleDenominator = maxScale
    }

    /**
     * Get the min scale
     * @return The min scale
     */
    double getMinScale() {
        rule.minScaleDenominator
    }

    /**
     * Set the min scale
     * @param minScale The new min scale
     */
    void setMinScale(double minScale) {
        rule.minScaleDenominator = minScale
    }

    /**
     * Get the name
     * @return The name
     */
    String getName() {
        rule.name
    }

    /**
     * Set the name
     * @param name The new name
     */
    void setName(String name) {
        rule.name = name
    }

    /**
     * Get the title
     * @return The title
     */
    String getTitle() {
        rule.description.title
    }

    /**
     * Set the title
     * @param title The new title
     */
    void setTitle(String title) {
        rule.description.setTitle(title)
    }

    /**
     * Get the abstract
     * @return The abstract
     */
    String getAbstract() {
        rule.description.getAbstract()
    }

    /**
     * Set the abstract
     * @return abstractStr The new abstract
     */
    void setAbstract(String abstractStr) {
        rule.description.setAbstract(abstractStr)
    }

    /**
     * The string representation
     * @return The string representation
     */
    String toString() {
        //getName() + " (" + getTitle() + ") " + getAbstract()
        rule.toString()
    }
}

