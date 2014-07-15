import geoscript.feature.Feature
import geoscript.layer.Layer
import geoscript.layer.MBTiles
import geoscript.workspace.*

File dir = new File("mbtiles")
dir.mkdir()

File file = new File("states.mbtiles")
MBTiles mbtiles = new MBTiles(file, "states", "A map of the united states")

Directory workspace = new Directory("mbtiles")
Layer layer = mbtiles.getLayer(outLayer: "mbtiles", outWorkspace: workspace, mbtiles.tiles(2))

layer.eachFeature{ Feature f ->
    println "${f['z']}/${f['x']}/${f['y']} ${f.geom.wkt}"
}