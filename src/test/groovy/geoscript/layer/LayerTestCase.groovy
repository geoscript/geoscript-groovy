package geoscript.layer

import org.junit.Test
import static org.junit.Assert.*
import geoscript.feature.Schema
import geoscript.feature.Field
import geoscript.feature.Feature
import geoscript.proj.Projection
import geoscript.filter.Filter
import geoscript.geom.*

/**
 * The Layer UnitTest
 */
class LayerTestCase {

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
        layer1.add(new Feature([new Point(111,-47), "House", 12.5], "house1", s1))
        assertEquals 1, layer1.count()
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
        assertEquals "(111.0,-47.0,111.0,-47.0)", bounds.toString()
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
        String expected = """{"features":[{"properties":{"price":12.5,"name":"House"},"type":"Feature","geometry":{"type":"Point","coordinates":[111,-47]}}],"type":"FeatureCollection"}"""
        String actual = out.toString()
        assertEquals expected, actual
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
        layer1.add(new Feature([new Point(111,-47), "House", 12.5], "house1", s1))
        Layer layer2 = layer1.reproject(new Projection("EPSG:2927"))
        assertEquals 1, layer2.count()
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

        Layer layer2 = new Layer()
        assertEquals 0, layer2.count()
        layer2.add([new Point(1,2)])
        layer2.add([new Point(3,4)])
        assertEquals 2, layer2.count()
    }

    @Test void update() {
        Schema s = new Schema("facilities", [new Field("geom","Point", "EPSG:2927"), new Field("name","string"), new Field("price","float")])
        Layer layer = new Layer("facilities", s)
        layer.add(new Feature([new Point(111,-47), "House 1", 12.5], "house1", s))
        layer.add(new Feature([new Point(112,-46), "House 2", 13.5], "house2", s))
        layer.add(new Feature([new Point(113,-45), "House 3", 14.5], "house3", s))

        def features = layer.features
        assertEquals "House 1", features[0].get('name')
        assertEquals "House 2", features[1].get('name')
        assertEquals "House 3", features[2].get('name')

        layer.update(s.get('name'), 'Building')

        features = layer.features
        assertEquals "Building", features[0].get('name')
        assertEquals "Building", features[1].get('name')
        assertEquals "Building", features[2].get('name')

        layer.update(s.get('name'), 'Building 1', new Filter('price = 12.5'))
        layer.update(s.get('name'), 'Building 2', new Filter('price = 13.5'))
        layer.update(s.get('name'), 'Building 3', new Filter('price = 14.5'))

        features = layer.features
        assertEquals "Building 1", features[0].get('name')
        assertEquals "Building 2", features[1].get('name')
        assertEquals "Building 3", features[2].get('name')

        layer.update(s.get('price'), {f ->
            f.get('price') * 2
        })

        features = layer.features
        features.each{println(it)}
        assertEquals 12.5 * 2, features[0].get('price'), 0.01
        assertEquals 13.5 * 2, features[1].get('price'), 0.01
        assertEquals 14.5 * 2, features[2].get('price'), 0.01
    }

}

