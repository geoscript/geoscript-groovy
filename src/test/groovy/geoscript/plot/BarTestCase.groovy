package geoscript.plot

import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import static org.junit.Assert.*

/**
 * The Bar Unit Test
 * @author Jared Erickson
 */
class BarTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Test void xy() {
        List data = [
            [1,10],[45,12],[23,3],[5,20]
        ]
        Chart chart = Bar.xy(data)
        File file = folder.newFile("xy.png")
        chart.save(file)
        assertTrue(file.exists())
    }

    @Test void category() {
        Map data = [
            "A":20,"B":45,"C":2,"D":14
        ]
        Chart chart = Bar.category(data)
        File file = folder.newFile("category.png")
        chart.save(file)
        assertTrue(file.exists())
    }

}
