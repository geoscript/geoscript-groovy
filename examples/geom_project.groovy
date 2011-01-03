import geoscript.geom.*
import geoscript.proj.Projection

def p = new Point(-111.0, 45.7)
println(p)

def p2 = Projection.transform(p, 'epsg:4326', 'epsg:26912')
println(p2)

def poly = p2.buffer(100)
println(poly)
println(poly.area)

