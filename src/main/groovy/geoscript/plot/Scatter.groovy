package geoscript.plot

import org.jfree.chart.ChartFactory
import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.plot.PlotOrientation
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import org.jfree.util.ShapeUtilities

/**
 * Create a scatter plot Chart
 * @author Jared Erickson
 */
class Scatter {

    /**
     * Create a scatter plot Chart
     * @param options Named parameter options
     * <ul>
     *      <li>title: The chart title</li>
     *      <li>xLabel: The x axis label</li>
     *      <li>yLabel: The y axis label</li>
     *      <li>size: The size of the markers</li>
     *      <li>legend: Whether to show the legend</li>
     *      <li>tooltips: Whether to show the tooltips</li>
     *      <li>urls: Whether to show the urls</li>
     *      <li>orientation: The plot orientation (vertical or horizontal)</li>
     * </ul>
     * @param data A List of data where each item is a List with two items.
     * @return A Chart
     */
    static Chart scatterplot(Map options = [:], List data) {
        String title = options.get("title","")
        String xLabel = options.get("xLabel","")
        String yLabel = options.get("yLabel","")
        int size = options.get("size",3)
        boolean legend = options.get("legend", true)
        boolean tooltips = options.get("tooltips", true)
        boolean urls = options.get("urls", false)
        String orientation = options.get("orientation","vertical")
        PlotOrientation plotOrientation = orientation.equalsIgnoreCase("vertical") ? PlotOrientation.VERTICAL : PlotOrientation.HORIZONTAL

        def xAxis = new NumberAxis(xLabel)
        xAxis.autoRangeIncludesZero = false
        def yAxis = new NumberAxis(yLabel)
        yAxis.autoRangeIncludesZero = false

        def series = new XYSeries("Values")
        data.each {datum ->
            series.add(datum[0], datum[1])
        }

        def dataset = new XYSeriesCollection()
        dataset.addSeries(series)
        def chart = ChartFactory.createScatterPlot(title, xLabel,yLabel,dataset,plotOrientation,legend,tooltips,urls)
        def plot = chart.plot
        plot.getRenderer().setSeriesShape(0, ShapeUtilities.createRegularCross(size,size))
        new Chart(chart)
    }

}
