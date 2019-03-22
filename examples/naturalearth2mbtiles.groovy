import geoscript.layer.ImageTileRenderer
import geoscript.layer.Layer
import geoscript.layer.MBTiles
import geoscript.layer.Pyramid
import geoscript.layer.TileGenerator
import geoscript.layer.TileRenderer
import geoscript.style.Fill
import geoscript.style.Stroke
import geoscript.workspace.Directory
import geoscript.workspace.GeoPackage

import static geoscript.GeoScript.download
import static geoscript.GeoScript.unzip

// Download data from natural earth
File dataDir = new File("naturalearth")
[
        [name: "countries", url: "http://www.naturalearthdata.com/http//www.naturalearthdata.com/download/110m/cultural/ne_110m_admin_0_countries.zip"],
        [name: "ocean",     url: "http://www.naturalearthdata.com/http//www.naturalearthdata.com/download/110m/physical/ne_110m_ocean.zip"],
        [name: "places",    url: "http://www.naturalearthdata.com/http//www.naturalearthdata.com/download/110m/cultural/ne_110m_populated_places.zip"],
        [name: "rivers",    url: "http://www.naturalearthdata.com/http//www.naturalearthdata.com/download/110m/physical/ne_110m_rivers_lake_centerlines.zip"],
        [name: "states",    url: "http://www.naturalearthdata.com/http//www.naturalearthdata.com/download/110m/cultural/ne_110m_admin_1_states_provinces.zip"]

].each { Map item ->
    println "Downloading ${item.name} from ${item.url}..."
    unzip(download(new URL(item.url), new File(dataDir, "${item.name}.zip"), overwrite: false))
}

Directory directory = new Directory("naturalearth")
Layer countries = directory.get("ne_110m_admin_0_countries")
countries.style = new Fill("#ffffff") + new Stroke("#b2b2b2", 0.5)
Layer ocean = directory.get("ne_110m_ocean")
ocean.style = new Fill("#a5bfdd")

File mbtilesFile = new File("world.mbtiles")
if (mbtilesFile.exists()) {
    mbtilesFile.delete()
}
TileGenerator generator = new TileGenerator(verbose: true)
MBTiles mbtiles = new MBTiles(mbtilesFile, "World", "Natural Earth")
ImageTileRenderer renderer = new ImageTileRenderer(mbtiles, [ocean, countries])
generator.generate(mbtiles, renderer, 0, 3)
