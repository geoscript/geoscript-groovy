package geoscript.carto

import geoscript.layer.GeoTIFF
import geoscript.layer.Shapefile
import geoscript.render.Map
import geoscript.style.Fill
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

class OverviewMapItemTest {

    @Test
    void create() {

        Map map = new Map(layers: [
                new Shapefile(new File(getClass().getClassLoader().getResource("states.shp").toURI()))
        ])

        Map overViewMap = new Map(layers: [
                new GeoTIFF(new File(getClass().getClassLoader().getResource("raster.tif").toURI())).read()
        ])

        OverviewMapItem item = new OverviewMapItem(10,20,300,400)
                .linkedMap(map)
                .overviewMap(overViewMap)
                .areaStyle(new Fill("red",0.1) + new geoscript.style.Stroke("red",1))
                .zoomIntoBounds(true)
                .scaleFactor(3.0)

        assertEquals(map, item.linkedMap)
        assertEquals(overViewMap, item.overviewMap)
        assertEquals(10, item.x)
        assertEquals(20, item.y)
        assertEquals(300, item.width)
        assertEquals(400, item.height)
        assertTrue(item.toString().startsWith("OverviewMapItem(x = 10, y = 20, width = 300, height = 400,"))
        assertTrue(item.toString().endsWith(")"))
    }


}
