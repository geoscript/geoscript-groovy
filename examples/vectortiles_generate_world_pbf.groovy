import geoscript.layer.Layer
import geoscript.layer.PbfVectorTileRenderer
import geoscript.layer.Pyramid
import geoscript.layer.Shapefile
import geoscript.layer.TileGenerator
import geoscript.layer.VectorTiles
import geoscript.style.Fill
import geoscript.style.Stroke

import static geoscript.GeoScript.download
import static geoscript.GeoScript.unzip

// Download data from natural earth
File dataDir = new File("naturalearth")
[
        [name: "countries", url: "http://www.naturalearthdata.com/http//www.naturalearthdata.com/download/110m/cultural/ne_110m_admin_0_countries.zip"],
        [name: "ocean",     url: "http://www.naturalearthdata.com/http//www.naturalearthdata.com/download/110m/physical/ne_110m_ocean.zip"]
].each { Map item ->
    unzip(download(new URL(item.url), new File(dataDir, "${item.name}.zip"), overwrite: false))
}

// Generate
File dir = new File("vectortiles_world_pbf")
if (!dir.exists()) {
    dir.mkdir()
}

Pyramid pyramid = Pyramid.createGlobalMercatorPyramid()
pyramid.origin = Pyramid.Origin.TOP_LEFT
VectorTiles vectorTiles = new VectorTiles(
    "world",
    dir,
    pyramid,
    "pbf",
    style: [
        "countries": new Fill("white") + new Stroke("black", 0.1),
        "ocean": new Fill("blue")
    ]
)

Layer countries = new Shapefile(new File(dataDir, "ne_110m_admin_0_countries.shp"))
Layer ocean = new Shapefile(new File(dataDir, "ne_110m_ocean.shp"))

PbfVectorTileRenderer renderer = new PbfVectorTileRenderer([countries, ocean], [
        "countries": ["NAME"],
        "ocean": ["FeatureCla"]
])
TileGenerator generator = new TileGenerator(verbose: true)
generator.generate(vectorTiles, renderer, 0, 6)