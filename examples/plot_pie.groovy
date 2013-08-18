import geoscript.layer.*
import geoscript.plot.Pie

Layer layer = new Shapefile("states.shp")
Map data = [:] as TreeMap
layer.cursor.each {f ->
    String region = f['SUB_REGION']
    if (!data.containsKey(region)) {
        data[region] = 0
    }
    data[region] = data[region] + 1
}

Pie.pie(data, title: "States per Region").show()