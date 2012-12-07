import static geoscript.render.Draw.*
import geoscript.layer.Shapefile

def layer = new Shapefile("states.shp")
draw(layer)