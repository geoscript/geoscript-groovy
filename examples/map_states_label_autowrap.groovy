import geoscript.layer.*
import geoscript.style.*
import geoscript.map.Map

def statesShp = new Shapefile("states.shp")
statesShp.style = new Fill("#E6E6E6") + new Stroke("#4C4C4C",0.5) +
        new Label("STATE_NAME").font(new Font("normal", "bold", 10, "Arial")).fill(new Fill("#004080")).point([0.5,0.5]).autoWrap(10)

def map = new Map(width: 600, height: 400, fixAspectRatio: true)
map.proj = "EPSG:4326"
map.addLayer(statesShp)
map.bounds = statesShp.bounds
map.render(new File("states_label_autowrap.png"))
