package geoscript.render

import geoscript.layer.Layer
import geoscript.layer.Shapefile
import geoscript.style.Fill
import geoscript.style.Stroke
import org.junit.Test
import static org.junit.Assert.assertNotNull

/**
 * The Pdf UnitTest
 * @author Jared Erickson
 */
class PdfTestCase {

    @Test void renderToDocument() {
        File shpFile = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Layer layer = new Shapefile(shpFile)
        layer.style = new Stroke('black', 0.1) + new Fill('gray', 0.75)
        Map map = new Map(layers: [layer])
        Pdf pdf = new Pdf()
        def doc = pdf.render(map)
        assertNotNull(doc)
    }

    @Test void renderToOutputStream() {
        File shpFile = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Layer layer = new Shapefile(shpFile)
        layer.style = new Stroke('black', 0.1) + new Fill('gray', 0.75)
        Map map = new Map(layers: [layer])
        Pdf pdf = new Pdf()
        File file = File.createTempFile("pdf_",".pdf")
        println file
        OutputStream out = new FileOutputStream(file)
        pdf.render(map, out)
        out.close()
    }
}
