package geoscript.render

import org.apache.batik.dom.svg.SVGDOMImplementation
import org.apache.batik.svggen.SVGGraphics2D
import org.w3c.dom.DOMImplementation
import org.w3c.dom.Document

/**
 * Render a {@link geoscript.render.Map Map} to an SVG Document.
 * <p><blockquote><pre>
 * import geoscript.render.*
 * import geoscript.layer.*
 * import geoscript.style.*

 * Map map = new Map(layers:[new Shapefile("states.shp")])
 * Svg svg = new Svg()
 * svg.render(map, new FileOutputStream(new File("states.svg")))
 * </pre></blockquote></p>
 * @author Jared Ericksons
 */
class Svg extends Renderer<Document> {

    /**
     * Render the Map to a SVG Document
     * @param map The Map
     * @return The SVG Document
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
     * Render the Map to the OutputStream and return the SVG Document
     * @param map The Map
     * @param out The OutputStream
     * @return The SVG Document
     */
    private Document renderAndReturn(Map map, OutputStream out) {
        DOMImplementation impl = SVGDOMImplementation.getDOMImplementation()
        String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI
        Document doc = impl.createDocument(svgNS, "svg", null)
        SVGGraphics2D g = new SVGGraphics2D(doc)
        map.render(g)
        g.stream(new OutputStreamWriter(out))
        g.dispose()
        return doc
    }
}
