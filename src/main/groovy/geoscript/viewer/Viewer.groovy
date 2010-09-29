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
import org.geotools.renderer.chart.GeometryDataset
import org.geotools.renderer.chart.GeometryRenderer
import org.jfree.chart.JFreeChart
import org.jfree.chart.ChartPanel
import org.jfree.chart.ChartUtilities
import org.jfree.chart.plot.XYPlot
import geoscript.geom.*

/**
 * A Viewer can be used to visualize Geometry.
 * <code><pre>
 * import geoscript.geom.Point
 * import geoscript.viewer.Viewer
 * Point p = new Point(10,10)
 * Viewer.draw(p.buffer(100))
 * </pre></code>
 * Or you can plot a List of Geometries.
 * <code<pre>
 * import geoscript.geom.Point
 * import geoscript.viewer.Viewer
 * Point p = new Point(10,10)
 * Viewer.plot([p, p.buffer(50), p.buffer(100)])
 * </pre></code>
 * @author Jared Erickson
 */
class Viewer {

    /**
     * Draw a Geometry (or List of Geometries) onto a GUI
     * @param geom The Geomtry to List of Geometries to draw
     * @param A List containing the size of the GUI (defaults to 500 by 500)
     */
    static void draw(def geom, List size=[500,500], double buf = 50) {
        if (!(geom instanceof List)) {
            geom = [geom]
        }
        Panel panel = new Panel(geom, worldToScreen(geom, size, buf))
        Dimension dim = new Dimension((int) (size[0] + 2 * buf), (int) (size[1] + 2 * buf))
        panel.preferredSize = dim
        panel.minimumSize = dim
        JFrame frame = new JFrame("GeoScript Viewer")
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
     * Draw Geometry (or List of Geometries) to a BufferedImage
     * @param geom A Geometry or a List of Geometries
     * @param size The size of image to create
     * @return A BufferedImage
     */
    static BufferedImage drawToImage(def geom, List size=[500,500]) {
        BufferedImage image = new BufferedImage(size[0], size[1], BufferedImage.TYPE_INT_ARGB)
        Graphics2D g2d = image.createGraphics()
        g2d.color = Color.WHITE
        g2d.fillRect(0,0,size[0],size[1])
        drawToGraphics(g2d, worldToScreen(geom, size), geom)
        g2d.dispose()
        image
    }

    /**
     * Save a drawing of the Geometry (or List of Geometries) to a File
     * @param file The File
     * @param geom A Geometry or a List of Geometries
     * @param size The image size
     * @param formatName The type of image to create (png, jpg)
     */
    static void drawToFile(def geom, List size=[500,500], File file) {
        String fileName = file.absolutePath
        String imageFormat = fileName.substring(fileName.lastIndexOf(".")+1)
        FileOutputStream out = new FileOutputStream(file)
        ImageIO.write(drawToImage(geom,size), imageFormat, out)
        out.close()
    }

    /**
     * Plot the Geometry (or List of Geometries) to a Swing JFrame using JFreeChart
     * @param geom A Geometry (or List of Geometries)
     * @param size The Size of the Frame
     */
    static void plot(def geom, List size=[500,500]) {
        def panel = new ChartPanel(createPlot(geom))
        def frame = new JFrame("GeoScript Geometry Plot")
        try {
            frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        }
        catch(SecurityException ex) {
            frame.defaultCloseOperation = WindowConstants.HIDE_ON_CLOSE
        }
        frame.contentPane = panel
        frame.setSize(size[0] as int, size[1] as int)
        frame.visible = true
    }

    /**
     * Plot a Geometry (or List of Geometries) to a BufferedImage
     * @param geom A Geometry (or List of Geometries)
     * @param size The size of image to create
     */
    static BufferedImage plotToImage(def geom, List size=[500,500]) {
        def chart = createPlot(geom)
        chart.createBufferedImage(size[0] as int, size[1] as int)
    }

    /**
     * Plot a Geometry (or List of Geometries) to an image File
     * @param geom The Geometry (or List of Geometries)
     * @param size The size of the image
     * @param file The File
     */
    static void plotToFile(def geom, List size=[500,500], File file) {
        def chart = createPlot(geom)
        String fileName = file.absolutePath
        String imageFormat = fileName.substring(fileName.lastIndexOf(".")+1)
        if (imageFormat.equalsIgnoreCase("png")) {
            ChartUtilities.saveChartAsPNG(file, chart, size[0] as int, size[1] as int)
        }
        else if (imageFormat.equalsIgnoreCase("jpg") || imageFormat.equalsIgnoreCase("jpeg")) {
            ChartUtilities.saveChartAsJPEG(file, chart, size[0] as int, size[1] as int)
        }
        else {
            throw new Exception("Unsupported image format! Only PNGs and JPEGs are supported!")
        }
    }

    /**
     * Create a JFreeChart from a Geometry (or List of Geometries) using the
     * GeoTools GeoemtryDataset
     * @param geom A Geometry (or List of Geometries)
     * @return The JFreeChart
     */
    private static JFreeChart createPlot(def geom) {
        if (!(geom instanceof List)) {
            geom = [geom]
        }
        def dataset = new GeometryDataset(geom.collect{g->g.g} as JtsGeometry[])
        def renderer = new GeometryRenderer()
        def plot = new XYPlot(dataset, dataset.domain, dataset.range, renderer)
        new JFreeChart(plot)
    }

    /**
     * Create an AffineTransform that transforms coordinates from world
     * to the screen
     */
    private static AffineTransform worldToScreen(def geom, List size=[500,500], double buf = 50.0) {

        // Make sure the geom parameter is a List of Geometries
        if (!(geom instanceof List)) {
            geom = [geom]
        }

        // Turn into an Array of JTS Geometries
        def g = geom.collect{
            it.g
        }.toArray() as JtsGeometry[]

        // Create a JTS GeometryCollection
        JtsGeometry gc = Geometry.factory.createGeometryCollection(g)
        Envelope e = gc.envelopeInternal

        // Image width and height
        double imageWidth  = size[0] as double
        double imageHeight = size[1] as double

        // Extent width and height
        double extentWidth = e.width
        double extentHeight = e.height

        // Scale
        double scaleX = extentWidth  > 0 ? imageWidth  / extentWidth  : java.lang.Double.MAX_VALUE
        double scaleY = extentHeight > 0 ? imageHeight / extentHeight : 1.0 as double
        double scale = Math.min(scaleX, scaleY)

        double tx = -e.minX * scaleX
        double ty = (e.minY * scaleY) + (imageHeight)

        // AffineTransform
        AffineTransform at = new AffineTransform(scaleX, 0.0d, 0.0d, -scaleY, tx, ty)

        //AffineTransform at = new AffineTransform()
        // Scale to size of canvas by inverting the y axis
        //at.scale(scale, -scale)

        // translate to the origin
        //at.translate(-e.minX, -e.minY)

        // translate to account for the invert
        //at.translate(0, -(imageHeight / scale))

        // translate to account for the buffer
        //at.translate(buf / scale, -buf / scale)

        return at
    }

    /**
     * Draw a Geometry or a List of Geometries to the given Graphics2D with the AffineTransform
     * @param g2d The Graphics2D
     * @param atx The AffineTransform
     * @param geom A Geometry or a List of Geometries
     */
    private static void drawToGraphics(Graphics2D g2d, AffineTransform atx, def geom) {
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
