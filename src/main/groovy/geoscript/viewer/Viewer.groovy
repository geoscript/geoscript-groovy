package geoscript.viewer

import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Composite
import java.awt.Dimension
import java.awt.BasicStroke
import java.awt.RenderingHints
import java.awt.AlphaComposite
import java.awt.BorderLayout
import java.awt.Rectangle
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.WindowConstants
import java.awt.geom.AffineTransform
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import com.vividsolutions.jts.geom.Geometry as JtsGeometry
import com.vividsolutions.jts.geom.Polygon as JtsPolygon
import com.vividsolutions.jts.geom.Envelope
import com.vividsolutions.jts.geom.MultiPolygon as JtsMultiPolygon
import org.geotools.geometry.jts.LiteShape
import geoscript.geom.*

/**
 * A Viewer can be used to visualize Geometry.
 * <code><pre>
 * import geoscript.geom.Point
 * import geoscript.viewer.Viewer
 * Point p = new Point(10,10)
 * Viewer.open(p.buffer(100))
 * </pre></code>
 * @author Jared Erickson
 */
class Viewer {

    /**
     * Draw a Geometry (or Geometries) onto a Canvas
     * @param geom The Geomtry to List of Geometries to draw
     * @param A List containing the size of the viewer (defaults to 500 by 500)
     */
    static void open(def geom, List size=[500,500]) {
        Viewer v = new Viewer()
        v.draw(geom, size)
    }

    /**
     * Draw a Geometry (or Geometries) onto a Canvas
     * @param geom The Geomtry to List of Geometries to draw
     * @param A List containing the size of the viewer (defaults to 500 by 500)
     */
    void draw(def geom, List size=[500,500], double buf = 50) {
        if (!(geom instanceof List)) {
            geom = [geom]
        }
        Panel panel = new Panel(geom, worldToScreen(geom, size, buf))
        Dimension dim = new Dimension((int) (size[0] + 2 * buf), (int) (size[1] + 2 * buf))
        panel.preferredSize = dim
        panel.minimumSize = dim
        JFrame frame = new JFrame("Geoescript Viewer")
        try {
            frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        }
        catch(SecurityException ex) {
            frame.defaultCloseOperation = WindowConstants.HIDE_ON_CLOSE
        }
        frame.layout = new BorderLayout()
        frame.add(panel, BorderLayout.CENTER)
        frame.pack()
        frame.visible = true
    }



    /**
     * Create an AffineTransform that transforms coordinates from world
     * to the screen
     */
    static AffineTransform worldToScreen(def geom, List size=[500,500], double buf = 50) {

        if (!(geom instanceof List)) {
            geom = [geom]
        }

        def g = geom.collect{
            it.g
        }.toArray() as JtsGeometry[]

        JtsGeometry gc = Geometry.factory.createGeometryCollection(g)
        Envelope e = gc.envelopeInternal

        double scale =  (e.width > 0) ? size[0] / e.width : Double.MAX_VALUE
        scale = (e.height > 0) ? Math.min(scale, size[1] / e.height) : new Double(1).doubleValue()

        double tx = -e.minX
        double ty = -e.minY

        //AffineTransform at = new AffineTransform()
        // Scale to size of canvas (inverting the y axis)
        //at.scale(scale, -scale)
        // translate to the origin
        //at.translate(tx, ty)
        // translate to account for invert
        //at.translate(0, (-size[1]/ scale))
        // buffer
        //at.translate(buf/scale, -buf/scale)

        AffineTransform at = new AffineTransform()
        at.scale(scale, -scale)
        at.translate(
            (buf/scale) - e.minX + (((size[0] / scale) - e.width) / 2),
            (-buf/scale) - e.maxY - (((size[1] / scale) - e.height) / 2))


        //double scaleX = size[0] / e.width
        //double scaleY = size[1] / e.height
        //double tx = -e.minX * scaleX
        //double ty = (e.minY * scaleY) + size[1]
        //AffineTransform at = new AffineTransform(scaleX, 0.0, 0.0, -scaleY, tx, ty)

        //org.geotools.renderer.lite.RendererUtilities.worldToScreenTransform(
        //  new Bounds(e.minX, e.minY, e.maxX, e.maxY).env, new
        //  Rectangle(size[0], size[1]))

        return at
    }

    static BufferedImage createImage(def geom, List size=[500,500]) {
        BufferedImage image = new BufferedImage(size[0], size[1], BufferedImage.TYPE_INT_ARGB)
        Graphics2D g2d = image.createGraphics()
        g2d.color = Color.WHITE
        g2d.fillRect(0,0,size[0],size[1])
        drawToGraphics(g2d, worldToScreen(geom, size), geom)
        g2d.dispose()
        image
    }

    static void save(File file, def geom, List size=[500,500], String formatName = "png") {
        FileOutputStream out = new FileOutputStream(file)
        ImageIO.write(createImage(geom,size), formatName, out)
        out.close()
    }

    static void drawToGraphics(Graphics2D g2d, AffineTransform atx, def geom) {
        g2d.color = Color.BLACK
        Composite c = g2d.composite
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2d.stroke = new BasicStroke(2)

        if (!(geom instanceof List)) {
            geom = [geom]
        }

        geom.each{g ->
            LiteShape shp = new LiteShape(g.g, atx, false)
            if (g instanceof Polygon || g instanceof MultiPolygon) {
                g2d.color = Color.WHITE
                g2d.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, new Float(0.5).floatValue())
                g2d.fill(shp)
            }
            g2d.composite = c
            g2d.color = Color.BLACK
            g2d.draw(shp)
        }
    }

}

/**
 * The JPanel used to draw Geometry
 */
private class Panel extends JPanel {

    /**
     * The List of Geometries to draw
     */
    List<Geometry> geoms

    /**
     * The AffineTransform that converts between mapping and screen coordinates
     */
    AffineTransform atx

    /**
     * Create a new Panel with the List of Geometries to draw and the
     * AffineTransform that converts between mapping and screen coordinates
     * @param geoms The List of Geometries to draw
     * @param atx The AffineTransform that converts between mapping and screen coordinates
     */
    Panel(List<Geometry> geoms, AffineTransform atx) {
        super()
        background = Color.WHITE
        opaque = true
        this.geoms = geoms
        this.atx = atx
    }

    /**
     * Override the paintComponent method to draw the Geometries
     * @param gr The Graphics context
     */
    void paintComponent(Graphics gr) {
        super.paintComponent(gr)
        Graphics2D g2d = (Graphics2D)gr
        Viewer.drawToGraphics(g2d, atx, geoms)
    }
}
