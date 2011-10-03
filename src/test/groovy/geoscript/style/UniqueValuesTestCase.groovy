package geoscript.style

import org.junit.Test
import static org.junit.Assert.*
import geoscript.filter.Color
import geoscript.filter.Expression
import geoscript.map.Map
import geoscript.layer.Shapefile

/**
 * The UniqueValues UnitTest
 * @author Jared Erickson
 */
class UniqueValuesTestCase {

    @Test void create() {

        // Get states shapefile
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Shapefile shapefile = new Shapefile(file)

        // Create a Map
        Map map = new Map()
        map.addLayer(shapefile)

        // Default is random colors
        UniqueValues sym1 = new UniqueValues(shapefile, "STATE_ABBR", "Greens")
        assertNotNull(sym1)
        assertEquals(49, sym1.parts.size())

        shapefile.style = sym1
        File imgFile = File.createTempFile("states",".png")
        println imgFile
        map.render(imgFile)

        // Color palette
        UniqueValues sym2 = new UniqueValues(shapefile, "STATE_ABBR", "Greens")
        assertNotNull(sym2)
        assertEquals(49, sym2.parts.size())

        shapefile.style = sym2
        imgFile = File.createTempFile("states",".png")
        println imgFile
        map.render(imgFile)

        // Color list
        UniqueValues sym3 = new UniqueValues(shapefile, "STATE_ABBR", ["teal","slateblue","tan","wheat","salmon"])
        assertNotNull(sym3)
        assertEquals(49, sym3.parts.size())

        shapefile.style = sym3
        imgFile = File.createTempFile("states",".png")
        println imgFile
        map.render(imgFile)

        // Color Closure
        UniqueValues sym4 = new UniqueValues(shapefile, "STATE_ABBR", {i,v -> Color.getRandom()})
        assertNotNull(sym4)
        assertEquals(49, sym4.parts.size())

        shapefile.style = sym4
        imgFile = File.createTempFile("states",".png")
        println imgFile
        map.render(imgFile)
    }
}
