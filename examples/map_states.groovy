import geoscript.layer.*
import geoscript.proj.*
import geoscript.style.*
import geoscript.map.*

statesShp = new Shapefile("states.shp")
centroidShp = new Shapefile("states_centroids.shp")

statesSym = new PolygonSymbolizer(
    fillColor: "#E6E6E6",
    strokeColor: "#4C4C4C",
    strokeWidth: 0.5f
)
statesStyle = new Style(new SubStyle(new Rule(statesSym)))
//statesStyle = new Style(new File('states.sld'))

centroidSym = new PointSymbolizer(
    fillColor: "#66CCff",
    strokeColor: "#004080",
    size: 6f,
    shape: "circle"
)
centroidLabelSym = new TextSymbolizer(
    field: centroidShp.schema.get("STATE_ABBR"),
    color: "#004080",
    fontWeight: "bold"
)
centroidStyle = new Style([centroidSym, centroidLabelSym])

map = new Map(width: 600, height: 400, fixAspectRatio: true)
map.setProjection(new Projection("EPSG:4326"))
map.addLayer(statesShp, statesStyle)
map.addLayer(centroidShp, centroidStyle)
map.render(statesShp.bounds(), new File("states.png"))
//map.renderToImage(statesShp.bounds())
