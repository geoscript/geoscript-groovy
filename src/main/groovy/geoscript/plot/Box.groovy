package geoscript.plot

import org.jfree.chart.ChartFactory
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset

/**
 * Create box and whiskers Charts.
 * <p><blockquote><pre>
 * Chart chart = Box.box(["A":[1,10,20],"B":[45,39,10],"C":[2,4,9],"D":[14,15,19]])
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class Box {

    /**
     * Create a box and whisker Chart.
     * @param options Named parameter options
     * <ul>
     *     <li>title: The Chart title</li>
     *     <li>xLabel: The x axis label</li>
     *     <li>yLabel: The y axis label</li>
     *     <li>legend: Whether to show the legend</li>
     * </ul>
     * @param data A Map of data
     * @return A Chart
     */
    static Chart box(Map options=[:], Map data) {
        String title = options.get("title","")
        String xLabel = options.get("xLabel","")
        String yLabel = options.get("yLabel","")
        boolean legend = options.get("legend", true)
        def dataset = new DefaultBoxAndWhiskerCategoryDataset()
        data.each{k,v ->
            dataset.add(v, "", k)
        }
        def chart = ChartFactory.createBoxAndWhiskerChart(title,xLabel,yLabel,dataset,legend)
        new Chart(chart)
    }

}
