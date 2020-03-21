package geoscript.layer

import geoscript.feature.Feature
import geoscript.feature.Field
import geoscript.geom.Bounds
import geoscript.layer.io.Pbf
import geoscript.layer.io.Reader
import geoscript.layer.io.Writer
import geoscript.layer.io.Readers
import geoscript.layer.io.Writers
import geoscript.proj.Projection
import geoscript.style.Style
import geoscript.workspace.Memory
import geoscript.workspace.Workspace
import groovy.sql.Sql
import org.geotools.map.FeatureLayer
import org.geotools.mbtiles.MBTilesFile
import org.geotools.mbtiles.MBTilesMetadata
import org.geotools.mbtiles.MBTilesTile
import org.geotools.util.logging.Logging

import java.util.logging.Level
import java.util.logging.Logger

/**
 * A TileLayer for VectorTiles
 * @author Jared Erickson
 */
class VectorTiles extends TileLayer<Tile> implements Renderable {

    /**
     * The File directory
     */
    File dir

    /**
     * The base URL
     */
    URL url

    /**
     * The MBTiles File
     */
    File file

    /**
     * The TileStore used to get, put, and delete Tiles
     */
    private TileStore tileStore

    /**
     * The Pyramid structure
     */
    Pyramid pyramid

    /**
     * The vector tile type (json, pbf)
     */
    String type

    /**
     * The Layer Reader
     */
    Reader reader

    /**
     * Either a Style or a Map of Styles by Layer name
     */
    def style

    /**
     * The Projection of the Layers
     */
    Projection proj

    /**
     * The Logger
     */
    private static final Logger LOGGER = Logging.getLogger("geoscript.layer.VectorTiles");

    /**
     * Create a new VectorTiles TileLayer for a directory
     * @param options The optional named parameters
     * <ul>
     *     <li>proj = The Projection of the vector Layers</li>
     *     <li>style = A Style or a Map of Styles with the Layer name as key</li>
     * </ul>
     * @param name The name of the TileLayer
     * @param file The directory of the Tiles or the MBTiles File
     * @param pyramid The Pyramid
     * @param type The type (pbf, mvt, geojson, kml)
     */
    VectorTiles(Map options = [:], String name, File file, Pyramid pyramid, String type) {
        this.name = name
        this.pyramid = pyramid
        this.bounds = this.pyramid.bounds
        this.type = type
        this.reader = getReaderForType(type)
        this.proj = options.get("proj", pyramid.proj)
        this.style = options.get("style")
        if (file.isDirectory()) {
            this.dir = file
            this.tileStore = new DirectoryTileStore(this.dir, this.type)
        } else {
            this.file = file
            this.tileStore = new MBTilesTileStore(this.file, this.name, "Vector Tiles", format: this.type)
        }
    }

    /**
     * Create a new VectorTiles TileLayer for a URL
     * @param options The optional named parameters
     * <ul>
     *     <li>proj = The Projection of the vector Layers</li>
     *     <li>style = A Style or a Map of Styles with the Layer name as key</li>
     * </ul>
     * @param name The name of the TileLayer
     * @param url The base URL
     * @param pyramid The Pyramid
     * @param type The type (pbf, mvt, geojson, kml)
     */
    VectorTiles(Map options = [:], String name, URL url, Pyramid pyramid, String type) {
        this.name = name
        this.url = url
        this.pyramid = pyramid
        this.bounds = this.pyramid.bounds
        this.type = type
        this.reader = getReaderForType(type)
        this.proj = options.get("proj", pyramid.proj)
        this.style = options.get("style")
        this.tileStore = new UrlTileStore(this.url, this.type)
    }

    private Reader getReaderForType(String type) {
        if (type.equalsIgnoreCase("json")) {
            type = "geojson"
        }
        Readers.find(type)
    }

    /**
     * Get the Pyramid
     * @return The Pyramid
     */
    @Override
    Pyramid getPyramid() {
        pyramid
    }

    /**
     * Get a Tile
     * @param z The zoom level
     * @param x The column
     * @param y The row
     * @return A Tile
     */
    @Override
    Tile get(long z, long x, long y) {
        tileStore.get(z,x,y)
    }

    /**
     * Add a Tile
     * @param t The Tile
     */
    @Override
    void put(Tile t) {
        tileStore.put(t)
    }

    /**
     * Delete a Tile
     * @param t The Tile
     */
    @Override
    void delete(Tile t) {
        tileStore.delete(t)
    }

    /**
     * Close the TileLayer
     */
    @Override
    void close() throws IOException {
        tileStore.close()
    }

    private static interface TileStore {
        Tile get(long z, long x, long y)
        void put(Tile t)
        void delete(Tile t)
        void close() throws IOException
    }

    private static class DirectoryTileStore implements TileStore {

        private File dir

        private String type

        DirectoryTileStore(File dir, String type) {
            this.dir = dir
            this.type = type
        }

        @Override
        Tile get(long z, long x, long y) {
            Tile tile = new Tile(z, x, y)
            File file = new File(new File(new File(this.dir, String.valueOf(z)), String.valueOf(x)), "${y}.${type}")
            if (file.exists()) {
                tile.data = file.bytes
            }
            tile
        }

        @Override
        void put(Tile t) {
            File file = new File(new File(new File(this.dir, String.valueOf(t.z)), String.valueOf(t.x)), "${t.y}.${type}")
            if (!file.parentFile.exists()) {
                file.parentFile.mkdirs()
            }
            file.withOutputStream { out ->
                out.write(t.data)
            }
        }

        @Override
        void delete(Tile t) {
            File file = new File(new File(new File(this.dir, String.valueOf(t.z)), String.valueOf(t.x)), "${t.y}.${type}")
            if (file.exists()) {
                file.delete()
            }
        }

        @Override
        void close() throws IOException {
        }
    }

    private static class UrlTileStore implements TileStore {

        private URL url

        private String type

        UrlTileStore(URL url, String type) {
            this.url = url
            this.type = type
        }

        @Override
        Tile get(long z, long x, long y) {
            Tile tile = new Tile(z, x, y)
            String urlString = url.toString()
            URL tileUrl = new URL("${urlString}${urlString.endsWith("/") ? '' : '/'}${z}/${x}/${y}.${type}")
            tileUrl.withInputStream { input ->
                tile.data = input.bytes
            }
            tile
        }

        @Override
        void put(Tile t) {
            throw new IllegalArgumentException("Vector Tiles with URL are ready only!")
        }

        @Override
        void delete(Tile t) {
            throw new IllegalArgumentException("Vector Tiles with URL are ready only!")
        }

        @Override
        void close() throws IOException {
        }
    }

    private static class MBTilesTileStore implements TileStore {

        /**
         * The MBTiles File
         */
        private final File file

        /**
         * The GeoTools MBTilesFile
         */
        MBTilesFile tiles

        /**
         * The name
         */
        String name

        /**
         * The Bounds
         */
        Bounds bounds

        /**
         * The Projection
         */
        Projection proj

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
         * Create a new MBTilesTileStore with a existing File
         * @param file The existing File
         */
        MBTilesTileStore(File file) {
            this.file = file
            this.tiles = new MBTilesFile(file)
            this.bounds = mercatorBounds
            this.proj = mercatorProj
            this.name = this.tiles.loadMetaData().name
        }

        /**
         * Create a new MBTilesTileStore with a existing File
         * @param file The existing File name
         */
        MBTilesTileStore(String file) {
            this(new File(file))
        }

        /**
         * Create a new MBTilesTileStore with a new File
         * @param options The optional named parameters
         * <ul>
         *     <li>type = The type of layer (base_layer is the default or overlay)</li>
         *     <li>version = The version number (1.0 is the default)</li>
         *     <li>format = The image format (png is the default, jpeg, or pbf)</li>
         *     <li>attribution = The attributes</li>
         * </ul>
         * @param file The new File
         * @param name The name of the layer
         * @param description The description of the layer
         */
        MBTilesTileStore(java.util.Map options = [:], File file, String name, String description) {

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
         * Create a new MBTilesTileStore with a new File
         * @param options The optional named parameters
         * <ul>
         *     <li>type = The type of layer (base_layer is the default or overlay)</li>
         *     <li>version = The version number (1.0 is the default)</li>
         *     <li>format = The image format (png is the default, jpeg, or pbf)</li>
         *     <li>attribution = The attributes</li>
         * </ul>
         * @param file The new File name
         * @param name The name of the layer
         * @param description The description of the layer
         */
        MBTilesTileStore(java.util.Map options = [:], String fileName, String name, String description) {
            this(options, new File(fileName), name, description)
        }

        Pyramid getPyramid() {
            if (!this.pyramid) {
                this.pyramid = Pyramid.createGlobalMercatorPyramid()
            }
            this.pyramid
        }

        @Override
        Tile get(long z, long x, long y) {
            MBTilesTile t = tiles.loadTile(z, x, y)
            new Tile(t.zoomLevel, t.tileColumn, t.tileRow, t.data)
        }

        @Override
        void put(Tile t) {
            MBTilesTile tile = new MBTilesTile(t.z, t.x, t.y)
            tile.data = t.data
            tiles.saveTile(tile)
        }

        @Override
        void delete(Tile t) {
            getSql().execute("DELETE FROM tiles WHERE zoom_level = ? AND tile_column = ? AND tile_row = ?", [t.z, t.x, t.y])
        }

        /**
         * Delete all Tiles in the TileCursor
         * @param tiles The TileCursor
         */
        void delete(TileCursor<Tile> tiles) {
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
    }

    /**
     * Get a List of Layers for the Tiles in the TileCursor
     * @param cursor The TileCursor
     * @return A List of Layers
     */
    List<Layer> getLayers(TileCursor<Tile> cursor) {
        Map layers = [:]
        if (!cursor.empty) {
            cursor.each { Tile t ->
                if (type.equalsIgnoreCase("pbf")) {
                    try {
                        List<Layer> layerList = Pbf.read(t.data, cursor.tileLayer.pyramid.bounds(t))
                        layerList.each { Layer tileLayer ->
                            // Create Schema if necessary
                            if (!layers.containsKey(tileLayer.name)) {
                                Workspace workspace = new Memory()
                                Layer layer = workspace.create(tileLayer.schema.reproject(this.proj, tileLayer.name))
                                layers.put(tileLayer.name, layer)
                            }
                            // Add Features
                            Layer layer = layers.get(tileLayer.name)
                            layer.withWriter { geoscript.layer.Writer w ->
                                tileLayer.eachFeature { Feature f ->
                                    w.add(f)
                                }
                            }
                        }
                    } catch (Exception ex) {
                        LOGGER.log(Level.WARNING, "Can't read ${t}!", ex)
                    }
                } else {
                    try {
                        Layer tileLayer = reader.read(new ByteArrayInputStream(t.data))
                        // Create Schema if necessary
                        if (layers.isEmpty()) {
                            Workspace workspace = new Memory()
                            Layer layer = workspace.create(tileLayer.schema.reproject(this.proj, this.name))
                            layers.put(this.name, layer)
                        }
                        // Add Features
                        Layer layer = layers.get(this.name)
                        layer.withWriter { geoscript.layer.Writer w ->
                            tileLayer.eachFeature { Feature f ->
                                w.add(f)
                            }
                        }
                    } catch (Exception ex) {
                        LOGGER.log(Level.WARNING, "Can't read ${t}!", ex)
                    }
                }
            }
        }
        // Add styles
        layers.each { String name, Layer layer ->
            if (style) {
                if (style instanceof Style) {
                    layer.style = style
                } else {
                    layer.style = style[name]
                }
            }
        }
        layers.values().collect { it }
    }

    /**
     * The VectorTiles TileLayerFactory
     */
    static class Factory extends TileLayerFactory<VectorTiles> {

        @Override
        VectorTiles create(Map params) {
            String type = params.get("type","").toString()
            if (type.equalsIgnoreCase("vectortiles")) {
                String name = params.get("name", "vectortiles")
                Object p = params.get("pyramid", Pyramid.createGlobalMercatorPyramid())
                Pyramid pyramid = p instanceof Pyramid ? p as Pyramid : Pyramid.fromString(p as String)
                String format = params.get("format", "pbf")
                if (params.containsKey("file")) {
                    File file = params["file"] instanceof File ? params["file"] as File : new File(params["file"])
                    new VectorTiles(name, file, pyramid, format)
                } else {
                    URL url = params["url"] instanceof URL ? params["url"] as URL : new URL(params["url"])
                    new VectorTiles(name, url, pyramid, format)
                }
            } else {
                null
            }
        }

        @Override
        TileRenderer getTileRenderer(Map options, TileLayer tileLayer, List<Layer> layers) {
            if (tileLayer instanceof VectorTiles) {
                VectorTiles vectorTiles = tileLayer as VectorTiles
                if (vectorTiles.type.equalsIgnoreCase("pbf")) {
                    Map<String, List> fields = options.get("fields", [:])
                    if (fields.isEmpty()) {
                        layers.each { Layer layer ->
                            fields[(layer.name)] = layer.schema.fields
                        }
                    }
                    new PbfVectorTileRenderer(layers, fields)
                } else {
                    String type = vectorTiles.type
                    if (type.equalsIgnoreCase("json")) {
                        type = "geojson"
                    }
                    Writer layerWriter = Writers.find(type)
                    Layer layer = layers[0]
                    List<Field> fields = options.fields ?
                            options.fields.collect {
                                it instanceof Field ? it : layer.schema.get(it)
                            } : layer.schema.fields
                    new VectorTileRenderer(layerWriter, layer, fields)
                }
            } else {
                null
            }
        }
    }

    @Override
    List<org.geotools.map.Layer> getMapLayers(Bounds bounds, List size) {
        this.getLayers(this.tiles(bounds.reproject(this.proj), size[0], size[1])).collect { Layer lyr ->
            new FeatureLayer(lyr.fs, lyr.style.gtStyle)
        }
    }
}
