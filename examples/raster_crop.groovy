import geoscript.layer.*
import geoscript.geom.Bounds
import geoscript.proj.Projection

// Read
def geoTiffFormat = new GeoTIFF()
def raster = geoTiffFormat.read(new File("raster.tif"), new Projection("urn:ogc:def:crs:EPSG::EPSG:4326"))

// Crop
def croppedRaster = raster.crop(new Bounds(-10, -10, 10, 10, raster.proj))
println croppedRaster.bounds
println croppedRaster.proj.id
println croppedRaster.size

// Write
geoTiffFormat.write(croppedRaster, new File("raster_cropped.tif"))