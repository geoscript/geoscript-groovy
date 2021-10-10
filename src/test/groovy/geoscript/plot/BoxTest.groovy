package geoscript.plot

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import static org.junit.jupiter.api.Assertions.*

/**
 * The Box Unit Test
 * @author Jared Erickson
 */
class BoxTest {

    @TempDir
    File folder

    @Test void box() {
        Map data = [
            "A":[1,10,20],"B":[45,39,10],"C":[2,4,9],"D":[14,15,19]
        ]
        Chart chart = Box.box(data)
        File file = new File(folder,"box.png")
        chart.save(file)
        assertTrue(file.exists())
    }
}
