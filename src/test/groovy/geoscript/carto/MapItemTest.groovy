package geoscript.carto

import geoscript.layer.Shapefile
import geoscript.render.Map
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue

class MapItemTest {

    @Test
    void create() {

        Map map = new Map(layers: [
            new Shapefile(new File(getClass().getClassLoader().getResource("states.shp").toURI()))
        ])

        MapItem item = new MapItem(10,20,300,400)
            .map(map)

        assertEquals(map, item.map)
        assertEquals(10, item.x)
        assertEquals(20, item.y)
        assertEquals(300, item.width)
        assertEquals(400, item.height)
        assertTrue(item.toString().startsWith("MapItem(x = 10, y = 20, width = 300, height = 400, map = "))
        assertTrue(item.toString().endsWith(")"))
    }


}
