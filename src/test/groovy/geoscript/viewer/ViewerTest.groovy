package geoscript.viewer

import org.geotools.image.test.ImageAssert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import static org.junit.jupiter.api.Assertions.*
import geoscript.geom.Geometry
import javax.imageio.ImageIO

/**
 * The Viewer UnitTest
 * @author Jared Erickson
 */
class ViewerTest {

    @TempDir
    File folder

    @Test void drawToImage() {
        def geom = Geometry.fromWKT("POINT (-111 45.7)").buffer(10)
        def image = Viewer.drawToImage(geom)
        assertNotNull(image)
        def file = new File(folder,"viewer_drawtoimage.png")
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
        def file = new File(folder,"viewer_drawtoimage_options.png")
        ImageIO.write(image,"png", file)
        assertTrue file.exists()
        File expectedFile = new File(getClass().getClassLoader().getResource("geoscript/viewer/drawtoimagewithoptions.png").toURI())
        ImageAssert.assertEquals(expectedFile, image, 100)
    }

    @Test void drawToBase64EncodedString() {
        def geom = Geometry.fromWKT("POINT (-111 45.7)").buffer(10)
        String str = Viewer.drawToBase64EncodedString(geom)
        assertNotNull(str)
        assertTrue(str.startsWith("image/png;base64,"))
    }

    @Test void drawToFile() {
        def file = new File(folder,"viewer_drawtofile.png")
        def geom = Geometry.fromWKT("POINT (-111 45.7)").buffer(10)
        Viewer.drawToFile(geom, file, size: [400,400])
        assertTrue(file.exists())
        File expectedFile = new File(getClass().getClassLoader().getResource("geoscript/viewer/drawtofile.png").toURI())
        ImageAssert.assertEquals(expectedFile, ImageIO.read(file), 100)
    }

    @Test void drawToFileWithOptions() {
        def file = new File(folder,"viewer_drawtofile_options.png")
        def geom = Geometry.fromWKT("POINT (-111 45.7)").buffer(10)
        Viewer.drawToFile([geom.buffer(10), geom], file, size: [400,400], bounds: geom.bounds.scale(2.0),
            strokeColor: "navy", fillColor: "wheat", markerShape: "cross", markerSize: 10,
            opacity: 0.65, strokeWidth: 1.5, drawCoords: true
        )
        assertTrue file.exists()
        File expectedFile = new File(getClass().getClassLoader().getResource("geoscript/viewer/drawtofilewithoptions.png").toURI())
        ImageAssert.assertEquals(expectedFile, ImageIO.read(file), 100)
    }

    @Test void plotToImage() {
        def geom = Geometry.fromWKT("POINT (-111 45.7)").buffer(10)
        def image = Viewer.plotToImage(geom)
        assertNotNull(image)
        def file = new File(folder,"viewer_plottoimage.png")
        ImageIO.write(image,"png", file)
        assertTrue file.exists()
        File expectedFile = new File(getClass().getClassLoader().getResource("geoscript/viewer/plottoimage.png").toURI())
        ImageAssert.assertEquals(expectedFile, image, 10000)
    }

    @Test void plotToImageWithOptions() {
        def geom = Geometry.fromWKT("POINT (-111 45.7)").buffer(10)
        def image = Viewer.plotToImage(geom, size: [400,400], legend: true, fillCoords: true, fillPolys: true)
        assertNotNull(image)
        def file = new File(folder,"viewer_plottoimage_withoptions.png")
        ImageIO.write(image,"png", file)
        assertTrue file.exists()
        File expectedFile = new File(getClass().getClassLoader().getResource("geoscript/viewer/plottoimagewithoptions.png").toURI())
        ImageAssert.assertEquals(expectedFile, image, 10000)
    }

    @Test void plotToFile() {
        def file = new File(folder,"viewer_plottofile.png")
        def geom = Geometry.fromWKT("POINT (-111 45.7)").buffer(10)
        Viewer.plotToFile(geom, size: [400,400], file, legend: false)
        assertTrue(file.exists())
        File expectedFile = new File(getClass().getClassLoader().getResource("geoscript/viewer/plottofile.png").toURI())
        ImageAssert.assertEquals(expectedFile, ImageIO.read(file), 10000)
    }

    @Test void plotToFileWithOptions() {
        def file = new File(folder,"viewer_plottofile_withoptions.png")
        def geom = Geometry.fromWKT("POINT (-111 45.7)").buffer(10)
        Viewer.plotToFile([geom, geom.buffer(10)], size: [400,400], file, legend: false, drawCoords: false)
        assertTrue(file.exists())
        File expectedFile = new File(getClass().getClassLoader().getResource("geoscript/viewer/plottofilewithoptions.png").toURI())
        ImageAssert.assertEquals(expectedFile, ImageIO.read(file), 10000)
    }

}

