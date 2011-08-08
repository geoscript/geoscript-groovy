package geoscript.render

import org.junit.Test
import static org.junit.Assert.*
import geoscript.layer.Shapefile
import geoscript.style.*

/**
 * The Image UnitTest
 * @author Jared Erickson
 */
class ImageTestCase {

    @Test void render() {
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Shapefile shapefile = new Shapefile(file)
        Symbolizer sym = new Stroke('black', 2) + new Fill('gray',0.75)

        ["png","jpeg","gif"].each {format ->
            File imageFile = File.createTempFile("states",".${format}")
            println(imageFile)

            Image image = new Image(format)
            image.render([shapefile], [sym], shapefile.bounds.expandBy(0.1), [500,500], [file: imageFile])
            assertTrue imageFile.exists()
        }
    }

}
