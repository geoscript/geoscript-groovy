import geoscript.geom.*

def poly1 = Geometry.fromWKT('POLYGON ((0 0, 5 0, 5 5, 0 5, 0 0))')
def prep = Geometry.prepare(poly1)
def poly2 = Geometry.fromWKT('POLYGON ((2 2, 8 2, 8 8, 2 8, 2 2))')
println("Does ${prep} intersect with ${poly2}? ${prep.intersects(poly2)}")
println("Does ${prep} contain with ${poly2}? ${prep.contains(poly2)}")