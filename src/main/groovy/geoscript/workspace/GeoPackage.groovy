package geoscript.workspace

import geoscript.geom.Bounds
import org.geotools.data.DataStore
import org.geotools.geopkg.GeoPkgDataStoreFactory
import org.geotools.geopkg.TileEntry
import org.geotools.geopkg.TileMatrix
import org.geotools.geopkg.TileReader
import org.geotools.geopkg.mosaic.GeoPackageFormat
import  org.geotools.geopkg.GeoPackage as GtGeoPackage

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

    private final String userName

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
        private static DataStore createDataStore(String name, File dir, String userName = null, String password = null) {
            Map params = new java.util.HashMap()
            params.put("database", new File(dir, name).absolutePath)
            params.put("dbtype", "geopkg")
            params.put("user", userName)
            params.put("passwd", password)
            GeoPkgDataStoreFactory factory = new GeoPkgDataStoreFactory()
            factory.createDataStore(params)
        }
    }

    private GeoPackage.Tiles tiles

    GeoPackage.Tiles getTiles() {
        if (!tiles) {
            tiles = new GeoPackage.Tiles(this.file)
        }
        tiles
    }

    // http://docs.geotools.org/latest/javadocs/org/geotools/geopkg/GeoPackage.html
    // https://github.com/geotools/geotools/blob/master/modules/unsupported/geopkg/src/test/java/org/geotools/geopkg/GeoPackageTest.java
    static class Tiles {

        /**
         * TileEntry(tableName, bounds, tileMatrices)
         * TileMatrix(zoom, width, height, tileWidth, tileHeight, xPixelSize, yPixelSize)
         * Tile()
         * TileReader r = geopkg.reader(tileEntry, lowZoom, highZoom, lowCol, highCol, lowRow, highRow)
         */

        private GtGeoPackage geopackage

        Tiles(File file) {
            this.geopackage = new GtGeoPackage(file)
            this.geopackage.init()
        }

        Entry addEntry(String name, Bounds bounds) {
            Entry entry = new Entry(this.geopackage, name, bounds)
            //geopackage.create(entry.tileEntry)
            entry
        }

        List getEntries() {
            geopackage.tiles().collect{new Entry(geopackage, it)}
        }

        void eachEntry(Closure c) {
            geopackage.tiles().collect{ c.call(new Entry(geopackage, it)) }
        }

        def getEntry(String name) {
            new Entry(geopackage, geopackage.tile(name))
        }

        static class Entry {

            private TileEntry tileEntry

            private GtGeoPackage geopackage

            Entry(GtGeoPackage geopackage, TileEntry entry) {
                this.geopackage = geopackage
                this.tileEntry = entry
            }

            Entry(GtGeoPackage geopackage, String name, Bounds bounds) {
                this(geopackage, new TileEntry(tableName: name, bounds: bounds.env))
            }

            String getName() {
                tileEntry.tableName
            }

            Bounds getBounds() {
                new Bounds(tileEntry.bounds)
            }

            String getDescription() {
                tileEntry.description
            }

            String getId() {
                tileEntry.identifier
            }

            Date getLastChange() {
                tileEntry.lastChange
            }

            List<Matrix> getMatricies() {
                tileEntry.tileMatricies.collect { new Matrix(it) }
            }

            void eachMatrix(Closure c) {
                tileEntry.tileMatricies.each { c.call(new Matrix(it)) }
            }

            Entry addMatrix(int zoom, int width, int height, int tileWidth, int tileHeight, double xPixel, double yPixel) {
                Matrix matrix = new Matrix(zoom, width, height, tileWidth, tileHeight, xPixel, yPixel)
                tileEntry.tileMatricies.add(matrix.matrix)
                this
            }

            Entry addTile(int zoom, int col, int row, byte[] data) {
                Tile tile = new Tile(zoom, col, row, data)
                geopackage.add(this.tileEntry, tile.tile)
                this
            }

            List getTiles(Map options = [:]) {
                List list = []
                eachTile(options, { list.add(it) })
                list
            }

            void eachTile(Map options = [:], Closure c) {
                TileReader r = geopackage.reader(tileEntry, options["lowZoom"], options["highZoom"],
                        options["lowCol"], options["highCol"],
                        options["lowRow"], options["highRow"])
                r.each{ c.call(new Tile(it)) }
                r.close()
            }

            String toString() {
                name
            }

            Entry create() {
                geopackage.create(tileEntry)
                this
            }

        }

        static class Matrix {

            private TileMatrix matrix

            Matrix(TileMatrix matrix) {
                this.matrix = matrix
            }

            Matrix(int zoom, int width, int height, int tileWidth, int tileHeight, double xPixel, double yPixel) {
                this(new TileMatrix(zoom, width, height, tileWidth, tileHeight, xPixel, yPixel))
            }

            int getZoom() {
                matrix.zoomLevel
            }

            int getWidth() {
                matrix.matrixWidth
            }

            int getHeight() {
                matrix.matrixHeight
            }

            int getTileWidth() {
                matrix.tileWidth
            }

            int getTileHeight() {
                matrix.tileHeight
            }

            double getXPixel() {
                matrix.getXPixelSize()
            }

            double getYPixel() {
                matrix.getYPixelSize()
            }

            String toString() {
                "${zoom} ${width}x${height} ${tileWidth}x${tileHeight} ${getXPixel()}x${getYPixel()}"
            }

        }

        static class Tile {

            private org.geotools.geopkg.Tile tile

            Tile(org.geotools.geopkg.Tile tile) {
                this.tile = tile
            }

            Tile(int zoom, int col, int row, byte[] data) {
                this(new org.geotools.geopkg.Tile(zoom: zoom, column:col, row: row, data: data))
            }

            int getColumn() {
                tile.column
            }

            int getRow() {
                tile.row
            }

            int getZoom() {
                tile.zoom
            }

            byte[] getData() {
                tile.data
            }

            String toString() {
                "${zoom}/${column}/${row}"
            }
        }
    }

}
