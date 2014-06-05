package geoscript.tile

import geoscript.layer.Shapefile
import geoscript.style.Fill
import geoscript.style.Stroke
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import static org.junit.Assert.*

/**
 * The TileGenerator Unit Test
 * @author Jared Erickson
 */
class TileGeneratorTestCase {

    @Rule public TemporaryFolder folder = new TemporaryFolder()

    @Test void generateMbTiles() {
        Shapefile shp = new Shapefile(new File(getClass().getClassLoader().getResource("states.shp").toURI()))
        shp.style = new Fill("wheat") + new Stroke("navy", 0.1)
        File file = folder.newFile("states.mbtiles")
        geoscript.tile.MBTiles mbtiles = new geoscript.tile.MBTiles(
                file, "states", "A map of the united states"
        )
        TileGenerator generator = new TileGenerator()
        generator.generate(mbtiles, shp, 0, 2)
        assertNotNull mbtiles.get(0,0,0).data
        assertNotNull mbtiles.get(1,1,1).data
        assertNotNull mbtiles.get(2,2,2).data
        mbtiles.close()
    }

    @Test void generateGeoPackage() {
        Shapefile shp = new Shapefile(new File(getClass().getClassLoader().getResource("states.shp").toURI()))
        shp.style = new Fill("wheat") + new Stroke("navy", 0.1)
        File file = folder.newFile("states.gpkg")
        GeoPackage geopkg = new GeoPackage(file, "states", Pyramid.createGlobalMercatorPyramid())
        println geopkg.bounds
        TileGenerator generator = new TileGenerator()
        generator.generate(geopkg, shp, 0, 2)
        assertNotNull geopkg.get(0,0,0).data
        assertNotNull geopkg.get(1,1,1).data
        assertNotNull geopkg.get(2,2,2).data
        geopkg.close()
    }

}
