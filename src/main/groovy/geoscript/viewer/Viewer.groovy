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
import java.awt.Shape
import java.awt.geom.Point2D
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.WindowConstants
import java.awt.geom.AffineTransform
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import com.vividsolutions.jts.awt.ShapeWriter
import com.vividsolutions.jts.awt.PointTransformation
import com.vividsolutions.jts.awt.SqarePointShapeFactory
import com.vividsolutions.jts.geom.Coordinate
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
        Panel panel = new Panel(geom)
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
        //g2d.color = Color.WHITE
        //g2d.fillRect(0,0,size[0],size[1])
        Bounds bounds = new GeometryCollection(geom).bounds
        bounds.expandBy(bounds.width * 0.10)
        geom = geom instanceof List ? geom : [geom]
        geom.each{g ->
            def c = randomColor()
            draw(g2d, size, [g], bounds, c.darker(), c)
        }
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
     * Generate a random color
     * @return A Color
     */
    private static Color randomColor() {
        def random = new java.util.Random()
        int red = random.nextInt(256)
        int green = random.nextInt(256)
        int blue = random.nextInt(256)
        new Color(red,green,blue)
    }

    /**
     * Draw a List of GeoScript Geometries to a Graphics2D.
     * @param g2d The Graphics2D
     * @param size The size of the Graphics2D/Image
     * @param geometries A List of GeoScript Geometries
     */
    static void draw(Graphics2D g2d, List size, List geometries, Bounds bounds,
        Color strokeColor = new Color(99,99,99), Color fillColor = new Color(206,206,206),
        String markerShape = "square", double markerSize = 8, float opacity = 0.75, float strokeWidth = 1.0,
        boolean drawCoordinates = false) {

        int imageWidth = size[0]
        int imageHeight = size[1]

        // @TODO Add support for other shapes when jts 1.11 comes out
        def shapeFactory = new SqarePointShapeFactory(markerSize)

        ShapeWriter shapeWriter = new ShapeWriter({Coordinate mapCoordinate, Point2D shape ->
            double imageX = (1 - (bounds.r - mapCoordinate.x) / bounds.width) * imageWidth
            double imageY = ((bounds.t - mapCoordinate.y) / bounds.height) * imageHeight
            shape.setLocation(imageX,imageY);
        } as PointTransformation, shapeFactory)

        Composite strokeComposite = g2d.getComposite()
        Composite fillComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity)

        geometries.each{geometry->
            Shape shp = shapeWriter.toShape(geometry.g)
            // Fill
            g2d.setComposite(fillComposite)
            g2d.setColor(fillColor)
            g2d.fill(shp)
            // Stroke
            g2d.setComposite(strokeComposite)
            g2d.setStroke(new BasicStroke(strokeWidth))
            g2d.setColor(strokeColor)
            g2d.draw(shp)
            if (drawCoordinates) {
                g2d.setStroke(new BasicStroke(strokeWidth))
                List coords = geometry.coordinates
                if (coords.size() > 1) {
                    coords.each{c ->
                        Shape coordinateShp = shapeWriter.toShape(geometry.g.getFactory().createPoint(c))
                        // Fill
                        g2d.setComposite(fillComposite)
                        g2d.setColor(fillColor)
                        g2d.fill(coordinateShp)
                        // Stroke
                        g2d.setComposite(strokeComposite)
                        g2d.setColor(strokeColor)
                        g2d.draw(coordinateShp)
                    }
                }
            }
        }
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
}

/**
 * The JPanel used to draw Geometry
 */
private static class Panel extends JPanel {

    /**
     * The List of Geometries to draw
     */
    private List<Geometry> geoms

    private boolean drawCoordinates = false
    private Color color = Viewer.randomColor()
    private String markerShape = "square"
    private double markerSize = 8
    private float opacity = 0.75
    private float strokeWidth = 1.0

    /**
     * Create a new Panel with the List of Geometries to draw
     * @param geoms The List of Geometries to draw
     */
    Panel(List<Geometry> geoms) {
        super()
        background = Color.WHITE
        opaque = true
        this.geoms = geoms
    }

    /**
     * Override the paintComponent method to draw the Geometries
     * @param gr The Graphics context
     */
    void paintComponent(Graphics gr) {
        super.paintComponent(gr)
        List size = [getWidth(), getHeight()]
        Graphics2D g2d = (Graphics2D)gr
        Bounds bounds = new GeometryCollection(geoms).bounds
        bounds.expandBy(bounds.width * 0.10)
        geoms.each{g ->
            Viewer.draw(g2d, size, [g], bounds, color.darker(), color, markerShape, markerSize, opacity, strokeWidth, drawCoordinates)
        }
    }
}