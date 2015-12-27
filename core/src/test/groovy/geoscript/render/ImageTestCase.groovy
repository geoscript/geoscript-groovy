package geoscript.render

import geoscript.workspace.Workspace
import org.geotools.image.test.ImageAssert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.*
import geoscript.layer.Layer
import geoscript.style.Stroke
import geoscript.style.Fill
import javax.imageio.ImageIO

/**
 * The Image UnitTest
 * @author Jared Erickson
 */
class ImageTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    private File getFile(String resource) {
        return new File(getClass().getClassLoader().getResource(resource).toURI())
    }

    @Test void renderToImage() {
        Workspace w = Workspace.getWorkspace([url: getClass().getClassLoader().getResource("states.shp")])
        Layer layer = w.get("states")
        layer.style = new Stroke('black', 0.1) + new Fill('gray', 0.75)
        Map map = new Map(layers: [layer])
        Image image = new Image("png")
        def img = image.render(map)
        assertNotNull(img)
        File file = folder.newFile("image.png")
        ImageIO.write(img, "png", file)
        assertTrue file.exists()
        assertTrue file.length() > 0
        ImageAssert.assertEquals(getFile("geoscript/render/image_to_image.png"), ImageIO.read(file), 100)
    }

    @Test void renderToOutputStream() {
        Workspace w = Workspace.getWorkspace([url: getClass().getClassLoader().getResource("states.shp")])
        Layer layer = w.get("states")
        layer.style = new Stroke('navy', 0.1) + new Fill('wheat', 0.75)
        Map map = new Map(layers: [layer])
        Image image = new Image("png")
        File file = folder.newFile("image.png")
        OutputStream out = new FileOutputStream(file)
        image.render(map, out)
        out.close()
        assertTrue file.exists()
        assertTrue file.length() > 0
        ImageAssert.assertEquals(getFile("geoscript/render/image_to_out.png"), ImageIO.read(file), 100)
    }

    @Test void getImageType() {
        assertEquals "png", new Image("png").imageType
        assertEquals "jpeg", new Image("jpeg").imageType
        assertEquals "gif", new Image("gif").imageType
    }
}