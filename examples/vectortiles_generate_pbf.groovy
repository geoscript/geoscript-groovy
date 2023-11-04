import geoscript.feature.Feature
import geoscript.geom.Bounds
import geoscript.layer.Layer
import geoscript.layer.OSM
import geoscript.layer.PbfVectorTileRenderer
import geoscript.layer.Pyramid
import geoscript.layer.Shapefile
import geoscript.layer.TileGenerator
import geoscript.layer.VectorTiles
import geoscript.style.Fill
import geoscript.style.Shape

// Generate

File dir = new File("states_vector_tiles_pbf")
if (!dir.exists()) {
    dir.mkdir()
}

Pyramid pyramid = Pyramid.createGlobalMercatorPyramid()
pyramid.origin = Pyramid.Origin.TOP_LEFT
VectorTiles vectorTiles = new VectorTiles(
    "states",
    dir,
    pyramid,
    "pbf",
    style: [
        "states": new Fill("wheat"),
        "states_centroids": new Shape("red",12,"circle")
    ]
)

Layer layer = new Shapefile("states.shp")
Layer centroidLayer = layer.transform("states_centroids", [
        "geom": "centroid(the_geom)",
        "name": "STATE_NAME"
])

PbfVectorTileRenderer renderer = new PbfVectorTileRenderer([layer, centroidLayer], [
        "states": ["STATE_NAME"],
        "states_centroids": ["name"]
])
TileGenerator generator = new TileGenerator(verbose: true)
generator.generate(vectorTiles, renderer, 0, 6)

// Render

OSM osm = new OSM("OSM", [
        "http://a.tile.openstreetmap.org",
        "http://b.tile.openstreetmap.org",
        "http://c.tile.openstreetmap.org"
])

["North Dakota", "Oregon", "Washington"].each { String name ->
    println name
    layer.getFeatures("STATE_NAME = '${name}'").each { Feature f ->
        Bounds b = f.geom.bounds.expandBy(0.5)
        b.proj = "EPSG:4326"
        geoscript.render.Map map = new geoscript.render.Map(
                layers: [osm, vectorTiles],
                bounds: b,
                width: 400,
                height: 400
        )
        map.render(new File(dir, "${f['STATE_NAME']}.png"))
    }
}


