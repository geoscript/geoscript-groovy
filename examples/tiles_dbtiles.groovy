import geoscript.layer.DBTiles
import geoscript.layer.ImageTileRenderer
import geoscript.layer.Layer
import geoscript.layer.Raster
import geoscript.layer.Shapefile
import geoscript.layer.TileGenerator
import geoscript.layer.TileRenderer
import geoscript.style.io.SimpleStyleReader

import javax.imageio.ImageIO

import static geoscript.GeoScript.download
import static geoscript.GeoScript.unzip

// Create a directory fo
File dir = new File("dbtiles")
dir.mkdir()

// Download data from natural earth
File shpDir = new File("naturalearth")
[
        [name: "countries", url: "http://www.naturalearthdata.com/http//www.naturalearthdata.com/download/110m/cultural/ne_110m_admin_0_countries.zip"],
        [name: "ocean",     url: "http://www.naturalearthdata.com/http//www.naturalearthdata.com/download/110m/physical/ne_110m_ocean.zip"]
].each { Map item ->
    unzip(download(new URL(item.url), new File(shpDir, "${item.name}.zip"), overwrite: false))
}

// Use simple style reader to create styles
SimpleStyleReader styleReader = new SimpleStyleReader()

// Get Layers and their styles
Layer ocean = new Shapefile("naturalearth/ne_110m_ocean.shp")
ocean.style = styleReader.read("fill=#88caf8")

Layer countries = new Shapefile("naturalearth/ne_110m_admin_0_countries.shp")
countries.style = styleReader.read("stroke=black stroke-width=0.5 fill=white")

// DBTiles with sqlite
File file = new File("world.mbtiles")
DBTiles dbtiles = new DBTiles("jdbc:sqlite:${file}","org.sqlite.JDBC", "World", "A map of the world")

TileRenderer renderer = new ImageTileRenderer(dbtiles, [ocean, countries])
TileGenerator generator = new TileGenerator(verbose: true)
generator.generate(dbtiles, renderer, 0, 4)

(0..3).each{int zoom ->
    Raster raster = dbtiles.getRaster(dbtiles.tiles(zoom))
    ImageIO.write(raster.image, "png", new File(dir, "sqlite_${zoom}.png"))
}

dbtiles.close()

// DBTiles with h2
file = new File("world.db")
dbtiles = new DBTiles("jdbc:h2:${file}","org.h2.Driver", "World", "A map of the world")

renderer = new ImageTileRenderer(dbtiles, [ocean, countries])
generator = new TileGenerator(verbose: true)
generator.generate(dbtiles, renderer, 0, 4)

(0..3).each{int zoom ->
    Raster raster = dbtiles.getRaster(dbtiles.tiles(zoom))
    ImageIO.write(raster.image, "png", new File(dir, "h2_${zoom}.png"))
}

dbtiles.close()

