package geoscript.render

import geoscript.layer.Layer
import geoscript.layer.Shapefile
import geoscript.style.Fill
import geoscript.style.Stroke
import org.geotools.image.test.ImageAssert

import javax.imageio.ImageIO
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import static org.junit.Assert.*

/**
 * The JPEG Unit Test
 * @author Jared Erickson
 */
class JPEGTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    private File getFile(String resource) {
        return new File(getClass().getClassLoader().getResource(resource).toURI())
    }

    @Test void renderToImage() {
        File shpFile = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Layer layer = new Shapefile(shpFile)
        layer.style = new Stroke('black', 0.1) + new Fill('gray', 0.75)
        Map map = new Map(layers: [layer], backgroundColor: "white")
        JPEG jpeg = new JPEG()
        def img = jpeg.render(map)
        assertNotNull(img)
        File file = folder.newFile("image.jpeg")
        ImageIO.write(img, "gif", file)
        assertTrue file.exists()
        assertTrue file.length() > 0
        ImageAssert.assertEquals(getFile("geoscript/render/jpeg_to_image.jpeg"), ImageIO.read(file), 100)
    }

    @Test void renderToOutputStream() {
        File shpFile = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Layer layer = new Shapefile(shpFile)
        layer.style = new Stroke('navy', 0.1) + new Fill('wheat', 0.75)
        Map map = new Map(layers: [layer], backgroundColor: "white")
        JPEG jpeg = new JPEG()
        File file = folder.newFile("image.jpeg")
        OutputStream out = new FileOutputStream(file)
        jpeg.render(map, out)
        out.close()
        assertTrue file.exists()
        assertTrue file.length() > 0
        ImageAssert.assertEquals(getFile("geoscript/render/jpeg_to_out.jpeg"), ImageIO.read(file), 100)
    }

    @Test void getImageType() {
        assertEquals "jpeg", new JPEG().imageType
    }
}
