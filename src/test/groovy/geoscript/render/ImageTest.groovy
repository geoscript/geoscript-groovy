package geoscript.render

import org.geotools.image.test.ImageAssert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import static org.junit.jupiter.api.Assertions.*
import geoscript.layer.Layer
import geoscript.layer.Shapefile
import geoscript.style.Stroke
import geoscript.style.Fill
import javax.imageio.ImageIO

/**
 * The Image UnitTest
 * @author Jared Erickson
 */
class ImageTest {

    @TempDir
    File folder

    private File getFile(String resource) {
        return new File(getClass().getClassLoader().getResource(resource).toURI())
    }

    @Test void renderToImage() {
        File shpFile = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Layer layer = new Shapefile(shpFile)
        layer.style = new Stroke('black', 0.1) + new Fill('gray', 0.75)
        Map map = new Map(layers: [layer])
        Image image = new Image("png")
        def img = image.render(map)
        assertNotNull(img)
        File file = new File(folder,"image.png")
        ImageIO.write(img, "png", file)
        assertTrue file.exists()
        assertTrue file.length() > 0
        ImageAssert.assertEquals(getFile("geoscript/render/image_to_image.png"), ImageIO.read(file), 100)
    }

    @Test void renderToOutputStream() {
        File shpFile = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Layer layer = new Shapefile(shpFile)
        layer.style = new Stroke('navy', 0.1) + new Fill('wheat', 0.75)
        Map map = new Map(layers: [layer])
        Image image = new Image("png")
        File file = new File(folder,"image.png")
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