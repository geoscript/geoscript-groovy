import geoscript.feature.Feature
import geoscript.geom.Bounds
import geoscript.layer.Layer
import geoscript.layer.OSM
import geoscript.layer.Pyramid
import geoscript.layer.Shapefile
import geoscript.layer.Tile
import geoscript.layer.VectorTiles
import geoscript.layer.io.MvtReader
import geoscript.style.Fill
import geoscript.style.Stroke

File dir = new File("vectortiles")
dir.mkdir()

OSM osm = new OSM("Stamen Toner", [
        "http://a.tile.stamen.com/toner",
        "http://b.tile.stamen.com/toner",
        "http://c.tile.stamen.com/toner",
        "http://d.tile.stamen.com/toner"
])

Pyramid pyramid = Pyramid.createGlobalMercatorPyramid()
pyramid.origin = Pyramid.Origin.TOP_LEFT
VectorTiles vectorTiles = new VectorTiles(
    "Land",
    new URL("http://tile.openstreetmap.us/vectiles-land-usages"),
    pyramid,
    "mvt",
    style: new Fill("green") + new Stroke("black", 0.1)
)

Shapefile shp = new Shapefile("states.shp")
["North Dakota", "Oregon", "Washington"].each { String name ->
    println name
    shp.getFeatures("STATE_NAME = '${name}'").each { Feature f ->
        Bounds b = f.geom.bounds.expandBy(0.5)
        b.proj = "EPSG:4326"
        geoscript.render.Map map = new geoscript.render.Map(
                layers: [osm, shp, vectorTiles],
                bounds: b,
                width: 400,
                height: 400
        )
        map.render(new File(dir, "${f['STATE_NAME']}.png"))
    }
}

