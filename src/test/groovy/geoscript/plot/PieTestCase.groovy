package geoscript.plot

import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import static org.junit.Assert.*

/**
 * The Pie Unit Test
 * @author Jared Erickson
 */
class PieTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Test void pie() {
        Map data = [
            "A":20,"B":45,"C":2,"D":14
        ]
        Chart chart = Pie.pie(data)
        File file = folder.newFile("pie.png")
        chart.save(file)
        assertTrue(file.exists())
    }

    @Test void pie3d() {
        Map data = [
                "A":20,"B":45,"C":2,"D":14
        ]
        Chart chart = Pie.pie(data, trid: true)
        File file = folder.newFile("pie3d.png")
        chart.save(file)
        assertTrue(file.exists())
    }

}
