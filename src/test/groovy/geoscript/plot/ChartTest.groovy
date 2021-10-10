package geoscript.plot

import org.junit.jupiter.api.io.TempDir

import javax.imageio.ImageIO

import static org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * The Chart Unit Test
 * @author Jared Erickson
 */
class ChartTest {

    @TempDir
    File folder

    @Test void constructor() {
        List data = [
            [1,10],[45,12],[23,3],[5,20]
        ]
        Chart chart = Bar.xy(data)
        assertNotNull chart
        assertNotNull chart.chart
    }

    @Test void save() {
        List data = [
            [1,10],[45,12],[23,3],[5,20]
        ]
        Chart chart = Bar.xy(data)
        File file = new File(folder,"xy.png")
        chart.save(file)
        assertTrue file.exists()
        assertTrue file.length() > 0

        file = new File(folder,"xy.jpg")
        chart.save(file)
        assertTrue file.exists()
        assertTrue file.length() > 0

        file = new File(folder,"xy.jpeg")
        chart.save(file)
        assertTrue file.exists()
        assertTrue file.length() > 0
    }

    @Test void saveGif() {
        assertThrows(IllegalArgumentException) {
            List data = [
                    [1, 10], [45, 12], [23, 3], [5, 20]
            ]
            Chart chart = Bar.xy(data)
            File file = new File(folder,"xy.gif")
            chart.save(file)
        }
    }

    @Test void getImage() {
        List data = [
            [1,10],[45,12],[23,3],[5,20]
        ]
        Chart chart = Bar.xy(data)
        def image = chart.image
        assertNotNull image
        File file = new File(folder,"xy.png")
        ImageIO.write(image, "png", file)
        assertTrue file.exists()
        assertTrue file.length() > 0
    }

    @Test void overlay() {
        List data = [
            [1,10],[45,12],[23,3],[5,20]
        ]
        Chart chart1 = Bar.xy(data)
        Chart chart2 = Curve.curve(data)
        Chart chart3 = Regression.linear(data)
        chart1.overlay([chart2,chart3])
        File file = new File(folder,"overlay.png")
        chart1.save(file)
    }
}
