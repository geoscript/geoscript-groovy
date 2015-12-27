package geoscript.workspace

import geoscript.filter.Filter
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import static org.junit.Assert.*
import geoscript.layer.*
import geoscript.feature.*
import geoscript.geom.*

/**
 * The H2 Workspace Unit Test
 */
class H2TestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Test void remove() {
        H2 h2 = new H2(folder.newFile("h2.db"))
        assertEquals "H2", h2.format
        // Add
        Layer l = h2.create('widgets',[new Field("geom", "Point"), new Field("name", "String")])
        assertNotNull(l)
        l.add([new Point(1,1), "one"])
        l.add([new Point(2,2), "two"])
        l.add([new Point(3,3), "three"])
        assertEquals 3, l.count()
        // Get
        assertNotNull(h2.get("widgets"))
        // Remove
        h2.remove("widgets")
        boolean exceptionThrown = false
        try {
            h2.get("widgets")
        } catch (IOException ex) {
            exceptionThrown = true
        }
        assertTrue(exceptionThrown)
        h2.close()
    }

    @Test void create() {
        H2 h2 = new H2(folder.newFile("h2.db"))
        assertEquals "H2", h2.format
        Layer l = h2.create('widgets',[new Field("geom", "Point"), new Field("name", "String")])
        assertNotNull(l)
        l.add([new Point(1,1), "one"])
        l.add([new Point(2,2), "two"])
        l.add([new Point(3,3), "three"])
        assertEquals 3, l.count()
        h2.close()

        h2 = new H2(folder.newFile("h2.db"))
        assertEquals "H2", h2.format
        assertTrue h2.names.contains("widgets")
        l = h2.get("widgets")
        assertEquals 3, l.count()
        h2.close()
    }

    @Test void add() {
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Shapefile shp = new Shapefile(file)
        H2 h2 = new H2(folder.newFile("h2.db"))
        Layer l = h2.add(shp, 'counties')
        assertEquals shp.count(), l.count()
        h2.close()
    }

    @Test void createView2() {
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Shapefile shp = new Shapefile(file)
        H2 h2 = new H2(folder.newFile("h2.db"))
        Layer statesLayer = h2.add(shp, 'states')
        assertEquals shp.count(), statesLayer.count()

        String sql = "select st_centroid(\"the_geom\") as \"the_geom\", \"STATE_NAME\" FROM \"states\""
        Layer statesCentroidLayer = h2.createView("states_centroids", sql, new Field("the_geom", "Point", "EPSG:4326"))
        statesCentroidLayer.features.each{feat->
            assertTrue feat.geom instanceof geoscript.geom.Point
        }
        assertEquals statesLayer.count, statesCentroidLayer.count

        String sql2 = "select st_buffer(st_centroid(\"the_geom\"), 500) as \"the_geom\", \"STATE_NAME\" FROM \"states\""
        Layer statesCentroidBufferLayer = h2.createView("states_centroid_buffers", sql2, new Field("the_geom", "Polygon", "EPSG:4326"))
        statesCentroidBufferLayer.features.each{feat->
            assertTrue feat.geom instanceof geoscript.geom.Polygon
        }
        assertEquals statesLayer.count, statesCentroidBufferLayer.count

        h2.close()
    }

    @Test void createView() {
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Shapefile shp = new Shapefile(file)
        H2 h2 = new H2(folder.newFile("h2.db"))
        Layer statesLayer = h2.add(shp, 'states')
        assertEquals shp.count(), statesLayer.count()

        def layer = h2.createView("state","SELECT * FROM \"states\" WHERE \"STATE_ABBR\" = '%abbr%'", new Field("the_geom","Polygon","EPSG:4326"), params: [['abbr', 'TX']])
        assertNotNull layer
        // Check geometry
        layer.eachFeature{ft ->
            assertNotNull ft.geom
        }
        // Count
        assertEquals 1, layer.count
        // First
        Feature feature = layer.first()
        assertEquals "Texas", feature['STATE_NAME']
        // Params
        assertEquals "New York", layer.first(params: ['abbr': 'NY'])['STATE_NAME']
        assertEquals "North Dakota", layer.first(params: ['abbr': 'ND'])['STATE_NAME']
        assertEquals new Bounds(-104.0625,45.930859,-96.551582,49.0,"EPSG:4326"), layer.bounds(params: ['abbr': 'ND'])
        assertEquals "Washington", layer.getFeatures(params: ['abbr': 'WA'])[0]['STATE_NAME']
        assertEquals "Oregon", layer.getCursor(params: ['abbr': 'OR']).next()['STATE_NAME']
        assertEquals "South Dakota", layer.collectFromFeature(params: ['abbr': 'SD']) {ft -> ft['STATE_NAME']}[0]
        h2.close()
    }

    @Test void indexes() {
        // Create a layer
        H2 h2 = new H2(folder.newFile("h2.db"))
        Layer l = h2.create('widgets',[new Field("geom", "Point"), new Field("name", "String")])
        assertNotNull(l)
        l.add([new Point(1,1), "one"])
        l.add([new Point(2,2), "two"])
        l.add([new Point(3,3), "three"])
        assertEquals 3, l.count()
        // Add two indexes
        h2.createIndex("widgets","geom_idx","geom",false)
        h2.createIndex("widgets","name_idx","name",true)
        // Get the indexes
        List indexes = h2.getIndexes("widgets")
        // Check the geom index
        Map index = indexes.find{ it.name.equals("geom_idx") }
        assertNotNull(index)
        assertEquals("geom", index.attributes[0])
        assertFalse(index.unique)        
        // Check the name index
        index = indexes.find{ it.name.equals("name_idx") }
        assertNotNull(index)
        assertEquals("name", index.attributes[0])
        assertTrue(index.unique)        
        // Delete the geom index
        h2.deleteIndex("widgets","geom_idx")
        assertNull(h2.getIndexes("widgets").find{it.name.equals("geom_idx")})
        h2.close()
    }

    @Test void getSql() {
        // Create a layer
        H2 h2 = new H2(folder.newFile("h2.db"))
        Layer l = h2.create('widgets',[new Field("geom", "Point"), new Field("name", "String")])
        assertNotNull(l)
        l.add([new Point(1,1), "one"])
        l.add([new Point(2,2), "two"])
        l.add([new Point(3,3), "three"])
        assertEquals 3, l.count()
        // Get groovy.sql.Sql
        def sql = h2.sql
        // Count rows
        assertEquals 3, sql.firstRow("SELECT COUNT(*) as count FROM \"widgets\"").get("count") as int
        // Query
        List names = []
        sql.eachRow "SELECT \"name\" FROM \"widgets\" ORDER BY \"name\" DESC", {
            names.add(it["name"])
        }
        assertEquals "two,three,one", names.join(",")
        // Insert
        sql.execute("INSERT INTO \"widgets\" (\"geom\", \"name\") VALUES (ST_GeomFromText('POINT (6 6)',4326), 'four')")
        assertEquals 4, sql.firstRow("SELECT COUNT(*) as count FROM \"widgets\"").get("count") as int
        // Query
        sql.eachRow "SELECT ST_Buffer(\"geom\", 10) as buffer, \"name\" FROM \"widgets\"", {row ->
            Geometry poly = Geometry.fromWKB(row.buffer as byte[])
            assertNotNull poly
            assertTrue poly instanceof Polygon
            assertNotNull row.name
        }
        h2.close()
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
}
