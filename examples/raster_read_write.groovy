import geoscript.layer.*

// Read
def geoTiffFormat = new GeoTIFF()
def raster = geoTiffFormat.read(new File("raster.tif"))

// Write
def pngFormat = new WorldImage()
pngFormat.write(raster, new File("raster.png"))

// Read
def pngRaster = pngFormat.read(new File("raster.png"))
println pngRaster.format
