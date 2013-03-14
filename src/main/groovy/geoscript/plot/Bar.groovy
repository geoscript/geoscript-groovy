package geoscript.plot

import org.jfree.chart.ChartFactory
import org.jfree.chart.JFreeChart
import org.jfree.chart.plot.PlotOrientation
import org.jfree.data.category.DefaultCategoryDataset
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection

/**
 * Create bar Charts.
 * @author Jared Erickson
 */
class Bar {

    /**
     * Create an XY Bar Chart.
     * @param options Named parameter options
     * <ul>
     *  <li>name: The name of the data series</li>
     *  <li>title: The title of the chart</li>
     *  <li>xLabel: The x axis label</li>
     *  <li>yLabel: The y axis label</li>
     *  <li>orientation: The plot orientation (vertical or horizontal)</li>
     *  <li>legend: Whether to show the legend</li>
     *  <li>tooltips: Whether to show the tooltips</li>
     *  <li>urls: Whether to show urls</li>
     * </ul>
     * @param data A List of data where each datum is a List with two items.
     * @return A Chart
     */
    static Chart xy(java.util.Map options = [:], List data) {
        String name = options.get("name","")
        String title = options.get("title")
        String xLabel = options.get("xLabel","")
        String yLabel = options.get("yLabel","")
        String orientation = options.get("orientation","vertical")
        boolean legend = options.get("legend", true)
        boolean tooltips = options.get("tooltips", true)
        boolean urls = options.get("urls", false)
        def series = new XYSeries(name)
        data.each{datum ->
            series.add(datum[0],datum[1])
        }
        def dataset = new XYSeriesCollection(series)
        def chart = ChartFactory.createXYBarChart(title, xLabel, false, yLabel, dataset,
            orientation.equalsIgnoreCase("vertical") ? PlotOrientation.VERTICAL : PlotOrientation.HORIZONTAL, legend, tooltips, urls)
        new Chart(chart)
    }

    /**
     * Create an Category Bar Chart.
     * @param options Named parameter options
     * <ul>
     *  <li>title: The title of the chart</li>
     *  <li>xLabel: The x axis label</li>
     *  <li>yLabel: The y axis label</li>
     *  <li>stacked: Whether the bars should be stacked</li>
     *  <li>trid: Whether the bars should be 3D</li>
     *  <li>orientation: The plot orientation (vertical or horizontal)</li>
     *  <li>legend: Whether to show the legend</li>
     *  <li>tooltips: Whether to show the tooltips</li>
     *  <li>urls: Whether to show urls</li>
     * </ul>
     * @param data A List of data where each datum is a List with two items.
     * @return A Chart
     */
    static Chart category(java.util.Map options = [:], Map data) {
        String title = options.get("title","")
        String xLabel = options.get("xLabel","")
        String yLabel = options.get("yLabel","")
        boolean stacked = options.get("stacked", false)
        boolean trid = options.get("trid", false)
        String orientation = options.get("orientation","vertical")
        boolean legend = options.get("legend", true)
        boolean tooltips = options.get("tooltips", true)
        boolean urls = options.get("urls", false)

        def dataset = new DefaultCategoryDataset()
        data.each{k,v ->
            if (v instanceof Map) {
                v.each{k2,v2 ->
                    dataset.addValue(v2, k2, k)
                }
            } else {
                dataset.addValue(v, "", k)
            }
        }

        PlotOrientation plotOrientation = orientation.equalsIgnoreCase("vertical") ? PlotOrientation.VERTICAL : PlotOrientation.HORIZONTAL

        JFreeChart chart
        if (trid) {
            if (stacked) {
                chart = ChartFactory.createStackedBarChart3D(title, xLabel, yLabel, dataset, plotOrientation, legend, tooltips, urls)
            } else {
                chart = ChartFactory.createBarChart3D(title,xLabel, yLabel, dataset, plotOrientation, legend, tooltips, urls)
            }
        } else {
            if (stacked) {
                chart = ChartFactory.createStackedBarChart(title,xLabel, yLabel, dataset, plotOrientation, legend, tooltips, urls)
            } else {
                chart = ChartFactory.createBarChart(title,xLabel, yLabel, dataset, plotOrientation, legend, tooltips, urls)
            }
        }
        new Chart(chart)
    }
}
