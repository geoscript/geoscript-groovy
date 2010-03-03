import geoscript.geom.*

def poly = Geometry.fromWKT('POLYGON ((0 0, 10 0, 10 10, 0 10, 0 0))')
println(poly.centroid)
