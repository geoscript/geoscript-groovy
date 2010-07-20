package geoscript.style

import geoscript.filter.Filter
import org.geotools.styling.Rule as GtRule
import org.geotools.filter.text.cql2.CQL
import org.junit.Test
import static org.junit.Assert.*

/**
 * The Rule UniTest
 * @author Jared Erickson
 */
class RuleTestCase {

    @Test void singleSymbolizer() {
        def rule = new Rule(
            symbolizers: [
                new LineSymbolizer(
                    strokeColor: "#000000",
                    strokeWidth: 3
                )
            ]
        )
        assertNotNull(rule)
        assertEquals(1, rule.symbolizers.size())
        assertNull(rule.filter)
        assertEquals(0.0, rule.minScaleDenominator, 0.01)
        assertEquals(Double.POSITIVE_INFINITY, rule.maxScaleDenominator, 0.01)

        GtRule gtRule = rule.gtRule
        assertNotNull(gtRule)
        assertEquals(1, gtRule.symbolizers().size())
        assertNull(gtRule.filter)
        assertEquals(0.0, gtRule.minScaleDenominator, 0.01)
        assertEquals(Double.POSITIVE_INFINITY, gtRule.maxScaleDenominator, 0.01)
    }

    @Test void doubleLineRoads() {
        def rule = new Rule(
            symbolizers: [
                new LineSymbolizer(
                    strokeColor: "#333333",
                    strokeWidth: 5,
                    strokeLineCap: "round",
                    zIndex: 0
                ),
                new LineSymbolizer(
                    strokeColor: "#6699FF",
                    strokeWidth: 3,
                    strokeLineCap: "round",
                    zIndex: 1
                )

            ]
        )
        assertNotNull(rule)
        assertEquals(2, rule.symbolizers.size())
        assertNull(rule.filter)
        assertEquals(0.0, rule.minScaleDenominator, 0.01)
        assertEquals(Double.POSITIVE_INFINITY, rule.maxScaleDenominator, 0.01)

        GtRule gtRule = rule.gtRule
        assertNotNull(gtRule)
        assertEquals(2, gtRule.symbolizers().size())
        assertNull(gtRule.filter)
        assertEquals(0.0, gtRule.minScaleDenominator, 0.01)
        assertEquals(Double.POSITIVE_INFINITY, gtRule.maxScaleDenominator, 0.01)
    }

    @Test void ruleWithFilter() {
        def rule = new Rule(
            symbolizers: [
                new LineSymbolizer(
                    strokeColor: "#000000",
                    strokeWidth: 3
                )
            ],
            filter: new Filter("type = 'local-road'")
        )
        assertNotNull(rule)
        assertEquals(1, rule.symbolizers.size())
        assertNotNull(rule.filter)
        assertEquals("type = 'local-road'", rule.filter.cql)
        assertEquals(0.0, rule.minScaleDenominator, 0.01)
        assertEquals(Double.POSITIVE_INFINITY, rule.maxScaleDenominator, 0.01)

        GtRule gtRule = rule.gtRule
        assertNotNull(gtRule)
        assertEquals(1, gtRule.symbolizers().size())
        assertNotNull(gtRule.filter)
        assertEquals("type = 'local-road'", CQL.toCQL(gtRule.filter))
        assertEquals(0.0, gtRule.minScaleDenominator, 0.01)
        assertEquals(Double.POSITIVE_INFINITY, gtRule.maxScaleDenominator, 0.01)
    }

    @Test void ruleWithScale() {
        def rule = new Rule(
            symbolizers: [
                new LineSymbolizer(
                    strokeColor: "#000000",
                    strokeWidth: 3
                )
            ],
            minScaleDenominator: 1800000000,
            maxScaleDenominator: 3600000000
        )
        assertNotNull(rule)
        assertEquals(1, rule.symbolizers.size())
        assertNull(rule.filter)
        assertEquals(1800000000, rule.minScaleDenominator, 0.01)
        assertEquals(3600000000, rule.maxScaleDenominator, 0.01)

        GtRule gtRule = rule.gtRule
        assertNotNull(gtRule)
        assertEquals(1, gtRule.symbolizers().size())
        assertNull(gtRule.filter)
        assertEquals(1800000000, gtRule.minScaleDenominator, 0.01)
        assertEquals(3600000000, gtRule.maxScaleDenominator, 0.01)
    }

}

