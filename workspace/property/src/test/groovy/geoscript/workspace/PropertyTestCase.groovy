package geoscript.workspace

import geoscript.feature.Feature
import geoscript.geom.Point
import geoscript.proj.Projection
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.*
import geoscript.layer.Layer
import geoscript.feature.Field
import geoscript.feature.Schema

/**
 * The Property Workspace UnitTest
 * @author Jared Erickson
 */
class PropertyTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Test void read() {

        File dir = new File(getClass().getClassLoader().getResource("points.properties").toURI()).parentFile
        assertNotNull(dir)

        Property property = new Property(dir)
        assertNotNull(property)
        assertEquals "Property", property.format
        assertEquals "Property[${dir}]".toString(), property.toString()

        assertEquals 2, property.layers.size()
        assertTrue property.has("points")
        assertTrue property.has("earthquakes")

        Layer layer = property.get("points")
        assertNotNull(layer)
        assertEquals 4, layer.count

        def features = layer.features

        assertEquals "point 1", features[0].get("name")
        assertEquals "point 2", features[1].get("name")
        assertEquals "point 3", features[2].get("name")
        assertEquals "point 4", features[3].get("name")

        assertEquals "POINT (0 0)", features[0].geom.wkt
        assertEquals "POINT (10 10)", features[1].geom.wkt
        assertEquals "POINT (20 20)", features[2].geom.wkt
        assertEquals "POINT (30 30)", features[3].geom.wkt

        layer = property.get("points.properties")
        assertNotNull(layer)
        assertEquals 4, layer.count
    }

    @Test void create() {
        File dir = folder.newFolder("points")
        Property property = new Property(dir)
        Layer layer = property.create("points", [new Field("geom","Point","EPSG:4326")])
        assertNotNull(layer)
        assertTrue(new File(dir,"points.properties").exists())

        Layer layer2 = property.create(new Schema("lines", [new Field("geom","Point","EPSG:4326")]))
        assertNotNull(layer2)
        assertTrue(new File(dir,"lines.properties").exists())
    }

    @Test void add() {
        File shpDir = new File(getClass().getClassLoader().getResource("states.shp").toURI()).parentFile
        Directory directory = new Directory(shpDir)
        Layer statesLayer = directory.get("states")

        File tempDir = folder.newFolder("states")
        Property property = new Property(tempDir)
        Layer propLayer = property.add(statesLayer, "states")
        assertTrue(new File(tempDir,"states.properties").exists())
        assertEquals statesLayer.count, propLayer.count
    }

    @Test void reprojectToWorkspace() {
        // Create Layer in Memory
        Schema s1 = new Schema("facilities", [new Field("geom","Point", "EPSG:4326"), new Field("name","string"), new Field("price","float")])
        Layer layer1 = new Layer("facilities", s1)
        layer1.add(new Feature([new Point(-122.494165, 47.198096), "House", 12.5], "house1", s1))

        // Reproject to a Property Workspace
        File file = folder.newFile("reproject.properties")
        Property property = new Property(file.parentFile)
        Layer layer2 = layer1.reproject(new Projection("EPSG:2927"), property, "facilities_reprojected")
        assertEquals 1, layer2.count()
        assertEquals 1144727.44, layer2.features[0].geom.x, 0.01
        assertEquals 686301.31, layer2.features[0].geom.y, 0.01
    }

    @Test void reprojectToLayer() {
        // Create Layer in Memory
        Schema s1 = new Schema("facilities", [new Field("geom","Point", "EPSG:4326"), new Field("name","string"), new Field("price","float")])
        Layer layer1 = new Layer("facilities", s1)
        layer1.add(new Feature([new Point(-122.494165, 47.198096), "House", 12.5], "house1", s1))

        // Reproject to a Property Workspace Layer
        File file = folder.newFile("reproject.properties")
        Property property = new Property(file.parentFile)
        Layer propertyLayer = property.create(layer1.schema.reproject("EPSG:2927","facilities_epsg_2927"))
        Layer layer2 = layer1.reproject(propertyLayer)
        assertEquals 1, layer2.count()
        assertEquals 1144727.44, layer2.features[0].geom.x, 0.01
        assertEquals 686301.31, layer2.features[0].geom.y, 0.01
    }

}
