package geoscript.render

import org.junit.Test
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

    @Test void renderToImage() {
        File shpFile = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Layer layer = new Shapefile(shpFile)
        layer.style = new Stroke('black', 0.1) + new Fill('gray', 0.75)
        Map map = new Map(layers: [layer])
        Image image = new Image("png")
        def img = image.render(map)
        assertNotNull(img)
        File file = File.createTempFile("image_",".png")
        println file
        ImageIO.write(img, "png", file)
    }

    @Test void renderToOutputStream() {
        File shpFile = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Layer layer = new Shapefile(shpFile)
        layer.style = new Stroke('black', 0.1) + new Fill('gray', 0.75)
        Map map = new Map(layers: [layer])
        Image image = new Image("png")
        File file = File.createTempFile("image_",".png")
        println file
        OutputStream out = new FileOutputStream(file)
        image.render(map, out)
        out.close()
    }
}