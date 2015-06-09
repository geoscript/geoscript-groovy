import geoscript.feature.Feature
import geoscript.geom.Bounds
import geoscript.layer.TMS
import geoscript.layer.ImageTileRenderer
import geoscript.layer.Pyramid
import geoscript.layer.Raster
import geoscript.layer.Shapefile
import geoscript.layer.TileGenerator
import geoscript.layer.TileRenderer
import geoscript.layer.io.PyramidWriter
import geoscript.layer.io.GdalTmsPyramidWriter
import geoscript.style.*

import javax.imageio.ImageIO

// The directory for the TMS images
File dir = new File("tms")
dir.mkdir()

// The Layer used to render the tiles
Shapefile shp = new Shapefile(new File("states.shp"))
shp.style = new Fill("wheat") + new Stroke("navy", 0.1)

// The TMS TileLayer
TMS tms = new TMS("states", "png", dir, Pyramid.createGlobalMercatorPyramid())

// Create GDAL TMS Mini Driver XML Files for QGIS
PyramidWriter writer = new GdalTmsPyramidWriter()
// The first uses the directory
String xml = writer.write(tms)
new File(dir, "states_dir.xml").text = xml

// The second uses the directory served by python's webserver
// cd tms; python -m SimpleHTTPServer
writer.write(tms.pyramid, serverUrl: 'http://localhost:8000/${z}/${x}/${y}.png')
new File(dir, "states_url.xml").text = xml

// Generate the tiles
TileRenderer renderer = new ImageTileRenderer(tms, shp)
TileGenerator generator = new TileGenerator(verbose: true)
generator.generate(tms, renderer, 0, 4)

// Stitch together images for entire zoom levels
(0..3).each{int zoom ->
    Raster raster = tms.getRaster(tms.tiles(zoom))
    ImageIO.write(raster.image, "png", new File(dir, "${zoom}.png"))
}

// Stitch together images for a few states
["North Dakota", "Oregon", "Washington"].each { String name ->
    shp.getFeatures("STATE_NAME = '${name}'").each { Feature f ->
        Bounds b = f.geom.bounds.expandBy(0.5)
        b.proj = "EPSG:4326"
        Raster raster = tms.getRaster(tms.tiles(b.reproject("EPSG:3857"), 4))
        ImageIO.write(raster.image, "png", new File(dir, "${name}.png"))
    }
}

tms.close()
