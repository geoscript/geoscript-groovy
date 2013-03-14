package geoscript.plot

import geoscript.geom.Bounds
import geoscript.geom.Geometry
import org.junit.Test

/**
 * The Regression Unit Test
 * @author Jared Erickson
 */
class RegressionTestCase {

    @Test void linear() {
        def points = Geometry.createRandomPoints(new Bounds(0,0,100,100).geometry, 10)
        List data = points.geometries.collect{pt ->
            [pt.x,pt.y]
        }
        Chart chart = Regression.linear(data)
        File file = File.createTempFile("regression_linear_",".png")
        println file
        chart.save(file)
    }

    @Test void power() {
        def points = Geometry.createRandomPoints(new Bounds(0,0,100,100).geometry, 10)
        List data = points.geometries.collect{pt ->
            [pt.x,pt.y]
        }
        Chart chart = Regression.power(data)
        File file = File.createTempFile("regression_power_",".png")
        println file
        chart.save(file)
    }

}
