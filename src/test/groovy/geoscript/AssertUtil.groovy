package geoscript

import static org.junit.Assert.*

/**
 * A collection of assertion utilities.
 * @author Jared Erickson
 */
class AssertUtil {

    static void assertStringsEqual(String expected, String actual) {
        StringReader expectedReader = new StringReader(expected)
        StringReader actualReader = new StringReader(actual)
        List<String> expectedLines = expectedReader.readLines()
        List<String> actualLines = actualReader.readLines()
        assertEquals(expectedLines.size(), actualLines.size())
        expectedLines.eachWithIndex { String exp, int i ->
            String act = actualLines[i]
            assertEquals(exp, act)
        }
    }

}
