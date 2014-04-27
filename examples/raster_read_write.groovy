import geoscript.layer.*

// Read
def geoTiffFormat = new GeoTIFF(new File("raster.tif"))
def raster = geoTiffFormat.read()

// Write
def pngFormat = new WorldImage(new File("raster.png"))
pngFormat.write(raster)

// Read
def pngRaster = pngFormat.read()
println pngRaster.format
