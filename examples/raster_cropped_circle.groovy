import geoscript.geom.Geometry
import geoscript.layer.*
import geoscript.geom.Point
import geoscript.proj.Projection

// Read
def geoTiffFormat = new GeoTIFF(new File("alki.tif"))
def raster = geoTiffFormat.read(new Projection("EPSG:2927"))

// Crop
Geometry geom = new Point(1166761.4391797914, 823593.195575958).buffer(400)
def croppedRaster = raster.crop(geom)

// Write
GeoTIFF croppedGeoTIFF = new GeoTIFF(new File("alki_circle_cropped.tif"))
croppedGeoTIFF.write(croppedRaster)
