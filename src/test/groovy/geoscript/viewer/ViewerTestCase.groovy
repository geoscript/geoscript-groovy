package geoscript.viewer

import org.junit.Test
import static org.junit.Assert.*
import geoscript.geom.Geometry
import javax.imageio.ImageIO

/**
 * The Viewer UnitTest
 * @author Jared Erickson
 */
class ViewerTestCase {

    @Test void drawToImage() {
        def geom = Geometry.fromWKT("POINT (-111 45.7)").buffer(10)
        def image = Viewer.drawToImage(geom)
        assertNotNull(image)
        def file = File.createTempFile("viewer_drawtoimage",".png")
        println file
        ImageIO.write(image,"png", file)
        assertTrue file.exists()
    }

    @Test void drawToImageWithOptions() {
        def geom = Geometry.fromWKT("POINT (-111 45.7)").buffer(10)
        def image = Viewer.drawToImage(geom, size: [400,400], bounds: geom.bounds.scale(2.0),
            strokeColor: "navy", fillColor: "wheat", markerShape: "cross", markerSize: 10,
            opacity: 0.65, strokeWidth: 1.5, drawCoords: true
        )
        assertNotNull(image)
        def file = File.createTempFile("viewer_drawtoimage_options",".png")
        println file
        ImageIO.write(image,"png", file)
        assertTrue file.exists()
    }

    @Test void drawToFile() {
        def file = File.createTempFile("viewer_drawtofile",".png")
        println(file)
        def geom = Geometry.fromWKT("POINT (-111 45.7)").buffer(10)
        Viewer.drawToFile(geom, file, size: [400,400])
        assertTrue(file.exists())
    }

    @Test void drawToFileWithOptions() {
        def file = File.createTempFile("viewer_drawtofile_options",".png")
        println file
        def geom = Geometry.fromWKT("POINT (-111 45.7)").buffer(10)
        Viewer.drawToFile([geom.buffer(10), geom], file, size: [400,400], bounds: geom.bounds.scale(2.0),
            strokeColor: "navy", fillColor: "wheat", markerShape: "cross", markerSize: 10,
            opacity: 0.65, strokeWidth: 1.5, drawCoords: true
        )
        assertTrue file.exists()
    }

    @Test void plotToImage() {
        def geom = Geometry.fromWKT("POINT (-111 45.7)").buffer(10)
        def image = Viewer.plotToImage(geom)
        assertNotNull(image)
        def file = File.createTempFile("viewer_plottoimage",".png")
        println file
        ImageIO.write(image,"png", file)
        assertTrue file.exists()
    }

    @Test void plotToImageWithOptions() {
        def geom = Geometry.fromWKT("POINT (-111 45.7)").buffer(10)
        def image = Viewer.plotToImage(geom, size: [400,400], legend: true, fillCoords: true, fillPolys: true)
        assertNotNull(image)
        def file = File.createTempFile("viewer_plottoimage_withoptions",".png")
        println file
        ImageIO.write(image,"png", file)
        assertTrue file.exists()
    }

    @Test void plotToFile() {
        def file = File.createTempFile("viewer_plottofile",".png")
        println(file)
        def geom = Geometry.fromWKT("POINT (-111 45.7)").buffer(10)
        Viewer.plotToFile(geom, size: [400,400], file, legend: false)
        assertTrue(file.exists())
    }

    @Test void plotToFileWithOptions() {
        def file = File.createTempFile("viewer_plottofile_withoptions",".png")
        println(file)
        def geom = Geometry.fromWKT("POINT (-111 45.7)").buffer(10)
        Viewer.plotToFile([geom, geom.buffer(10)], size: [400,400], file, legend: false, drawCoords: false)
        assertTrue(file.exists())
    }

}

