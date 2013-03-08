package geoscript.render

import geoscript.layer.Layer
import geoscript.layer.Shapefile
import geoscript.style.Fill
import geoscript.style.Stroke
import org.junit.Test

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

import static org.junit.Assert.assertNotNull

/**
 * The GIF Unit Test
 * @author Jared Erickson
 */
class GIFTestCase {

    @Test void renderToImage() {
        File shpFile = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Layer layer = new Shapefile(shpFile)
        layer.style = new Stroke('black', 0.1) + new Fill('gray', 0.75)
        Map map = new Map(layers: [layer], backgroundColor: "white")
        GIF gif = new GIF()
        def img = gif.render(map)
        assertNotNull(img)
        File file = File.createTempFile("image_",".gif")
        println file
        ImageIO.write(img, "gif", file)
    }

    @Test void renderToOutputStream() {
        File shpFile = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Layer layer = new Shapefile(shpFile)
        layer.style = new Stroke('black', 0.1) + new Fill('gray', 0.75)
        Map map = new Map(layers: [layer], backgroundColor: "white")
        GIF gif = new GIF()
        File file = File.createTempFile("image_",".gif")
        println file
        OutputStream out = new FileOutputStream(file)
        gif.render(map, out)
        out.close()
    }

    @Test void renderAnimatedToFile() {
        File shpFile = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Layer layer = new Shapefile(shpFile)
        layer.style = new Stroke('black', 0.1) + new Fill('gray', 0.75)

        List states = ["WA","OR","CA"]

        Map map = new Map(layers: [layer], backgroundColor: "white")
        GIF gif = new GIF()
        List images = states.collect {state ->
            map.bounds = layer.getFeatures("STATE_ABBR = '${state}'")[0].bounds
            def image = gif.render(map)
            image
        }
        File file = File.createTempFile("image_",".gif")
        println file
        gif.renderAnimated(images, file, 500, true)
    }

    @Test void renderAnimatedToBytes() {
        File shpFile = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Layer layer = new Shapefile(shpFile)
        layer.style = new Stroke('black', 0.1) + new Fill('gray', 0.75)

        List states = ["MT","ND","SD","MN"]

        Map map = new Map(layers: [layer], backgroundColor: "white")
        GIF gif = new GIF()
        List images = states.collect {state ->
            map.bounds = layer.getFeatures("STATE_ABBR = '${state}'")[0].bounds
            def image = gif.render(map)
            image
        }
        File file = File.createTempFile("image_",".gif")
        println file
        def bytes = gif.renderAnimated(images, 500, false)
        file.withOutputStream {out ->
            out.write(bytes)
        }
    }
}
