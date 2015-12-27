package geoscript.render

import geoscript.layer.Layer
import geoscript.style.Fill
import geoscript.style.Stroke
import geoscript.workspace.Workspace
import org.geotools.image.test.ImageAssert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import static org.junit.Assert.*
import javax.imageio.ImageIO

/**
 * The GIF Unit Test
 * @author Jared Erickson
 */
class GIFTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    private File getFile(String resource) {
        return new File(getClass().getClassLoader().getResource(resource).toURI())
    }

    @Test
    void renderToImage() {
        Workspace w = Workspace.getWorkspace([url: getClass().getClassLoader().getResource("states.shp")])
        Layer layer = w.get("states")
        layer.style = new Stroke('black', 0.1) + new Fill('gray', 0.75)
        Map map = new Map(layers: [layer], backgroundColor: "white")
        GIF gif = new GIF()
        def img = gif.render(map)
        assertNotNull(img)
        File file = folder.newFile("image.gif")
        ImageIO.write(img, "gif", file)
        assertTrue file.exists()
        assertTrue file.length() > 0
        ImageAssert.assertEquals(getFile("geoscript/render/image.gif"), ImageIO.read(file), 100)
    }

    @Test
    void renderToOutputStream() {
        Workspace w = Workspace.getWorkspace([url: getClass().getClassLoader().getResource("states.shp")])
        Layer layer = w.get("states")
        layer.style = new Stroke('black', 0.1) + new Fill('gray', 0.75)
        Map map = new Map(layers: [layer], backgroundColor: "white")
        GIF gif = new GIF()
        File file = folder.newFile("image.gif")
        OutputStream out = new FileOutputStream(file)
        gif.render(map, out)
        out.close()
        assertTrue file.exists()
        assertTrue file.length() > 0
        ImageAssert.assertEquals(getFile("geoscript/render/image_out.gif"), ImageIO.read(file), 100)
    }

    @Test
    void renderAnimatedToFile() {
        Workspace w = Workspace.getWorkspace([url: getClass().getClassLoader().getResource("states.shp")])
        Layer layer = w.get("states")
        layer.style = new Stroke('black', 0.1) + new Fill('gray', 0.75)

        List states = ["WA", "OR", "CA"]

        Map map = new Map(layers: [layer], backgroundColor: "white")
        GIF gif = new GIF()
        List images = states.collect { state ->
            map.bounds = layer.getFeatures("STATE_ABBR = '${state}'")[0].bounds
            def image = gif.render(map)
            image
        }
        File file = folder.newFile("image.gif")
        gif.renderAnimated(images, file, 500, true)
        assertTrue file.exists()
        assertTrue file.length() > 0
        ImageAssert.assertEquals(getFile("geoscript/render/animated.gif"), ImageIO.read(file), 100)
    }

    @Test
    void renderAnimatedToBytes() {
        Workspace w = Workspace.getWorkspace([url: getClass().getClassLoader().getResource("states.shp")])
        Layer layer = w.get("states")
        layer.style = new Stroke('black', 0.1) + new Fill('gray', 0.75)

        List states = ["MT", "ND", "SD", "MN"]

        Map map = new Map(layers: [layer], backgroundColor: "white")
        GIF gif = new GIF()
        List images = states.collect { state ->
            map.bounds = layer.getFeatures("STATE_ABBR = '${state}'")[0].bounds
            def image = gif.render(map)
            image
        }
        File file = folder.newFile("image.gif")
        def bytes = gif.renderAnimated(images, 500, false)
        file.withOutputStream { out ->
            out.write(bytes)
        }
        assertTrue file.exists()
        assertTrue file.length() > 0
        ImageAssert.assertEquals(getFile("geoscript/render/animated_bytes.gif"), ImageIO.read(file), 100)
    }

    @Test void getImageType() {
        assertEquals "gif", new GIF().imageType
    }
}
