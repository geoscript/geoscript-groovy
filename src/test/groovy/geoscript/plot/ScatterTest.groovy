package geoscript.plot

import geoscript.geom.Bounds
import geoscript.geom.Geometry
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import static org.junit.jupiter.api.Assertions.*

/**
 * The Scatter Unit Test
 * @author Jared Erickson
 */
class ScatterTest {

    @TempDir
    File folder

    @Test void scatterplot() {
        def points = Geometry.createRandomPoints(new Bounds(0,0,100,100).geometry, 10)
        List data = points.geometries.collect{pt ->
            [pt.x,pt.y]
        }
        Chart chart = Scatter.scatterplot(data)
        File file = new File(folder,"scatterplot.png")
        chart.save(file)
        assertTrue(file.exists())
    }

}
