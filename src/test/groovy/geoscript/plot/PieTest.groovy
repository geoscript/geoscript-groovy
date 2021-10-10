package geoscript.plot

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import static org.junit.jupiter.api.Assertions.*

/**
 * The Pie Unit Test
 * @author Jared Erickson
 */
class PieTest {

    @TempDir
    File folder

    @Test void pie() {
        Map data = [
            "A":20,"B":45,"C":2,"D":14
        ]
        Chart chart = Pie.pie(data)
        File file = new File(folder,"pie.png")
        chart.save(file)
        assertTrue(file.exists())
    }

    @Test void pie3d() {
        Map data = [
                "A":20,"B":45,"C":2,"D":14
        ]
        Chart chart = Pie.pie(data, trid: true)
        File file = new File(folder,"pie3d.png")
        chart.save(file)
        assertTrue(file.exists())
    }

}
