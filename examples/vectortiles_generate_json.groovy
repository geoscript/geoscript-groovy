import geoscript.layer.Pyramid
import geoscript.layer.Shapefile
import geoscript.layer.TileGenerator
import geoscript.layer.VectorTileRenderer
import geoscript.layer.VectorTiles
import geoscript.layer.io.GeoJSONReader
import geoscript.layer.io.GeoJSONWriter
import geoscript.proj.Projection

/*
    To View in OpenLayers, run python -m SimpleHTTPServer from the examples/ directory and then
    browse  http://localhost:8000/vectortiles.html
*/

File dir = new File("states_vector_tiles")
if (!dir.exists()) {
    dir.mkdir()
}

Pyramid pyramid = Pyramid.createGlobalMercatorPyramid()
pyramid.origin = Pyramid.Origin.TOP_LEFT
VectorTiles vectorTiles = new VectorTiles(
    "states",
    dir,
    pyramid,
    "json",
    proj: new Projection("EPSG:4326")
)

Shapefile shp = new Shapefile("states.shp")
VectorTileRenderer renderer = new VectorTileRenderer(new GeoJSONWriter(), shp, [shp.schema.get("STATE_NAME")])
TileGenerator generator = new TileGenerator(verbose: true)
generator.generate(vectorTiles, renderer, 0, 4)


