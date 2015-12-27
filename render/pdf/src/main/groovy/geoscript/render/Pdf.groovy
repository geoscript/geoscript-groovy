package geoscript.render

import java.awt.Graphics

import com.lowagie.text.Document
import com.lowagie.text.Rectangle
import com.lowagie.text.pdf.PdfWriter
import com.lowagie.text.pdf.PdfContentByte

/**
 * Render the {@link geoscript.render.Map Map} to a PDF.
 * <p><blockquote><pre>
 * import geoscript.render.*
 * import geoscript.layer.*
 * import geoscript.style.*
 *
 * Map map = new Map(layers:[new Shapefile("states.shp")])
 * Pdf pdf = new Pdf()
 * pdf.render(map, new FileOutputStream(new File("states.pdf")))
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class Pdf extends Renderer<Document> {

    /**
     * Render the Map to a PDF Document
     * @param map The Map
     * @return The PDF Document
     */
    @Override
    public Document render(Map map) {
        OutputStream out = new ByteArrayOutputStream()
        return renderAndReturn(map, out)
    }

    /**
     * Render the Map to the OutputStream
     * @param map The Map
     * @param out The OutputStream
     */
    @Override
    public void render(Map map, OutputStream out) {
        renderAndReturn(map, out)
    }

    /**
     * Render the Map to the OutputStream and return the PDF Document.
     * @param map The Map
     * @param out The OutputStream
     * @return The PDF Document
     */
    private Document renderAndReturn(Map map, OutputStream out) {
        Document document = new Document(new Rectangle(map.width as float, map.height as float))
        PdfWriter writer = PdfWriter.getInstance(document, out)
        document.open()
        PdfContentByte cb = writer.getDirectContent()
        Graphics g = cb.createGraphicsShapes(document.pageSize.width, document.pageSize.height)
        map.render(g)
        g.dispose()
        document.close()
        return document
    }
}
