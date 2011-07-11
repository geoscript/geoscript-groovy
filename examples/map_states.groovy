import geoscript.layer.*
import geoscript.proj.*
import geoscript.style.*
import geoscript.map.*
import geoscript.filter.Function

def statesShp = new Shapefile("states.shp")
statesShp.style = (new Fill("#E6E6E6") + new Stroke("#4C4C4C",0.5)) +
        (new Shape("#66CCff", 6, "circle").stroke("#004080") + new Transform("centroid(the_geom)")).zindex(1) +
        (new Label("STATE_ABBR").font(new Font("normal", "bold", 10, "serif")).fill(new Fill("#004080")))

def map = new Map(width: 600, height: 400, fixAspectRatio: true)
map.proj = "EPSG:4326"
map.addLayer(statesShp)
map.bounds = statesShp.bounds
map.render(new File("states.png"))
