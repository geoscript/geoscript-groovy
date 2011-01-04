package geoscript.print

import java.awt.Graphics2D
import com.lowagie.text.Document
import com.lowagie.text.Rectangle
import com.lowagie.text.pdf.PdfWriter
import com.lowagie.text.pdf.PdfContentByte
import com.lowagie.text.pdf.PdfTemplate

/**
 * The PdfProducer can write PDF documents.
 * @author Jared Erickson
 */
class PdfProducer implements Producer {

    /**
     * Does this Producer support the given mime type?
     * @param mimeType The mime type
     * @return Whether this Producer handles the mime type
     */
    boolean handlesMimeType(String mimeType) {
        if (mimeType.equalsIgnoreCase("application/pdf") || mimeType.equalsIgnoreCase("pdf")) {
            return true
        } else {
            return false
        }
    }

    /**
     * Write the print Template to the OutputStream in the given mime type
     * @param template The print Template
     * @param mimeType The mime type
     * @param out The OutputStream
     */
    void produce(Template template, String mimeType, OutputStream out) {
        Document document = new Document(new Rectangle(template.width as float, template.height as float))
        PdfWriter writer = PdfWriter.getInstance(document, out)
        document.open()
        PdfContentByte cb = writer.getDirectContent()
        Graphics2D g2 = cb.createGraphicsShapes(document.pageSize.width, document.pageSize.height)
        template.draw(g2)
        g2.dispose()
        document.close()
    }
}

