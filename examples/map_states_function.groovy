import geoscript.layer.Shapefile
import geoscript.render.Map
import geoscript.style.*
import geoscript.filter.*

def myCentroid = new Function("myCentroid(the_geom)", {g ->
    g.centroid
})

def statesShp = new Shapefile("states.shp")
statesShp.style = (new Fill("#E6E6E6") + new Stroke("#4C4C4C",0.5)) +
        (new Shape("#66CCff", 6, "circle").stroke("#004080") + new Transform(
                myCentroid
        )).zindex(1) +
        (new Label(new Function("strToLowerCase(STATE_ABBR)")).font(new Font("normal", "bold", 10, "serif")).fill(new Fill("#004080")))

def map = new Map(width: 600, height: 400, fixAspectRatio: true)
map.proj = "EPSG:4326"
map.addLayer(statesShp)
map.bounds = statesShp.bounds
map.render(new File("states_function.png"))
