package geoscript.workspace

import geoscript.FileUtil
import geoscript.layer.Shapefile
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import static org.junit.jupiter.api.Assertions.*
import geoscript.layer.Layer

/**
 * The SpatiaLite Unit Test
 */
class SpatiaLiteTest {

    @TempDir
    File folder

    private boolean shouldRunTest() {
        boolean isAvailable = OGR.isAvailable()
        if (!isAvailable) {
            println "OGR is not available!"
        } else {
            OGR.setErrorHandler("quiet")
        }
        isAvailable
    }

    @Test void writeReadSpatialite() {
        if (shouldRunTest()) {
            File file = new File(FileUtil.createDir(folder,"states_sqlite"), "states.sqlite")
            SpatiaLite spatialite = new SpatiaLite(file)
            File shpFile = new File(getClass().getClassLoader().getResource("states.shp").toURI())
            Layer shpLayer = new Shapefile(shpFile)
            Layer layer = spatialite.create(shpLayer.cursor)
            assertNotNull layer
            assertEquals "SpatiaLite", spatialite.format
            assertTrue spatialite.toString().startsWith("SpatiaLite(")
            assertTrue spatialite.toString().endsWith("states.sqlite)")
            assertEquals 1, spatialite.names.size()
            assertTrue spatialite.names.contains("states")
            assertEquals 1, spatialite.layers.size()
            int i = 0
            layer.eachFeature{f ->
                assertNotNull f.geom
                assertNotNull f['state_name']
                i++
            }
            assertEquals 49, i
        }
    }
}

