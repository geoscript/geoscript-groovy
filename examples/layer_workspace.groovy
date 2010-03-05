import geoscript.layer.Shapefile

def shp = new Shapefile('states.shp')
println("Count: ${shp.count()}")
println("Bound: ${shp.bounds()}")
