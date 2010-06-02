package geoscript.style

import geoscript.filter.Filter
import org.geotools.styling.Rule as GtRule

/**
 * A Style Rule
 * @author Jared Erickson
 */
class Rule {

    GtRule rule

    Rule() {
        this(Style.builder.createRule(null))
    }

    Rule(Symbolizer symbolizer) {
        this(Style.builder.createRule(symbolizer.symbolizer))
    }

    Rule(List<Symbolizer> symbolizers) {
        this(createGtRule(symbolizers))
    }

    private static GtRule createGtRule(List<Symbolizer> symbolizers) {
        Style.builder.createRule(symbolizers.collect{sym->
            sym.symbolizer
        }.toArray() as org.geotools.styling.Symbolizer[])
    }

    Rule(GtRule gtRule) {
        this.rule = gtRule
    }

    Filter getFilter() {
        new Filter(rule.filter)
    }

    void setFilter(Filter filter) {
        rule.filter = filter.filter
    }

    List<Symbolizer> getSymbolizers() {
        rule.symbolizers.collect{sym -> new Symbolizer(sym)}
    }

    double getMaxScale() {
        rule.maxScaleDenominator
    }

    void setMaxScale(double maxScale) {
        rule.maxScaleDenominator = maxScale
    }

    double getMinScale() {
        rule.minScaleDenominator
    }

    void setMinScale(double minScale) {
        rule.minScaleDenominator = minScale
    }

    String getName() {
        rule.name
    }

    void setName(String name) {
        rule.name = name
    }

    String getTitle() {
        rule.description.title
    }

    void setTitle(String title) {
        rule.description.setTitle(title)
    }

    String getAbstract() {
        rule.description.getAbstract()
    }

    void setAbstract(String abstractStr) {
        rule.description.setAbstract(abstractStr)
    }

    String toString() {
        //getName() + " (" + getTitle() + ") " + getAbstract()
        rule.toString()
    }
}

