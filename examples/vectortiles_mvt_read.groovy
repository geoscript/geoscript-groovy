import geoscript.feature.Feature
import geoscript.layer.Layer
import geoscript.layer.io.MvtReader
import geoscript.render.Draw

MvtReader reader = new MvtReader()
Layer layer = reader.read(new URL("http://tile.openstreetmap.us/vectiles-highroad/12/656/1582.mvt").openStream())
println layer.count
println layer.schema
layer.eachFeature { Feature f ->
    println f
}
Draw.draw(layer)
