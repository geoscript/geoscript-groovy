import geoscript.layer.*
import geoscript.workspace.*
import geoscript.layer.io.GeoJSONReader
import geoscript.process.Process
import geoscript.render.*
import geoscript.style.*

// Get earthquakes from USGS GeoJSON feed
tempLayer = new GeoJSONReader().read(new URL("http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_week.geojson").text)
Memory workspace = new Memory()
layer = workspace.create(tempLayer.schema.reproject("EPSG:4326", "earthquakes"))
tempLayer.eachFeature{f->
    layer.add(f)
}

// Get the heatmap process
process = new Process("vec:Heatmap")
println "Process ${process.name} parameters: ${process.parameters}"

// Create a heatmap of the earthquakes
results = process.execute([
        data: layer,
        radiusPixels: 10,
        pixelsPerCell: 10,
        outputWidth: 600,
        outputHeight: 400,
        outputBBOX: layer.bounds
])

Raster raster = results.result
println "Raster extrema: ${raster.extrema}"
raster.style = new ColorMap([
        [color: "#f7da22", quantity:0],
        [color: "#ecbe1d", quantity:0.1],
        [color: "#e77124", quantity:0.2],
        [color: "#d54927", quantity:0.3],
        [color: "#cf3a27", quantity:0.7],
        [color: "#a33936", quantity:0.8],
        [color: "#7f182a", quantity:0.9],
        [color: "#68101a", quantity:1]
])

Map map = new Map([layers:[raster,layer]])
map.renderToImage()