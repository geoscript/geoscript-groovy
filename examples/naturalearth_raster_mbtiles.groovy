import geoscript.geom.Bounds
import geoscript.layer.Format
import geoscript.layer.GeoTIFF
import geoscript.layer.MBTiles
import geoscript.layer.Raster
import geoscript.layer.RasterTileRenderer
import geoscript.layer.TileGenerator
import geoscript.proj.Projection

import static geoscript.GeoScript.download
import static geoscript.GeoScript.unzip

// Download data from natural earth
File dataDir = new File("naturalearth")
String url = "https://www.naturalearthdata.com/http//www.naturalearthdata.com/download/50m/raster/NE2_50M_SR_W.zip"
unzip(download(new URL(url), new File(dataDir, "NE2_50M_SR_W.zip"), overwrite: false))

// Raster Raster, crop and project
Format format = new GeoTIFF(new File(dataDir, "NE2_50M_SR_W/NE2_50M_SR_W.tif"))
Raster raster = format.read()
Raster croppedRaster = raster.crop(new Bounds(-179.99, -85.0511, 179.99, 85.0511, new Projection("EPSG:4326")))
Raster projectedRaster = croppedRaster.reproject(new Projection("EPSG:3857"))

// Generate Tiles
TileGenerator generator = new TileGenerator(verbose: true)
MBTiles mbtiles = new MBTiles(new File("NE2_50M_SR_W.mbtiles"), "NE2_50M_SR_W", "Natural Earth II with Shaded Relief and Water")
RasterTileRenderer renderer = new RasterTileRenderer(projectedRaster)
generator.generate(mbtiles, renderer, 0, 6)