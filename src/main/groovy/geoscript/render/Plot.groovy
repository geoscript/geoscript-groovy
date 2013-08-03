package geoscript.render

import com.vividsolutions.jts.geom.Geometry as JtsGeometry

import geoscript.feature.Feature
import geoscript.geom.Geometry
import geoscript.layer.Layer
import java.awt.image.BufferedImage
import javax.swing.JFrame
import org.geotools.renderer.chart.GeometryDataset
import org.geotools.renderer.chart.GeometryRenderer
import org.jfree.chart.ChartPanel
import org.jfree.chart.ChartUtilities
import org.jfree.chart.JFreeChart
import org.jfree.chart.plot.XYPlot

/**
 * Plot a {@link geoscript.geom.Geometry Geometry}, {@link geoscript.feature.Feature Feature}, or {@geoscript.layer.Layer Layer}.
 * <p><blockquote><pre>
 * import static geoscript.render.Plot.*
 * import geoscript.geom.Point
 *
 * plot(new Point(1,1).buffer(10.0))
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class Plot {

    /**
     * Plot a Geometry
     * @param options A Map of options or named parameters
     * <ul>
     *  <li>size = The size of the plot ([500,500])</li>
     *  <li>out = OutputStream or File to write to.  If null an interactive app is opened.</li>
     *  <li>type = The image type ("png", "jpeg")</li>
     * </ul>
     * @param geometry The Geometry
     */
    static void plot(java.util.Map options = [:], Geometry geometry) {
        plot(options, [geometry])
    }

    /**
     * Plot a List of Geometries
     * @param options A Map of options or named parameters
     * <ul>
     *  <li>size = The size of the plot ([500,500])</li>
     *  <li>out = OutputStream or File to write to.  If null an interactive app is opened.</li>
     *  <li>type = The image type ("png", "jpeg")</li>
     * </ul>
     * @param geometries The List of Geometries
     */
    static void plot(java.util.Map options = [:], List geometries) {
        if (geometries.size() > 0 && geometries[0] instanceof Feature) {
            geometries = geometries.collect {it.geom}
        }
        def plot = createPlot(options, geometries)
        List size = options.get("size", [500,500])
        String type = options.get("type","png")
        def out = options.get("out",null)
        if (out == null) {
            def panel = new ChartPanel(plot)
            def frame = new JFrame("GeoScript Geometry Plot")
            // If we are opening Windows from the GroovyConsole, we can't use EXIT_ON_CLOSE because the GroovyConsole
            // itself will exit
            if (java.awt.Frame.frames.find{it.title.contains("GroovyConsole")}) {
                frame.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
            } else {
                // The Groovy Shell has a special SecurityManager that doesn't allow EXIT_ON_CLOSE
                try { frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE } catch (SecurityException ex) {frame.defaultCloseOperation = JFrame.HIDE_ON_CLOSE}
            }
            frame.contentPane = panel
            frame.setSize(size[0] as int, size[1] as int)
            frame.resizable = false
            frame.visible = true
        } else if (out instanceof OutputStream) {
            String
            plotToOutputStream(plot, size, out, type)
        } else {
            File file = out instanceof File ? out : new File(out.toString())
            plotToFile(plot, size, file)
        }
    }

    /**
     * Plot a Feature
     * @param options A Map of options or named parameters
     * <ul>
     *  <li>size = The size of the plot ([500,500])</li>
     *  <li>out = OutputStream or File to write to.  If null an interactive app is opened.</li>
     *  <li>type = The image type ("png", "jpeg")</li>
     * </ul>
     * @param feature The Feature
     */
    static void plot(java.util.Map options = [:], Feature feature) {
        plot(options, feature.geom)
    }

    /**
     * Plot a Layer
     * @param options A Map of options or named parameters
     * <ul>
     *  <li>size = The size of the plot ([500,500])</li>
     *  <li>out = OutputStream or File to write to.  If null an interactive app is opened.</li>
     *  <li>type = The image type ("png", "jpeg")</li>
     * </ul>
     * @param layer The Layer
     */
    static void plot(java.util.Map options = [:], Layer layer) {
        plot(options, layer.features.collect {it.geom})
    }

    /**
     * Plot a Geometry to an Image
     * @param options A Map of options or named parameters
     * <ul>
     *  <li>size = The size of the plot ([500,500])</li>
     * </ul>
     * @param geometry The Geometry
     * @return A BufferedImage
     */
    static BufferedImage plotToImage(java.util.Map options = [:], Geometry geometry) {
        plotToImage(options, [geometry])
    }

    /**
     * Plot a List of Geometries to an Image
     * @param options A Map of options or named parameters
     * <ul>
     *  <li>size = The size of the plot ([500,500])</li>
     * </ul>
     * @param geometries The List of Geometries
     * @return A BufferedImage
     */
    static BufferedImage plotToImage(java.util.Map options = [:], List geometries) {
        if (geometries.size() > 0 && geometries[0] instanceof Feature) {
            geometries = geometries.collect {it.geom}
        }
        def chart = createPlot(options, geometries)
        List size = options.get("size", [500,500])
        chart.createBufferedImage(size[0] as int, size[1] as int)
    }

    /**
     * Plot a Feature to an Image
     * @param options A Map of options or named parameters
     * <ul>
     *  <li>size = The size of the plot ([500,500])</li>
     * </ul>
     * @param feature The Feature
     * @return A BufferedImage
     */
    static BufferedImage plotToImage(java.util.Map options = [:], Feature feature) {
        plotToImage(options, feature.geom)
    }

    /**
     * Plot a Layer to an Image
     * @param options A Map of options or named parameters
     * <ul>
     *  <li>size = The size of the plot ([500,500])</li>
     * </ul>
     * @param layer The Layer
     * @return A BufferedImage
     */
    static BufferedImage plotToImage(java.util.Map options = [:], Layer layer) {
        plotToImage(options, layer.features.collect {it.geom})
    }

    /**
     * Create a JFreeChart from a Geometry (or List of Geometries) using the
     * GeoTools GeoemtryDataset
     * @param geom A Geometry (or List of Geometries)
     * @return The JFreeChart
     */
    private static JFreeChart createPlot(java.util.Map options = [:], List<Geometry> geom) {
        def dataset = new GeometryDataset(geom.collect {g -> g.g} as JtsGeometry[])
        def renderer = new GeometryRenderer()
        renderer.legend = options.get("legend", false)
        renderer.fillCoordinates = options.get("fillCoords", false)
        renderer.fillPolygons = options.get("fillPolys", false)
        renderer.renderCoordinates = options.get("drawCoords", true)
        def plot = new XYPlot(dataset, dataset.domain, dataset.range, renderer)
        new JFreeChart(plot)
    }

    /**
     * Plot the JFreeChart to the OutputStream
     * @param chart The JFreeChart
     * @param size The chart size
     * @param out The OutputStream
     * @param type The image type ("png" or "jpeg")
     */
    private static void plotToOutputStream(JFreeChart chart, List size, OutputStream out, String type) {
        if (type.equalsIgnoreCase("png")) {
            ChartUtilities.writeChartAsPNG(out, chart, size[0] as int, size[1] as int)
        }
        else if (type.equalsIgnoreCase("jpg") || type.equalsIgnoreCase("jpeg")) {
            ChartUtilities.writeChartAsJPEG(out, chart, size[0] as int, size[1] as int)
        }
        else {
            throw new IllegalArgumentException("Unsupported image format! Only PNGs and JPEGs are supported!")
        }
    }

    /**
     * Plot a Geometry (or List of Geometries) to an image File
     * @param geom The Geometry (or List of Geometries)
     * @param size The size of the image
     * @param file The File
     */
    private static void plotToFile(JFreeChart chart, List size = [500, 500], File file) {
        String fileName = file.absolutePath
        String imageFormat = fileName.substring(fileName.lastIndexOf(".") + 1)
        OutputStream out = new BufferedOutputStream(new FileOutputStream(file))
        plotToOutputStream(chart, size, out, imageFormat)
        out.flush()
        out.close()
    }
}
