package geoscript.plot

import org.jfree.chart.ChartFactory
import org.jfree.data.general.DefaultPieDataset

/**
 * Create a pie Chart.
 * <p><blockquote><pre>
 * Chart chart = Pie.pie(["A":20,"B":45,"C":2,"D":14])
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class Pie {

    /**
     * Create a pie Chart
     * @param options Named parameter options
     * <ul>
     *      <li>title: The chart title</li>
     *      <li>legend: Whether to show the lenged</li>
     *      <li>tooltips: Whether to show the tooltips</li>
     *      <li>urls: Whether to show urls</li>
     *      <li>trid: Whether the Chart should be 3D</li>
     * </ul>
     * @param data A Map of data
     * @return A Chart
     */
    static Chart pie(Map options = [:], Map data) {
        String title = options.get("title","")
        boolean legend = options.get("legend", true)
        boolean tooltips = options.get("tooltips", true)
        boolean urls = options.get("urls", false)
        boolean trid = options.get("trid", true)

        def dataset = new DefaultPieDataset()
        data.each{k,v ->
            dataset.setValue(k,v)
        }

        def chart
        if (trid) {
            chart = ChartFactory.createPieChart3D(title,dataset,legend,tooltips,urls)
        } else {
            chart = ChartFactory.createPieChart(title,dataset,legend, tooltips, urls)
        }
        new Chart(chart)
    }

}
