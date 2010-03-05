import geoscript.proj.Projection
import geoscript.geom.Point

// Create a Projection from an EPSG code
def prj = new Projection("epsg:4326")
println(prj)
println("ID: ${prj.id}")

// Create a Projection from WKT
String wkt = 'GEOGCS["GCS_WGS_1984",DATUM["D_WGS_1984",SPHEROID["WGS_1984",6378137,298.257223563]],PRIMEM["Greenwich",0],UNIT["Degree",0.017453292519943295]]'
prj = new Projection(wkt)
println(prj)
println("ID: ${prj.id}")

// Access WKT
prj = new Projection('epsg:26912')
println(prj.wkt)

// Transform coordinates 
Projection src = new Projection('epsg:4326')
Point pt = src.transform(new Point(-111, 45.7), 'epsg:26912')
println("Transformed Point: ${pt}")

