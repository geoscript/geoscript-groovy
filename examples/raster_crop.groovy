import geoscript.layer.*
import geoscript.geom.Bounds
import geoscript.proj.Projection

// Read
def geoTiffFormat = new GeoTIFF(new File("raster.tif"))
def raster = geoTiffFormat.read(new Projection("urn:ogc:def:crs:EPSG::EPSG:4326"))

// Crop
def croppedRaster = raster.crop(new Bounds(-10, -10, 10, 10, raster.proj))
println croppedRaster.bounds
println croppedRaster.proj.id
println croppedRaster.size

// Write
GeoTIFF outTiff = new GeoTIFF(new File("raster_cropped.tif"))
outTiff.write(croppedRaster)