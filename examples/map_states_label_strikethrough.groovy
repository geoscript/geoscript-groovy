import geoscript.layer.Shapefile
import geoscript.render.Map
import geoscript.style.*
import geoscript.geom.Bounds

def statesShp = new Shapefile("states.shp")
statesShp.style = (new Fill("#E6E6E6") + new Stroke("#4C4C4C",0.5)) +
        (new Shape("#66CCff", 6, "circle").stroke("#004080") + new Transform("centroid(the_geom)")).zindex(1) +
        (new Label("STATE_NAME").strikethrough(true).font(new Font("normal", "bold", 10, "serif")).fill(new Fill("#004080")))

def map = new Map(
    width: 600,
    height: 400,
    bounds: new Bounds(-114.675293,37.317752,-83.078613,49.210420,"EPSG:4326"),
    fixAspectRatio: true
)
map.proj = "EPSG:4326"
map.addLayer(statesShp)
map.render(new File("states_label_strikethrough.png"))
