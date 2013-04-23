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
        
        File f = new File("target/h2").absoluteFile
        if (f.exists()) {
            boolean deleted = f.deleteDir()
        }

        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Shapefile shp = new Shapefile(file)
        
        H2 h2 = new H2("acme", "target/h2")
        Layer l = h2.add(shp, 'counties')
        assertEquals shp.count(), l.count()
        h2.close()
    }

    @Test void addSqlQuery() {

        File f = new File("target/h2").absoluteFile
        if (f.exists()) {
            boolean deleted = f.deleteDir()
        }

        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Shapefile shp = new Shapefile(file)

        H2 h2 = new H2("acme", "target/h2")
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

}

