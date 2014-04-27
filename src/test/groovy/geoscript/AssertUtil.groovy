package geoscript

import static org.junit.Assert.*

/**
 * A collection of assertion utilities.
 * @author Jared Erickson
 */
class AssertUtil {

    static void assertStringsEqual(Map options = [:], String expected, String actual) {
        boolean trim = options.get("trim", false)
        boolean debug = options.get("debug", false)
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
            if (debug) {
                println "Expected: ${exp}"
                println "Actual  : ${act}"
            }
            assertEquals("Lines should match", exp, act)
        }
    }

}
