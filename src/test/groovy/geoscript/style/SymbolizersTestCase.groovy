package geoscript.style

import org.junit.Test
import static org.junit.Assert.*
import static geoscript.style.Symbolizers.*

/**
 * The Symbolizers UnitTest
 */
class SymbolizersTestCase {

    @Test void fill() {
        Fill fill1 = fill("#003300", 0.65)
        assertEquals "#003300", fill1.color.value
        assertEquals 0.65, fill1.opacity.value, 0.1

        Fill fill2 = fill(opacity: 0.65, color: "#003300")
        assertEquals "#003300", fill2.color.value
        assertEquals 0.65, fill2.opacity.value, 0.1
    }

    @Test void stroke() {
        Stroke stroke1 = stroke("wheat", 1.2, [5,2], "square", "bevel",0.45)
        assertEquals "#f5deb3", stroke1.color.value
        assertEquals 1.2, stroke1.width.value, 0.1
        assertEquals 5, stroke1.dash[0]
        assertEquals 2, stroke1.dash[1]
        assertEquals "square", stroke1.cap.value
        assertEquals "bevel", stroke1.join.value
        assertEquals 0.45, stroke1.opacity.value
        assertEquals "Stroke(color = #f5deb3, width = 1.2)", stroke1.toString()

        Stroke stroke2 = stroke(color: "wheat", width: 1.2, dash: [5,2], cap: "square", join: "bevel", opacity: 0.45)
        assertEquals "#f5deb3", stroke2.color.value
        assertEquals 1.2, stroke2.width.value, 0.1
        assertEquals 5, stroke2.dash[0]
        assertEquals 2, stroke2.dash[1]
        assertEquals "square", stroke2.cap.value
        assertEquals "bevel", stroke2.join.value
        assertEquals 0.45, stroke2.opacity.value
        assertEquals "Stroke(color = #f5deb3, width = 1.2)", stroke2.toString()
    }

}
