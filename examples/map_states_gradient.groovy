import geoscript.layer.*
import geoscript.style.*
import geoscript.map.*

def statesShp = new Shapefile("states.shp")
statesShp.style = new Gradient(
    "PERSONS / LAND_KM",
    [0,200],
    [new Fill("#000066") + new Stroke("black",0.1), new Fill("red") + new Stroke("black",0.1)],
    10,
    "exponential"
) + ((new Fill("red")  + new Stroke("black",0.1)).where("PERSONS / LAND_KM > 200"))

def map = new Map(width: 600, height: 400, fixAspectRatio: true)
map.addLayer(statesShp)
map.render(new File("states_gradient.png"))
