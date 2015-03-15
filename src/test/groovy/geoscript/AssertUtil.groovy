package geoscript

import geoscript.geom.Bounds
import static org.junit.Assert.*

/**
 * A collection of assertion utilities.
 * @author Jared Erickson
 */
class AssertUtil {

    static void assertStringsEqual(Map options = [:], String expected, String actual) {
        boolean trim = options.get("trim", false)
        boolean debug = options.get("debug", false)
        boolean removeXmlNS = options.get("removeXmlNS", false)
        StringReader expectedReader = new StringReader(expected)
        StringReader actualReader = new StringReader(actual)
        List<String> expectedLines = expectedReader.readLines()
        List<String> actualLines = actualReader.readLines()
        assertEquals("The number of lines should be equal", expectedLines.size(), actualLines.size())
        expectedLines.eachWithIndex { String exp, int i ->
            String act = actualLines[i]
            if (trim) {
                exp = exp.trim()
                act = act.trim()
            }
            if (removeXmlNS) {
                exp = exp.replaceAll("xmlns.*?(\"|\').*?(\"|\')", "")
                act = act.replaceAll("xmlns.*?(\"|\').*?(\"|\')", "")
            }
            if (debug) {
                println "Expected: ${exp}"
                println "Actual  : ${act}"
            }
            assertEquals("Lines should match", exp, act)
        }
    }

    static void assertBoundsEquals(Bounds expected, Bounds actual, double delta) {
        assertEquals expected.minX, actual.minX, delta
        assertEquals expected.minY, actual.minY, delta
        assertEquals expected.maxX, actual.maxX, delta
        assertEquals expected.maxY, actual.maxY, delta
        assertEquals expected.proj, actual.proj
    }

}
