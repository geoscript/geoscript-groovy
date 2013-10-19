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

    private H2 createH2() {
        File f = new File("target/h2").absoluteFile
        if (f.exists()) {
            boolean deleted = f.deleteDir()
        }
        new H2("acme", "target/h2")
    }

    @Test void create() {
        H2 h2 = createH2()
        assertEquals "H2", h2.format
        Layer l = h2.create('widgets',[new Field("geom", "Point"), new Field("name", "String")])
        assertNotNull(l)
        l.add([new Point(1,1), "one"])
        l.add([new Point(2,2), "two"])
        l.add([new Point(3,3), "three"])
        assertEquals 3, l.count()
        h2.close()

        h2 = new H2(new File("target/h2/acme"))
        assertEquals "H2", h2.format
        assertTrue h2.names.contains("widgets")
        l = h2.get("widgets")
        assertEquals 3, l.count()
        h2.close()
    }

    @Test void add() {
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Shapefile shp = new Shapefile(file)
        H2 h2 = createH2()
        Layer l = h2.add(shp, 'counties')
        assertEquals shp.count(), l.count()
        h2.close()
    }

    @Test void addSqlQuery() {
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Shapefile shp = new Shapefile(file)
        H2 h2 = createH2()
        Layer statesLayer = h2.add(shp, 'states')
        assertEquals shp.count(), statesLayer.count()

        String sql = "select st_centroid(\"the_geom\") as \"the_geom\", \"STATE_NAME\" FROM \"states\""
        Layer statesCentroidLayer = h2.addSqlQuery("states_centroids", sql, new Field("the_geom", "Point", "EPSG:4326"), [])
        statesCentroidLayer.features.each{feat->
            assertTrue feat.geom instanceof geoscript.geom.Point
        }
        assertEquals statesLayer.count, statesCentroidLayer.count

        String sql2 = "select st_buffer(st_centroid(\"the_geom\"), 500) as \"the_geom\", \"STATE_NAME\" FROM \"states\""
        Layer statesCentroidBufferLayer = h2.addSqlQuery("states_centroid_buffers", sql2, "the_geom", "Polygon", 4326, [])
        statesCentroidBufferLayer.features.each{feat->
            assertTrue feat.geom instanceof geoscript.geom.Polygon
        }
        assertEquals statesLayer.count, statesCentroidBufferLayer.count

        h2.close()
    }

    @Test void createView() {
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Shapefile shp = new Shapefile(file)
        H2 h2 = createH2()
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
    }

}

