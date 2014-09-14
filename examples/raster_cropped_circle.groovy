import geoscript.layer.*
import geoscript.geom.Point
import geoscript.proj.Projection

// Read
def geoTiffFormat = new GeoTIFF(new File("alki.tif"))
def raster = geoTiffFormat.read(new Projection("urn:ogc:def:crs:EPSG::EPSG:2927"))

// Crop
def geom = new Point(150,150).buffer(20)
def croppedRaster = raster.crop(geom)

// Write
GeoTIFF croppedGeoTIFF = new GeoTIFF(new File("raster_circle_cropped.tif"))
croppedGeoTIFF.write(croppedRaster)
