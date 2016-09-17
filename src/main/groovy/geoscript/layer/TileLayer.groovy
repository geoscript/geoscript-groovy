package geoscript.layer

import geoscript.feature.Field
import geoscript.geom.Bounds
import geoscript.proj.Projection
import geoscript.workspace.Memory
import geoscript.workspace.Workspace

/**
 * A TileLayer
 * @author Jared Erickson
 */
abstract class TileLayer<T extends Tile> implements Closeable {

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
     * Get the Pyramid
     * @return The Pyramid
     */
    abstract Pyramid getPyramid()

    /**
     * Get a Tile
     * @param z The zoom level
     * @param x The column
     * @param y The row
     * @return A Tile
     */
    abstract T get(long z, long x, long y)

    /**
     * Add a Tile
     * @param t The Tile
     */
    abstract void put(T t)

    /**
     * Delete a Tile
     * @param t The Tile
     */
    abstract void delete(T t)

    /**
     * Close the TileLayer
     */
    abstract void close() throws IOException

    /**
     * Get a TileCursor for all the Tiles in the zoom level
     * @param z The zoom level
     * @return A TileCursor
     */
    TileCursor<T> tiles(long z) {
        new TileCursor(this, z)
    }

    /**
     * Get a TileCursor for all the Tiles in the zoom level for the given columns and rows
     * @param z The zoom level
     * @param minX The min x or column
     * @param minY The min y or row
     * @param maxX The max x or column
     * @param maxY The max y or row
     * @return A TileCursor
     */
    TileCursor<T> tiles(long z, long minX, long minY, long maxX, long maxY) {
        new TileCursor(this, z, minX, minY, maxX, maxY)
    }

    /**
     * Get a TileCursor for all the Tiles within the given Bounds
     * @param z The zoom level
     * @param b The Bounds
     * @return A TileCursor
     */
    TileCursor<T> tiles(Bounds b, long z) {
        new TileCursor(this, b, z)
    }

    /**
     * Get a TileCursor for all the Tiles within the given Bounds and resolutions
     * @param b The Bounds
     * @param resX The x resolution
     * @param resY The y resolution
     * @return A TileCursor
     */
    TileCursor<T> tiles(Bounds b, double resX, double resY) {
        new TileCursor(this, b, resX, resY)
    }

    /**
     * Get a TileCursor for all the Tiles withing the given Bounds and image size
     * @param b The Bounds
     * @param w The image width
     * @param h The image height
     * @return A TileCursor
     */
    TileCursor<T> tiles(Bounds b, int w, int h) {
        new TileCursor(this, b, w, h)
    }

    /**
     * Delete all of the Tiles in the TileCursor
     * @param tiles The TileCursor
     */
    void delete(TileCursor<T> tiles) {
        tiles.each {T tile ->
            delete(tile)
        }
    }

    /**
     * Get Tile coordinates (minX, minY, maxX, maxY) for the given Bounds and Grid
     * @param b The Bounds
     * @param g The Grid
     * @return A Map with tile coordinates (minX, minY, maxX, maxY)
     */
    Map getTileCoordinates(Bounds b, Grid g) {
        this.pyramid.getTileCoordinates(b, g)
    }

    /**
     * Get a Layer of the Tiles in a TileCursor
     * @param options Optional named parameters
     * <ul>
     *     <li>outLayer = The name of the Layer</li>
     *     <li>outWorkspace = The Workspace</li>
     *     <li>geomFieldName = The name of the geometry Field</li>
     *     <li>idFieldName = The name of the ID Field</li>
     *     <li>zFieldName = The name of the Z Field</li>
     *     <li>xFieldName = The name of the X Field</li>
     *     <li>yFieldName = The name of the Y Field</li>
     * </ul>
     * @param cursor
     * @return
     */
    Layer getLayer(Map options = [:], TileCursor cursor) {
        String outLayerName = options.get("outLayer", "${this.name}_tiles")
        Workspace outWorkspace = options.get("outWorkspace", new Memory())
        String geomFieldName = options.get("geomFieldName","the_geom")
        String idFieldName = options.get("idFieldName","id")
        String zFieldName = options.get("zFieldName","z")
        String xFieldName = options.get("xFieldName","x")
        String yFieldName = options.get("yFieldName","y")
        Layer outLayer = outWorkspace.create(outLayerName, [
                new Field(idFieldName, "int"),
                new Field(zFieldName, "int"),
                new Field(xFieldName, "int"),
                new Field(yFieldName, "int"),
                new Field(geomFieldName, "Polygon", this.proj)
        ])
        outLayer.withWriter{ geoscript.layer.Writer w ->
            cursor.eachWithIndex { Tile tile, int i ->
                w.add(outLayer.schema.feature([
                        (idFieldName): i,
                        (zFieldName): tile.z,
                        (xFieldName): tile.x,
                        (yFieldName): tile.y,
                        (geomFieldName): this.pyramid.bounds(tile).geometry
                ]))
            }
        }
        outLayer
    }

    @Override
    String toString() {
        this.name
    }
    
    /**
     * Use a TileLayer within a Closure and make sure it gets closed.
     * @param tileLayer The TileLayer
     * @param closure A Closure that takes the TileLayer
     */
    static void withTileLayer(TileLayer tileLayer, Closure closure) {
        try {
            closure.call(tileLayer)
        } finally {
            tileLayer.close()
        }
    }

    /**
     * Get a TileLayer from a string of connection parameters.
     * <ul>
     *     <li> MBTiles = 'states.mbtiles' </li>
     *     <li> MBTiles = 'type=mbtiles file=states.mbtiles' </li>
     *     <li> GeoPackage = 'states.gpkg' </li>
     *     <li> GeoPackage = 'type=geopackage file=states.gpkg' </li>
     *     <li> TMS = 'type=tms file=C:\Tiles\states format=jpeg' </li>
     *     <li> OSM = 'type=osm url=http://a.tile.openstreetmap.org' </li>
     *     <li> UTFGrid = 'type=utfgrid file=/Users/me/tiles/states' </li>
     *     <li> VectorTiles = 'type=vectortiles name=states file=/Users/me/tiles/states format=mvt pyramid=GlobalMercator' </li>
     *     <li> VectorTiles = 'type=vectortiles name=states url=http://vectortiles.org format=pbf pyramid=GlobalGeodetic' </li>
     * </ul>
     * @param paramsStr The connection parameter string
     * @return A TileLayer or null
     */
    static TileLayer getTileLayer(String paramsStr) {
        TileLayer tileLayer = null
        for(TileLayerFactory tileLayerFactory : TileLayerFactories.list()) {
            tileLayer = tileLayerFactory.create(paramsStr)
            if (tileLayer != null) {
                break
            }
        }
        tileLayer
    }

    /**
     * Get a TileLayer from a connection parameter Map.
     * @param params A Map of connection parameters.
     * <ul>
     *     <li> MBTiles = [type: 'mbtiles', file: 'states.mbtiles'] </li>
     *     <li> GeoPackage = '[type: 'geopackage', file: 'states.gpkg'] </li>
     *     <li> TMS = [type: 'tms', file: 'C:\Tiles\states format:jpeg'] </li>
     *     <li> OSM = [type: 'osm', url: 'http://a.tile.openstreetmap.org'] </li>
     *     <li> UTFGrid = [type: 'utfgrid', file: '/Users/me/tiles/states'] </li>
     *     <li> VectorTiles = [type: 'vectortiles', name: 'states', file: '/Users/me/tiles/states format:mvt pyramid:GlobalMercator'] </li>
     *     <li> VectorTiles = [type: 'vectortiles', name: 'states', url: 'http://vectortiles.org format:pbf pyramid:GlobalGeodetic'] </li>
     * </ul>
     * @return A TileLayer or null
     */
    static TileLayer getTileLayer(Map params) {
        TileLayer tileLayer = null
        for(TileLayerFactory tileLayerFactory : TileLayerFactories.list()) {
            tileLayer = tileLayerFactory.create(params)
            if (tileLayer != null) {
                break
            }
        }
        tileLayer
    }

    /**
     * Get a default TileRenderer for the given TileLayer.
     * @param options The optional named parameters:
     * <ul>
     *     <li> fields = Some TileRenderers can be customized with a List of Fields.</li>
     * </ul>
     * @param tileLayer The TileLayer
     * @param layer The Layer
     * @return A TileRenderer or null
     */
    static TileRenderer getTileRenderer(Map options = [:], TileLayer tileLayer, Layer layer) {
        getTileRenderer(options, tileLayer, [layer])
    }

    /**
     * Get a default TileRenderer for the given TileLayer.
     * @param options The optional named parameters:
     * <ul>
     *     <li> fields = Some TileRenderers can be customized with a List of Fields.</li>
     * </ul>
     * @param tileLayer The TileLayer
     * @param layers The List of Layers (some TileRenderers can only render one Layer at a time)
     * @return A TileRenderer or null
     */
    static TileRenderer getTileRenderer(Map options = [:], TileLayer tileLayer, List<Layer> layers) {
        TileRenderer tileRenderer = null
        for(TileLayerFactory tileLayerFactory : TileLayerFactories.list()) {
            tileRenderer = tileLayerFactory.getTileRenderer(options, tileLayer, layers)
            if (tileRenderer != null) {
                break
            }
        }
        tileRenderer
    }

}
