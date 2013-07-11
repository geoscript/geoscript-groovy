package geoscript.layer

import geoscript.filter.Expression
import geoscript.workspace.Directory
import org.junit.Test
import static org.junit.Assert.*
import geoscript.feature.Schema
import geoscript.feature.Field
import geoscript.feature.Feature
import geoscript.proj.Projection
import geoscript.filter.Filter
import geoscript.workspace.Memory
import geoscript.workspace.Property
import geoscript.geom.*
import geoscript.workspace.Workspace
import geoscript.workspace.H2

/**
 * The Layer UnitTest
 */
class LayerTestCase {

    @Test void eachFeature() {
        Layer layer = new Shapefile(new File(getClass().getClassLoader().getResource("states.shp").toURI()))
        int count = 0
        layer.eachFeature({f ->
            assertTrue f instanceof Feature
            count++
        })
        assertEquals 49, count
        
        String name
        layer.eachFeature("STATE_NAME = 'Maryland'", {f ->
            name = f.get("STATE_NAME")
        })
        assertEquals "Maryland", name
    }

    @Test void collectFromFeature() {
        Layer layer = new Shapefile(new File(getClass().getClassLoader().getResource("states.shp").toURI()))
        List results = layer.collectFromFeature({f ->
            f.get("STATE_NAME")
        })
        assertEquals 49, results.size()
        assertTrue results.contains("Utah")

        results = layer.collectFromFeature("STATE_NAME = 'Utah'", {f ->
            f.get("STATE_NAME")
        })
        assertEquals 1, results.size()
        assertEquals "Utah", results[0]
    }
    
    @Test void getProjection() {
        Schema s1 = new Schema("facilities", [new Field("geom","Point", "EPSG:2927"), new Field("name","string"), new Field("price","float")])
        Layer layer1 = new Layer("facilities", s1)
        assertEquals "EPSG:2927", layer1.proj.toString()
    }

    @Test void setProjection() {
        Schema s1 = new Schema("facilities", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])
        Layer layer1 = new Layer("facilities", s1)
        layer1.proj = "EPSG:2927"
        assertEquals "EPSG:2927", layer1.proj.toString()
    }

    @Test void count() {
        Schema s1 = new Schema("facilities", [new Field("geom","Point", "EPSG:2927"), new Field("name","string"), new Field("price","float")])
        Layer layer1 = new Layer("facilities", s1)
        assertEquals 0, layer1.count()
        layer1.add(new Feature([new Point(111,-47), "House", 12.5], "house1", s1))
        assertEquals 1, layer1.count()

        Layer layer2 = new Shapefile(new File(getClass().getClassLoader().getResource("states.shp").toURI()))
        assertEquals 49, layer2.count()
        assertEquals 1, layer2.count(new Filter("STATE_NAME='Washington'"))
        assertEquals 1, layer2.count("STATE_NAME='Washington'")
        assertEquals 0, layer2.count(new Filter("STATE_NAME='BAD_STATE_NAME'"))
    }

    @Test void add() {
        Schema s1 = new Schema("facilities", [new Field("geom","Point", "EPSG:2927"), new Field("name","string"), new Field("price","float")])
        Layer layer1 = new Layer("facilities", s1)
        assertEquals 0, layer1.count()
        // Add a Feature
        layer1.add(new Feature([new Point(111,-47), "House", 12.5], "house1", s1))
        assertEquals 1, layer1.count()
        // Add a Feature by passing a List of values
        layer1.add([new Point(110,-46), "House 2", 14.1])
        assertEquals 2, layer1.count()
        // Add a List of Features
        layer1.add([
            new Feature([new Point(109,-45), "House 3", 15.5], "house2", s1),
            new Feature([new Point(108,-44), "House 4", 16.5], "house3", s1),
            new Feature([new Point(107,-43), "House 5", 17.5], "house4", s1)
        ])
        assertEquals 5, layer1.count()
    }

    @Test void addFeaturesFromOneLayerToAnother() {
        Schema s1 = new Schema("facilities1", [new Field("geom","Point", "EPSG:2927"), new Field("name","string"), new Field("price","float")])
        Layer layer1 = new Layer("facilities1", s1)

        Schema s2 = new Schema("facilities2", [new Field("geom","Point", "EPSG:2927"), new Field("name","string"), new Field("price","float")])
        Layer layer2 = new Layer("facilities2", s2)

        layer1.add([
            new Feature([new Point(109,-45), "House 3", 15.5], "house2", s1),
            new Feature([new Point(108,-44), "House 4", 16.5], "house3", s1),
            new Feature([new Point(107,-43), "House 5", 17.5], "house4", s1)
        ])
        assertEquals 3, layer1.count()

        layer1.eachFeature {f ->
            layer2.add(f)
        }
        assertEquals 3, layer2.count()

        layer2.add(layer1.features)
        assertEquals 6, layer2.count()
    }

    @Test void addFeaturesToLayerWithDifferentGeomName() {
        Schema schema1 = new Schema("points", [
            new Field("geom","Point","EPSG:4326"),
            new Field("id","int")
        ])
        Layer layer1 = new Memory().create(schema1)
        layer1.add([
            new Feature([geom: new Point(0,0), id: 1], "feature1"),
            new Feature([geom: new Point(1,1), id: 2], "feature2"),
            new Feature([geom: new Point(2,2), id: 3], "feature3")
        ])
        assertEquals 3, layer1.count

        File file = File.createTempFile("points",".shp")
        String name = file.name.substring(0, file.name.lastIndexOf(".shp"))
        Schema schema2 = new Schema(name, [
            new Field("the_geom","Point","EPSG:4326"),
            new Field("id","int")
        ])
        Layer layer2 = new Directory(file.parentFile).create(schema2)
        layer1.eachFeature{ f ->
            layer2.add(f)
        }
        assertEquals 3, layer2.count
        layer2.eachFeature{ f->
            assertNotNull f.geom
            assertNotNull f["id"]
        }
    }

    @Test void plus() {
        Schema s1 = new Schema("facilities", [new Field("geom","Point", "EPSG:2927"), new Field("name","string"), new Field("price","float")])
        Layer layer1 = new Layer("facilities", s1)
        assertEquals 0, layer1.count()
        layer1 + new Feature([new Point(111,-47), "House", 12.5], "house1", s1)
        assertEquals 1, layer1.count()
    }

    @Test void features() {
        Schema s1 = new Schema("facilities", [new Field("geom","Point", "EPSG:2927"), new Field("name","string"), new Field("price","float")])
        Layer layer1 = new Layer("facilities", s1)
        layer1.add(new Feature([new Point(111,-47), "House", 12.5], "house1", s1))
        List<Feature> features = layer1.features
        println(features)
        assertEquals 1, features.size()
    }

    @Test void bounds() {
        Schema s1 = new Schema("facilities", [new Field("geom","Point", "EPSG:2927"), new Field("name","string"), new Field("price","float")])
        Layer layer1 = new Layer("facilities", s1)
        layer1.add(new Feature([new Point(111,-47), "House", 12.5], "house1", s1))
        Bounds bounds = layer1.bounds()
        assertNotNull(bounds);
        println("Bounds: ${bounds}")
        assertEquals(111.0, bounds.minX, 0.1)
        assertEquals(-47.0, bounds.minY, 0.1)
        assertEquals(111.0, bounds.maxX, 0.1)
        assertEquals(-47.0, bounds.maxY, 0.1)
        layer1.add(new Feature([new Point(108,-44), "House 2", 16.5], "house2", s1))
        bounds = layer1.bounds("name = 'House 2'")
        assertNotNull(bounds);
        println("Bounds for House 2: ${bounds}")
    }

    @Test void cursor() {
        Schema s1 = new Schema("facilities", [new Field("geom","Point", "EPSG:2927"), new Field("name","string"), new Field("price","float")])
        Layer layer1 = new Layer("facilities", s1)
        layer1.add(new Feature([new Point(111,-47), "House", 12.5], "house1", s1))
        Cursor c = layer1.getCursor()
        while(c.hasNext()) {
            println(c.next())
        }
        c.close()
    }

    @Test void toGML() {
        Schema s1 = new Schema("facilities", [new Field("geom","Point", "EPSG:2927"), new Field("name","string"), new Field("price","float")])
        Layer layer1 = new Layer("facilities", s1)
        layer1.add(new Feature([new Point(111,-47), "House", 12.5], "house1", s1))
        def out = new java.io.ByteArrayOutputStream()
        layer1.toGML(out)
        String gml = out.toString()
        assertNotNull gml
    }

    @Test void toJSON() {
        Schema s1 = new Schema("facilities", [new Field("geom","Point", "EPSG:2927"), new Field("name","string"), new Field("price","float")])
        Layer layer1 = new Layer("facilities", s1)
        layer1.add(new Feature([new Point(111,-47), "House", 12.5], "house1", s1))
        def out = new java.io.ByteArrayOutputStream()
        layer1.toJSON(out)
        String json = out.toString()
        assertNotNull json
        assertTrue json.startsWith("{\"type\":\"FeatureCollection\",\"features\":[")
        json = layer1.toJSONString()
        assertTrue json.startsWith("{\"type\":\"FeatureCollection\",\"features\":[")
    }

    @Test void toKML() {
        Schema s1 = new Schema("facilities", [new Field("geom","Point", "EPSG:2927"), new Field("name","string"), new Field("price","float")])
        Layer layer1 = new Layer("facilities", s1)
        layer1.add(new Feature([new Point(-122.444,47.2528), "House", 12.5], "house1", s1))
        def out = new java.io.ByteArrayOutputStream()
        layer1.toKML(out, {f->f.get("name")}, {f-> "${f.get('name')} ${f.get('price')}"})
        String kml = out.toString()
        assertNotNull kml
    }

    @Test void reproject() {
        Schema s1 = new Schema("facilities", [new Field("geom","Point", "EPSG:4326"), new Field("name","string"), new Field("price","float")])
        Layer layer1 = new Layer("facilities", s1)
        layer1.add(new Feature([new Point(-122.494165, 47.198096), "House", 12.5], "house1", s1))
        Layer layer2 = layer1.reproject(new Projection("EPSG:2927"))
        assertEquals 1, layer2.count()
        assertEquals 1144731.06, layer2.features[0].geom.x, 0.01
        assertEquals 686299.16, layer2.features[0].geom.y, 0.01
    }

    @Test void reprojectToWorkspace() {
        // Create Layer in Memory
        Schema s1 = new Schema("facilities", [new Field("geom","Point", "EPSG:4326"), new Field("name","string"), new Field("price","float")])
        Layer layer1 = new Layer("facilities", s1)
        layer1.add(new Feature([new Point(-122.494165, 47.198096), "House", 12.5], "house1", s1))

        // Reproject to a Property Workspace
        File file = File.createTempFile("reproject",".properties")
        Property property = new Property(file.parentFile)
        Layer layer2 = layer1.reproject(new Projection("EPSG:2927"), property, "facilities_reprojected")
        assertEquals 1, layer2.count()
        assertEquals 1144731.06, layer2.features[0].geom.x, 0.01
        assertEquals 686299.16, layer2.features[0].geom.y, 0.01
    }

    @Test void reprojectToLayer() {
        // Create Layer in Memory
        Schema s1 = new Schema("facilities", [new Field("geom","Point", "EPSG:4326"), new Field("name","string"), new Field("price","float")])
        Layer layer1 = new Layer("facilities", s1)
        layer1.add(new Feature([new Point(-122.494165, 47.198096), "House", 12.5], "house1", s1))

        // Reproject to a Property Workspace Layer
        File file = File.createTempFile("reproject",".properties")
        Property property = new Property(file.parentFile)
        Layer propertyLayer = property.create(layer1.schema.reproject("EPSG:2927","facilities_epsg_2927"))
        Layer layer2 = layer1.reproject(propertyLayer)
        assertEquals 1, layer2.count()
        assertEquals 1144731.06, layer2.features[0].geom.x, 0.01
        assertEquals 686299.16, layer2.features[0].geom.y, 0.01
    }

    @Test void delete() {
        Schema s1 = new Schema("facilities", [new Field("geom","Point", "EPSG:2927"), new Field("name","string"), new Field("price","float")])
        Layer layer1 = new Layer("facilities", s1)
        assertEquals 0, layer1.count()
        layer1.add(new Feature([new Point(111,-47), "House", 12.5], "house1", s1))
        assertEquals 1, layer1.count()
        layer1.delete()
        assertEquals 0, layer1.count()
    }

    @Test void filter() {
        Schema s1 = new Schema("facilities", [new Field("geom","Point", "EPSG:2927"), new Field("name","string"), new Field("price","float")])
        Layer layer1 = new Layer("facilities", s1)
        layer1.add(new Feature([new Point(111,-47), "House", 12.5], "house1", s1))
        layer1.add(new Feature([new Point(112,-48), "Work", 67.2], "house2", s1))
        Layer layer2 = layer1.filter()
        assertEquals 2, layer2.count()
    }


    @Test void constructors() {
        Schema s1 = new Schema("facilities", [new Field("geom","Point", "EPSG:2927"), new Field("name","string"), new Field("price","float")])
        Layer layer1 = new Layer("facilities", s1)
        assertNotNull(layer1)
        assertEquals "org.geotools.data.memory.MemoryDataStore", layer1.format
        assertEquals "facilities", layer1.name
        assertTrue(layer1.style instanceof geoscript.style.Shape)

        Layer layer2 = new Layer()
        assertEquals 0, layer2.count()
        layer2.add([new Point(1,2)])
        layer2.add([new Point(3,4)])
        assertEquals 2, layer2.count()

        Layer layer3 = new Layer("points")
        layer3.add([new Point(0,0)])
        layer3.add([new Point(1,1)])
        layer3.add([new Point(2,2)])
        layer3.add([new Point(3,3)])
        layer3.add([new Point(4,4)])

        assertEquals 5, layer3.count
        assertEquals "(0.0,0.0,4.0,4.0)", layer3.bounds.toString()

        // Make sure that the namepsace uri is passed through when creating Layers from FeatureCollections
        URL url = getClass().getClassLoader().getResource("states.shp")
        Workspace workspace = new Workspace(["url": url, namespace: 'http://omar.ossim.org'])
        Layer layer4 = workspace["states"]
        assertEquals layer4.schema.uri, 'http://omar.ossim.org'
        Layer layer5 = new Layer(layer4.fs.features)
        assertEquals layer5.schema.uri, 'http://omar.ossim.org'

        // Make sure Layers without geometry don't through an Exception
        Schema s2 = new Schema("facilities", [new Field("name","string"), new Field("price","float")])
        Layer layer6 = new Layer("facilities", s2)

    }

    @Test void updateFeatures() {

        // Create a Layer in memory
        Memory mem = new Memory()
        Layer l = mem.create('coffee_stands',[new Field("geom", "Point"), new Field("name", "String")])
        assertNotNull(l)

        // Add some Features
        l.add([new Point(1,1), "Hot Java"])
        l.add([new Point(2,2), "Cup Of Joe"])
        l.add([new Point(3,3), "Hot Wire"])

        // Make sure they are there and the attributes are equal
        assertEquals 3, l.count()
        List<Feature> features = l.features
        assertEquals features[0].get("geom").wkt, "POINT (1 1)"
        assertEquals features[1].get("geom").wkt, "POINT (2 2)"
        assertEquals features[2].get("geom").wkt, "POINT (3 3)"
        assertEquals features[0].get("name"), "Hot Java"
        assertEquals features[1].get("name"), "Cup Of Joe"
        assertEquals features[2].get("name"), "Hot Wire"

        // Now do some updating
        features[0].set("geom", new Point(5,5))
        features[1].set("name", "Coffee")
        features[2].set("geom", new Point(6,6))
        features[2].set("name", "Hot Coffee")
        l.update()

        // Ok, now do some more checking
        features = l.features
        assertEquals features[0].get("geom").wkt, "POINT (5 5)"
        assertEquals features[1].get("geom").wkt, "POINT (2 2)"
        assertEquals features[2].get("geom").wkt, "POINT (6 6)"
        assertEquals features[0].get("name"), "Hot Java"
        assertEquals features[1].get("name"), "Coffee"
        assertEquals features[2].get("name"), "Hot Coffee"
    }

    @Test void update() {
        // Create a Layer
        Schema s = new Schema("facilities", [new Field("geom","Point", "EPSG:2927"), new Field("name","string"), new Field("price","float")])
        Layer layer = new Layer("facilities", s)
        layer.add(new Feature([new Point(111,-47), "House 1", 12.5], "house1", s))
        layer.add(new Feature([new Point(112,-46), "House 2", 13.5], "house2", s))
        layer.add(new Feature([new Point(113,-45), "House 3", 14.5], "house3", s))
        assertEquals 3, layer.count

        // Test original values
        def features = layer.features
        assertEquals "House 1", features[0].get('name')
        assertEquals "House 2", features[1].get('name')
        assertEquals "House 3", features[2].get('name')

        // Update with static value
        layer.update(s.get('name'), 'Building')
        features = layer.features
        assertEquals "Building", features[0].get('name')
        assertEquals "Building", features[1].get('name')
        assertEquals "Building", features[2].get('name')

        // Update static value with Filter
        layer.update(s.get('name'), 'Building 1', new Filter('price = 12.5'))
        layer.update(s.get('name'), 'Building 2', new Filter('price = 13.5'))
        layer.update(s.get('name'), 'Building 3', new Filter('price = 14.5'))
        features = layer.features
        assertEquals "Building 1", features[0].get('name')
        assertEquals "Building 2", features[1].get('name')
        assertEquals "Building 3", features[2].get('name')

        // Update with closure
        layer.update(s.get('price'), {f ->
            f.get('price') * 2
        })
        features = layer.features
        assertEquals 12.5 * 2, features[0].get('price'), 0.01
        assertEquals 13.5 * 2, features[1].get('price'), 0.01
        assertEquals 14.5 * 2, features[2].get('price'), 0.01
        assertEquals 3, layer.count

        // Update with script
        layer.update(s.get('name'), "return c + '). ' + f.get('name')", Filter.PASS, true)
        features = layer.features
        assertEquals "0). Building 1", features[0].get('name')
        assertEquals "1). Building 2", features[1].get('name')
        assertEquals "2). Building 3", features[2].get('name')
        assertEquals 3, layer.count

        // Update with an Expression
        layer.update(s.get("price"), Expression.fromCQL("price * 2"))
        features = layer.features
        // We already multiplied the price * 2 with a Closure
        assertEquals 12.5 * 4, features[0].get('price'), 0.01
        assertEquals 13.5 * 4, features[1].get('price'), 0.01
        assertEquals 14.5 * 4, features[2].get('price'), 0.01
        assertEquals 3, layer.count
    }

    @Test void minmax() {
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Shapefile shp = new Shapefile(file)

        // No high/low
        def minMax = shp.minmax("SAMP_POP")
        assertEquals 72696.0, minMax.min, 0.1
        assertEquals 3792553.0, minMax.max, 0.1

        // low
        minMax = shp.minmax("SAMP_POP", 80000)
        assertEquals 83202.0, minMax.min, 0.1
        assertEquals 3792553.0, minMax.max, 0.1

        // high
        minMax = shp.minmax("SAMP_POP", null, 3000000)
        assertEquals 72696.0, minMax.min, 0.1
        assertEquals 2564485.0, minMax.max, 0.1

        // high and low
        minMax = shp.minmax("SAMP_POP", 80000, 3000000)
        assertEquals 83202.0, minMax.min, 0.1
        assertEquals 2564485.0, minMax.max, 0.1
    }

    @Test void histogram() {
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Shapefile shp = new Shapefile(file)
        def h = shp.histogram("SAMP_POP")
        assertEquals 10, h.size()
        assertEquals 72696.0, h[0][0], 0.1
        assertEquals 3792553.0, h[h.size() - 1][1], 0.1
    }

    @Test void interpolate() {
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Shapefile shp = new Shapefile(file)
        def values = shp.interpolate("SAMP_POP")
        assertEquals 11, values.size()
        assertEquals 72696.0, values[0], 0.1
        assertEquals 3792553.0, values[values.size() - 1], 0.1
    }

    @Test void cursorSorting() {
        File f = new File("target/h2").absoluteFile
        if (f.exists()) {
            boolean deleted = f.deleteDir()
        }
        H2 h2 = new H2("facilities", "target/h2")
        Layer layer = h2.create('facilities',[new Field("geom","Point", "EPSG:2927"), new Field("name","string"), new Field("price","float")])
        layer.add(new Feature(["geom": new Point(111,-47), "name": "A", "price": 10], "house1"))
        layer.add(new Feature(["geom": new Point(112,-46), "name": "B", "price": 12], "house2"))
        layer.add(new Feature(["geom": new Point(113,-45), "name": "C", "price": 13], "house3"))
        layer.add(new Feature(["geom": new Point(113,-45), "name": "D", "price": 14], "house4"))
        layer.add(new Feature(["geom": new Point(113,-45), "name": "E", "price": 15], "house5"))
        layer.add(new Feature(["geom": new Point(113,-45), "name": "F", "price": 16], "house6"))

        Cursor c = layer.getCursor(Filter.PASS, [["name","ASC"]])
        assertEquals "A", c.next()["name"]
        assertEquals "B", c.next()["name"]
        assertEquals "C", c.next()["name"]
        assertEquals "D", c.next()["name"]
        assertEquals "E", c.next()["name"]
        assertEquals "F", c.next()["name"]
        c.close()

        c = layer.getCursor(Filter.PASS, ["name"])
        assertEquals "A", c.next()["name"]
        assertEquals "B", c.next()["name"]
        assertEquals "C", c.next()["name"]
        assertEquals "D", c.next()["name"]
        assertEquals "E", c.next()["name"]
        assertEquals "F", c.next()["name"]
        c.close()

        c = layer.getCursor(Filter.PASS, [["name","DESC"]])
        assertEquals "F", c.next()["name"]
        assertEquals "E", c.next()["name"]
        assertEquals "D", c.next()["name"]
        assertEquals "C", c.next()["name"]
        assertEquals "B", c.next()["name"]
        assertEquals "A", c.next()["name"]
        c.close()

        c = layer.getCursor(Filter.PASS, ["name ASC"])
        assertEquals "A", c.next()["name"]
        assertEquals "B", c.next()["name"]
        assertEquals "C", c.next()["name"]
        assertEquals "D", c.next()["name"]
        assertEquals "E", c.next()["name"]
        assertEquals "F", c.next()["name"]
        c.close()

        // Named Parameters
        c = layer.getCursor(filter: "price >= 14.0", sort: [["price", "DESC"]])
        assertTrue c.hasNext()
        assertEquals "F", c.next()["name"]
        assertEquals "E", c.next()["name"]
        assertEquals "D", c.next()["name"]
        assertFalse c.hasNext()
        c.close()

        h2.close()
    }

    @Test void cursorSortingAndPagingWithUnsupportedLayer() {
        Schema s = new Schema("facilities", [new Field("geom","Point", "EPSG:2927"), new Field("name","string"), new Field("price","float")])
        Layer layer = new Layer("facilities", s)
        layer.add(new Feature([new Point(111,-47), "A", 10], "house1", s))
        layer.add(new Feature([new Point(112,-46), "B", 12], "house2", s))
        layer.add(new Feature([new Point(113,-45), "C", 11], "house3", s))
        layer.add(new Feature([new Point(113,-44), "D", 15], "house4", s))

        // Sort ascending explicitly
        Cursor c = layer.getCursor(Filter.PASS, [["name","ASC"]])
        assertEquals "A", c.next()["name"]
        assertEquals "B", c.next()["name"]
        assertEquals "C", c.next()["name"]
        assertEquals "D", c.next()["name"]
        assertFalse c.hasNext()
        c.close()

        // Sort ascending implicitly
        c = layer.getCursor(Filter.PASS, ["name"])
        assertEquals "A", c.next()["name"]
        assertEquals "B", c.next()["name"]
        assertEquals "C", c.next()["name"]
        assertEquals "D", c.next()["name"]
        assertFalse c.hasNext()
        c.close()

        // Sort descending
        c = layer.getCursor(Filter.PASS, [["name","DESC"]])
        assertEquals "D", c.next()["name"]
        assertEquals "C", c.next()["name"]
        assertEquals "B", c.next()["name"]
        assertEquals "A", c.next()["name"]
        assertFalse c.hasNext()
        c.close()

        // Page
        c = layer.getCursor(start:0, max:2)
        assertEquals "A", c.next()["name"]
        assertEquals "B", c.next()["name"]
        assertFalse c.hasNext()
        c.close()
        c = layer.getCursor(start:2, max:2)
        assertEquals "C", c.next()["name"]
        assertEquals "D", c.next()["name"]
        assertFalse c.hasNext()
        c.close()
        c = layer.getCursor("price > 10", [["price", "DESC"]], 2, 1, [])
        assertEquals "B", c.next()["name"]
        assertEquals "C", c.next()["name"]
        assertFalse c.hasNext()
        c.close()
    }

    @Test void cursorPaging() {
        File f = new File("target/h2").absoluteFile
        if (f.exists()) {
            boolean deleted = f.deleteDir()
        }
        H2 h2 = new H2("facilities", "target/h2")
        Layer layer = h2.create('facilities',[new Field("geom","Point", "EPSG:2927"), new Field("name","string"), new Field("price","float")])
        layer.add(new Feature(["geom": new Point(111,-47), "name": "A", "price": 10], "house1"))
        layer.add(new Feature(["geom": new Point(112,-46), "name": "B", "price": 12], "house2"))
        layer.add(new Feature(["geom": new Point(113,-45), "name": "C", "price": 13], "house3"))
        layer.add(new Feature(["geom": new Point(113,-45), "name": "D", "price": 14], "house4"))
        layer.add(new Feature(["geom": new Point(113,-45), "name": "E", "price": 15], "house5"))
        layer.add(new Feature(["geom": new Point(113,-45), "name": "F", "price": 16], "house6"))

        Cursor c = layer.getCursor(Filter.PASS, [["name","ASC"]], 2, 0, [])
        assertEquals "A", c.next()["name"]
        assertEquals "B", c.next()["name"]
        assertFalse c.hasNext()
        c.close()

        c = layer.getCursor(Filter.PASS, [["name","ASC"]], 2, 2, [])
        assertEquals "C", c.next()["name"]
        assertEquals "D", c.next()["name"]
        assertFalse c.hasNext()
        c.close()

        c = layer.getCursor(Filter.PASS, [["name","ASC"]], 2, 4, [])
        assertEquals "E", c.next()["name"]
        assertEquals "F", c.next()["name"]
        assertFalse c.hasNext()
        c.close()

        // Named parameters
        c = layer.getCursor(start: 0, max: 4)
        assertEquals "A", c.next()["name"]
        assertEquals "B", c.next()["name"]
        assertEquals "C", c.next()["name"]
        assertEquals "D", c.next()["name"]
        c.close()

        h2.close()
    }

    @Test void getRaster() {
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Shapefile shp = new Shapefile(file)
        Raster raster = shp.getRaster("SAMP_POP", [800,600], shp.bounds, "SAMP_POP")
        assertNotNull raster
        File rasterFile = File.createTempFile("states_pop_",".tif")
        println rasterFile
        GeoTIFF geotiff = new GeoTIFF()
        geotiff.write(raster, rasterFile)
    }

    @Test void cursorSubFields() {
        Schema s = new Schema("facilities", [new Field("geom","Point", "EPSG:2927"), new Field("name","string"), new Field("price","float")])
        Layer layer = new Layer("facilities", s)
        layer.add(new Feature([new Point(111,-47), "House 1", 12.5], "house1", s))
        layer.add(new Feature([new Point(112,-46), "House 2", 13.5], "house2", s))
        layer.add(new Feature([new Point(113,-45), "House 3", 14.5], "house3", s))

        layer.getCursor([fields: ["name"]]).each { f ->
            println f
            assertNotNull f["name"]
            assertNull f["price"]
        }
    }

    @Test void first() {
        Schema s = new Schema("facilities", [new Field("geom","Point", "EPSG:2927"), new Field("name","string"), new Field("price","float")])
        Layer layer = new Layer("facilities", s)
        layer.add(new Feature([new Point(111,-47), "House 1", 12.5], "house1", s))
        layer.add(new Feature([new Point(112,-46), "House 2", 13.5], "house2", s))
        layer.add(new Feature([new Point(113,-45), "House 3", 14.5], "house3", s))

        assertEquals "House 3", layer.first(sort: "price DESC").get("name")
        assertEquals "House 1", layer.first(sort: "price ASC").get("name")
        assertEquals "House 2", layer.first(filter: "price > 13 AND price < 14").get("name")
        assertEquals "House 3", layer.first(filter: "price > 13 AND price < 15", sort: "price DESC").get("name")
    }

    @Test void transform() {
        Schema s = new Schema("facilities", [new Field("geom","Point", "EPSG:2927"), new Field("name","string"), new Field("price","float")])
        Layer layer = new Layer("facilities", s)
        layer.add(new Feature([new Point(111,-47), "House 1", 12.5], "house1", s))
        layer.add(new Feature([new Point(112,-46), "House 2", 13.5], "house2", s))
        layer.add(new Feature([new Point(113,-45), "House 3", 14.5], "house3", s))

        Layer layer2 = layer.transform("buffered_facilities", [
            geom: "buffer(geom, 10)",
            name: "strToUpperCase(name)",
            price: "price * 10"
        ])

        List features1 = layer.features
        List features2 = layer2.features
        // 1
        assertTrue(features2[0].geom instanceof Polygon)
        assertEquals(features1[0].get("name").toString().toUpperCase(),  features2[0].get("name"))
        assertEquals(features1[0].get("price") * 10,  features2[0].get("price"), 0.1)
        // 2
        assertTrue(features2[1].geom instanceof Polygon)
        assertEquals(features1[1].get("name").toString().toUpperCase(),  features2[1].get("name"))
        assertEquals(features1[1].get("price") * 10,  features2[1].get("price"), 0.1)
        // 3
        assertTrue(features2[2].geom instanceof Polygon)
        assertEquals(features1[2].get("name").toString().toUpperCase(),  features2[2].get("name"))
        assertEquals(features1[2].get("price") * 10,  features2[2].get("price"), 0.1)
    }
}

