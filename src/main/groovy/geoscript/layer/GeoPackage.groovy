package geoscript.layer

import geoscript.geom.Bounds
import geoscript.proj.Projection
import org.geotools.geopkg.TileEntry
import org.geotools.geopkg.TileMatrix
import org.geotools.geopkg.TileReader

/**
 * A GeoPackage TileLayer
 * @author Jared Erickson
 */
class GeoPackage extends TileLayer {

    /**
     * The MBTiles File
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
                    origin: Pyramid.Origin.BOTTOM_LEFT,
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
    Tile get(long z, long x, long y) {
        Tile t = null
        TileReader tileReader = this.geopkg.reader(this.tileEntry,
                z as Integer, z as Integer,
                x as Integer, x as Integer,
                y as Integer, y as Integer)
        try {
            if (tileReader.hasNext()) {
                org.geotools.geopkg.Tile tile = tileReader.next()
                t = new Tile(tile.zoom, tile.column, tile.row, tile.data)
            } else {
                t = new Tile(z, x, y)
            }
        } finally {
            tileReader.close()
        }
        t
    }

    @Override
    void put(Tile t) {
        this.geopkg.add(this.tileEntry,
                new org.geotools.geopkg.Tile(t.z as Integer, t.x as Integer, t.y as Integer, t.data))
    }

    @Override
    void close() throws IOException {
        this.geopkg.close()
    }
}
