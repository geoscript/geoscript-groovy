package geoscript.plot

import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import static org.junit.Assert.*

/**
 * The Box Unit Test
 * @author Jared Erickson
 */
class BoxTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Test void box() {
        Map data = [
                "A":[1,10,20],"B":[45,39,10],"C":[2,4,9],"D":[14,15,19]
        ]
        Chart chart = Box.box(data)
        File file = folder.newFile("box.png")
        chart.save(file)
        assertTrue(file.exists())
    }
}
