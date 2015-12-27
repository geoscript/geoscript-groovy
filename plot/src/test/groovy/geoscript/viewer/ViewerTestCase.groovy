package geoscript.viewer

import org.geotools.image.test.ImageAssert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.*
import geoscript.geom.Geometry
import javax.imageio.ImageIO

/**
 * The Viewer UnitTest
 * @author Jared Erickson
 */
class ViewerTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Test void plotToImage() {
        def geom = Geometry.fromWKT("POINT (-111 45.7)").buffer(10)
        def image = Viewer.plotToImage(geom)
        assertNotNull(image)
        def file = folder.newFile("viewer_plottoimage.png")
        ImageIO.write(image,"png", file)
        assertTrue file.exists()
        File expectedFile = new File(getClass().getClassLoader().getResource("geoscript/viewer/plottoimage.png").toURI())
        ImageAssert.assertEquals(expectedFile, image, 10000)
    }

    @Test void plotToImageWithOptions() {
        def geom = Geometry.fromWKT("POINT (-111 45.7)").buffer(10)
        def image = Viewer.plotToImage(geom, size: [400,400], legend: true, fillCoords: true, fillPolys: true)
        assertNotNull(image)
        def file = folder.newFile("viewer_plottoimage_withoptions.png")
        ImageIO.write(image,"png", file)
        assertTrue file.exists()
        File expectedFile = new File(getClass().getClassLoader().getResource("geoscript/viewer/plottoimagewithoptions.png").toURI())
        ImageAssert.assertEquals(expectedFile, image, 10000)
    }

    @Test void plotToFile() {
        def file = folder.newFile("viewer_plottofile.png")
        def geom = Geometry.fromWKT("POINT (-111 45.7)").buffer(10)
        Viewer.plotToFile(geom, size: [400,400], file, legend: false)
        assertTrue(file.exists())
        File expectedFile = new File(getClass().getClassLoader().getResource("geoscript/viewer/plottofile.png").toURI())
        ImageAssert.assertEquals(expectedFile, ImageIO.read(file), 10000)
    }

    @Test void plotToFileWithOptions() {
        def file = folder.newFile("viewer_plottofile_withoptions.png")
        def geom = Geometry.fromWKT("POINT (-111 45.7)").buffer(10)
        Viewer.plotToFile([geom, geom.buffer(10)], size: [400,400], file, legend: false, drawCoords: false)
        assertTrue(file.exists())
        File expectedFile = new File(getClass().getClassLoader().getResource("geoscript/viewer/plottofilewithoptions.png").toURI())
        ImageAssert.assertEquals(expectedFile, ImageIO.read(file), 10000)
    }

}

