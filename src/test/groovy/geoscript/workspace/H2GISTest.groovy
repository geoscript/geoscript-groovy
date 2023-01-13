package geoscript.workspace

import geoscript.feature.Field
import geoscript.geom.Point
import geoscript.layer.Layer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import static org.junit.jupiter.api.Assertions.*

/**
 * The H2 Workspace Unit Test
 */

class H2GISTest {
    @TempDir
    File folder

    @Test void loadShapeFile() {
        H2GIS h2gis = new H2GIS(new File(folder,"h2gis.db"))
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        String tableName = h2gis.load(file.getAbsolutePath(), true)
        println(tableName)
    }

    @Test void remove() {
        H2GIS h2gis = new H2GIS(new File(folder,"h2gis.db"))
        assertEquals "H2GIS", h2gis.format
        // Add
        Layer l = h2gis.create('widgets',[new Field("geom", "Point"), new Field("name", "String")])
        assertNotNull(l)
        l.add([new Point(1,1), "one"])
        l.add([new Point(2,2), "two"])
        l.add([new Point(3,3), "three"])
        assertEquals 3, l.count()
        // Get
        assertNotNull(h2gis.get("widgets"))
        // Remove
        h2gis.remove("widgets")
        boolean exceptionThrown = false
        try {
            h2gis.get("widgets")
        } catch (IOException ex) {
            exceptionThrown = true
        }
        assertTrue(exceptionThrown)
        h2gis.close()
    }

    @Test void createFromPath() {
        H2GIS h2gis = new H2GIS("./target/mydb")
        assertEquals "H2GIS", h2gis.format
        Layer l = h2gis.create('widgets',[new Field("geom", "Point"), new Field("name", "String")])
        assertNotNull(l)
        l.add([new Point(1,1), "one"])
        l.add([new Point(2,2), "two"])
        l.add([new Point(3,3), "three"])
        assertEquals 3, l.count()
        h2gis.close()
    }

    @Test void writeLinkShapeFile() {
        H2GIS h2gis = new H2GIS("./target/mydb")
        h2gis.getSql().execute("drop table if exists  \"widgets\"".toString())
        assertEquals "H2GIS", h2gis.format
        Layer layer = h2gis.create('widgets',[new Field("geom", "Point"), new Field("name", "String")])
        assertNotNull(layer)
        layer.add([new Point(1,1), "one"])
        layer.add([new Point(2,2), "two"])
        layer.add([new Point(3,3), "three"])
        assertEquals 3, layer.count()
        File outputFile =  new File("./target/widgets.shp")
        h2gis.save("widgets", outputFile.getAbsolutePath())
        def  outputTable = h2gis.linkedFile(outputFile.getAbsolutePath(), true)
        assertEquals(3, h2gis.get(outputTable).count)
        h2gis.close()
    }

    @Test void writeSaveLoadShapeFile() {
        H2GIS h2gis = new H2GIS("./target/mydb")
        h2gis.getSql().execute("drop table if exists  \"widgets\"".toString())
        assertEquals "H2GIS", h2gis.format
        Layer layer = h2gis.create('widgets',[new Field("geom", "Point"), new Field("name", "String")])
        assertNotNull(layer)
        layer.add([new Point(1,1), "one"])
        layer.add([new Point(2,2), "two"])
        layer.add([new Point(3,3), "three"])
        assertEquals 3, layer.count()
        File outputFile =  new File("./target/widgets.shp")
        h2gis.save("widgets", outputFile.getAbsolutePath())
        def  outputTable = h2gis.load(outputFile.getAbsolutePath(), "\"test_imported\"")
        assertEquals(3, h2gis.get("test_imported").count)
        h2gis.close()
    }

}
