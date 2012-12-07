import geoscript.filter.Function
import geoscript.geom.Bounds
import geoscript.layer.Shapefile
import geoscript.style.*
import geoscript.render.Map

def statesShp = new Shapefile("states.shp")
statesShp.style = new Fill("#E6E6E6") + new Stroke("#4C4C4C",0.5) +
        new Label(new Function("numberFormat('#.##', UNEMPLOY / (EMPLOYED / UNEMPLOY))"))
        .font(new Font("normal", "bold", 10, "serif"))
        .fill(new Fill("#004080")).point([0.5,0.5])

def map = new Map(width: 600, height: 400, fixAspectRatio: true)
map.proj = "EPSG:4326"
map.addLayer(statesShp)
map.bounds = new Bounds(-110, 40, -95, 50, "EPSG:4326")
map.render(new File("states_label_numberformat.png"))
