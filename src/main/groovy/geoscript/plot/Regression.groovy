package geoscript.plot

import org.jfree.chart.JFreeChart
import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.plot.XYPlot
import org.jfree.chart.renderer.xy.XYDotRenderer
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
import org.jfree.data.function.LineFunction2D
import org.jfree.data.function.PowerFunction2D
import org.jfree.data.general.DatasetUtilities
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import org.jfree.data.statistics.Regression as JFreeRegression

/**
 * Create a regression Chart
 * @author Jared Erickson
 */
class Regression {

    /**
     * Create a linear regression Chart
     * @param options Named parameter options
     * <ul>
     *     <li>xLabel: The x axis label</li>
     *     <li>yLabel: The y axis label</li>
     *     <li>name: The series name</li>
     *     <li>samples: The number of samples</li>
     *     <li>lines: Whether to show lines</li>
     *     <li>shapes: Whether to show shapes</li>
     *     <li>legend: Whether to show legend</li>
     *     <li>size: The size of markers</li>
     * </ul>
     * @param data A List of data where each item is a List with two items.
     * @return A Chart
     */
    static Chart linear(Map options = [:], List data) {
        options.regtype = 0
        regression(options, data)
    }

    /**
     * Create a power regression Chart
     * @param options Named parameter options
     * <ul>
     *     <li>xLabel: The x axis label</li>
     *     <li>yLabel: The y axis label</li>
     *     <li>name: The series name</li>
     *     <li>samples: The number of samples</li>
     *     <li>lines: Whether to show lines</li>
     *     <li>shapes: Whether to show shapes</li>
     *     <li>legend: Whether to show legend</li>
     *     <li>size: The size of markers</li>
     * </ul>
     * @param data A List of data where each item is a List with two items.
     * @return A Chart
     */
    static Chart power(Map options = [:], List data) {
        options.regtype = 1
        regression(options, data)
    }

    /**
     * Create a regression Chart
     * @param options Named parameter options
     * <ul>
     *     <li>regType: The regression type (0 for linear, 1 for power)</li>
     *     <li>xLabel: The x axis label</li>
     *     <li>yLabel: The y axis label</li>
     *     <li>name: The series name</li>
     *     <li>samples: The number of samples</li>
     *     <li>lines: Whether to show lines</li>
     *     <li>shapes: Whether to show shapes</li>
     *     <li>legend: Whether to show legend</li>
     *     <li>size: The size of markers</li>
     * </ul>
     * @param data A List of data where each item is a List with two items.
     * @return A Chart
     */
    static Chart regression(Map options = [:], List data) {
        int regType = options.get("regtype",0)
        String xLabel = options.get("xLabel","x")
        String yLabel = options.get("yLabel","y")
        String name = options.get("name","Values")
        int samples = options.get("samples",100)
        boolean lines = options.get("lines", true)
        boolean shapes = options.get("shapes", false)
        boolean legend = options.get("legend", true)
        int size = options.get("size",3)

        def xAxis = new NumberAxis(xLabel)
        xAxis.autoRangeIncludesZero = false
        def yAxis = new NumberAxis(yLabel)
        yAxis.autoRangeIncludesZero = false

        def series = new XYSeries(name)
        double xMax = Double.MIN_VALUE
        double xMin = Double.MAX_VALUE
        data.each{datum ->
            series.add(datum[0], datum[1])
            xMax = Math.max(xMax, datum[0])
            xMin = Math.min(xMin, datum[0])
        }

        def dataset = new XYSeriesCollection()
        dataset.addSeries(series)

        def renderer1 = new XYDotRenderer()
        renderer1.setDotWidth(size)
        renderer1.setDotHeight(size)
        def plot = new XYPlot(dataset, xAxis, yAxis, renderer1)

        def coefficients
        def curve
        String regDesc
        if (regType == 1) {
            coefficients = JFreeRegression.getPowerRegression(dataset, 0)
            curve = new PowerFunction2D(coefficients[0], coefficients[1])
            regDesc = "Power Regression"
        } else {
            coefficients = JFreeRegression.getOLSRegression(dataset, 0)
            curve = new LineFunction2D(coefficients[0], coefficients[1])
            regDesc = "Linear Regression"
        }

        def regressionData = DatasetUtilities.sampleFunction2D(curve, xMin, xMax, samples, "Fitted Regression Line")
        plot.setDataset(1, regressionData)
        def renderer2 = new XYLineAndShapeRenderer(lines,shapes)
        plot.setRenderer(1, renderer2)

        def chart = new JFreeChart(regDesc, JFreeChart.DEFAULT_TITLE_FONT, plot, legend)
        new Chart(chart)
    }
}