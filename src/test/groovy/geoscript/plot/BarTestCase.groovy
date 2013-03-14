package geoscript.plot

import org.junit.Test

/**
 * The Bar Unit Test
 * @author Jared Erickson
 */
class BarTestCase {

    @Test void xy() {
        List data = [
            [1,10],[45,12],[23,3],[5,20]
        ]
        Chart chart = Bar.xy(data)
        File file = File.createTempFile("xy_",".png")
        println file
        chart.save(file)
    }

    @Test void category() {
        Map data = [
            "A":20,"B":45,"C":2,"D":14
        ]
        Chart chart = Bar.category(data)
        File file = File.createTempFile("category_",".png")
        println file
        chart.save(file)
    }

}
