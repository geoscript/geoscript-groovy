package geoscript.workspace

import geoscript.feature.Feature
import geoscript.geom.Bounds
import geoscript.layer.Format
import geoscript.layer.Layer
import geoscript.layer.Shapefile
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

/**
 * The GeoPackage Unit Test
 * @author Jared Erickson
 */
class GeoPackageTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Test void getWorkspace() {
        Workspace geopkg = new GeoPackage(folder.newFile("geopkg.gpkg")).workspace
        assertNotNull geopkg
    }

    @Test void getFormat() {
        Format geopkg = new GeoPackage(folder.newFile("geopkg.gpkg")).format
        assertNotNull geopkg
    }

    @Test
    void addLayersFromShp() {
        // Create GeoPackage database
        Workspace geopkg = new GeoPackage(folder.newFile("geopkg.gpkg")).workspace
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

    @Test
    void getWorkspaceFormat() {
        GeoPackage.Workspace geopkg = new GeoPackage(folder.newFile("geopkg.gpkg")).workspace
        assertEquals("GeoPackage", geopkg.format)
        geopkg.close()
    }

    @Test
    void tiles() {
        GeoPackage geopkg = new GeoPackage(folder.newFile("geopkg.gpkg"))
        geopkg.tiles.addEntry("world", new Bounds(-180,-90,180,90, "EPSG:4326"))
                .addMatrix(0,1,1,256,256,0.1,0.1)
                .addMatrix(1,2,2,256,256,0.1,0.1)
                .create()
                .addTile(0,0,0, [0] as byte[])
                .addTile(1,0,0, [1] as byte[])
                .addTile(1,0,1, [2] as byte[])
                .addTile(1,1,0, [3] as byte[])
                .addTile(1,1,1, [4] as byte[])


        // Iterate other entries and matrices
        geopkg.tiles.entries.each{ e ->
            assertNotNull e
            e.matricies.each{ matrix ->
                assertNotNull matrix
            }
        }

        // Tiles
        assertEquals(1, geopkg.tiles.entries.size())
        geopkg.tiles.eachEntry {entry ->
            assertNotNull(entry)
        }

        // Entry
        GeoPackage.Tiles.Entry entry = geopkg.tiles.getEntry("world")
        assertEquals(new Bounds(-180,-90,180,90, "EPSG:4326"), entry.bounds)
        assertEquals("world", entry.name)

        // Matrix
        assertEquals(2, entry.matricies.size())
        assertEquals(0, entry.matricies[0].zoom)
        assertEquals(1, entry.matricies[0].width)
        assertEquals(1, entry.matricies[0].height)
        assertEquals(256, entry.matricies[0].tileHeight)
        assertEquals(256, entry.matricies[0].tileWidth)
        assertEquals(0.1, entry.matricies[0].getXPixel(), 0.1)
        assertEquals(0.1, entry.matricies[0].getYPixel(), 0.1)

        // Tile
        assertEquals(5, entry.tiles.size())
        assertEquals(0, entry.tiles[0].zoom)
        assertEquals(0, entry.tiles[0].column)
        assertEquals(0, entry.tiles[0].row)
        assertNotNull(entry.tiles[0].data)
    }

}
