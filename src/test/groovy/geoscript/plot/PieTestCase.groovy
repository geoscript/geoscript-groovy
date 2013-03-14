package geoscript.plot

import org.junit.Test

/**
 * The Pie Unit Test
 * @author Jared Erickson
 */
class PieTestCase {

    @Test void pie() {
        Map data = [
            "A":20,"B":45,"C":2,"D":14
        ]
        Chart chart = Pie.pie(data)
        File file = File.createTempFile("pie_",".png")
        println file
        chart.save(file)
    }

}
