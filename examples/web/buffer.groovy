import geoscript.geom.*

def geom = Geometry.fromWKT(request.getParameter("geom"))
def distance = request.getParameter("d") as double
println(geom.buffer(distance).wkt)
