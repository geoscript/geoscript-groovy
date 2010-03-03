import geoscript.geom.Point

def point = new Point(0,0)
def poly = point.buffer(10)
println(poly)
