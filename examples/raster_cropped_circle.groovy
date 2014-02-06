import geoscript.layer.*
import geoscript.geom.Point
import geoscript.proj.Projection

// Read
def geoTiffFormat = new GeoTIFF(new File("raster.tif"))
def raster = geoTiffFormat.read(new Projection("urn:ogc:def:crs:EPSG::EPSG:4326"))

// Crop
def geom = new Point(50,50).buffer(20)
def croppedRaster = raster.crop(geom)

// Write
GeoTIFF croppedGeoTIFF = new GeoTIFF(new File("raster_circle_cropped.tif"))
geoTiffFormat.write(croppedRaster)
