package geoscript.render.io

import geoscript.render.Map
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class XmlMapReaderTest {

    @Test void getName() {
        MapReader mapReader = new XmlMapReader()
        assertEquals("xml", mapReader.name)
    }

    @Test void readWithOptions() {
        MapReader mapReader = new XmlMapReader()
        String dir = new File("src/test/resources").absolutePath
        Map map = mapReader.read("""<map>
            <width>400</width>
            <height>400</height>
            <imageType>png</imageType>
            <proj>EPSG:4326</proj>
            <backgroundColor>blue</backgroundColor>
            <fixAspectRatio>true</fixAspectRatio>
            <layers>
                <layer>
                    <layertype>layer</layertype>
                    <file>${dir}/states.shp</file>
                </layer>
            </layers>
            <bounds>
                <minX>-135.911779</minX>
                <minY>36.993573</minY>
                <maxX>-96.536779</maxX>
                <maxY>51.405899</maxY>
            </bounds>
        </map>""")
        assertEquals(400, map.width)
        assertEquals(400, map.height)
        assertEquals("png", map.type)
        assertEquals("#0000ff", map.backgroundColor)
        assertEquals(true, map.fixAspectRatio)
        assertEquals(1, map.layers.size())
        map.render("target/map1.png")
    }

    @Test void read() {
        MapReader mapReader = new XmlMapReader()
        String dir = new File("src/test/resources").absolutePath
        Map map = mapReader.read("""<map>
            <layers>
                <layer>
                    <layertype>layer</layertype>
                    <file>${dir}/states.shp</file>
                </layer>
            </layers>
        </map>""")
        assertEquals(600, map.width)
        assertEquals(400, map.height)
        assertEquals("png", map.type)
        assertEquals(null, map.backgroundColor)
        assertEquals(true, map.fixAspectRatio)
        assertEquals(1, map.layers.size())
        map.render("target/map2.png")
    }

}
