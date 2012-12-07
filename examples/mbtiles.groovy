import geoscript.geom.Bounds
import geoscript.render.Map
import geoscript.layer.Shapefile
import geoscript.style.*
import javax.imageio.ImageIO

import org.sqlite.JDBC
import java.sql.DriverManager;

/**
 * Render a GeoScript Map to an mbtiles database.
 * @author Jared Erickson
 */
class MbTiles {
    
    File database

    String name

    String description

    String type

    double version

    String format

    MbTiles(java.util.Map options = [:], String name, String description, File database) {
        this.name = name
        this.description = description
        this.database = database
        this.type = options.get("type","baseLayer")
        this.version = options.get("version", 1.0)
        this.type = options.get("format","png")
    }

    void render(Map map, Bounds bounds, int startZoom, int endZoom) {
       
        // Load the sqlite JDBC classes
        Class.forName("org.sqlite.JDBC")

        def connection = null
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:${database}") 
            def statement = connection.createStatement()
            statement.setQueryTimeout(30)

            statement.executeUpdate("drop table if exists metadata")
            statement.executeUpdate("create table metadata (name text, value text)")
            statement.executeUpdate("insert into metadata values('name', '${name}')")
            statement.executeUpdate("insert into metadata values('type', '${type}')")
            statement.executeUpdate("insert into metadata values('version', '${version}')")
            statement.executeUpdate("insert into metadata values('description', '${description}')")
            statement.executeUpdate("insert into metadata values('format', '${format}')")

            statement.executeUpdate("drop table if exists tiles")
            statement.executeUpdate("create table tiles (zoom_level integer, tile_column integer, tile_row integer, tile_data blob)")
             
            (startZoom..endZoom).each {zoom ->
                println "Zoom Level ${zoom}"
                long numberOfRowsOrColumns = getNumberOfRowsOrColumns(zoom)
                def tile1 = getTile(bounds.minX, bounds.minY, zoom)
                def tile2 = getTile(bounds.maxX, bounds.maxY, zoom)
                if (zoom == startZoom) {
                    Bounds b1 = createBounds(tile1.c, tile1.r, zoom)
                    Bounds b2 = createBounds(tile2.c, tile2.r, zoom)
                    Bounds b3 = b1.expand(b2)
                    println "Total Bounds: ${b3}"
                    statement.executeUpdate("insert into metadata values ('bounds', '${b3.minX},${b3.minY},${b3.maxX},${b3.maxY}')")
                }
                int numberOfColumns = tile2.c - tile1.c + 1
                int numberOfRows = tile1.r - tile2.r + 1
                List columns = (0..numberOfColumns).collect{i->tile1.c + i}
                List rows = (0..numberOfRows).collect{i->tile2.r + i}

                (0..<numberOfColumns).each{c ->
                    int column = columns[c]
                    println "   Column: ${column}"
                    (0..<numberOfRows).each{r ->
                        int row = rows[r]
                        println "      Row: ${row}"
                        int invertedY = numberOfRowsOrColumns - row - 1
                        Bounds b = createBounds(column, row, zoom)
                        println "         Bounds: ${b}"
                        map.bounds = b
                        
                        def image = map.renderToImage()
                        //map.render("state_tiles/states_${zoom}_${column}_${row}.png")
                        def out = new ByteArrayOutputStream()
                        ImageIO.write(image, "png", out)
                        out.close()
                        
                        def prep = connection.prepareStatement("insert into tiles values(?,?,?,?)")
                        prep.setInt(1, zoom)
                        prep.setInt(2, column)
                        prep.setInt(3, invertedY)
                        prep.setBytes(4, out.toByteArray())
                        prep.execute()
                    }
                }
            }
        }
        finally {
            connection?.close()
        }
    }

    private long getNumberOfRowsOrColumns(int zoom) {
        Math.pow(2, zoom) as long
    }

    private java.util.Map getTile(double lon, double lat, int zoom) {
        int x = Math.floor((lon + 180) / 360 * (1 << zoom)) as int   
        int y = Math.floor((1-Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1 << zoom)) as int
        [c: x, r: y]
    }

    private double getLongitude(int col, int zoom) {
        col / Math.pow(2.0, zoom) * 360.0 - 180
    }

    private double getLatitude(int row, int zoom) {
        double n = Math.PI - (2.0 * Math.PI * row) / Math.pow(2.0, zoom)
        Math.toDegrees(Math.atan(Math.sinh(n)))
    }

    private Bounds createBounds(int col, int row, int zoom) {
        double minX = getLongitude(col, zoom)
        double minY = getLatitude(row + 1, zoom)
        double maxX = getLongitude(col + 1, zoom)
        double maxY = getLatitude(row, zoom)
        new Bounds(minX, minY, maxX, maxY)
    }
}

def shp = new Shapefile("states.shp")
shp.style = new Fill("wheat") + new Stroke("navy", 0.1)
Map map = new Map()
map.fixAspectRatio = false
map.proj = "EPSG:4326"
map.width = 256
map.height = 256
map.addLayer(shp)
Bounds bounds = shp.bounds.reproject("EPSG:4326")

MbTiles mbtiles = new MbTiles("States","A Map of the United States", new File("states.mbtiles"))
mbtiles.render(map, bounds, 1, 5)
