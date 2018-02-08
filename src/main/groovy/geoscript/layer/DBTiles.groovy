package geoscript.layer

import geoscript.geom.Bounds
import geoscript.proj.Projection
import groovy.sql.GroovyRowResult
import groovy.sql.Sql

import javax.sql.DataSource

/**
 * The DBTiles TileLayer stores tiles like MBTiles but in any JDBC database.
 * Currently only H2, SQLite, and Postgresql have been tested.
 * @author Jared Erickson
 */
class DBTiles extends ImageTileLayer {

    /**
     * The JDBC URL
     */
    private String url

    /**
     * The JDBC driver class name
     */
    private String driver

    /**
     * The database user
     */
    private String user

    /**
     * The database password
     */
    private String password

    /**
     * The name of the metadata table (defaults to metadata)
     */
    private String metadataTable

    /**
     * The name of the tiles table (defaults to tiles)
     */
    private String tilesTable

    /**
     * The Sql instance
     */
    private Sql sql

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
     * Open an existing DBTiles.
     * @param options Optional named parameters
     * <ul>
     *     <li>user = The database user (empty string by default)</li>
     *     <li>password = The database password (empty string by default)</li>
     *     <li>metadataTable = The name of the metadata table (metadata by default)</li>
     *     <li>tilesTable = The name of the tiles table (tiles by default)</li>
     * </ul>
     * @param url The Database url
     * @param driver The Database driver class name
     */
    DBTiles(Map options = [:], String url, String driver) {
        this.url = url
        this.driver = driver
        this.user = options.get("user","")
        this.password = options.get("password","")
        this.metadataTable = options.get("metadataTable","metadata")
        this.tilesTable = options.get("tilesTable", "tiles")
        this.bounds = mercatorBounds
        this.proj = mercatorProj
        sql = Sql.newInstance(url, user, password, driver)
    }

    /**
     * Open an existing DBTiles Layer
     * @param options Optional named parameters
     * <ul>
     *     <li>metadataTable = The name of the metadata table (metadata by default)</li>
     *     <li>tilesTable = The name of the tiles table (tiles by default)</li>
     * </ul>
     * @param dataSource The SQL DataSource
     */
    DBTiles(Map options = [:], DataSource dataSource) {
        this.metadataTable = options.get("metadataTable","metadata")
        this.tilesTable = options.get("tilesTable", "tiles")
        this.bounds = mercatorBounds
        this.proj = mercatorProj
        sql = new Sql(dataSource.connection)
    }

    /**
     * Create a new DBTiles
     * @param options The optional named parameters
     * <ul>
     *     <li>binaryType = The name of the binary type (blob by default, but should be bytea for postgresql)</li>
     *     <li>user = The database user (empty string by default)</li>
     *     <li>password = The database password (empty string by default)</li>
     *     <li>metadataTable = The name of the metadata table (metadata by default)</li>
     *     <li>tilesTable = The name of the tiles table (tiles by default)</li>
     *     <li>type = The type of layer (base_layer is the default or overlay)</li>
     *     <li>version = The version number (1.0 is the default)</li>
     *     <li>format = The image format (png is the default or jpeg)</li>
     *     <li>attribution = The attributes</li>
     * </ul>
     * @param url The JDBC URL
     * @param driver The JDBC driver class name
     * @param name The name of the layer
     * @param description The description of the layer
     */
    DBTiles(Map options = [:], String url, String driver, String name, String description) {
        this.url = url
        this.driver = driver
        this.user = options.get("user","")
        this.password = options.get("password","")
        sql = Sql.newInstance(url, user, password, driver)
        initNewDBTiles(options, name, description)
    }

    /**
     * Create a new DBTiles from a SQL DataSource
     * @param options The optional named parameters
     * <ul>
     *     <li>binaryType = The name of the binary type (blob by default, but should be bytea for postgresql)</li>
     *     <li>metadataTable = The name of the metadata table (metadata by default)</li>
     *     <li>tilesTable = The name of the tiles table (tiles by default)</li>
     *     <li>type = The type of layer (base_layer is the default or overlay)</li>
     *     <li>version = The version number (1.0 is the default)</li>
     *     <li>format = The image format (png is the default or jpeg)</li>
     *     <li>attribution = The attributes</li>
     * </ul>
     * @param dataSource The SQL DataSource
     * @param name The name of the layer
     * @param description The description of the layer
     */
    DBTiles(Map options = [:], DataSource dataSource, String name, String description) {
        sql = new Sql(dataSource.connection)
        initNewDBTiles(options, name, description)
    }

    private initNewDBTiles(Map options, String name, String description) {
        this.metadataTable = options.get("metadataTable","metadata")
        this.tilesTable = options.get("tilesTable", "tiles")
        String binaryType = options.get("binaryType", "blob")
        this.bounds = mercatorBounds
        this.proj = mercatorProj
        this.name = name

        String type = options.get("type", "baselayer")
        String version = options.get("version", "1.0")
        String format = options.get("format", "png")
        String attribution = options.get("attribution","Created with GeoScript")

        sql.execute "CREATE TABLE IF NOT EXISTS ${metadataTable} (name text, value text);".toString()
        sql.execute "CREATE TABLE IF NOT EXISTS ${tilesTable} (zoom_level integer, tile_column integer, tile_row integer, tile_data ${binaryType});".toString()

        [
                name        : name,
                type        : type,
                version     : version,
                description : description,
                format      : format,
                bounds      : latLonBounds,
                attribution : attribution
        ].each { String key, Object value ->
            if (value instanceof Bounds) {
                value = "${value.minX},${value.minY},${value.maxX},${value.maxY}".toString()
            }
            if (sql.rows("SELECT value FROM ${metadataTable} WHERE name = ?".toString(),[key]).size() == 0) {
                sql.execute "INSERT INTO ${metadataTable} (name, value) VALUES (?,?)".toString(), [key, value]
            } else {
                sql.execute "UPDATE ${metadataTable} SET value = ? WHERE name = ?".toString(), [value, key]
            }
        }
    }

    /**
     * Get metadata (type, name, description, format, version, attribution, bounds)
     * @return A Map of metadata
     */
    Map<String, String> getMetadata() {
        Map<String, String> data = [:]
        sql.rows("SELECT name, value FROM ${metadataTable}".toString()).each {
            String name = getValue(it, 'name')
            String value = getValue(it, 'value')
            data[name] = value
        }
        data
    }

    private String getValue(GroovyRowResult result, String key) {
        Object obj = result[key]
        if (obj instanceof org.h2.jdbc.JdbcClob) {
            obj = obj.characterStream.text
        }
        obj?.toString()
    }

    /**
     * Get the number of tiles per zoom level.
     * @return A List of Maps with zoom, tiles, total, and percent keys
     */
    List<Map> getTileCounts() {
        List stats = []
        sql.eachRow("select count(*) as num_tiles, zoom_level from ${tilesTable} group by zoom_level order by zoom_level".toString(), { def row ->
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
        sql.eachRow("select max(zoom_level) as max_zoom_level from ${tilesTable}".toString(), {  def row ->
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
        sql.eachRow("select min(zoom_level) as min_zoom_level from ${tilesTable}".toString(), {  def row ->
            min = row.min_zoom_level
        })
        min
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
        ImageTile imageTile = new ImageTile(z,x,y)
        def rows = sql.rows("SELECT tile_data FROM ${tilesTable} WHERE zoom_level = ? AND tile_column = ? AND tile_row = ?".toString(), [z, x, y])
        if (rows.size() > 0) {
            Object bytes = rows[0].tile_data
            if (bytes instanceof org.h2.jdbc.JdbcBlob) {
                bytes = bytes.getBytes(0 as long, bytes.length() as int)
            }
            imageTile = new ImageTile(z, x, y, bytes as byte[])
        }
        imageTile
    }

    @Override
    void put(ImageTile t) {
        if (sql.rows("SELECT zoom_level FROM ${tilesTable} WHERE zoom_level = ? AND tile_column = ? AND tile_row = ?".toString(), [t.z, t.x, t.y]).size() == 0) {
            sql.execute "INSERT INTO ${tilesTable} (zoom_level, tile_column, tile_row, tile_data) VALUES (?,?,?,?)".toString(), [t.z, t.x, t.y, t.data]
        } else {
            sql.execute "UPDATE ${tilesTable} SET tile_data = ? WHERE zoom_level = ? AND tile_column = ? AND tile_row = ?".toString(), [t.data, t.z, t.x, t.y]
        }
    }

    @Override
    void delete(ImageTile t) {
        sql.execute "DELETE FROM ${tilesTable} WHERE zoom_level = ? AND tile_column = ? AND tile_row = ?".toString(), [t.z, t.x, t.y]
    }

    @Override
    void close() throws IOException {
        sql.close()
    }

    /**
     * The DBTiles TileLayerFactory
     */
    static class Factory extends TileLayerFactory<DBTiles> {

        @Override
        DBTiles create(Map params) {
            String type = params.get("type","").toString()
            if (type.equalsIgnoreCase("dbtiles")) {
                Map dbTilesParams = [:]
                dbTilesParams.putAll(params)
                dbTilesParams.type = "baselayer"
                String url = params.url
                String driver = params.driver
                String name = params.name
                String description = params.description
                if (!name && !description) {
                    new DBTiles(dbTilesParams, url, driver)
                } else {
                    new DBTiles(dbTilesParams, url, driver, name, description)
                }
            } else {
                null
            }
        }

        @Override
        TileRenderer getTileRenderer(Map options, TileLayer tileLayer, List<Layer> layers) {
            if (tileLayer instanceof DBTiles) {
                new ImageTileRenderer(tileLayer, layers)
            } else {
                null
            }
        }
    }

}
