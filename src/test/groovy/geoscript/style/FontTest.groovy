package geoscript.style

import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*
import geoscript.filter.Expression

/**
 * The Font Unit Test
 * @author Jared Erickson
 */
class FontTest {

    @Test void constructors() {

        // Default constructor
        Font font = new Font()
        assertEquals "normal", font.style.value
        assertEquals "normal", font.weight.value
        assertEquals 10, font.size.value
        assertEquals "serif", font.family.value
        assertEquals "Font(style = normal, weight = normal, size = 10, family = serif)", font.toString()

        // Full constructor
        font = new Font("italic", "bold", 12, "Verdana")
        assertEquals "italic", font.style.value
        assertEquals "bold", font.weight.value
        assertEquals 12, font.size.value
        assertEquals "Verdana", font.family.value
        assertEquals "Font(style = italic, weight = bold, size = 12, family = Verdana)", font.toString()

        // Full constructor with Expressions
        font = new Font(new Expression("italic"), new Expression("bold"), new Expression(12), new Expression("Verdana"))
        assertEquals "italic", font.style.value
        assertEquals "bold", font.weight.value
        assertEquals 12, font.size.value
        assertEquals "Verdana", font.family.value
        assertEquals "Font(style = italic, weight = bold, size = 12, family = Verdana)", font.toString()

        // Named parameters
        font = new Font(weight: "bold", size: 32)
        assertEquals "normal", font.style.value
        assertEquals "bold", font.weight.value
        assertEquals 32, font.size.value
        assertEquals "serif", font.family.value
        assertEquals "Font(style = normal, weight = bold, size = 32, family = serif)", font.toString()
    }

    @Test void apply() {
        def text = Symbolizer.styleFactory.createTextSymbolizer()
        Font font = new Font()
        font.apply(text)
        assertEquals "normal", text.font.style.value
        assertEquals "normal", text.font.weight.value
        assertEquals 10, text.font.size.value
        assertEquals "serif", text.font.family[0].value
    }

    @Test void prepare() {
        def rule = Symbolizer.styleFactory.createRule()
        rule.symbolizers().add(Symbolizer.styleFactory.createTextSymbolizer())
        Font font = new Font()
        font.prepare(rule)
        def text = rule.symbolizers()[0]
        assertEquals "normal", text.font.style.value
        assertEquals "normal", text.font.weight.value
        assertEquals 10, text.font.size.value
        assertEquals "serif", text.font.family[0].value
    }

}
