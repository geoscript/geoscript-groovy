import geoscript.layer.Shapefile
import geoscript.style.io.SLDReader
import geoscript.map.Map

def statesShp = new Shapefile("states.shp")
statesShp.style = new SLDReader().read(new File("states.sld"))

def map = new Map(width: 600, height: 400, fixAspectRatio: true)
map.proj = "EPSG:4326"
map.addLayer(statesShp)
map.bounds = statesShp.bounds
map.render(new File("states_sld.png"))
