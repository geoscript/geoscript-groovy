import geoscript.layer.Shapefile
import geoscript.layer.TileGenerator
import geoscript.layer.UTFGrid
import geoscript.layer.UTFGridTileRenderer

Shapefile shp = new Shapefile(new File("states.shp"))
File dir = new File("utfgrid")
dir.mkdirs()
UTFGrid utf = new UTFGrid(dir)

UTFGridTileRenderer renderer = new UTFGridTileRenderer(utf, shp, [shp.schema.get("STATE_NAME")])
TileGenerator generator = new TileGenerator(verbose: true)
generator.generate(utf, renderer, 0, 4)
