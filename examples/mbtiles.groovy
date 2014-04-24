import geoscript.geom.Bounds
import geoscript.layer.Raster
import geoscript.layer.Shapefile
import geoscript.layer.MBTiles
import geoscript.style.Fill
import geoscript.style.Stroke

import javax.imageio.ImageIO

def shp = new Shapefile("states.shp")
shp.style = new Fill("wheat") + new Stroke("navy", 0.1)

File file = new File("states.mbtiles")
file.delete()
MBTiles mbtiles = new MBTiles(file)
mbtiles.create("states","A map of the united states")
mbtiles.generate(shp, 0, 4, verbose: true)

Bounds bounds = new Bounds(-179.999999, -85.0511, 179.999999, 85.0511, "EPSG:4326")
Raster raster = mbtiles.read(bounds, [500, 500])
ImageIO.write(raster.image, "PNG", new File("states1.png"))
raster = mbtiles.read()
ImageIO.write(raster.image, "PNG", new File("states2.png"))
raster = mbtiles.read(shp.bounds, [800,800])
ImageIO.write(raster.image, "PNG", new File("states3.png"))