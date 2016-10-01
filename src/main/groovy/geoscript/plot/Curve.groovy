package geoscript.plot

import org.jfree.chart.ChartFactory
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.renderer.xy.XYLine3DRenderer
import org.jfree.chart.renderer.xy.XYSplineRenderer
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection

/**
 * Create a curved line Chart.
 * <p><blockquote><pre>
 * Chart chart = Curve.curve([[1,10],[45,12],[23,3],[5,20]])
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class Curve {

    /**
     * Create a curved line Chart
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
     *  <li>smooth: Whether to smooth the line</li>
     *  <li>trid: Whether to make the Chart 3D</li>
     * </ul>
     * @param data A List of data where each datum is a List with two items.
     * @return A Chart
     */
    static Chart curve(Map options = [:], List data) {
        String name = options.get("name","")
        String title = options.get("title")
        String xLabel = options.get("xLabel")
        String yLabel = options.get("yLabel")
        String orientation = options.get("orientation","vertical")
        PlotOrientation plotOrientation = orientation.equalsIgnoreCase("vertical") ? PlotOrientation.VERTICAL : PlotOrientation.HORIZONTAL
        boolean legend = options.get("legend", true)
        boolean tooltips = options.get("tooltips", true)
        boolean urls = options.get("urls", false)
        boolean smooth = options.get("smooth", false)
        boolean trid = options.get("trid", false)

        def dataset = new XYSeriesCollection()
        def xy = new XYSeries(name)
        data.each{datum ->
            xy.add(datum[0],datum[1])
        }
        dataset.addSeries(xy)
        def chart = ChartFactory.createXYLineChart(title,xLabel,yLabel,dataset,plotOrientation,legend,tooltips,urls)
        if (smooth) {
            chart.getXYPlot().setRenderer(new XYSplineRenderer())
        } else if (trid) {
            chart.getXYPlot().setRenderer(new XYLine3DRenderer())
        }
        new Chart(chart)
    }

}
