import geoscript.layer.*
import geoscript.proj.*
import geoscript.style.*
import geoscript.map.*
import geoscript.filter.Function

def statesShp = new Shapefile("states.shp")

statesShp.style = new Style([
    new PolygonSymbolizer(
        fillColor: "#E6E6E6",
        strokeColor: "#4C4C4C",
        strokeWidth: 0.5f
    ),
    new PointSymbolizer(
        geometry: new Function("centroid(the_geom)"),
        fillColor: "#66CCff",
        strokeColor: "#004080",
        size: 6f,
        shape: "circle"
    ),
    new TextSymbolizer(
        label: "STATE_ABBR",
        color: "#004080",
        fontWeight: "bold"
    )
])

def map = new Map(width: 600, height: 400, fixAspectRatio: true)
map.proj = "EPSG:4326"
map.addLayer(statesShp)
map.bounds = statesShp.bounds
map.render(new File("states.png"))
