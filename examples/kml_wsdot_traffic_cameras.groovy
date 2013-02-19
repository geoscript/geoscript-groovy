import geoscript.layer.io.KmlReader
import geoscript.layer.Shapefile
import geoscript.render.Map
import geoscript.style.*
import geoscript.geom.Bounds

url = new URL("http://wsdot.wa.gov/traffic/api/HighwayCameras/kml.aspx")
text = url.text.trim()
text = text.substring(text.indexOf("<kml"))

reader = new KmlReader()
layer = reader.read(text)
println layer.count

def statesShp = new Shapefile("states.shp")
statesShp.style = (new Fill("#E6E6E6") + new Stroke("#4C4C4C",0.5))

layer.style = new Shape("navy", 4, "circle", 0.50).stroke("navy", 0.1)

def map = new Map(width: 600, height: 400, fixAspectRatio: true)
map.proj = "EPSG:4326"
map.addLayer(statesShp)
map.addLayer(layer)
map.bounds = new Bounds(-124.73142200000001,45.543251,-116.91815199999999,49.00000399999999,"EPSG:4326").expandBy(0.5)
map.render(new File("wsdot_traffic_cameras.png"))
