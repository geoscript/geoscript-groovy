package geoscript.plot

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import static org.junit.jupiter.api.Assertions.*

/**
 * The Bar Unit Test
 * @author Jared Erickson
 */
class BarTest {

    @TempDir
    File folder

    @Test void xy() {
        List data = [
            [1,10],[45,12],[23,3],[5,20]
        ]
        Chart chart = Bar.xy(data)
        File file = new File(folder,"xy.png")
        chart.save(file)
        assertTrue(file.exists())
    }

    @Test void category() {
        Map data = [
            "A":20,"B":45,"C":2,"D":14
        ]
        Chart chart = Bar.category(data)
        File file = new File(folder,"category.png")
        chart.save(file)
        assertTrue(file.exists())
    }

    @Test void categoryStacked() {
        Map data = [
                "A": ["B":45,"C":2,"D":14],
                "F": ["G":45,"H":2,"I":14]
        ]
        Chart chart = Bar.category(data, stacked: true)
        File file = new File(folder,"categoryStacked.png")
        chart.save(file)
        assertTrue(file.exists())
    }

    @Test void category3DStacked() {
        Map data = [
                "A": ["B":45,"C":2,"D":14],
                "F": ["G":45,"H":2,"I":14]
        ]
        Chart chart = Bar.category(data, trid: true, stacked: true)
        File file = new File(folder,"category3Dstacked.png")
        chart.save(file)
        assertTrue(file.exists())
    }

    @Test void category3D() {
        Map data = [
                "A":20,"B":45,"C":2,"D":14
        ]
        Chart chart = Bar.category(data, trid: true)
        File file = new File(folder,"category3D.png")
        chart.save(file)
        assertTrue(file.exists())
    }

    @Test void categorySeries() {
        Map data = [
                "A": ["B":45,"C":2,"D":14],
                "F": ["G":45,"H":2,"I":14],
        ]
        Chart chart = Bar.category(data)
        File file = new File(folder,"categorySeries.png")
        chart.save(file)
        assertTrue(file.exists())
    }

}
