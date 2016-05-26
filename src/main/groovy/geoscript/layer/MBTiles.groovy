package geoscript.layer

import geoscript.geom.Bounds
import geoscript.proj.Projection
import groovy.sql.Sql
import org.geotools.mbtiles.MBTilesFile
import org.geotools.mbtiles.MBTilesMetadata
import org.geotools.mbtiles.MBTilesTile

/**
 * The MBTiles TileLayer
 * @author Jared Erickson
 */
class MBTiles extends ImageTileLayer {

    /**
     * The MBTiles File
     */
    private final File file

    /**
     * The GeoTools MBTilesFile
     */
    MBTilesFile tiles

    /**
     * The cached internal Pyramid
     */
    private Pyramid pyramid

    /**
     * The EPSG:4326 Projection
     */
    private Projection latLonProj = new Projection("EPSG:4326")

    /**
     * The EPSG:3857 Projection
     */
    private Projection mercatorProj = new Projection("EPSG:3857")

    /**
     * The world wide Bounds in EPSG:4326
     */
    private Bounds latLonBounds = new Bounds(-179.99, -85.0511, 179.99, 85.0511, latLonProj)

    /**
     * The world wide Bounds in EPSG:3857
     */
    private Bounds mercatorBounds = latLonBounds.reproject(mercatorProj)

    /**
     * The Groovy Sql Connection
     */
    private Sql sql

    /**
     * Create a new MBTilesLayer with a existing File
     * @param file The existing File
     */
    MBTiles(File file) {
        this.file = file
        this.tiles = new MBTilesFile(file)
        this.bounds = mercatorBounds
        this.proj = mercatorProj
        this.name = this.tiles.loadMetaData().name
    }

    /**
     * Create a new MBTilesLayer with a existing File
     * @param file The existing File name
     */
    MBTiles(String file) {
        this(new File(file))
    }

    /**
     * Create a new MBTilesLayer with a new File
     * @param options The optional named parameters
     * <ul>
     *     <li>type = The type of layer (base_layer is the default or overlay)</li>
     *     <li>version = The version number (1.0 is the default)</li>
     *     <li>format = The image format (png is the default or jpeg)</li>
     *     <li>attribution = The attributes</li>
     * </ul>
     * @param file The new File
     * @param name The name of the layer
     * @param description The description of the layer
     */
    MBTiles(java.util.Map options = [:], File file, String name, String description) {

        this.file = file
        this.tiles = new MBTilesFile(file)
        this.bounds = mercatorBounds
        this.proj = mercatorProj
        this.name = name

        String type = options.get("type", "base_layer")
        String version = options.get("version", "1.0")
        String format = options.get("format", "png")
        String attribution = options.get("attribution","Created with GeoScript")

        tiles.init()
        MBTilesMetadata metadata = new MBTilesMetadata()
        metadata.name = name
        metadata.description = description
        metadata.formatStr = format
        metadata.version = version
        metadata.typeStr = type
        metadata.bounds = latLonBounds.env
        metadata.attribution = attribution
        tiles.saveMetaData(metadata)
    }

    /**
     * Create a new MBTilesLayer with a new File
     * @param options The optional named parameters
     * <ul>
     *     <li>type = The type of layer (base_layer is the default or overlay)</li>
     *     <li>version = The version number (1.0 is the default)</li>
     *     <li>format = The image format (png is the default or jpeg)</li>
     *     <li>attribution = The attributes</li>
     * </ul>
     * @param file The new File name
     * @param name The name of the layer
     * @param description The description of the layer
     */
    MBTiles(java.util.Map options = [:], String fileName, String name, String description) {
        this(options, new File(fileName), name, description)
    }

    @Override
    Pyramid getPyramid() {
        if (!this.pyramid) {
            this.pyramid = Pyramid.createGlobalMercatorPyramid()
        }
        this.pyramid
    }

    @Override
    ImageTile get(long z, long x, long y) {
        MBTilesTile t = tiles.loadTile(z, x, y)
        new ImageTile(t.zoomLevel, t.tileColumn, t.tileRow, t.data)
    }

    @Override
    void put(ImageTile t) {
        MBTilesTile tile = new MBTilesTile(t.z, t.x, t.y)
        tile.data = t.data
        tiles.saveTile(tile)
    }

    /**
     * Delete a Tile
     * @param t The Tile
     */
    @Override
    void delete(ImageTile t) {
        getSql().execute("DELETE FROM tiles WHERE zoom_level = ? AND tile_column = ? AND tile_row = ?", [t.z, t.x, t.y])
    }

    /**
     * Delete all Tiles in the TileCursor
     * @param tiles The TileCursor
     */
    @Override
    void delete(TileCursor<ImageTile> tiles) {
        getSql().execute("DELETE FROM tiles WHERE zoom_level = ? AND tile_column >= ? AND tile_column <= ? " +
                "AND tile_row >= ? AND tile_row <= ?", [tiles.z, tiles.minX, tiles.maxX, tiles.minY, tiles.maxY])
    }

    /**
     * Lazily create the Groovy Sql Connection
     * @return
     */
    private Sql getSql() {
        if (!sql) {
            sql = Sql.newInstance("jdbc:sqlite:${file.absolutePath}", "org.sqlite.JDBC")
        }
        sql
    }

    @Override
    void close() throws IOException {
        this.tiles.close()
        if (sql) sql.close()
    }

    /**
     * Get metadata (type, name, description, format, version, attribution, bounds)
     * @return A Map of metadata
     */
    Map<String,String> getMetadata() {
        MBTilesMetadata metadata = tiles.loadMetaData()
        [
                type: metadata.typeStr,
                name: metadata.name,
                description: metadata.description,
                format: metadata.formatStr,
                version: metadata.version,
                attribution: metadata.attribution,
                bounds: metadata.boundsStr
        ]
    }

    /**
     * Get the number of tiles per zoom level.
     * @return A List of Maps with zoom, tiles, total, and percent keys
     */
    List<Map> getTileCounts() {
        List stats = []
        getSql().eachRow("select count(*) as num_tiles, zoom_level from tiles group by zoom_level order by zoom_level", { def row ->
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
     * Get the maximum zoom level of the tiles present.
     * @return The maximum zoom level of the tile present
     */
    int getMaxZoom() {
        int max
        getSql().eachRow("select max(zoom_level) as max_zoom_level from tiles", {  def row ->
            max = row.max_zoom_level
        })
        max
    }

    /**
     * Get the minimum zoom level of the tiles present.
     * @return The minimum zoom level of the tile present
     */
    int getMinZoom() {
        int min
        getSql().eachRow("select min(zoom_level) as min_zoom_level from tiles", {  def row ->
            min = row.min_zoom_level
        })
        min
    }

    /**
     * The MBTiles TileLayerFactory
     */
    static class Factory extends TileLayerFactory<MBTiles> {

        @Override
        MBTiles create(String paramsStr) {
            Map params = [:]
            if (paramsStr.endsWith(".mbtiles") && !paramsStr.contains("type=")) {
                params["type"] = "mbtiles"
                params["file"] = new File(paramsStr)
                create(params)
            } else {
                super.create(paramsStr)
            }
        }

        @Override
        MBTiles create(Map params) {
            String type = params.get("type","").toString()
            if (type.equalsIgnoreCase("mbtiles")) {
                File file = params.get("file") instanceof File ? params.get("file") as File : new File(params.get("file"))
                if (!file.exists() || file.length() == 0 || (params.get("name") && params.get("description"))) {
                    String name = file.name.replaceAll(".mbtiles","")
                    new MBTiles(file, params.get("name", name), params.get("description", name))
                } else {
                    new MBTiles(file)
                }
            } else {
                null
            }
        }

        @Override
        TileRenderer getTileRenderer(Map options, TileLayer tileLayer, List<Layer> layers) {
            if (tileLayer instanceof MBTiles) {
                new ImageTileRenderer(tileLayer, layers)
            } else {
                null
            }
        }
    }
}
