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
        layer1.toGML()
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


}

