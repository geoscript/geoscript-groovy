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
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.WindowConstants
import java.awt.geom.AffineTransform
import com.vividsolutions.jts.geom.Geometry as JtsGeometry
import com.vividsolutions.jts.geom.Polygon as JtsPolygon
import com.vividsolutions.jts.geom.Envelope
import com.vividsolutions.jts.geom.MultiPolygon as JtsMultiPolygon
import org.geotools.geometry.jts.LiteShape
import geoscript.geom.*

/**
 * A Viewer
 */
class Viewer {

    /**
     * Draw a Geometry (or Geometries) onto a Canvas
     */
    void draw(def geom, List size=[500,500]) {
        
        double buf = 50.0
        
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
        double ty = -e.maxY

        AffineTransform at = new AffineTransform()
        // Scale to size of canvas (inverting the y axis)
        at.scale(scale, -scale)
        // transolate to the origin
        at.translate(tx, ty) //ok
        // translate to account for invert
        at.translate(0, -(size[1] / scale))
        // buffer
        at.translate(buf/scale, -buf/scale)

        Panel panel = new Panel(geom, at)
        Dimension dim = new Dimension((int) (size[0] + 2 * buf), (int) (size[1] + 2 * buf))
        panel.preferredSize = dim
        panel.minimumSize = dim
        JFrame frame = new JFrame("Geoescript Viewer")
        try {
            frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        }
        catch(SecurityException ex) {
            frame.defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
        }
        frame.layout = new BorderLayout()
        frame.add(panel, BorderLayout.CENTER)
        frame.pack()
        frame.visible = true
    }

}

class Panel extends JPanel {
    
    List<Geometry> geoms

    AffineTransform atx

    Panel(List<Geometry> geoms, AffineTransform atx) {
        super()
        background = Color.WHITE
        opaque = true
        this.geoms = geoms
        this.atx = atx
    }

    void paintComponent(Graphics gr) {
        super.paintComponent(gr)
        Graphics2D g2d = (Graphics2D)gr
        g2d.color = Color.BLACK
        Composite c = g2d.composite
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2d.stroke = new BasicStroke(2)

        geoms.each{g ->
            LiteShape shp = new LiteShape(g.g, this.atx, false)
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




