package geoscript.workspace

import geoscript.feature.Feature
import geoscript.feature.Field
import geoscript.feature.Schema
import geoscript.layer.Layer
import geoscript.layer.Shapefile
import org.geotools.data.ogr.OGRDataStore
import org.geotools.data.ogr.jni.JniOGRDataStoreFactory
import org.junit.Rule
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.*
import org.junit.Test

/**
 * The OGR Workspace Unit Test
 * @author Jared Erickson
 */
class OGRTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    private boolean shouldRunTest() {
        boolean isAvailable = OGR.isAvailable()
        if (!isAvailable) {
            println "OGR is not available!"
        } else {
            OGR.setErrorHandler("quiet")
        }
        isAvailable
    }

    @Test void readShapefile() {
        if (shouldRunTest()) {
            File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
            OGR ogr = new OGR("ESRI Shapefile", file.absolutePath)
            assertEquals "OGR", ogr.format
            assertTrue ogr.toString().startsWith("OGR (Driver = ESRI Shapefile, Dataset = ")
            assertTrue ogr.toString().endsWith("states.shp)")
            assertEquals 1, ogr.names.size()
            assertTrue ogr.names.contains("states")
            assertEquals 1, ogr.layers.size()
            Layer layer = ogr.get("states")
            int i = 0
            layer.eachFeature{f ->
                assertNotNull f.geom
                assertNotNull f['STATE_NAME']
                i++
            }
            assertEquals 49, i
        }
    }

    @Test void readShapefileFromOgrDataStore() {
        if (shouldRunTest()) {
            File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
            OGRDataStore ds = new JniOGRDataStoreFactory().createDataStore([
                    "DriverName": "ESRI Shapefile",
                    "DatasourceName": file.absolutePath]
            )
            OGR ogr = new OGR(ds)
            assertEquals "OGR", ogr.format
            assertTrue ogr.toString().startsWith("OGR (Driver = ESRI Shapefile, Dataset = ")
            assertTrue ogr.toString().endsWith("states.shp)")
            assertEquals 1, ogr.names.size()
            assertTrue ogr.names.contains("states")
            assertEquals 1, ogr.layers.size()
            Layer layer = ogr.get("states")
            int i = 0
            layer.eachFeature{f ->
                assertNotNull f.geom
                assertNotNull f['STATE_NAME']
                i++
            }
            assertEquals 49, i
        }
    }

    @Test void writeReadSqlite() {
        if (shouldRunTest()) {
            File file = new File(folder.newFolder("states_sqlite"), "states.sqlite")
            OGR ogr = new OGR("SQLite", file.absolutePath)
            File shpFile = new File(getClass().getClassLoader().getResource("states.shp").toURI())
            Layer shpLayer = new Shapefile(shpFile)
            Layer layer = ogr.create(shpLayer.cursor)
            assertNotNull layer
            assertEquals "OGR", ogr.format
            assertTrue ogr.toString().startsWith("OGR (Driver = SQLite, Dataset = ")
            assertTrue ogr.toString().endsWith("states.sqlite)")
            assertEquals 1, ogr.names.size()
            assertTrue ogr.names.contains("states")
            assertEquals 1, ogr.layers.size()
            int i = 0
            layer.eachFeature{f ->
                assertNotNull f.geom
                assertNotNull f['state_name']
                i++
            }
            assertEquals 49, i
        }
    }

    @Test void writeReadSpatialite() {
        if (shouldRunTest()) {
            File file = new File(folder.newFolder("states_sqlite"), "states.sqlite")
            OGR ogr = new OGR("SQLite", file.absolutePath)
            File shpFile = new File(getClass().getClassLoader().getResource("states.shp").toURI())
            Layer shpLayer = new Shapefile(shpFile)
            Layer layer = ogr.create(shpLayer.cursor, options: [
                    "SPATIALITE=YES"
            ])
            assertNotNull layer
            assertEquals "OGR", ogr.format
            assertTrue ogr.toString().startsWith("OGR (Driver = SQLite, Dataset = ")
            assertTrue ogr.toString().endsWith("states.sqlite)")
            assertEquals 1, ogr.names.size()
            assertTrue ogr.names.contains("states")
            assertEquals 1, ogr.layers.size()
            int i = 0
            layer.eachFeature{f ->
                assertNotNull f.geom
                assertNotNull f['state_name']
                i++
            }
            assertEquals 49, i
        }
    }

    @Test void writeReadGeoJson() {
        if (shouldRunTest()) {
            File file = new File(folder.newFolder("states_geojson"), "states.geojson")
            OGR ogr = new OGR("GeoJSON", file.absolutePath)
            File shpFile = new File(getClass().getClassLoader().getResource("states.shp").toURI())
            Layer shpLayer = new Shapefile(shpFile)
            Layer layer = ogr.create(shpLayer.cursor)
            assertNotNull layer
            assertEquals "OGR", ogr.format
            assertTrue ogr.toString().startsWith("OGR (Driver = GeoJSON, Dataset = ")
            assertTrue ogr.toString().endsWith("states.geojson)")
            assertEquals 1, ogr.names.size()
            assertTrue ogr.names.contains("OGRGeoJSON")
            assertEquals 1, ogr.layers.size()
            int i = 0
            layer.eachFeature{f ->
                assertNotNull f.geom
                assertNotNull f['STATE_NAME']
                i++
            }
            assertEquals 49, i
        }
    }

    @Test void writeReadGeoJsonWithCreateAdd() {
        if (shouldRunTest()) {
            File file = new File(folder.newFolder("states_geojson"), "states.geojson")
            OGR ogr = new OGR("GeoJSON", file.absolutePath)
            File shpFile = new File(getClass().getClassLoader().getResource("states.shp").toURI())
            Layer shpLayer = new Shapefile(shpFile)
            Layer layer = ogr.create(shpLayer.schema)
            layer.add(shpLayer.cursor)
            assertNotNull layer
            assertEquals "OGR", ogr.format
            assertTrue ogr.toString().startsWith("OGR (Driver = GeoJSON, Dataset = ")
            assertTrue ogr.toString().endsWith("states.geojson)")
            assertEquals 1, ogr.names.size()
            assertTrue ogr.names.contains("OGRGeoJSON")
            assertEquals 1, ogr.layers.size()
            int i = 0
            layer.eachFeature{f ->
                assertNotNull f.geom
                assertNotNull f['STATE_NAME']
                i++
            }
            assertEquals 49, i
        }
    }

    @Test void writeReadKml() {
        if (shouldRunTest()) {
            File file = new File(folder.newFolder("states_kml"), "states.kml")
            OGR ogr = new OGR("KML", file.absolutePath)
            File shpFile = new File(getClass().getClassLoader().getResource("states.shp").toURI())
            Layer shpLayer = new Shapefile(shpFile)
            Layer layer = ogr.create(shpLayer.cursor, options: [
                    "NameField=STATE_NAME",
                    "DescriptionField=STATE_ABBR"
            ])
            assertNotNull layer
            assertEquals "OGR", ogr.format
            assertTrue ogr.toString().startsWith("OGR (Driver = KML, Dataset = ")
            assertTrue ogr.toString().endsWith("states.kml)")
            assertEquals 1, ogr.names.size()
            assertTrue ogr.names.contains("states")
            assertEquals 1, ogr.layers.size()
            int i = 0
            layer.eachFeature{f ->
                assertNotNull f.geom
                assertNotNull f['Name']
                assertNotNull f['Description']
                i++
            }
            assertEquals 49, i
        }
    }

    @Test void writeReadGeoRSS() {
        if(shouldRunTest()) {
            File file = new File(folder.newFolder("states_georss1"), "states.georss")
            OGR ogr = new OGR("GeoRSS", file.absolutePath)
            File shpFile = new File(getClass().getClassLoader().getResource("states.shp").toURI())
            Layer shpLayer = new Shapefile(shpFile)
            Layer tmpLayer = new Memory().create(new Schema("georss", [
                    new Field("geom", "POINT"),
                    new Field("title", "String"),
                    new Field("description", "String")
            ]))
            tmpLayer.withWriter {geoscript.layer.Writer w ->
                shpLayer.eachFeature{f ->
                    Feature newFeature = w.newFeature
                    newFeature.setGeom(f.geom.centroid)
                    newFeature.set("title", f.get('STATE_NAME'))
                    newFeature.set("description", f.get('STATE_ABBR'))
                    w.add(newFeature)
                }
            }
            Layer layer = ogr.create(tmpLayer.cursor, options: [
                    "FORMAT=RSS","USE_EXTENSIONS=YES"
            ])
            assertNotNull layer
            assertEquals "OGR", ogr.format
            assertTrue ogr.toString().startsWith("OGR (Driver = GeoRSS, Dataset = ")
            assertTrue ogr.toString().endsWith("states.georss)")
            assertEquals 1, ogr.names.size()
            assertTrue ogr.names.contains("georss")
            assertEquals 1, ogr.layers.size()
            int i = 0
            layer.eachFeature{f ->
                assertNotNull f.geom
                assertNotNull f['title']
                assertNotNull f['description']
                i++
            }
            assertEquals 49, i
        }
    }

    @Test void getDrivers() {
        if (shouldRunTest()) {
            Set drivers = OGR.drivers
            assertTrue drivers.size() > 0
            assertTrue drivers.contains("ESRI Shapefile")
            assertTrue drivers.contains("KML")
            assertTrue drivers.contains("CSV")
        }
    }

    @Test void writeSpatialiteWithNewName() {
        if (shouldRunTest()) {
            File file = new File(folder.newFolder("states_spat"), "states.sqlite")
            OGR ogr = new OGR("Sqlite", file.absolutePath)
            File shpFile = new File(getClass().getClassLoader().getResource("states.shp").toURI())
            Layer shp = new Shapefile(shpFile)
            Layer spatialite = ogr.add(shp, "test", options: [
                    "SPATIALITE=YES"
            ])
            assertNotNull spatialite
            assertEquals "OGR", ogr.format
            assertTrue ogr.toString().startsWith("OGR (Driver = Sqlite, Dataset = ")
            assertTrue ogr.toString().endsWith("states.sqlite)")
            assertEquals 1, ogr.names.size()
            assertTrue ogr.names.contains("test")
            assertEquals 1, ogr.layers.size()
            int i = 0
            spatialite.eachFeature { f ->
                assertNotNull f.geom
                assertNotNull f['state_name']
                i++
            }
            assertEquals 49, i
        }
    }
}