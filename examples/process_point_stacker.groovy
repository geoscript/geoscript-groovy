import geoscript.geom.*
import geoscript.layer.*
import geoscript.feature.*
import geoscript.workspace.*
import geoscript.layer.io.GeoJSONReader
import geoscript.process.Process
import geoscript.render.*
import geoscript.style.*

tempLayer = new GeoJSONReader().read(new URL("http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_week.geojson").text)
Memory workspace = new Memory()
layer = workspace.create(tempLayer.schema.reproject("EPSG:4326", "earthquakes"))
tempLayer.eachFeature{f->
    layer.add(f)
}

println "Layer ${layer.name} has ${layer.count} features!"
println "   Schema: ${layer.schema}"
println "   Bounds: ${layer.bounds}"
println "   CRS: ${layer.proj?.id}"

process = new Process("vec:PointStacker")
println "Process ${process.name} parameters: ${process.parameters}"

results = process.execute([
        data: layer,
        cellSize: 10,
        outputWidth: 600,
        outputHeight: 400,
        outputBBOX: layer.bounds
])

Cursor cursor = results.result
outLayer = workspace.create(new Schema("stacked",[["geom", "Point"],["count", "int"],["countunique", "int"]]))
cursor.each{f ->
    outLayer.add(f)
}

layer.style = new Shape(type: "triangle", size: 3, color: "red")
outLayer.style = (
(new Shape(type: "circle", size: 8, color: "wheat") + new Label("count").where("count >= 1 and count <=2")) +
        (new Shape(type: "circle", size: 12, color: "wheat") + new Label("count").where("count > 2 and count <=4")) +
        (new Shape(type: "circle", size: 18, color: "wheat") + new Label("count").where("count > 4"))
)

Map map = new Map(layers: [layer, outLayer])
map.renderToImage()