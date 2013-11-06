package geoscript.render

import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.*
import geoscript.layer.Layer
import geoscript.layer.Shapefile
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
    
    @Test void renderToImage() {
        File shpFile = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Layer layer = new Shapefile(shpFile)
        layer.style = new Stroke('black', 0.1) + new Fill('gray', 0.75)
        Map map = new Map(layers: [layer])
        Image image = new Image("png")
        def img = image.render(map)
        assertNotNull(img)
        File file = folder.newFile("image.png")
        ImageIO.write(img, "png", file)
        assertTrue file.exists()
        assertTrue file.length() > 0
    }

    @Test void renderToOutputStream() {
        File shpFile = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Layer layer = new Shapefile(shpFile)
        layer.style = new Stroke('black', 0.1) + new Fill('gray', 0.75)
        Map map = new Map(layers: [layer])
        Image image = new Image("png")
        File file = folder.newFile("image.png")
        OutputStream out = new FileOutputStream(file)
        image.render(map, out)
        out.close()
        assertTrue file.exists()
        assertTrue file.length() > 0
    }
}