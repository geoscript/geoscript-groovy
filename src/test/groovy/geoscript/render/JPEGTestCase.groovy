package geoscript.render

import geoscript.layer.Layer
import geoscript.layer.Shapefile
import geoscript.style.Fill
import geoscript.style.Stroke
import org.junit.Test

import javax.imageio.ImageIO

import static org.junit.Assert.assertNotNull

/**
 * The JPEG Unit Test
 * @author Jared Erickson
 */
class JPEGTestCase {

    @Test void renderToImage() {
        File shpFile = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Layer layer = new Shapefile(shpFile)
        layer.style = new Stroke('black', 0.1) + new Fill('gray', 0.75)
        Map map = new Map(layers: [layer], backgroundColor: "white")
        JPEG jpeg = new JPEG()
        def img = jpeg.render(map)
        assertNotNull(img)
        File file = File.createTempFile("image_",".jpeg")
        println file
        ImageIO.write(img, "gif", file)
    }

    @Test void renderToOutputStream() {
        File shpFile = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Layer layer = new Shapefile(shpFile)
        layer.style = new Stroke('black', 0.1) + new Fill('gray', 0.75)
        Map map = new Map(layers: [layer], backgroundColor: "white")
        JPEG jpeg = new JPEG()
        File file = File.createTempFile("image_",".jpeg")
        println file
        OutputStream out = new FileOutputStream(file)
        jpeg.render(map, out)
        out.close()
    }

}
