package geoscript.layer

import geoscript.layer.GeoPackage
import geoscript.layer.MBTiles
import geoscript.layer.Pyramid
import geoscript.layer.Shapefile
import geoscript.layer.TileGenerator
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
        MBTiles mbtiles = new MBTiles(
                file, "states", "A map of the united states"
        )
        ImageTileRenderer renderer = new ImageTileRenderer(mbtiles, shp)
        TileGenerator generator = new TileGenerator()
        generator.generate(mbtiles, renderer, 0, 2)
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
        ImageTileRenderer renderer = new ImageTileRenderer(geopkg, shp)
        TileGenerator generator = new TileGenerator()
        generator.generate(geopkg, renderer, 0, 2)
        assertNotNull geopkg.get(0,0,0).data
        assertNotNull geopkg.get(1,1,1).data
        assertNotNull geopkg.get(2,2,2).data
        geopkg.close()
    }

}
