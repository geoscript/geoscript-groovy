package geoscript.workspace

import org.junit.Test
import static org.junit.Assert.*
import geoscript.layer.*
import geoscript.feature.*
import geoscript.geom.*

/**
 * The H2 Workspace Unit Test
 */
class H2TestCase {

    @Test void create() {

        File f = new File("target/h2").absoluteFile
        if (f.exists()) {
            boolean deleted = f.deleteDir()
        }
            

        H2 h2 = new H2("acme", "target/h2")
        Layer l = h2.create('widgets',[new Field("geom", "Point"), new Field("name", "String")])
        assertNotNull(l)
        l.add([new Point(1,1), "one"])
        l.add([new Point(2,2), "two"])
        l.add([new Point(3,3), "three"])
        assertEquals 3, l.count()
    }

    @Test void add() {
        
        File f = new File("target/h2").absoluteFile
        if (f.exists()) {
            boolean deleted = f.deleteDir()
        }

        File file = new File(getClass().getClassLoader().getResource("110m-admin-0-countries.shp").toURI())
        Shapefile shp = new Shapefile(file)
        
        H2 h2 = new H2("acme", "target/h2")
        Layer l = h2.add(shp, 'counties')
        assertEquals shp.count(), l.count()
    }

}

