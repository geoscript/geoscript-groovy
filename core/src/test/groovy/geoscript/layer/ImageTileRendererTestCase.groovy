package geoscript.layer

import geoscript.style.Fill
import geoscript.style.Stroke
import geoscript.workspace.Workspace
import org.geotools.image.test.ImageAssert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

import static org.junit.Assert.*

/**
 * The ImageTileRenderer Unit Test
 * @author Jared Erickson
 */
class ImageTileRendererTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Test void render() {
        Workspace w = Workspace.getWorkspace([url: getClass().getClassLoader().getResource("states.shp")])
        Layer shp = w.get("states")
        shp.style = new Fill("wheat") + new Stroke("navy", 0.1)
        File file = folder.newFolder("cache")
        XYZ xyz = new XYZ("States", "png", file, Pyramid.createGlobalMercatorPyramid())
        TileRenderer renderer = new ImageTileRenderer(xyz, shp)
        byte[] data = renderer.render(xyz.pyramid.bounds(xyz.get(0,0,0)))
        assertNotNull data
        assertTrue data.length > 0
        InputStream input = new ByteArrayInputStream(data)
        BufferedImage image = ImageIO.read(input)
        input.close()
        assertNotNull image
        ImageAssert.assertEquals(new File(getClass().getClassLoader().getResource("geoscript/layer/imagetilerenderer.png").toURI()), image, 100)
    }

}
