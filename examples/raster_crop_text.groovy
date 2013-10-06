import geoscript.layer.*
import geoscript.geom.Geometry
import geoscript.proj.Projection

// Read
def geoTiffFormat = new GeoTIFF()
def raster = geoTiffFormat.read(new File("raster.tif"), new Projection("urn:ogc:def:crs:EPSG::EPSG:4326"))

// Crop
def geom = Geometry.createFromText("CUGOS", "Arial", 60).translate(-50,-50)
def croppedRaster = raster.crop(geom)

// Write
geoTiffFormat.write(croppedRaster, new File("raster_crop_text.tif"))
