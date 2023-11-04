import geoscript.layer.OSM
import geoscript.layer.Raster
import javax.imageio.ImageIO

File dir = new File("osm")
dir.mkdir()

// OSM
OSM osm = new OSM()
(0..2).each{ int z ->
    Raster raster = osm.getRaster(osm.tiles(z))
    ImageIO.write(raster.image, "png", new File(dir, "osm_${z}.png"))
}

OSM wikiMedia = OSM.getWellKnownOSM("wikimedia")
(0..2).each{ int z ->
    Raster raster = wikiMedia.getRaster(wikiMedia.tiles(z))
    ImageIO.write(raster.image, "png", new File(dir, "osm_wikimedia_${z}.png"))
}