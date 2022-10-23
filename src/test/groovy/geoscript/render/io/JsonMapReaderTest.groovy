package geoscript.render.io

import geoscript.render.Map
import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*

class JsonMapReaderTest {

    @Test void getName() {
        MapReader mapReader = new JsonMapReader()
        assertEquals("json", mapReader.name)
    }

    @Test void readWithOptions() {
        MapReader mapReader = new JsonMapReader()
        String dir = new File("src/test/resources").absolutePath
        Map map = mapReader.read("""{
            "width": 400,
            "height": 400,
            "imageType": "png",
            "backgroundColor": "blue",
            "proj": "EPSG:4326",
            "bounds": {
                "minX": -135.911779,
                "minY": 36.993573,
                "maxX": -96.536779,
                "maxY": 51.405899
            },
            "layers": [
                {"layertype": "layer", "file": "${dir}/states.shp"}
            ]
        }""")
        assertEquals(400, map.width)
        assertEquals(400, map.height)
        assertEquals("png", map.type)
        assertEquals("#0000ff", map.backgroundColor)
        assertEquals(1, map.layers.size())
    }

    @Test void read() {
        MapReader mapReader = new JsonMapReader()
        String dir = new File("src/test/resources").absolutePath
        Map map = mapReader.read("""{
            "layers": [
                {"layertype": "layer", "file": "${dir}/states.shp"}
            ]
        }""")
        assertEquals(600, map.width)
        assertEquals(400, map.height)
        assertEquals("png", map.type)
        assertEquals(1, map.layers.size())
    }
}
