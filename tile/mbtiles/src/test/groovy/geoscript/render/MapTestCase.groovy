package geoscript.render

import geoscript.layer.Shapefile
import geoscript.layer.MBTiles
import geoscript.style.Stroke
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.*

class MapTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Test void renderTileLayer() {
        MBTiles layer = new MBTiles(new File(getClass().getClassLoader().getResource("states.mbtiles").toURI()))
        Shapefile shp = new Shapefile(new File(getClass().getClassLoader().getResource("states.shp").toURI()))
        shp.style = new Stroke("#ff0000", 2.0)

        Map map = new Map()
        map.addTileLayer(layer)
        map.addLayer(shp)
        def image = map.renderToImage()
        assertNotNull(image)

        File out = folder.newFile("raster.png")
        javax.imageio.ImageIO.write(image, "png", out);
        assertTrue(out.exists())
        map.close()
    }

}
