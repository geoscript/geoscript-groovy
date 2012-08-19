import geoscript.layer.Shapefile
import geoscript.render.*
import geoscript.style.*

def shp = new Shapefile("states.shp")
shp.style = new Fill("#999999") + new Stroke("black")

def map = new Map()
map.addLayer(shp)

new MapWindow(map)
