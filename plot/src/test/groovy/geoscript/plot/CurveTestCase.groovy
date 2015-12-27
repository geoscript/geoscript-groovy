package geoscript.plot

import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import static org.junit.Assert.*

/**
 * The Curve Unit Test
 * @author Jared Erickson
 */
class CurveTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Test void curve() {
        List data = [
                [1,10],[45,12],[23,3],[5,20]
        ]
        Chart chart = Curve.curve(data)
        File file = folder.newFile("curve.png")
        chart.save(file)
        assertTrue(file.exists())
    }

}
