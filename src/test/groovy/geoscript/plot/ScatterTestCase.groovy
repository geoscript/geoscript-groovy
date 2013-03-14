package geoscript.plot

import geoscript.geom.Bounds
import geoscript.geom.Geometry
import org.junit.Test

/**
 * The Scatter Unit Test
 * @author Jared Erickson
 */
class ScatterTestCase {

    @Test void scatterplot() {
        def points = Geometry.createRandomPoints(new Bounds(0,0,100,100).geometry, 10)
        List data = points.geometries.collect{pt ->
            [pt.x,pt.y]
        }
        Chart chart = Scatter.scatterplot(data)
        File file = File.createTempFile("scatterplot_",".png")
        println file
        chart.save(file)
    }

}
