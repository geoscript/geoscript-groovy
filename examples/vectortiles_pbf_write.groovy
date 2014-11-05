import geoscript.feature.Feature
import geoscript.geom.Bounds
import geoscript.layer.Layer
import geoscript.layer.Pyramid
import geoscript.layer.Shapefile
import geoscript.layer.Tile
import geoscript.layer.io.Pbf
import geoscript.render.Draw
import geoscript.render.Window
import geoscript.style.Fill
import geoscript.style.Shape
import geoscript.style.Stroke

Layer layer = new Shapefile("states.shp")
Layer centroidLayer = layer.transform("states_centroids", [
        "geom": "centroid(the_geom)",
        "name": "STATE_NAME"
])

Pyramid pyramid = Pyramid.createGlobalMercatorPyramid()
Bounds bounds = pyramid.bounds(new Tile(5, 5, 20))

byte[] bytes = Pbf.write([layer, centroidLayer], bounds)
File file = new File("states_5_5_20.pbf")
file.withOutputStream { out ->
    out.write(bytes)
}

List layers = Pbf.read(bytes, bounds)
println "# Layers = ${layers.size()}"
layers.each { Layer lyr ->
    println lyr.name
    lyr.eachFeature { Feature f ->
        println f
    }
    println ""
}

layers[0].style = new Fill("white") + new Stroke("black",0.1)
layers[1].style = new Shape("red", 6, "circle")

geoscript.render.Map map = new geoscript.render.Map(
        layers: layers,
        bounds: bounds,
        width: 400,
        height: 400,
        proj: pyramid.proj,
        backgroundColor: "white"
)
Window window = new Window(map)

