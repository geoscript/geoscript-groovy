import geoscript.layer.Shapefile
import geoscript.render.Map
import geoscript.style.io.CSSReader

def statesShp = new Shapefile("states.shp")
statesShp.style = new CSSReader().read(new File("states.css"))

def map = new Map(width: 600, height: 400, fixAspectRatio: true)
map.proj = "EPSG:4326"
map.addLayer(statesShp)
map.bounds = statesShp.bounds
map.render(new File("states_css.png"))

statesShp.style = new CSSReader().read("""
states {
    fill: "#FFEBC3";
    stroke: "#342D36";
}
""")
map.render(new File("states_css2.png"))

