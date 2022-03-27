import geoscript.layer.ImageTileRenderer
import geoscript.layer.Layer
import geoscript.layer.Pyramid
import geoscript.layer.TileGenerator
import geoscript.layer.TileRenderer
import geoscript.style.Fill
import geoscript.style.Stroke
import geoscript.workspace.Directory
import geoscript.workspace.GeoPackage as GeoPackageWorkspace
import geoscript.layer.GeoPackage as GeoPackageTiles

import static geoscript.GeoScript.download
import static geoscript.GeoScript.unzip

// Download data from natural earth
File dataDir = new File("naturalearth")
[
        [name: "countries",  url: "https://www.naturalearthdata.com/http//www.naturalearthdata.com/download/110m/cultural/ne_110m_admin_0_countries.zip"],
        [name: "ocean",      url: "https://www.naturalearthdata.com/http//www.naturalearthdata.com/download/110m/physical/ne_110m_ocean.zip"],
        [name: "graticules", url: "https://www.naturalearthdata.com/http//www.naturalearthdata.com/download/110m/physical/ne_110m_graticules_20.zip"],
        [name: "places",     url: "https://www.naturalearthdata.com/http//www.naturalearthdata.com/download/110m/cultural/ne_110m_populated_places.zip"],
        [name: "rivers",     url: "https://www.naturalearthdata.com/http//www.naturalearthdata.com/download/110m/physical/ne_110m_rivers_lake_centerlines.zip"],
        [name: "states",     url: "https://www.naturalearthdata.com/http//www.naturalearthdata.com/download/110m/cultural/ne_110m_admin_1_states_provinces.zip"]

].each { Map item ->
    println "Downloading ${item.name} from ${item.url}..."
    unzip(download(new URL(item.url), new File(dataDir, "${item.name}.zip"), overwrite: false))
}

// Add Shapefiles to a GeoPackage
Directory directory = new Directory("naturalearth")

File file = new File("naturalearth.gpkg")
if (file.exists()) {
    file.delete()
}
GeoPackageWorkspace workspace = new GeoPackageWorkspace(file)

[
  "ne_110m_admin_0_countries": "countries",
  "ne_110m_ocean": "ocean",
  "ne_110m_graticules_20": "graticules",
  "ne_110m_populated_places": "places",
  "ne_110m_rivers_lake_centerlines": "rivers",
  "ne_110m_admin_1_states_provinces": "states"
].each { String name, String alias ->
    println "Adding ${name} as ${alias}"
    workspace.add(directory.get(name), alias)
}

// Generate Tiles
Layer countries = workspace.get("countries")
countries.style = new Fill("#ffffff") + new Stroke("#b2b2b2", 0.5)
Layer ocean = workspace.get("ocean")
ocean.style = new Fill("#a5bfdd")

TileGenerator generator = new TileGenerator(verbose: true)

GeoPackageTiles geodeticTiles = new GeoPackageTiles(file, "world", Pyramid.createGlobalGeodeticPyramid(origin: Pyramid.Origin.TOP_LEFT))
TileRenderer geodeticRenderer = new ImageTileRenderer(geodeticTiles, [ocean, countries])
println "Generating world global geodetic tiles..."
generator.generate(geodeticTiles, geodeticRenderer, 0, 3)

GeoPackageTiles mercatorTiles = new GeoPackageTiles(file, "world_mercator", Pyramid.createGlobalMercatorPyramid(origin: Pyramid.Origin.TOP_LEFT))
TileRenderer mercatorRenderer = new ImageTileRenderer(mercatorTiles, [ocean, countries])
println "Generating world_mercator global mercator tiles..."
generator.generate(mercatorTiles, mercatorRenderer, 0, 3)
