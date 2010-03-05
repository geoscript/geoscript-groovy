import geoscript.geom.*

// Create a Point from coordinates
def point = new Point(10,10)
println(point)

// Create a LineString from a set of coordinates
def line = new LineString([10,10], [20,20], [30,40])
println(line)

// Create a Polygon from Well Known Text (WKT)
def poly = Geometry.fromWKT('POLYGON ((10 10, 10 20, 20 20, 20 15, 10 10))')
println(poly)

// Calculate statistics like area and length
double area = poly.area
println("Area: ${area}")

double length  = poly.length
println("Length: ${length}")

// Buffer a line
def buffer = line.buffer(10)
println("Buffer: ${buffer}")
println("Area: ${buffer.area}")

// Calculate a centroid
def centroid = line.centroid
println("Centroid: ${centroid}")

// Use spatial operators and predicates
println("Does ${poly} intersect ${line}? ${poly.intersects(line)}")
println("Intersection: ${poly.intersection(line)}")


