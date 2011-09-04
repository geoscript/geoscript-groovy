package geoscript.style

import org.junit.Test
import static org.junit.Assert.*
import geoscript.filter.Filter

/**
 * The Composite Unit Test
 * @author Jared Erickson
 */
class CompositeTestCase {

    @Test void constructors() {
        Composite composite = new Composite(new Fill("red"))
        assertEquals 1, composite.parts.size()

        composite = new Composite([new Fill("red"), new Stroke("blue")])
        assertEquals 2, composite.parts.size()
    }

    @Test void where() {
        Composite comp = new Fill("red") + new Stroke("blue")
        assertEquals Filter.PASS, comp.filter
        assertEquals Filter.PASS, comp.parts[0].filter
        assertEquals Filter.PASS, comp.parts[1].filter

        assertTrue comp.where("NAME = 'Washington'") instanceof Symbolizer
        assertEquals new Filter("NAME = 'Washington'"), comp.filter
        assertEquals new Filter("NAME = 'Washington'"), comp.parts[0].filter
        assertEquals new Filter("NAME = 'Washington'"), comp.parts[1].filter
    }

    @Test void range() {
        Composite comp = new Fill("red") + new Stroke("blue")
        assertEquals(-1, comp.scale.min, 0.1)
        assertEquals(-1, comp.scale.max, 0.1)
        assertEquals(-1, comp.parts[0].scale.min, 0.1)
        assertEquals(-1, comp.parts[0].scale.max, 0.1)
        assertEquals(-1, comp.parts[1].scale.min, 0.1)
        assertEquals(-1, comp.parts[1].scale.max, 0.1)

        assertTrue comp.range(100, 950) instanceof Symbolizer
        assertEquals(100, comp.scale.min, 0.1)
        assertEquals(950, comp.scale.max, 0.1)
        assertEquals(100, comp.parts[0].scale.min, 0.1)
        assertEquals(950, comp.parts[0].scale.max, 0.1)
        assertEquals(100, comp.parts[1].scale.min, 0.1)
        assertEquals(950, comp.parts[1].scale.max, 0.1)
    }

    @Test void zindex() {
        Composite comp = new Fill("red") + new Stroke("blue")
        assertEquals 0, comp.z
        assertEquals 0, comp.parts[0].z
        assertEquals 0, comp.parts[1].z

        assertTrue comp.zindex(5) instanceof Symbolizer
        assertEquals 5, comp.z
        assertEquals 5, comp.parts[0].z
        assertEquals 5, comp.parts[1].z
    }

    @Test void string() {
        Composite comp = new Fill("red") + new Stroke("blue")
        assertEquals "Composite (Fill(color = #ff0000, opacity = 1.0), Stroke(color = #0000ff, width = 1))", comp.toString()
    }

}
