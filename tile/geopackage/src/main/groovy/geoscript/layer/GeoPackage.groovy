package geoscript.layer

import geoscript.geom.Bounds
import geoscript.proj.Projection
import groovy.sql.Sql
import org.geotools.geopkg.TileEntry
import org.geotools.geopkg.TileMatrix
import org.geotools.geopkg.TileReader

/**
 * A GeoPackage TileLayer
 * @author Jared Erickson
 */
class GeoPackage extends ImageTileLayer {

    /**
     * The GeoPackage File
     */
    private final File file

    /**
     * The GeoTools GeoPackage
     */
    private final org.geotools.geopkg.GeoPackage geopkg

    /**
     * The GeoTools GeoPackage TileEntry for the given layer
     */
    private final TileEntry tileEntry

    /**
     * The cached internal Pyramid
     */
    private Pyramid pyramid

    /**
     * The Groovy Sql connection
     */
    private Sql sql

    /**
     * Create a new GeoPackage from an existing database and tile layer
     * @param file The GeoPackage database file
     * @param layerName The existing tile layer name
     */
    GeoPackage(File file, String layerName) {
        this.file = file
        this.geopkg = new org.geotools.geopkg.GeoPackage(file)
        this.tileEntry = geopkg.tile(layerName)
        this.name = layerName
        this.proj = new Projection("EPSG:" + this.tileEntry.srid)
        this.bounds = new Bounds(this.tileEntry.bounds)
    }

    /**
     * Create a new GeoPackage from a new database
     * @param file The new database File
     * @param layerName The layer name
     * @param pyramid The Pyramid structure
     */
    GeoPackage(File file, String layerName, Pyramid pyramid) {
        this.file = file
        this.name = layerName
        this.pyramid = pyramid
        this.pyramid.origin = Pyramid.Origin.TOP_LEFT
        this.proj = pyramid.proj
        this.bounds = pyramid.bounds
        this.geopkg = new org.geotools.geopkg.GeoPackage(file)
        this.geopkg.init()
        TileEntry tileEntry = new TileEntry()
        tileEntry.tableName = layerName
        tileEntry.bounds = pyramid.bounds.env
        tileEntry.srid = pyramid.proj.epsg
        tileEntry.tileMatricies = pyramid.grids.collect { Grid g ->
            new TileMatrix(g.z as Integer,
                    g.width as Integer, g.height as Integer,
                    pyramid.tileWidth as Integer, pyramid.tileHeight as Integer,
                    g.xResolution as Double, g.yResolution as Double)
        }
        this.geopkg.create(tileEntry)
        this.tileEntry = geopkg.tile(layerName)
    }

    /**
     * Get all of the names of TileLayers from the GeoPackage database
     * @param file The database File
     * @return A List of TileLayer names
     */
    static List<String> getNames(File file) {
        List names = []
        def geopkg = new org.geotools.geopkg.GeoPackage(file)
        try {
            names.addAll(geopkg.tiles().collect { TileEntry e -> e.tableName })
        } finally {
            geopkg.close()
        }
        names
    }

    @Override
    Pyramid getPyramid() {
        if (!this.pyramid) {
            this.pyramid = new Pyramid(
                    proj: new Projection("EPSG:${this.tileEntry.srid}"),
                    bounds: new Bounds(this.tileEntry.bounds),
                    origin: Pyramid.Origin.TOP_LEFT,
                    tileWidth: 256, // @TODO This should come from TileMatrix
                    tileHeight: 256
            )
            this.pyramid.grids = this.tileEntry.tileMatricies.collect { TileMatrix m ->
                new Grid(
                        m.zoomLevel,
                        m.matrixWidth,
                        m.matrixHeight,
                        // @TODO Should tile width and height be pushed down to Grid from Pyramid?
                        // m.tileWidth
                        // m.tileHeight
                        m.XPixelSize,
                        m.YPixelSize
                )
            }
        }
        this.pyramid
    }

    @Override
    ImageTile get(long z, long x, long y) {
        ImageTile t = null
        TileReader tileReader = this.geopkg.reader(this.tileEntry,
                z as Integer, z as Integer,
                x as Integer, x as Integer,
                y as Integer, y as Integer)
        try {
            if (tileReader.hasNext()) {
                org.geotools.geopkg.Tile tile = tileReader.next()
                t = new ImageTile(tile.zoom, tile.column, tile.row, tile.data)
            } else {
                t = new ImageTile(z, x, y)
            }
        } finally {
            tileReader.close()
        }
        t
    }

    @Override
    void put(ImageTile t) {
        this.geopkg.add(this.tileEntry,
                new org.geotools.geopkg.Tile(t.z as Integer, t.x as Integer, t.y as Integer, t.data))
    }

    /**
     * Delete a Tile
     * @param t The Tile
     */
    @Override
    void delete(ImageTile t) {
        getSql().execute("DELETE FROM ${this.tileEntry.tableName} WHERE zoom_level = ? AND tile_column = ? AND tile_row = ?",
                [t.z, t.x, t.y])
    }

    /**
     * Delete all Tiles in the TileCursor
     * @param tiles The TileCursor
     */
    @Override
    void delete(TileCursor<ImageTile> tiles) {
        getSql().execute("DELETE FROM ${this.tileEntry.tableName} WHERE zoom_level = ? AND tile_column >= ? AND tile_column <= ? " +
                "AND tile_row >= ? AND tile_row <= ?", [tiles.z, tiles.minX, tiles.maxX, tiles.minY, tiles.maxY])
    }

    /**
     * Create the Groovy Sql connection lazily
     * @return The Groovy Sql connection
     */
    private Sql getSql() {
        if (!sql) {
            sql = Sql.newInstance("jdbc:sqlite:${file.absolutePath}", "org.sqlite.JDBC")
        }
        sql
    }

    @Override
    void close() throws IOException {
        this.geopkg.close()
        if (sql) sql.close()
    }

    /**
     * Get the number of tiles per zoom level.
     * @return A List of Maps with zoom, tiles, total, and percent keys
     */
    List<Map> getTileCounts() {
        List stats = []
        String sqlStr = "select count(*) as num_tiles, zoom_level from ${this.tileEntry.tableName} group by zoom_level order by zoom_level".toString()
        getSql().eachRow(sqlStr, { def row ->
            long zoom = row.zoom_level
            long numberOfTiles = row.num_tiles
            long totalNumberOfTiles = this.pyramid.grid(row.zoom_level).size
            double percent = totalNumberOfTiles / numberOfTiles
            stats.add([
                    zoom: zoom,
                    tiles: numberOfTiles,
                    total: totalNumberOfTiles,
                    percent: percent
            ])
        })
        stats
    }

    /**
     * The GeoPackage TileLayerFactory
     */
    static class Factory extends TileLayerFactory<GeoPackage> {

        @Override
        GeoPackage create(String paramsStr) {
            Map params = [:]
            if (paramsStr.endsWith(".gpkg") && !paramsStr.contains("type=")) {
                params["type"] = "geopackage"
                params["file"] = new File(paramsStr)
                create(params)
            } else {
                super.create(paramsStr)
            }
        }

        @Override
        GeoPackage create(Map params) {
            String type = params.get("type","").toString()
            if (type.equalsIgnoreCase("geopackage")) {
                File file = params.get("file") instanceof File ? params.get("file") as File : new File(params.get("file"))
                String name = params.get("name", file.name.replaceAll(".gpkg",""))
                if (!file.exists() || file.length() == 0 || params.get("pyramid")) {
                    Object p = params.get("pyramid", Pyramid.createGlobalMercatorPyramid())
                    Pyramid pyramid = p instanceof Pyramid ? p as Pyramid : Pyramid.fromString(p as String)
                    new GeoPackage(file, name, pyramid)
                } else {
                    new GeoPackage(file, name)
                }
            } else {
                null
            }
        }

        @Override
        TileRenderer getTileRenderer(Map options, TileLayer tileLayer, List<Layer> layers) {
            if (tileLayer instanceof GeoPackage) {
                new ImageTileRenderer(tileLayer, layers)
            } else {
                null
            }
        }
    }

}
