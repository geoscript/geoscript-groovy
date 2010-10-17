package geoscript.print

import java.io.OutputStream
import java.io.OutputStreamWriter
import org.apache.batik.dom.svg.SVGDOMImplementation
import org.apache.batik.svggen.SVGGraphics2D
import org.w3c.dom.DOMImplementation
import org.w3c.dom.Document

/**
 *
 * @author jericks
 */
class SvgProducer implements Producer {

    /**
     * Does this Producer support the given mime type?
     * @param mimeType The mime type
     * @return Whether this Producer handles the mime type
     */
    boolean handlesMimeType(String mimeType) {
        if (mimeType.equalsIgnoreCase("image/svg+xml") || mimeType.equalsIgnoreCase("svg")) {
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
        DOMImplementation impl = SVGDOMImplementation.getDOMImplementation()
        String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI
        Document doc = impl.createDocument(svgNS, "svg", null)
        SVGGraphics2D g = new SVGGraphics2D(doc)
        template.draw(g)
        g.stream(new OutputStreamWriter(out));
        g.dispose()
    }

}

