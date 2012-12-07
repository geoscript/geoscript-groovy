import static geoscript.render.Plot.*
import geoscript.layer.Shapefile

def layer = new Shapefile("states.shp")
plot(layer)