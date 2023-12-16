package geoscript.workspace

import geoscript.feature.Field
import geoscript.geom.Point
import geoscript.layer.Layer
import geoscript.proj.Projection
import geoscript.render.Map
import geoscript.style.Fill
import geoscript.style.Stroke
import geoscript.style.Symbolizer
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import static geoscript.render.Draw.*

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
        def tableName = h2gis.load(file.getAbsolutePath(), true)
        assertEquals("STATES", tableName[0])
        Layer layer = h2gis.get("STATES")
        assertEquals(49, layer.getCount())
        def minMax = layer.minmax("SAMP_POP")
        assertEquals 72696.0, minMax.min, 0.1
        assertEquals 3792553.0, minMax.max, 0.1
    }

    @Test void linkShapeFile() {
        H2GIS h2gis = new H2GIS(new File(folder,"h2gis.db"))
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        def tableName = h2gis.link(file.getAbsolutePath(), true)
        assertEquals("states", tableName)
        Layer layer = h2gis.get("STATES")
        assertEquals(49, layer.getCount())
        def minMax = layer.minmax("SAMP_POP")
        assertEquals 72696.0, minMax.min, 0.1
        assertEquals 3792553.0, minMax.max, 0.1
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
        H2GIS h2gis = new H2GIS(new File(folder,"h2gis.db"))
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
        H2GIS h2gis = new H2GIS(new File(folder,"h2gis.db"))
        h2gis.getSql().execute("drop table if exists  \"widgets\"".toString())
        assertEquals "H2GIS", h2gis.format
        Layer layer = h2gis.create('widgets',[new Field("geom", "Point"), new Field("name", "String")])
        assertNotNull(layer)
        layer.add([new Point(1,1), "one"])
        layer.add([new Point(2,2), "two"])
        layer.add([new Point(3,3), "three"])
        assertEquals 3, layer.count()
        File outputFile =  new File(folder, "widgets.shp")
        h2gis.save("widgets", outputFile.getAbsolutePath(), true)
        def  outputTable = h2gis.link(outputFile.getAbsolutePath(), true)
        assertEquals(3, h2gis.get(outputTable).count)
        h2gis.close()
    }

    @Test void writeSaveLoadShapeFile() {
        H2GIS h2gis = new H2GIS(new File(folder,"h2gis.db"))
        h2gis.getSql().execute("drop table if exists  \"widgets\"".toString())
        assertEquals "H2GIS", h2gis.format
        Layer layer = h2gis.create('widgets',[new Field("geom", "Point"), new Field("name", "String")])
        assertNotNull(layer)
        layer.add([new Point(1,1), "one"])
        layer.add([new Point(2,2), "two"])
        layer.add([new Point(3,3), "three"])
        assertEquals 3, layer.count()
        File outputFile =  new File(folder, "widgets.shp")
        h2gis.save(layer.getName(), outputFile.getAbsolutePath(), true)
        def  outputTable = h2gis.load(outputFile.getAbsolutePath(), "test_imported")
        assertEquals(3, h2gis.get(outputTable[0]).count)
        h2gis.close()
    }

    @Test void loadFromDatabase() {
        H2GIS h2gis = new H2GIS(new File(folder,"h2gis.db"))
        H2GIS h2gis_target = new H2GIS(new File(folder,"h2gis_target.db"))
        h2gis.getSql().execute("drop table if exists  \"widgets\"".toString())
        assertEquals "H2GIS", h2gis.format
        Layer layer = h2gis.create('widgets',[new Field("geom", "Point"), new Field("name", "String")])
        assertNotNull(layer)
        layer.add([new Point(1,1), "one"])
        layer.add([new Point(2,2), "two"])
        layer.add([new Point(3,3), "three"])
        assertEquals 3, layer.count()
        h2gis_target.load(h2gis, layer.getName(), -1)
        Layer target_Layer = h2gis_target.get(layer.getName())
        assertNotNull(target_Layer)
        assertEquals 3, target_Layer.count()
        h2gis.close()
        h2gis_target.close()
    }

    @Test void saveToDatabase() {
        H2GIS h2gis = new H2GIS(new File(folder,"h2gis.db"))
        H2GIS h2gis_target = new H2GIS(new File(folder,"h2gis_target.db"))
        h2gis.getSql().execute("drop table if exists  \"widgets\"".toString())
        assertEquals "H2GIS", h2gis.format
        Layer layer = h2gis.create('widgets',[new Field("geom", "Point"), new Field("name", "String")])
        assertNotNull(layer)
        layer.add([new Point(1,1), "one"])
        layer.add([new Point(2,2), "two"])
        layer.add([new Point(3,3), "three"])
        assertEquals 3, layer.count()
        h2gis.save(h2gis_target,layer.getName(), true)
        Layer target_Layer = h2gis_target.get(layer.getName())
        assertNotNull(target_Layer)
        assertEquals 3, target_Layer.count()
        h2gis.close()
        h2gis_target.close()
    }

    @Test void saveQueryToDatabase() {
        H2GIS h2gis = new H2GIS(new File(folder,"h2gis.db"))
        H2GIS h2gis_target = new H2GIS(new File(folder,"h2gis_target.db"))
        h2gis.getSql().execute("drop table if exists  \"widgets\"".toString())
        assertEquals "H2GIS", h2gis.format
        Layer layer = h2gis.create('widgets',[new Field("geom", "Point"), new Field("name", "String")])
        assertNotNull(layer)
        layer.add([new Point(1,1), "one"])
        layer.add([new Point(2,2), "two"])
        layer.add([new Point(3,3), "three"])
        assertEquals 3, layer.count()
        h2gis.save(h2gis_target,"(SELECT * FROM \"${layer.getName()}\" WHERE \"name\"= 'two')", "output_layer", true)
        Layer target_Layer = h2gis_target.get("output_layer")
        assertNotNull(target_Layer)
        assertEquals 1, target_Layer.count()
        h2gis.close()
        h2gis_target.close()
    }

    @Test void saveAndInsertToDatabase() {
        H2GIS h2gis = new H2GIS(new File(folder,"h2gis.db"))
        H2GIS h2gis_target = new H2GIS(new File(folder,"h2gis_target.db"))
        h2gis.getSql().execute("drop table if exists  \"widgets\"".toString())
        assertEquals "H2GIS", h2gis.format
        Layer layer = h2gis.create('widgets',[new Field("geom", "Point"), new Field("name", "String")])
        assertNotNull(layer)
        layer.add([new Point(1,1), "one"])
        layer.add([new Point(2,2), "two"])
        layer.add([new Point(3,3), "three"])
        assertEquals 3, layer.count()
        h2gis.save(h2gis_target,layer.getName(), true)
        Layer target_Layer = h2gis_target.get(layer.getName())
        assertNotNull(target_Layer)
        assertEquals 3, target_Layer.count()
        h2gis.insert(h2gis_target,layer.getName())
        target_Layer = h2gis_target.get(layer.getName())
        assertNotNull(target_Layer)
        assertEquals 6, target_Layer.count()
        h2gis.close()
        h2gis_target.close()
    }

    @Disabled
    //TODO the map is weel rendered but there is an error message on org.geotools.renderer.lite.StreamingRenderer getStyleQuery
    @Test void drawLinkedShapeFile() {
        H2GIS h2gis = new H2GIS(new File(folder,"h2gis.db"))
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        def tableName = h2gis.link(file.getAbsolutePath(), true)
        assertEquals("states", tableName)
        Layer layer = h2gis.get("STATES")
        Symbolizer symbolizer = new Fill("gray") + new Stroke("#ffffff")
        layer.setStyle(symbolizer)
        layer.setProj(new Projection("EPSG:2927"))
        Map map = new Map()
        map.proj = new Projection("EPSG:2927")
        map.addLayer(layer)
        map.bounds = layer.bounds
        def image = map.renderToImage()
        assertNotNull(image)

        File out = new File(folder,"map.png")
        javax.imageio.ImageIO.write(image, "png", out);
        assertTrue(out.exists())
        map.close()
        h2gis.close()
    }
}
