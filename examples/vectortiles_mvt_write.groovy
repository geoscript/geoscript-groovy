import geoscript.feature.Feature
import geoscript.layer.*
import geoscript.layer.io.MvtReader
import geoscript.layer.io.MvtWriter
import geoscript.render.Draw

Layer layer = new Shapefile("states.shp")
MvtWriter writer = new MvtWriter()
File file = new File("states.mvt")
writer.write(layer, file)

MvtReader reader = new MvtReader()
layer = reader.read(file)
println layer.count
println layer.schema
layer.eachFeature { Feature f ->
    println f
}
Draw.draw(layer)

