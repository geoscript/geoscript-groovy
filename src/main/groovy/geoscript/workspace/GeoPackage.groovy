package geoscript.workspace

import geoscript.geom.Bounds
import org.geotools.data.DataStore
import org.geotools.geopkg.GeoPackage as GtGeoPackage
import org.geotools.geopkg.GeoPkgDataStoreFactory
import org.geotools.geopkg.TileEntry
import org.geotools.geopkg.TileMatrix
import org.geotools.geopkg.TileReader
import org.geotools.geopkg.mosaic.GeoPackageFormat

/**
 * A GeoPackage Workspace.
 * https://github.com/opengis/geopackage/
 * http://www.gdal.org/ogr/drv_geopackage.html
 * @author Jared Erickson
 */
class GeoPackage {

    /**
     * The GeoPackage database file
     */
    private final File file

    /**
     * The user name
     */
    private final String userName

    /**
     * The password
     */
    private final String password

    /**
     * The GeoPackage.Workspace
     */
    private GeoPackage.Workspace workspace

    /**
     * The GeoPackage.Format
     */
    private GeoPackage.Format format

    /**
     * The GeoPackage.Tiles
     */
    private GeoPackage.Tiles tiles

    /**
     * Create a new GeoPackage Workspace with a name and directory
     * @param name The name of the database
     * @param dir The File containing the database
     */
    GeoPackage(String name, File dir, String userName = null, String password = null) {
        this(new File(dir, name), userName, password)
    }

    /**
     * Create a new GeoPackage Workspace with a name and directory
     * @param name The name of the database
     * @param dir The File containing the database
     */
    GeoPackage(String name, String dir, String userName = null, String password = null) {
        this(new File(dir, name), userName, password)
    }

    /**
     * Create a new GeoPackage Workspace from a database file
     * @param file The GeoPackage database file
     */
    GeoPackage(File file, String userName = null, String password = null) {
        this.file = file
        this.userName = userName
        this.password = password
    }

    /**
     * Get the GeoPackage.Workspace for this GeoPackage database
     * in order to access vector Layers
     * @return The GeoPackage.Workspace
     */
    GeoPackage.Workspace getWorkspace() {
        if (!workspace) {
            this.workspace = new GeoPackage.Workspace(this.file, this.userName, this.password)
        }
        workspace
    }

    /**
     * Get the GeoPackage.Format for this GeoPackage database
     * in order to access Rasters
     * @return The GeoPackage.Format
     */
    GeoPackage.Format getFormat() {
        if (!format) {
            this.format = new GeoPackage.Format(this.file)
        }
    }

    /**
     * The GeoPackage Format for accessing Rasters
     */
    static class Format extends geoscript.layer.Format {

        Format(def stream) {
            // @TODO need someway to pass in user name and password
            super(new GeoPackageFormat(), stream)
        }

    }

    /**
     * The GeoPackage Workspace
     */
    static class Workspace extends Database {

        /**
         * Create a new GeoPackage Workspace with a name and directory
         * @param name The name of the database
         * @param dir The File containing the database
         */
        Workspace(String name, File dir, String userName = null, String password = null) {
            super(createDataStore(name, dir, userName, password))
        }

        /**
         * Create a new GeoPackage Workspace with a name and directory
         * @param name The name of the database
         * @param dir The File containing the database
         */
        Workspace(String name, String dir, String userName = null, String password = null) {
            this(name, new File(dir).absoluteFile, userName, password)
        }

        /**
         * Create a new GeoPackage Workspace from a database file
         * @param file The GeoPackage database file
         */
        Workspace(File file, String userName = null, String password = null) {
            this(file.name, file.parentFile, userName, password)
        }

        /**
         * Get the format
         * @return The workspace format name
         */
        @Override
        String getFormat() {
            return "GeoPackage"
        }

        /**
         * Create a new GeoPackage Workspace with a name and directory
         */
        private
        static DataStore createDataStore(String name, File dir, String userName = null, String password = null) {
            Map params = new java.util.HashMap()
            params.put("database", new File(dir, name).absolutePath)
            params.put("dbtype", "geopkg")
            params.put("user", userName)
            params.put("passwd", password)
            GeoPkgDataStoreFactory factory = new GeoPkgDataStoreFactory()
            factory.createDataStore(params)
        }
    }

    /**
     * Get a GeoPackage.Tiles object for this GeoPackage
     * @return A GeoPackage.Tiles for this GeoPackage
     */
    GeoPackage.Tiles getTiles() {
        if (!tiles) {
            tiles = new GeoPackage.Tiles(this.file)
        }
        tiles
    }

    /**
     * The Tiles class is wrapper around GeoPackages Tile Support
     */
    static class Tiles implements Closeable {

        /**
         * The GeoTools GeoPackage
         */
        private GtGeoPackage geopackage

        /**
         * Create a new Tiles object with a GeoPackage database file
         * @param file The database file
         */
        Tiles(File file) {
            this.geopackage = new GtGeoPackage(file)
            this.geopackage.init()
        }

        @Override
        void close() throws IOException {
            this.geopackage.close()
        }

        /**
         * Start building an Entry.
         * @param name The name
         * @param bounds The Bounds
         * @return An Entry.Builder
         */
        EntryBuilder addEntry(String name, Bounds bounds) {
            EntryBuilder entryBuilder = new EntryBuilder().tiles(this).name(name).bounds(bounds)
            entryBuilder
        }

        /**
         * Get a List of all Entries in the database
         * @return A List of all Entries
         */
        List getEntries() {
            List list = []
            eachEntry { Entry e ->
                list.add(e)
            }
            list
        }

        /**
         * Call the Closure for each Entry in the database
         * @param c A Closure which takes an Entry
         */
        void eachEntry(Closure c) {
            geopackage.tiles().each { c.call(new Entry(this, it)) }
        }

        /**
         * Get an Entry by name
         * @param name The name
         * @return An Entry or null
         */
        def getEntry(String name) {
            TileEntry tileEntry = geopackage.tile(name)
            tileEntry != null ? new Entry(this, tileEntry) : null
        }
    }

    /**
     * An Entry is a named set of tiles at different zoom levels
     */
    static class Entry {

        /**
         * The GeoTools TileEntry
         */
        private TileEntry tileEntry

        /**
         * The GeoScript Tiles object that this Entry
         * belongs to
         */
        private Tiles tiles

        /**
         * Create a new Entry
         * @param tiles The GeoScript Tiles this Entry belongs to
         * @param entry The GeoTools TileEntry this Entry wraps
         */
        Entry(Tiles tiles, TileEntry entry) {
            this.tileEntry = entry
            this.tiles = tiles
        }

        /**
         * Get the name
         * @return the name
         */
        String getName() {
            tileEntry.tableName
        }

        /**
         * Get the Bounds
         * @return The Bounds
         */
        Bounds getBounds() {
            new Bounds(tileEntry.bounds)
        }

        /**
         * Get the description
         * @return The Description
         */
        String getDescription() {
            tileEntry.description
        }

        /**
         * Get the ID
         * @return The ID
         */
        String getId() {
            tileEntry.identifier
        }

        /**
         * Get the Date this Entry was last changed
         * @return The last changed Date
         */
        Date getLastChange() {
            tileEntry.lastChange
        }

        /**
         * Get a List of this Entry's Matrices or zoom levels
         * @return A List of Matrix objects
         */
        List<Matrix> getMatricies() {
            tileEntry.tileMatricies.collect { new Matrix(it) }
        }

        /**
         * Call the Closure for each Matrix in this Entry
         * @param c The Closure that takes a Matrix as a parameters
         */
        void eachMatrix(Closure c) {
            tileEntry.tileMatricies.each { c.call(new Matrix(it)) }
        }

        /**
         * Add a Tile to this Entry
         * @param zoom The zoom level
         * @param col The column
         * @param row The row
         * @param data The data as a byte array
         * @return This Entry
         */
        Entry addTile(int zoom, int col, int row, byte[] data) {
            Tile tile = new Tile(zoom, col, row, data)
            tiles.geopackage.add(this.tileEntry, tile.tile)
            this
        }

        /**
         * Get a List of all Tiles that belong to this Entry
         * @param options The optional named parameters
         * <ul>
         *     <li>lowZoom</li>
         *     <li>highZoom</li>
         *     <li>lowCol</li>
         *     <li>highCol</li>
         *     <li>lowRow</li>
         *     <li>highRow</li>
         * </ul>
         * @return A List of Tiles
         */
        List getTiles(Map options = [:]) {
            List list = []
            eachTile(options, { list.add(it) })
            list
        }

        /**
         * Call the given Closure for each Tile
         * @param options The optional named parameters
         * <ul>
         *     <li>lowZoom</li>
         *     <li>highZoom</li>
         *     <li>lowCol</li>
         *     <li>highCol</li>
         *     <li>lowRow</li>
         *     <li>highRow</li>
         * </ul>
         * @param c A Closure that take a Tile as a parameter
         */
        void eachTile(Map options = [:], Closure c) {
            TileReader r = tiles.geopackage.reader(tileEntry, options["lowZoom"], options["highZoom"],
                    options["lowCol"], options["highCol"],
                    options["lowRow"], options["highRow"])
            r.each { c.call(new Tile(it)) }
            r.close()
        }

        @Override
        String toString() {
            name
        }
    }
    /**
     * A EntryBuilder class for building Entries
     */
    static class EntryBuilder {

        /**
         * The name
         */
        String name

        /**
         * The geographic Bounds (with Projection)
         */
        Bounds bounds

        /**
         * The GeoScript Tiles object that this Entry
         * belongs to
         */
        Tiles tiles

        /**
         * A List of zoom levels of Matrices
         */
        List<Matrix> matrices = []

        /**
         * Set the name
         * @param name The name
         * @return This EntryBuilder
         */
        EntryBuilder name(String name) {
            this.name = name
            this
        }

        /**
         * Set the Bounds
         * @param bounds The Bounds
         * @return This EntryBuilder
         */
        EntryBuilder bounds(Bounds bounds) {
            this.bounds = bounds
            this
        }

        /**
         * Set the Tiles this Entry will belong to
         * @param name The Tiles
         * @return This EntryBuilder
         */
        EntryBuilder tiles(Tiles tiles) {
            this.tiles = tiles
            this
        }

        /**
         * Add a Matrix or zoom level
         * @param zoom The zoom level (0 based)
         * @param width The width
         * @param height The height
         * @param tileWidth The tile width
         * @param tileHeight The tile height
         * @param xPixel The x pixel size
         * @param yPixel The y pixel size
         * @return This EntryBuilder
         */
        EntryBuilder matrix(int zoom, int width, int height, int tileWidth, int tileHeight, double xPixel, double yPixel) {
            Matrix matrix = new Matrix(zoom, width, height, tileWidth, tileHeight, xPixel, yPixel)
            matrices.add(matrix)
            this
        }

        /**
         * Build and return the Entry
         * @return A new Entry
         */
        Entry build() {
            Entry entry = new Entry(this.tiles, new TileEntry(tableName: this.name, bounds: this.bounds.env))
            this.matrices.each { Matrix m ->
                entry.tileEntry.tileMatricies.add(m.tilesMatrix)
            }
            tiles.geopackage.create(entry.tileEntry)
            entry
        }

    }

    /**
     * A Matrix represents a zoom level of tiles
     */
    static class Matrix {

        /**
         * The GeoTools TileMatrix
         */
        private TileMatrix tilesMatrix

        /**
         * Create a new Matrix
         * @param tilesMatrix The GeoTools TileMatrix
         */
        Matrix(TileMatrix tilesMatrix) {
            this.tilesMatrix = tilesMatrix
        }

        /**
         * Create a new TileMatrix
         * @param zoom The zoom level
         * @param width The width
         * @param height The height
         * @param tileWidth The tile width
         * @param tileHeight The tile height
         * @param xPixel The x pixel size
         * @param yPixel The y pixel size
         */
        Matrix(int zoom, int width, int height, int tileWidth, int tileHeight, double xPixel, double yPixel) {
            this(new TileMatrix(zoom, width, height, tileWidth, tileHeight, xPixel, yPixel))
        }

        /**
         * Get the zoom level
         * @return The zoom level
         */
        int getZoom() {
            tilesMatrix.zoomLevel
        }

        /**
         * Get the width or number of columns
         * @return The width
         */
        int getWidth() {
            tilesMatrix.matrixWidth
        }

        /**
         * Get the height or number or rows
         * @return The height
         */
        int getHeight() {
            tilesMatrix.matrixHeight
        }

        /**
         * Get the tile width
         * @return The tile width
         */
        int getTileWidth() {
            tilesMatrix.tileWidth
        }

        /**
         * Get the tile height
         * @return The tile height
         */
        int getTileHeight() {
            tilesMatrix.tileHeight
        }

        /**
         * Get the x pixel size
         * @return The x pixel size
         */
        double getXPixel() {
            tilesMatrix.getXPixelSize()
        }

        /**
         * Get the y pixel size
         * @return The y pixel size
         */
        double getYPixel() {
            tilesMatrix.getYPixelSize()
        }

        @Override
        String toString() {
            "Zoom Level: ${zoom}, Width/Height${width}x${height}, Tile: ${tileWidth}x${tileHeight}, Pixel: ${getXPixel()}x${getYPixel()}"
        }
    }

    /**
     * A Tile represents a single tile at a particular zoom level, row, and column
     */
    static class Tile {

        /**
         * The GeoTools Tile
         */
        private org.geotools.geopkg.Tile tile

        /**
         * Create a new Tile
         * @param tile The GeoTools Tile
         */
        Tile(org.geotools.geopkg.Tile tile) {
            this.tile = tile
        }

        /**
         * Create a new Tile
         * @param zoom The zoom level
         * @param col The column
         * @param row The row
         * @param data The data as an array of bytes
         */
        Tile(int zoom, int col, int row, byte[] data) {
            this(new org.geotools.geopkg.Tile(zoom: zoom, column: col, row: row, data: data))
        }

        /**
         * Get the column
         * @return The column
         */
        int getColumn() {
            tile.column
        }

        /**
         * Get the row
         * @return The row
         */
        int getRow() {
            tile.row
        }

        /**
         * Get the zoom level
         * @return The zoom level
         */
        int getZoom() {
            tile.zoom
        }

        /**
         * Get the data
         * @return An array of bytes
         */
        byte[] getData() {
            tile.data
        }

        @Override
        String toString() {
            "${zoom}/${column}/${row}"
        }
    }

}
