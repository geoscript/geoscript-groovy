package geoscript.layer

import geoscript.feature.Feature
import geoscript.layer.io.*
import geoscript.proj.Projection
import geoscript.style.Style
import geoscript.workspace.Memory
import geoscript.workspace.Workspace
import org.geotools.util.logging.Logging

import java.util.logging.Level
import java.util.logging.Logger

/**
 * A TileLayer for VectorTiles
 * @author Jared Erickson
 */
class VectorTiles extends TileLayer<Tile> {

    /**
     * The File directory
     */
    File dir

    /**
     * The base URL
     */
    URL url

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
    geoscript.layer.io.Reader reader

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
     * @param dir The directory of the Tiles
     * @param pyramid The Pyramid
     * @param type The type (pbf, mvt, geojson, kml)
     */
    VectorTiles(Map options = [:], String name, File dir, Pyramid pyramid, String type) {
        this.name = name
        this.dir = dir
        this.pyramid = pyramid
        this.bounds = this.pyramid.bounds
        this.type = type
        this.reader = getReaderForType(type)
        this.proj = options.get("proj", pyramid.proj)
        this.style = options.get("style")
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
    }

    private geoscript.layer.io.Reader getReaderForType(String type) {
        if (type.equalsIgnoreCase("geojson") || type.equalsIgnoreCase("json")) {
            new GeoJSONReader()
        } else if (type.equalsIgnoreCase("csv")) {
            new CsvReader()
        } else if (type.equalsIgnoreCase("georss")) {
            new GeoRSSReader()
        } else if (type.equalsIgnoreCase("gml")) {
            new GmlReader()
        } else if (type.equalsIgnoreCase("gpx")) {
            new GpxReader()
        } else if (type.equalsIgnoreCase("kml")) {
            new KmlReader()
        } else if (type.equalsIgnoreCase("mvt")) {
            new MvtReader()
        } else {
            null
        }
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
        Tile tile = new ImageTile(z, x, y)
        if (dir) {
            File file = new File(new File(new File(this.dir, String.valueOf(z)), String.valueOf(x)), "${y}.${type}")
            if (file.exists()) {
                tile.data = file.bytes
            }
        } else {
            URL tileUrl = new URL("${url.toString()}/${z}/${x}/${y}.${type}")
            tileUrl.withInputStream { input ->
                tile.data = input.bytes
            }
        }
        tile
    }

    /**
     * Add a Tile
     * @param t The Tile
     */
    @Override
    void put(Tile t) {
        if (!dir) {
            throw new IllegalArgumentException("Vector Tiles with URL are ready only!")
        }
        File file = new File(new File(new File(this.dir, String.valueOf(t.z)), String.valueOf(t.x)), "${t.y}.${type}")
        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }
        file.withOutputStream { out ->
            out.write(t.data)
        }
    }

    /**
     * Delete a Tile
     * @param t The Tile
     */
    @Override
    void delete(Tile t) {
        if (!dir) {
            throw new IllegalArgumentException("Vector Tiles with URL are ready only!")
        }
        File file = new File(new File(new File(this.dir, String.valueOf(t.z)), String.valueOf(t.x)), "${t.y}.${type}")
        if (file.exists()) {
            file.delete()
        }
    }

    /**
     * Close the TileLayer
     */
    @Override
    void close() throws IOException {
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
}
