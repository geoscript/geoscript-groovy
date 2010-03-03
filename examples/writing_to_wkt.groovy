import geoscript.geom.*

def point = new Point(6,10)
def wkt = point.toString()
println(wkt)

def line = new LineString([[3,4],[10,50],[20,25]])
println(line)
