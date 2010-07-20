package geoscript.style

import geoscript.filter.Filter
import org.geotools.styling.Rule as GtRule

/**
 * A Style Rule
 * @author Jared Erickson
 */
class Rule {

    /**
     * A List of Symbolizers
     */
    List<Symbolizer> symbolizers

    /**
     * A Filter
     */
    Filter filter

    /**
     * The minimum scale denominator
     */
    double minScaleDenominator = 0.0

    /**
     * The maximum scale denominator
     */
    double maxScaleDenominator = Double.POSITIVE_INFINITY

    /**
     * The Rule's name
     */
    String name

    /**
     * The Rule's title
     */
    String title

    /**
     * Get a GeoTools Rule for this GeoScript Rule
     * @return A GeoTools Rule
     */
    GtRule getGtRule() {
        GtRule rule = Style.builder.createRule(symbolizers.collect{sym->
            sym.symbolizer
        }.toArray() as org.geotools.styling.Symbolizer[])
        if (filter) {
            rule.filter = filter.filter
        }
        rule.minScaleDenominator = minScaleDenominator
        rule.maxScaleDenominator = maxScaleDenominator
        rule.name = name
        rule.description.title = title
        rule
    }

}

