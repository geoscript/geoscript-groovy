package geoscript.layer

import geoscript.style.Fill
import geoscript.style.Stroke
import org.geotools.image.test.ImageAssert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

import static org.junit.jupiter.api.Assertions.*

/**
 * The ImageTileRenderer Unit Test
 * @author Jared Erickson
 */
class ImageTileRendererTest {

    @TempDir
    private File folder

    @Test void render() {
        Shapefile shp = new Shapefile(new File(getClass().getClassLoader().getResource("states.shp").toURI()))
        shp.style = new Fill("wheat") + new Stroke("navy", 0.1)
        File file = new File(folder, "cache")
        TMS tms = new TMS("States", "png", file, Pyramid.createGlobalMercatorPyramid())
        TileRenderer renderer = new ImageTileRenderer(tms, shp)
        byte[] data = renderer.render(tms.pyramid.bounds(tms.get(0,0,0)))
        assertNotNull data
        assertTrue data.length > 0
        InputStream input = new ByteArrayInputStream(data)
        BufferedImage image = ImageIO.read(input)
        input.close()
        assertNotNull image
        ImageAssert.assertEquals(new File(getClass().getClassLoader().getResource("geoscript/layer/imagetilerenderer.png").toURI()), image, 100)
    }

}
