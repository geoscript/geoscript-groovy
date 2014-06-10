import geoscript.tile.OSM
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

// Stamen Toner
osm = new OSM("Stamen Toner", [
        "http://a.tile.stamen.com/toner",
        "http://b.tile.stamen.com/toner",
        "http://c.tile.stamen.com/toner",
        "http://d.tile.stamen.com/toner"
])
(0..2).each{ int z ->
    Raster raster = osm.getRaster(osm.tiles(z))
    ImageIO.write(raster.image, "png", new File(dir, "stamen_toner_${z}.png"))
}

// Stamen Toner-lite
osm = new OSM("Stamen Toner", [
        "http://a.tile.stamen.com/toner-lite",
        "http://b.tile.stamen.com/toner-lite",
        "http://c.tile.stamen.com/toner-lite",
        "http://d.tile.stamen.com/toner-lite"
])
(0..2).each{ int z ->
    Raster raster = osm.getRaster(osm.tiles(z))
    ImageIO.write(raster.image, "png", new File(dir, "stamen_toner-lite_${z}.png"))
}

// Stamen Watercolor
osm = new OSM("Stamen Watercolor", [
        "http://a.tile.stamen.com/watercolor",
        "http://b.tile.stamen.com/watercolor",
        "http://c.tile.stamen.com/watercolor",
        "http://d.tile.stamen.com/watercolor"
])
(1..2).each{ int z ->
    Raster raster = osm.getRaster(osm.tiles(z))
    ImageIO.write(raster.image, "png", new File(dir, "stamen_watercolor_${z}.png"))
}

// Open MapQuest Street Map
osm = new OSM("MapQuest Street Map",[
        "http://otile1.mqcdn.com/tiles/1.0.0/map",
        "http://otile2.mqcdn.com/tiles/1.0.0/map",
        "http://otile3.mqcdn.com/tiles/1.0.0/map",
        "http://otile4.mqcdn.com/tiles/1.0.0/map"
])
(0..3).each{ int z ->
    Raster raster = osm.getRaster(osm.tiles(z))
    ImageIO.write(raster.image, "png", new File(dir, "mq_map_${z}.png"))
}

// Open MapQuest Satellite Map
osm = new OSM("MapQuest Satellite Map", [
        "http://otile1.mqcdn.com/tiles/1.0.0/sat",
        "http://otile2.mqcdn.com/tiles/1.0.0/sat",
        "http://otile3.mqcdn.com/tiles/1.0.0/sat",
        "http://otile4.mqcdn.com/tiles/1.0.0/sat"
])
(0..3).each{ int z ->
    Raster raster = osm.getRaster(osm.tiles(z))
    ImageIO.write(raster.image, "png", new File(dir, "mq_sat_${z}.png"))
}