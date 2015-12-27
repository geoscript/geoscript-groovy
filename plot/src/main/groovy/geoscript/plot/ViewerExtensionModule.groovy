package geoscript.plot

import com.vividsolutions.jts.geom.Geometry
import geoscript.viewer.Viewer
import org.geotools.renderer.chart.GeometryDataset
import org.geotools.renderer.chart.GeometryRenderer
import org.jfree.chart.ChartPanel
import org.jfree.chart.ChartUtilities
import org.jfree.chart.JFreeChart
import org.jfree.chart.plot.XYPlot

import javax.swing.JFrame
import javax.swing.WindowConstants
import java.awt.image.BufferedImage

/**
 * A Groovy Extension Module that adds static methods to the Viewer class.
 * @author Jared Erickson
 */
class ViewerExtensionModule {

    /**
     * Plot the Geometry (or List of Geometries) to a Swing JFrame using JFreeChart.
     * @param viewer The Viewer
     * @param options A Map of options or named parameters
     * <ul>
     *    <li>size = The width and height ([500,500])</li>
     *    <li>legend = Whether or not to show the legend (true | false)</li>
     *    <li>fillCoords = Whether or not to fill the coordinates (true | false)</li>
     *    <li>fillPolys = Whether or not to fill the polygons (true | false)</li>
     *    <li>drawCoords = Whether or not to draw the coordinates (true | false)</li>
     * </ul>
     * @param geom A Geometry (or List of Geometries)
     */
    static void plot(Viewer viewer, java.util.Map options = [:], def geom) {
        java.util.List size = options.get("size", [500,500])
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
     * @param viewer The Viewer
     * @param options A Map of options or named parameters
     * <ul>
     *    <li>size = The width and height ([500,500])</li>
     *    <li>legend = Whether or not to show the legend (true | false)</li>
     *    <li>fillCoords = Whether or not to fill the coordinates (true | false)</li>
     *    <li>fillPolys = Whether or not to fill the polygons (true | false)</li>
     *    <li>drawCoords = Whether or not to draw the coordinates (true | false)</li>
     * </ul>
     * @param geom A Geometry (or List of Geometries)
     */
    static BufferedImage plotToImage(Viewer viewer, java.util.Map options = [:], def geom) {
        def chart = createPlot(options, geom)
        java.util.List size = options.get("size", [500,500])
        chart.createBufferedImage(size[0] as int, size[1] as int)
    }

    /**
     * Plot a Geometry (or List of Geometries) to an image File
     * @param viewer The Viewer
     * @param options A Map of options or named parameters
     * <ul>
     *    <li>size = The width and height ([500,500])</li>
     *    <li>legend = Whether or not to show the legend (true | false)</li>
     *    <li>fillCoords = Whether or not to fill the coordinates (true | false)</li>
     *    <li>fillPolys = Whether or not to fill the polygons (true | false)</li>
     *    <li>drawCoords = Whether or not to draw the coordinates (true | false)</li>
     * </ul>
     * @param geom The Geometry (or List of Geometries)
     * @param file The File
     */
    static void plotToFile(Viewer viewer, java.util.Map options = [:], def geom, File file) {
        java.util.List size = options.get("size", [500,500])
        def chart = createPlot(options, geom)
        String fileName = file.absolutePath
        String imageFormat = fileName.substring(fileName.lastIndexOf(".")+1)
        if (imageFormat.equalsIgnoreCase("png")) {
            ChartUtilities.saveChartAsPNG(file, chart, size[0] as int, size[1] as int)
        }
        else if (imageFormat.equalsIgnoreCase("jpg") || imageFormat.equalsIgnoreCase("jpeg")) {
            ChartUtilities.saveChartAsJPEG(file, chart, size[0] as int, size[1] as int)
        }
        else {
            throw new IllegalArgumentException("Unsupported image format! Only PNGs and JPEGs are supported!")
        }
    }

    /**
     * Create a JFreeChart from a Geometry (or List of Geometries) using the
     * GeoTools Geometry Dataset
     * @param viewer The Viewer
     * @param options A Map of options or named parameters
     * <ul>
     *    <li>size = The width and height ([500,500])</li>
     *    <li>legend = Whether or not to show the legend (true | false)</li>
     *    <li>fillCoords = Whether or not to fill the coordinates (true | false)</li>
     *    <li>fillPolys = Whether or not to fill the polygons (true | false)</li>
     *    <li>drawCoords = Whether or not to draw the coordinates (true | false)</li>
     * </ul>
     * @param geom A Geometry (or List of Geometries)
     * @return The JFreeChart
     */
    private static JFreeChart createPlot(java.util.Map options = [:], def geom) {
        if (!(geom instanceof java.util.List)) {
            geom = [geom]
        }
        def dataset = new GeometryDataset(geom.collect{g->g.g} as Geometry[])
        def renderer = new GeometryRenderer()
        renderer.legend = options.get("legend", true)
        renderer.fillCoordinates = options.get("fillCoords", false)
        renderer.fillPolygons = options.get("fillPolys", false)
        renderer.renderCoordinates = options.get("drawCoords", true)
        def plot = new XYPlot(dataset, dataset.domain, dataset.range, renderer)
        new JFreeChart(plot)
    }
}
