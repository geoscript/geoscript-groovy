package geoscript.plot

import geoscript.geom.Bounds
import geoscript.geom.Geometry
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import static org.junit.jupiter.api.Assertions.*

/**
 * The Regression Unit Test
 * @author Jared Erickson
 */
class RegressionTest {

    @TempDir
    File folder

    @Test void linear() {
        def points = Geometry.createRandomPoints(new Bounds(0,0,100,100).geometry, 10)
        List data = points.geometries.collect{pt ->
            [pt.x,pt.y]
        }
        Chart chart = Regression.linear(data)
        File file = new File(folder,"regression_linear.png")
        chart.save(file)
        assertTrue(file.exists())
    }

    @Test void power() {
        def points = Geometry.createRandomPoints(new Bounds(0,0,100,100).geometry, 10)
        List data = points.geometries.collect{pt ->
            [pt.x,pt.y]
        }
        Chart chart = Regression.power(data)
        File file = new File(folder,"regression_power.png")
        chart.save(file)
        assertTrue(file.exists())
    }

}
