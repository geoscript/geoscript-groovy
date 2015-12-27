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

    @Test void drawToImage() {
        def geom = Geometry.fromWKT("POINT (-111 45.7)").buffer(10)
        def image = Viewer.drawToImage(geom)
        assertNotNull(image)
        def file = folder.newFile("viewer_drawtoimage.png")
        ImageIO.write(image,"png", file)
        assertTrue file.exists()
        File expectedFile = new File(getClass().getClassLoader().getResource("geoscript/viewer/drawtoimage.png").toURI())
        ImageAssert.assertEquals(expectedFile, image, 100)
    }

    @Test void drawToImageWithOptions() {
        def geom = Geometry.fromWKT("POINT (-111 45.7)").buffer(10)
        def image = Viewer.drawToImage(geom, size: [400,400], bounds: geom.bounds.scale(2.0),
            strokeColor: "navy", fillColor: "wheat", markerShape: "cross", markerSize: 10,
            opacity: 0.65, strokeWidth: 1.5, drawCoords: true
        )
        assertNotNull(image)
        def file = folder.newFile("viewer_drawtoimage_options.png")
        ImageIO.write(image,"png", file)
        assertTrue file.exists()
        File expectedFile = new File(getClass().getClassLoader().getResource("geoscript/viewer/drawtoimagewithoptions.png").toURI())
        ImageAssert.assertEquals(expectedFile, image, 100)
    }

    @Test void drawToFile() {
        def file = folder.newFile("viewer_drawtofile.png")
        def geom = Geometry.fromWKT("POINT (-111 45.7)").buffer(10)
        Viewer.drawToFile(geom, file, size: [400,400])
        assertTrue(file.exists())
        File expectedFile = new File(getClass().getClassLoader().getResource("geoscript/viewer/drawtofile.png").toURI())
        ImageAssert.assertEquals(expectedFile, ImageIO.read(file), 100)
    }

    @Test void drawToFileWithOptions() {
        def file = folder.newFile("viewer_drawtofile_options.png")
        def geom = Geometry.fromWKT("POINT (-111 45.7)").buffer(10)
        Viewer.drawToFile([geom.buffer(10), geom], file, size: [400,400], bounds: geom.bounds.scale(2.0),
            strokeColor: "navy", fillColor: "wheat", markerShape: "cross", markerSize: 10,
            opacity: 0.65, strokeWidth: 1.5, drawCoords: true
        )
        assertTrue file.exists()
        File expectedFile = new File(getClass().getClassLoader().getResource("geoscript/viewer/drawtofilewithoptions.png").toURI())
        ImageAssert.assertEquals(expectedFile, ImageIO.read(file), 100)
    }

}

