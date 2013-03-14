package geoscript.plot

import org.jfree.chart.ChartPanel
import org.jfree.chart.ChartUtilities
import org.jfree.chart.JFreeChart
import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.plot.DatasetRenderingOrder
import org.jfree.chart.plot.Plot

import javax.swing.JFrame
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.image.BufferedImage

/**
 * A Chart.
 * @author Jared Erickson
 */
class Chart {

    /**
     * The wrapped JFreeChart
     */
    JFreeChart chart

    /**
     * The JFrame
     */
    private JFrame frame

    /**
     * The number of datasets (used by overlay method)
     */
    private int datasets

    /**
     * Create a new Chart wrapping a JFreeChart
     * @param chart The wrapped JFreeChart
     */
    Chart(JFreeChart chart) {
        this.chart = chart
        this.datasets = 1
    }

    /**
     * Open this Chart in a Window.
     * @param options Named parameters options
     * <ul>
     *     <li>size: A List of width and height values</li>
     * </ul>
     */
    void show(java.util.Map options = [:]) {
        java.util.List size = options.get("size", [500,500])

        def panel = new ChartPanel(this.chart)

        JFrame frame = new JFrame("GeoScript Viewer")
        // If we are opening Windows from the GroovyConsole, we can't use EXIT_ON_CLOSE because the GroovyConsole
        // itself will exit
        if (java.awt.Frame.frames.find{it.title.contains("GroovyConsole")}) {
            frame.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
        } else {
            // The Groovy Shell has a special SecurityManager that doesn't allow EXIT_ON_CLOSE
            try { frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE } catch (SecurityException ex) {frame.defaultCloseOperation = JFrame.HIDE_ON_CLOSE}
        }
        frame.layout = new BorderLayout()
        frame.add(panel, BorderLayout.CENTER)
        frame.size = new Dimension(size[0], size[1])
        frame.pack()
        frame.visible = true
        this.frame = frame
    }

    /**
     * Dispose of this Chart.
     */
    void dispose() {
       if (this.frame) {
           this.frame.dispose()
       }
       this.frame = null
    }

    /**
     * Overlay a List of Charts over this Chart
     * @param charts A List of Charts
     */
    void overlay(List<Chart> charts) {
        charts.each{Chart chart ->
            Plot plot = this.chart.plot
            plot.setDataset(this.datasets, chart.chart.plot.dataset)
            plot.setRenderer(this.datasets, chart.chart.plot.renderer)
            plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD)
            def yAxis = new NumberAxis("")
            plot.setRangeAxis(this.datasets, yAxis)
            this.datasets++
        }
    }

    /**
     * Get an image of this Chart
     * @param options Named parameters options
     * <ul>
     *     <li>size: A List of width and height values</li>
     * </ul>
     */
    BufferedImage getImage(java.util.Map options = [:]) {
        java.util.List size = options.get("size", [500,500])
        this.chart.createBufferedImage(size[0] as int, size[1] as int)
    }

    /**
     * Svae this Chart to a File
     * @param options Named parameters options
     * <ul>
     *     <li>size: A List of width and height values</li>
     * </ul>
     * @param file The File
     */
    void save(java.util.Map options = [:], File file) {
        java.util.List size = options.get("size", [500,500])
        String fileName = file.absolutePath
        String imageFormat = fileName.substring(fileName.lastIndexOf(".")+1)
        if (imageFormat.equalsIgnoreCase("png")) {
            ChartUtilities.saveChartAsPNG(file, this.chart, size[0] as int, size[1] as int)
        }
        else if (imageFormat.equalsIgnoreCase("jpg") || imageFormat.equalsIgnoreCase("jpeg")) {
            ChartUtilities.saveChartAsJPEG(file, this.chart, size[0] as int, size[1] as int)
        }
        else {
            throw new IllegalArgumentException("Unsupported image format! Only PNGs and JPEGs are supported!")
        }
    }
}