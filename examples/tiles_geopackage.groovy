import geoscript.feature.Feature
import geoscript.geom.Bounds
import geoscript.layer.GeoPackage
import geoscript.layer.ImageTileRenderer
import geoscript.layer.Pyramid
import geoscript.layer.Raster
import geoscript.layer.Shapefile
import geoscript.layer.TileGenerator
import geoscript.layer.TileRenderer
import geoscript.style.*

import javax.imageio.ImageIO

File dir = new File("geopackage")
dir.mkdir()

Shapefile shp = new Shapefile(new File("states.shp"))
shp.style = new Fill("wheat") + new Stroke("navy", 0.1)

File file = new File("states.gpkg")
GeoPackage gpkg = new GeoPackage(file, "states", Pyramid.createGlobalMercatorPyramid())

TileRenderer renderer = new ImageTileRenderer(gpkg, shp)
TileGenerator generator = new TileGenerator(verbose: true)
generator.generate(gpkg, renderer, 0, 4)

(0..3).each{int zoom ->
    Raster raster = gpkg.getRaster(gpkg.tiles(zoom))
    ImageIO.write(raster.image, "png", new File(dir, "${zoom}.png"))
}

["North Dakota", "Oregon", "Washington"].each { String name ->
    shp.getFeatures("STATE_NAME = '${name}'").each { Feature f ->
        Bounds b = f.geom.bounds.expandBy(0.5)
        b.proj = "EPSG:4326"
        Raster raster = gpkg.getRaster(gpkg.tiles(b.reproject("EPSG:3857"), 4))
        ImageIO.write(raster.image, "png", new File(dir, "${name}.png"))
    }
}

gpkg.close()
