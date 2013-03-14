package geoscript.plot

import org.junit.Test

/**
 * The Curve Unit Test
 * @author Jared Erickson
 */
class CurveTestCase {

    @Test void curve() {
        List data = [
                [1,10],[45,12],[23,3],[5,20]
        ]
        Chart chart = Curve.curve(data)
        File file = File.createTempFile("curve_",".png")
        println file
        chart.save(file)
    }

}
