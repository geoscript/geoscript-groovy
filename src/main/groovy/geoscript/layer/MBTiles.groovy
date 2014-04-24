package geoscript.layer

import geoscript.geom.Point
import geoscript.proj.Projection
import geoscript.geom.Bounds
import org.geotools.factory.Hints
import org.geotools.mbtiles.MBTilesFile
import org.geotools.mbtiles.MBTilesMetadata
import org.geotools.mbtiles.MBTilesTile
import org.geotools.mbtiles.mosaic.MBTilesFormat

/**
 * A MBTiles Raster Format that also include tile creation.
 * @author Jared Erickson
 */
class MBTiles extends Format {

    /**
     * The MBTiles File
     */
    private final File file

    /**
     * Create a new MBTiles with a File
     * @param file The File
     */
    MBTiles(File file) {
        super(new MBTilesFormat(), file)
        this.file = file
    }

    /**
     * Create a new MBTiles with a file name
     * @param file The File name
     */
    MBTiles(String file) {
        this(new File(file))
    }

    /**
     * Read a Raster
     * @param bounds The Bounds
     * @param size The size fo the Raster as a list (w,h)
     * @return The Raster
     */
    Raster read(Bounds bounds, List size) {
        super.read([bounds: bounds, size: size])
    }

    @Override
    Raster read(java.util.Map options = [:], String name, Hints hints) {
        // Insert the default bounds and size if necessary
        if (!options.containsKey("bounds") && !options.containsKey("ReadGridGeometry2D")) {
            options.put("bounds", new Bounds(-180, -85.0511, 180, 85.0511, "EPSG:4326"))
            options.put("size", [500,500])
        }
        super.read(options, name, hints)
    }

    /**
     * Create a new MBTiles File with metadata
     * @param options The optional named parameters
     * <ul>
     *     <li>type = The type of layer (base_layer is the default or overlay)</li>
     *     <li>version = The version number (1.0 is the default)</li>
     *     <li>format = The image format (png is the default or jpeg)</li>
     *     <li>attribution = The attributes</li>
     * </ul>
     * @param name The name of the layer
     * @param description The description of the layer
     * @return This MBTiles instance
     */
    MBTiles create(java.util.Map options = [:], String name, String description) {

        String type = options.get("type", "base_layer")
        String version = options.get("version", "1.0")
        String format = options.get("format", "png")
        Bounds bounds = new Bounds(-180, -85.0511, 180, 85.0511, "EPSG:4326")
        String attribution = "Created with GeoScript"

        MBTilesFile tiles = new MBTilesFile(file)
        try {
            tiles.init()
            MBTilesMetadata metadata = new MBTilesMetadata()
            metadata.name = name
            metadata.description = description
            metadata.formatStr = format
            metadata.version = version
            metadata.typeStr = type
            metadata.bounds = bounds.env
            metadata.attribution = attribution
            tiles.saveMetaData(metadata)
        } finally {
            tiles.close()
        }

        this
    }

    /**
     * Generate tiles with the Layer or Layer from the start zoom level to the end zoom level.
     * @param options The optional named parameters
     * <ul>
     *     <li>verbose = Whether to print out zoom leve, column, and row (true or false. false is the default)</li>
     * </ul>
     * @param layers The Layer of List of Layers to render
     * @param startZoom The start zoom level
     * @param endZoom The end zoom level
     * @return This MBTiles instance
     */
    MBTiles generate(java.util.Map options = [:], def layers, int startZoom, int endZoom) {

        MBTilesFile tiles = new MBTilesFile(file)
        try {

            Projection proj = new Projection("EPSG:3857")

            Bounds bounds = new Bounds(-179.999999, -85.0511, 179.999999, 85.0511, "EPSG:4326")

            MBTilesMetadata metadata = tiles.loadMetaData()
            String imageType = metadata.format.name().toLowerCase()

            geoscript.render.Map map = new geoscript.render.Map(
                    fixAspectRatio: false,
                    proj: proj,
                    width: 256,
                    height: 256,
                    type: imageType,
                    layers: layers instanceof List ? layers : [layers],
                    bounds: bounds.reproject(proj)
            )

            boolean verbose = options.get("verbose", false) as boolean

            (startZoom..endZoom).each {zoom ->
                if (verbose) println "Zoom Level ${zoom}"
                // Number of rows and columns
                int n = Math.pow(2, zoom)
                (0..<n).each{column ->
                    if (verbose) println "   Column: ${column}"
                    (0..<n).each{row ->
                        if (verbose) println "         Row: ${row}"
                        int invertedY = n - row - 1
                        Bounds b = getSphericalMercatorBounds(getBounds(column, row, zoom))
                        map.bounds = b

                        def out = new ByteArrayOutputStream()
                        map.render(out)
                        out.close()

                        MBTilesTile tile = new MBTilesTile(zoom, column, invertedY)
                        tile.data = out.toByteArray()
                        tiles.saveTile(tile)
                    }
                }
            }

        } finally {
            tiles.close()
        }

        this
    }

    private Point getPoint(int x, int y, int zoom) {
        double n = Math.pow(2, zoom)
        double lon = x / n * 360.0 - 180.0
        double lat = Math.toDegrees(Math.atan(Math.sinh(Math.PI * (1-2 * y / n))))
        new Point(lon, lat)
    }

    private Bounds getBounds(int x, int y, int zoom) {
        Point a = getPoint(x,y,zoom)
        Point b = getPoint(x+1,y+1,zoom)
        new Bounds(a.x == -180 ? -179.99999 : a.x, a.y, b.x == 180 ? 179.9999 : b.x, b.y)
    }

    private Bounds getSphericalMercatorBounds(Bounds longLatBounds) {
        Point a = getSphericalMercatorPoint(new Point(longLatBounds.minX, longLatBounds.minY))
        Point b = getSphericalMercatorPoint(new Point(longLatBounds.maxX, longLatBounds.maxY))
        new Bounds(a.x, a.y, b.x, b.y)
    }

    private Point getSphericalMercatorPoint(Point longLatPt) {
        new Point(
            6378137.0 * Math.toRadians(longLatPt.x),
            6378137.0 * Math.log(Math.tan((Math.PI*0.25) + (0.5 * Math.toRadians(longLatPt.y))))
        )
    }

}
