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
}
