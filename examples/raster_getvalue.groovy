import geoscript.geom.Geometry
import geoscript.layer.*
import geoscript.geom.Bounds
import geoscript.proj.Projection

// Read
def geoTiffFormat = new GeoTIFF(new File("raster.tif"))
def raster = geoTiffFormat.read(new Projection("urn:ogc:def:crs:EPSG::EPSG:4326"))

def multiPoint = Geometry.createRandomPoints(new Bounds(-180,-90,180,80).geometry, 10)
multiPoint.points.eachWithIndex{pt,i ->
    def value = raster.getValue(pt)
    println "${i}). ${value} at ${pt}"
}
