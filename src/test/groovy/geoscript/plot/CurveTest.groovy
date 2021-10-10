package geoscript.plot

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import static org.junit.jupiter.api.Assertions.*

/**
 * The Curve Unit Test
 * @author Jared Erickson
 */
class CurveTest {

    @TempDir
    File folder

    @Test void curve() {
        List data = [
                [1,10],[45,12],[23,3],[5,20]
        ]
        Chart chart = Curve.curve(data)
        File file = new File(folder,"curve.png")
        chart.save(file)
        assertTrue(file.exists())
    }

    @Test void curveSmooth() {
        List data = [
                [1,10],[45,12],[23,3],[5,20]
        ]
        Chart chart = Curve.curve(data, smooth: true)
        File file = new File(folder,"curveSmooth.png")
        chart.save(file)
        assertTrue(file.exists())
    }

    @Test void curve3D() {
        List data = [
                [1,10],[45,12],[23,3],[5,20]
        ]
        Chart chart = Curve.curve(data, trid: true)
        File file = new File(folder,"curve3d.png")
        chart.save(file)
        assertTrue(file.exists())
    }

}
