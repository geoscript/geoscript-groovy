import geoscript.layer.*
import geoscript.proj.*
import geoscript.style.*
import geoscript.map.*

def statesShp = new Shapefile("states.shp")
statesShp.style = Style.createGraduatedStyle(statesShp, "WORKERS", "Quantile", 5, "Greens")

def map = new Map(width: 600, height: 400, fixAspectRatio: true)
map.proj = "EPSG:4326"
map.addLayer(statesShp)
map.bounds = statesShp.bounds
map.render(new File("states_workers.png"))
