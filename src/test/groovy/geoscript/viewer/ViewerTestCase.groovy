package geoscript.viewer

import org.junit.Test
import static org.junit.Assert.*
import geoscript.geom.Geometry

/**
 * The Viewer UnitTest
 * @author Jared Erickson
 */
class ViewerTestCase {

    @Test void drawToImage() {
        def geom = Geometry.fromWKT("POINT (-111 45.7)").buffer(10)
        def image = Viewer.drawToImage(geom, [400,400])
        assertNotNull(image)
    }

    @Test void drawToFile() {
        def file = File.createTempFile("image",".png")
        println(file)
        def geom = Geometry.fromWKT("POINT (-111 45.7)").buffer(10)
        Viewer.drawToFile(geom, [400,400], file)
        assertTrue(file.exists())
    }

    @Test void plotToImage() {
        def geom = Geometry.fromWKT("POINT (-111 45.7)").buffer(10)
        def image = Viewer.plotToImage(geom, [400,400])
        assertNotNull(image)
    }

    @Test void plotToFile() {
        def file = File.createTempFile("image",".png")
        println(file)
        def geom = Geometry.fromWKT("POINT (-111 45.7)").buffer(10)
        Viewer.plotToFile(geom, [400,400], file)
        assertTrue(file.exists())
    }

}

