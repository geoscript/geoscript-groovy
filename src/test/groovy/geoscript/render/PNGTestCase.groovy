package geoscript.render

import geoscript.layer.Layer
import geoscript.layer.Shapefile
import geoscript.style.Fill
import geoscript.style.Stroke
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import javax.imageio.ImageIO

import static org.junit.Assert.*

/**
 * The PNG Unit Test
 * @author Jared Erickson
 */
class PNGTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Test
    void renderToImage() {
        File shpFile = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Layer layer = new Shapefile(shpFile)
        layer.style = new Stroke('black', 0.1) + new Fill('gray', 0.75)
        Map map = new Map(layers: [layer], backgroundColor: "white")
        PNG png = new PNG()
        def img = png.render(map)
        assertNotNull(img)
        File file = folder.newFile("image.png")
        ImageIO.write(img, "gif", file)
        assertTrue file.exists()
        assertTrue file.length() > 0
    }

    @Test
    void renderToOutputStream() {
        File shpFile = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Layer layer = new Shapefile(shpFile)
        layer.style = new Stroke('black', 0.1) + new Fill('gray', 0.75)
        Map map = new Map(layers: [layer], backgroundColor: "white")
        PNG png = new PNG()
        File file = folder.newFile("image.png")
        OutputStream out = new FileOutputStream(file)
        png.render(map, out)
        out.close()
        assertTrue file.exists()
        assertTrue file.length() > 0
    }

}
