package geoscript.plot

import org.junit.Test

/**
 * The Box Unit Test
 * @author Jared Erickson
 */
class BoxTestCase {

    @Test void box() {
        Map data = [
            "A":[1,10,20],"B":[45,39,10],"C":[2,4,9],"D":[14,15,19]
        ]
        Chart chart = Box.box(data)
        File file = File.createTempFile("box_",".png")
        println file
        chart.save(file)
    }
}
