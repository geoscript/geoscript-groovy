package geoscript.workspace

import geoscript.feature.Feature
import geoscript.feature.Field
import geoscript.layer.Layer
import geoscript.layer.Shapefile
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import static org.junit.jupiter.api.Assertions.*

/**
 * The GeoPackage Unit Test
 * @author Jared Erickson
 */
class GeoPackageTest {

    @TempDir
    File folder

    @Test
    void getWorkspace() {
        Workspace geopkg = new GeoPackage(new File(folder,"geopkg.gpkg"))
        assertNotNull geopkg
    }

    @Test
    void addLayersFromShp() {
        // Create GeoPackage database
        Workspace geopkg = new GeoPackage(new File(folder,"geopkg.gpkg"))
        try {
            // Get the States Shapefile
            File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
            Shapefile shp = new Shapefile(file)

            // Add states shapefile to the GeoPackage database
            Layer l = geopkg.add(shp, 'states')
            assertEquals shp.count(), l.count()
            geopkg.get('states').eachFeature { Feature f ->
                assertNotNull f.geom
                assertEquals("MultiPolygon", f.geom.geometryType)
                assertNotNull(f['STATE_ABBR'])
                assertNotNull(f['STATE_NAME'])
            }

            // Add the centroids of each state to the GeoPackage database
            Layer l2 = geopkg.add(shp.transform("state_centroids", [
                    geom: "centroid(the_geom)",
                    abbr: "STATE_ABBR",
                    name: "STATE_NAME"
            ]))
            assertEquals shp.count(), l2.count()
            geopkg.get('state_centroids').eachFeature { Feature f ->
                assertNotNull f.geom
                assertEquals("Point", f.geom.geometryType)
                assertNotNull(f['abbr'])
                assertNotNull(f['name'])
            }
        } finally {
            geopkg.close()
        }
    }

    @Test void getWorkspaceFromString() {
        File file = new File(folder,"geopkg.gpkg")
        Workspace workspace = new GeoPackage(file)
        workspace.create("points",[new Field("geom","Point","EPSG:4326")])
        workspace.close()
        GeoPackage geopkg = Workspace.getWorkspace("type=geopackage database=${file}")
        assertNotNull geopkg
        assertTrue geopkg.names.contains("points")
        geopkg = Workspace.getWorkspace("type=geopackage file=${file}")
        assertNotNull geopkg
        assertTrue geopkg.names.contains("points")
    }

    @Test void getWorkspaceFromMap() {
        File file = new File(folder,"geopkg.gpkg")
        Workspace workspace = new GeoPackage(file)
        workspace.create("points",[new Field("geom","Point","EPSG:4326")])
        workspace.close()
        GeoPackage geopkg = Workspace.getWorkspace([type: 'geopackage', file: file])
        assertNotNull geopkg
        assertTrue geopkg.names.contains("points")
        geopkg = Workspace.getWorkspace([type: 'geopackage', database: file])
        assertNotNull geopkg
        assertTrue geopkg.names.contains("points")
    }

}