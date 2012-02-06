import geoscript.geom.*
import geoscript.render.Map
import geoscript.style.*
import geoscript.layer.Layer

// Create our Bounds from a Point and width and height
p = new Point(10,10)
b = new Bounds(p, 5, 5)

// Rectangle
r = b.createRectangle(20, 0.5)

// Ellipse
e = b.createEllipse(100)

// Squricle
sq = b.createSquircle(100)

// Super circle
s = b.createSuperCircle(3,100)

// Sine star
st = b.createSineStar(5, 0.5)

// Arc LineString
arc = b.createArc(Math.toRadians(45),Math.toRadians(90))

// Arc Polygon
arcp = b.createArcPolygon(Math.toRadians(270),Math.toRadians(45))

// Create a new in memory Layer from a Geometry and Style
def createLayer(Geometry g, Style style=null) {
    Layer layer = new Layer()
    layer.add([g])
    if (style) layer.style = style
    layer
}

// Create a Map and add Layers
map = new Map()
map.addLayer(createLayer(p, new Shape("navy", 10, "square") + new Stroke("navy", 1)))
map.addLayer(createLayer(b.polygon, new Fill("gray",0.2) + new Stroke("gray",1)))
map.addLayer(createLayer(r, new Fill("teal",0.2) + new Stroke("teal",1)))
map.addLayer(createLayer(e, new Fill("orange",0.2) + new Stroke("orange",1)))
map.addLayer(createLayer(sq, new Fill("blue",0.2) + new Stroke("blue",1)))
map.addLayer(createLayer(s, new Fill("pink",0.2) + new Stroke("pink",1)))
map.addLayer(createLayer(st, new Fill("green",0.2) + new Stroke("green",1)))
map.addLayer(createLayer(arc, new Stroke("red",3)))
map.addLayer(createLayer(arcp, new Fill("yellow",0.2) + new Stroke("yellow",1)))

// Display in a GUI
map.display()
