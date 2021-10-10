package geoscript.workspace

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import static org.junit.jupiter.api.Assertions.*
import geoscript.layer.*
import geoscript.feature.*
import geoscript.geom.*

/**
 * The H2 Workspace Unit Test
 */
class H2Test {

    @TempDir
    File folder

    @Test void remove() {
        H2 h2 = new H2(new File(folder,"h2.db"))
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
        H2 h2 = new H2(new File(folder,"h2.db"))
        assertEquals "H2", h2.format
        Layer l = h2.create('widgets',[new Field("geom", "Point"), new Field("name", "String")])
        assertNotNull(l)
        l.add([new Point(1,1), "one"])
        l.add([new Point(2,2), "two"])
        l.add([new Point(3,3), "three"])
        assertEquals 3, l.count()
        h2.close()
    }

    @Test void add() {
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Shapefile shp = new Shapefile(file)
        H2 h2 = new H2(new File(folder,"h2.db"))
        Layer l = h2.add(shp, 'counties')
        assertEquals shp.count(), l.count()
        h2.close()
    }

    @Test void createView2() {
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Shapefile shp = new Shapefile(file)
        H2 h2 = new H2(new File(folder,"h2.db"))
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
        H2 h2 = new H2(new File(folder,"h2.db"))
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
        H2 h2 = new H2(new File(folder,"h2.db"))
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
        H2 h2 = new H2(new File(folder,"h2.db"))
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

    @Test void getWorkspaceFromString() {
        File file = new File(folder,"h2.db")
        Workspace workspace = new H2(file)
        workspace.create("points",[new Field("geom","Point","EPSG:4326")])
        workspace.close()
        H2 h2 = Workspace.getWorkspace("type=h2 database=${file}")
        assertNotNull h2
        assertTrue h2.names.contains("points")
        h2 = Workspace.getWorkspace("type=h2 file=${file}")
        assertNotNull h2
        assertTrue h2.names.contains("points")
    }

    @Test void getWorkspaceFromMap() {
        File file = new File(folder,"h2.db")
        Workspace workspace = new H2(file)
        workspace.create("points",[new Field("geom","Point","EPSG:4326")])
        workspace.close()
        H2 h2 = Workspace.getWorkspace([type: 'h2', file: file])
        assertNotNull h2
        assertTrue h2.names.contains("points")
        h2 = Workspace.getWorkspace([type: 'h2', database: file])
        assertNotNull h2
        assertTrue h2.names.contains("points")
    }
    
}
