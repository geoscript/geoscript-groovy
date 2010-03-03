import geoscript.geom.Geometry

def point = Geometry.fromWKT('POINT(6 10)')
println(point)

def line = Geometry.fromWKT('LINESTRING(3 4,10 50,20 25)')
println(line)

def poly = Geometry.fromWKT('POLYGON((1 1,5 1,5 5,1 5,1 1),(2 2, 3 2, 3 3, 2 3,2 2))')
println(poly)

def mpoint = Geometry.fromWKT('MULTIPOINT(3.5 5.6, 4.8 10.5)')
println(mpoint)

def mline = Geometry.fromWKT('MULTILINESTRING((3 4,10 50,20 25),(-5 -8,-10 -8,-15 -4))')
println(mline)

def mpoly = Geometry.fromWKT('MULTIPOLYGON(((1 1,5 1,5 5,1 5,1 1),(2 2, 3 2, 3 3, 2 3,2 2)),((3 3,6 2,6 4,3 3)))')
println(mpoly)

def collection = Geometry.fromWKT('GEOMETRYCOLLECTION(POINT(4 6),LINESTRING(4 6,7 10))')
println(collection)

