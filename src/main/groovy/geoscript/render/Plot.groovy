package geoscript.render

import com.vividsolutions.jts.geom.Geometry as JtsGeometry

import java.awt.image.BufferedImage
import javax.swing.JFrame
import javax.swing.WindowConstants
import org.geotools.renderer.chart.GeometryDataset
import org.geotools.renderer.chart.GeometryRenderer
import org.jfree.chart.ChartPanel
import org.jfree.chart.ChartUtilities
import org.jfree.chart.JFreeChart
import org.jfree.chart.plot.XYPlot
import geoscript.geom.Geometry
import geoscript.feature.Feature
import geoscript.layer.Layer

/**
 * Plot a Geometry, Feature, or Layer.
 * @author Jared Erickson
 */
class Plot {

    static void plot(Geometry geometry, List size=[500,500], def out = null, String type = "png") {
        plot([geometry], size, out, type)
    }

    static void plot(List geometries, List size=[500,500], def out = null, String type = "png") {
        if (geometries.size() > 0 && geometries[0] instanceof Feature) {
            geometries = geometries.collect{it.geom}
        }
        def plot = createPlot(geometries)
        if (out == null) {
            def panel = new ChartPanel(plot)
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
        } else if (out instanceof OutputStream) {
            plotToOutputStream(plot, size, out, type)
        } else {
            File file = out instanceof File ? out : new File(out.toString())
            plotToFile(plot, size, file)
        }
    }

    static void plot(Feature feature, List size=[500,500], def out = null, String type = "png") {
        plot(feature.geom, size, out, type)
    }

    static void plot(Layer layer, List size=[500,500], def out = null, String type = "png") {
        plot(layer.features.collect{it.geom}, size, out, type)
    }

    static BufferedImage plotToImage(Geometry geometry, List size=[500,500]) {
        plotToImage([geometry], size)
    }

    static BufferedImage plotToImage(List geometries, List size=[500,500]) {
        if (geometries.size() > 0 && geometries[0] instanceof Feature) {
            geometries = geometries.collect{it.geom}
        }
        def chart = createPlot(geometries)
        chart.createBufferedImage(size[0] as int, size[1] as int)
    }

    static BufferedImage plotToImage(Feature feature, List size=[500,500]) {
        plotToImage(feature.geom, size)
    }

    static BufferedImage plotToImage(Layer layer, List size=[500,500]) {
        plotToImage(layer.features.collect{it.geom}, size)
    }

    /**
     * Create a JFreeChart from a Geometry (or List of Geometries) using the
     * GeoTools GeoemtryDataset
     * @param geom A Geometry (or List of Geometries)
     * @return The JFreeChart
     */
    private static JFreeChart createPlot(List<Geometry> geom) {
        def dataset = new GeometryDataset(geom.collect{g->g.g} as JtsGeometry[])
        def renderer = new GeometryRenderer()
        renderer.legend = false
        def plot = new XYPlot(dataset, dataset.domain, dataset.range, renderer)
        new JFreeChart(plot)
    }

    private static void plotToOutputStream(JFreeChart chart, List size, OutputStream out, String type) {
        if (type.equalsIgnoreCase("png")) {
            ChartUtilities.writeChartAsPNG(out, chart, size[0] as int, size[1] as int)
        }
        else if (type.equalsIgnoreCase("jpg") || type.equalsIgnoreCase("jpeg")) {
            ChartUtilities.writeChartAsJPEG(out, chart, size[0] as int, size[1] as int)
        }
        else {
            throw new Exception("Unsupported image format! Only PNGs and JPEGs are supported!")
        }
    }

    /**
     * Plot a Geometry (or List of Geometries) to an image File
     * @param geom The Geometry (or List of Geometries)
     * @param size The size of the image
     * @param file The File
     */
    private static void plotToFile(JFreeChart chart, List size=[500,500], File file) {
        String fileName = file.absolutePath
        String imageFormat = fileName.substring(fileName.lastIndexOf(".")+1)
        OutputStream out = new BufferedOutputStream(new FileOutputStream(file))
        plotToOutputStream(chart, size, out, imageFormat)
        out.flush()
        out.close()
    }
}
