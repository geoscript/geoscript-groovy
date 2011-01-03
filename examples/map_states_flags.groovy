import geoscript.layer.*
import geoscript.proj.*
import geoscript.style.*
import geoscript.map.*
import geoscript.geom.Bounds

def statesShp = new Shapefile("states.shp")

statesShp.style = new Style([
    new PolygonSymbolizer(
        fillColor: "#E6E6E6",
        strokeColor: "#4C4C4C",
        strokeWidth: 0.5f,
        zIndex: 0
    ),
    new PointSymbolizer(
        graphic: 'http://www.usautoparts.net/bmw/images/states/tn_${strToLowerCase(STATE_ABBR)}.jpg',
        zIndex: 1
    )
])

def map = new Map(width: 600, height: 400, fixAspectRatio: true)
map.proj = "EPSG:4326"
map.bounds = new Bounds(-110, 40, -95, 50, "EPSG:4326")
map.addLayer(statesShp)
map.render(new File("states_flags.png"))
