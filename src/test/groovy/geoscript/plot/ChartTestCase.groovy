package geoscript.plot

import javax.imageio.ImageIO

import static junit.framework.Assert.*
import org.junit.Test

/**
 * The Chart Unit Test
 * @author Jared Erickson
 */
class ChartTestCase {

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
        File file = File.createTempFile("xy_",".png")
        println file
        chart.save(file)
        assertTrue file.exists()
        assertTrue file.length() > 0
    }

    @Test void getImage() {
        List data = [
            [1,10],[45,12],[23,3],[5,20]
        ]
        Chart chart = Bar.xy(data)
        def image = chart.image
        assertNotNull image
        File file = File.createTempFile("xy_",".png")
        println file
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
        File file = File.createTempFile("overlay_",".png")
        println file
        chart1.save(file)
    }

}
