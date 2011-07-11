import geoscript.map.Map
import geoscript.style.*
import geoscript.layer.Shapefile
import geoscript.geom.Bounds

def file = new File("states.shp")
def shp = new Shapefile(file)
shp.style = new Fill("steelblue") + new Stroke("wheat", 0.1)

def map = new Map(
    width: 256, 
    height: 256, 
    layers: [shp],
    proj: shp.proj,
    fixAspectRatio: false
)

def bbox = request.getParameter("BBOX").split(",")
def bounds = new Bounds(bbox[0] as double, bbox[1] as double, bbox[2] as double, bbox[3] as double)

map.bounds = bounds
response.contentType = "image/png"
map.render(response.outputStream)
map.close()
