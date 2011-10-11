import geoscript.geom.Bounds
import geoscript.layer.Shapefile
import geoscript.render.Map
import geoscript.style.*

def statesShp = new Shapefile("states.shp")
statesShp.style = (new Fill("#E6E6E6") + new Stroke("#4C4C4C", 0.5)).zindex(0) +
        new Icon('http://www.usautoparts.net/bmw/images/states/tn_${strToLowerCase(STATE_ABBR)}.jpg',"image/jpeg")

def map = new Map(width: 600, height: 400, fixAspectRatio: true)
map.proj = "EPSG:4326"
map.bounds = new Bounds(-110, 40, -95, 50, "EPSG:4326")
map.addLayer(statesShp)
map.render(new File("states_flags.png"))
