import geoscript.geom.*
import geoscript.layer.*
import geoscript.style.*
import geoscript.workspace.*

import org.geotools.geopkg.GeoPackage as GtGeoPackage

import javax.imageio.ImageIO

/**
 * http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames
 */
class TileLoader {

    TileLoader() {

    }

    void render(geoscript.render.Map map, GeoPackage geoPackage, String name, int startZoom, int endZoom) {

        def tiles = geoPackage.tiles

        Bounds bounds = new Bounds(-180, -85.0511, 180, 85.0511, "EPSG:4326")

        GeoPackage.EntryBuilder entryBuilder = tiles.addEntry(name, bounds)
        (startZoom..endZoom).each { zoom ->
            int numColsAndRows = getNumberOfRowsOrColumns(zoom)
            entryBuilder.matrix(zoom, numColsAndRows, numColsAndRows, 256, 256, 0.1, 0.1)
        }
        GeoPackage.Entry entry = entryBuilder.build()

        (startZoom..endZoom).each { zoom ->
            println "Zoom Level ${zoom}"
            long numberOfRowsOrColumns = getNumberOfRowsOrColumns(zoom)
            def tile1 = getTile(bounds.minX, bounds.minY, zoom)
            def tile2 = getTile(bounds.maxX, bounds.maxY, zoom)
            // println "Tile 1 = ${tile1}"
            // println "Tile 2 = ${tile2}"
            int numberOfColumns = tile2.c - tile1.c + 1
            int numberOfRows = tile1.r - tile2.r + 1
            println "   Cols = ${numberOfColumns}"
            println "   Rows = ${numberOfRows}"
            List columns = (0..numberOfColumns).collect { i -> tile1.c + i }
            List rows = (0..numberOfRows).collect { i -> tile2.r + i }
            (0..<numberOfColumns).each { c ->
                int column = columns[c]
                println "   Column: ${column}"
                (0..<numberOfRows).each { r ->
                    int row = rows[r]
                    println "      Row: ${row}"
                    int invertedY = numberOfRowsOrColumns - row - 1
                    Bounds b = createBounds(column, row, zoom)
                    println "         Bounds: ${b}"
                    map.bounds = b
                    println "         Scale: ${map.scaleDenominator}"
                    println "         PixelsPerMeterRatio: ${org.geotools.renderer.lite.RendererUtilities.calculatePixelsPerMeterRatio(map.scaleDenominator,[:])}"

                    def image = map.renderToImage()
                    //map.render("state_tiles/states_${zoom}_${column}_${row}.png")
                    def out = new ByteArrayOutputStream()
                    ImageIO.write(image, "png", out)
                    out.close()

                    entry.addTile(zoom, column, invertedY, out.toByteArray())
                }
            }
        }

        tiles.close()
    }

    long getNumberOfRowsOrColumns(int zoom) {
        Math.pow(2, zoom) as long
    }

    long getNumberOfTiles(int zoom) {
        Math.pow(2, zoom) * 2
    }

    java.util.Map getTile(double lon, double lat, int zoom) {
        int x = Math.floor((lon + 180) / 360 * (1 << zoom)) as int
        int y = Math.floor((1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1 << zoom)) as int
        if (x < 0) {
            x = 0
        }
        if (x >= (1 << zoom)) {
            x = ((1 << zoom) - 1);
        }
        if (y < 0) {
            y = 0;
        }
        if (y >= (1 << zoom)) {
            y = ((1 << zoom) - 1);
        }
        [c: x, r: y]
    }

    double getLongitude(int col, int zoom) {
        col / Math.pow(2.0, zoom) * 360.0 - 180
    }

    double getLatitude(int row, int zoom) {
        double n = Math.PI - (2.0 * Math.PI * row) / Math.pow(2.0, zoom)
        Math.toDegrees(Math.atan(Math.sinh(n)))
    }

    Bounds createBounds(int col, int row, int zoom) {
        double minX = getLongitude(col, zoom)
        double minY = getLatitude(row + 1, zoom)
        double maxX = getLongitude(col + 1, zoom)
        double maxY = getLatitude(row, zoom)
        if (minX == -180) {
            minX = -179.9
        }
        if (maxX == 180) {
            maxX = 179.9
        }
        new Bounds(minX, minY, maxX, maxY)
    }

}

File file = new File("states.gpkg")
if (file.exists()) file.delete()
GeoPackage geoPackage = new GeoPackage(file)

def shp = new Shapefile("states.shp")
shp.style = new Fill("wheat") + new Stroke("navy", 0.1)
geoscript.render.Map map = new geoscript.render.Map()
map.fixAspectRatio = false
map.proj = "EPSG:4326"
map.width = 256
map.height = 256
map.addLayer(shp)

TileLoader tileLoader = new TileLoader()
tileLoader.render(map, geoPackage, "states", 0, 3)

Format format = geoPackage.format
println format.names
Raster raster = format.read("states")
println raster.bounds
geoscript.render.Draw.draw(raster, out: "states_gpkg.png")

