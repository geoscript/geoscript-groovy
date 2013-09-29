package geoscript.workspace

import org.junit.Test
import static org.junit.Assert.*
import geoscript.layer.*
import geoscript.feature.*
import geoscript.geom.Point

/**
 *
 */
class MemoryTestCase {

    @Test void create() {
        Memory mem = new Memory()
        Layer l = mem.create('widgets',[new Field("geom", "Point"), new Field("name", "String")])
        assertNotNull(l)
        l.add([new Point(1,1), "one"])
        l.add([new Point(2,2), "two"])
        l.add([new Point(3,3), "three"])
        assertEquals 3, l.count()
    }

    @Test void add() {
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Shapefile shp = new Shapefile(file)

        Memory mem = new Memory()
        Layer l = mem.add(shp, 'counties')
        assertEquals shp.count(), l.count()
    }

    @Test void format() {
        Memory m = new Memory()
        assertEquals "Memory", m.format
    }
}

