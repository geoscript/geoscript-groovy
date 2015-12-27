package geoscript.plot

import geoscript.geom.Bounds
import geoscript.geom.Geometry
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import static org.junit.Assert.*

/**
 * The Scatter Unit Test
 * @author Jared Erickson
 */
class ScatterTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Test void scatterplot() {
        def points = Geometry.createRandomPoints(new Bounds(0,0,100,100).geometry, 10)
        List data = points.geometries.collect{pt ->
            [pt.x,pt.y]
        }
        Chart chart = Scatter.scatterplot(data)
        File file = folder.newFile("scatterplot.png")
        chart.save(file)
        assertTrue(file.exists())
    }

}
