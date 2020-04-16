package geoscript.carto

import geoscript.layer.Shapefile
import geoscript.style.ColorMap
import geoscript.style.Fill
import geoscript.style.Shape
import geoscript.style.Stroke
import java.awt.Color
import org.junit.Test

import java.awt.Font

import static org.junit.Assert.*

class LegendItemTest {

    @Test
    void create() {

        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Shapefile shapefile = new Shapefile(file)
        shapefile.style = new Fill("white") + new Stroke("black", 0.1)

        LegendItem item = new LegendItem(10,20,300,400)
            .title("Legend")
            .titleFont(new Font("Verdana", Font.BOLD, 24))
            .titleColor(Color.BLACK)
            .backgroundColor(Color.WHITE)
            .textFont(new Font("Verdana", Font.PLAIN, 18))
            .textColor(Color.BLUE)
            .legendEntryWidth(25)
            .legendEntryHeight(25)
            .gapBetweenEntries(15)
            .numberFormat("#")
            .addPointEntry("Cities", new Shape("Black", 8, "circle"))
            .addLineEntry("Rivers", new Stroke("Blue", 2))
            .addPolygonEntry("Parcels", new Fill("red", 0.75) + new Stroke("black",1))
            .addGroupEntry("Raster")
            .addColorMapEntry("Potential", new ColorMap(0, 2300, geoscript.filter.Color.getPaletteColors("YellowToRedHeatMap", 7)))
            .addLayer(shapefile)

        assertEquals(10, item.x)
        assertEquals(20, item.y)
        assertEquals(300, item.width)
        assertEquals(400, item.height)
        assertTrue(item.toString().startsWith("LegendItem(x = 10, y = 20, width = 300, height = 400,"))
        assertTrue(item.toString().endsWith(")"))
    }

}
