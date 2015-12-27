package geoscript.layer

/**
 * A Open Street Map TileLayer that is ready only.
 * @author Jared Erickson
 */
class OSM extends ImageTileLayer {

    /**
     * The base URL for OSM map tiles
     */
    List baseUrls

    /**
     * The image type
     */
    String imageType = "png"

    /**
     * The global web mercator Pyramid
     */
    private Pyramid pyramid

    /**
     * Create an OSM TileLayer with the default base URL
     */
    OSM() {
        this("OSM", [
            "http://a.tile.openstreetmap.org",
            "http://b.tile.openstreetmap.org",
            "http://c.tile.openstreetmap.org"
        ])
    }

    /**
     * Create an OSM TileLayer with a default base URL
     * @param name The name of the TileLayer
     * @param baseUrls The List of base URL for OSM map tiles
     */
    OSM(String name, List baseUrls) {
        this.baseUrls = baseUrls
        this.pyramid = Pyramid.createGlobalMercatorPyramid()
        this.pyramid.origin = Pyramid.Origin.TOP_LEFT
        this.name = name
        this.bounds = pyramid.bounds
        this.proj = pyramid.proj
    }

    /**
     * Create an OSM TileLayer with a default base URL
     * @param baseUrl The base URL for OSM map tiles
     */
    OSM(String name, String baseUrl) {
        this(name, [baseUrl])
    }

    /**
     * Get the Pyramid
     * @return The Pyramid
     */
    @Override
    Pyramid getPyramid() {
        this.pyramid
    }

    /**
     * Get a Tile
     * @param z The zoom level
     * @param x The column
     * @param y The row
     * @return A Tile
     */
    @Override
    ImageTile get(long z, long x, long y) {
        ImageTile tile = new ImageTile(z, x, y)
        String baseUrl = getBaseUrl()
        URL url = new URL("${baseUrl}/${z}/${x}/${y}.${imageType}")
        url.withInputStream {input ->
            tile.data = input.bytes
        }
        tile
    }

    /**
     * Randomly get a baseUrl
     * @return
     */
    private String getBaseUrl() {
        if (this.baseUrls.size() > 1) {
            Random random = new Random()
            baseUrls[random.nextInt(this.baseUrls.size())]
        } else {
            baseUrls[0]
        }
    }

    /**
     * Add a Tile
     * @param t The Tile
     */
    @Override
    void put(ImageTile t) {
        throw new IllegalArgumentException("OSM is ready only!")
    }

    /**
     * Delete a Tile
     * @param t The Tile
     */
    @Override
    void delete(ImageTile t) {
        throw new IllegalArgumentException("OSM is ready only!")
    }

    /**
     * Close the TileLayer
     */
    @Override
    void close() throws IOException {
        // Do nothing
    }

    /**
     * The OSM TileLayerFactory
     */
    static class Factory extends TileLayerFactory<OSM> {

        @Override
        OSM create(String paramsStr) {
            Map params = [:]
            if (paramsStr.equalsIgnoreCase("osm")) {
                params["type"] = "osm"
                create(params)
            } else {
                super.create(paramsStr)
            }
        }

        @Override
        OSM create(Map params) {
            String type = params.get("type","").toString()
            if (type.equalsIgnoreCase("osm")) {
                String name = params.get("name")
                if (params.get("url")) {
                    List baseUrls = [params.get("url")]
                    new OSM(name, baseUrls)
                }
                else if (params.get("urls")) {
                    List baseUrls = params.get("urls").split(",")
                    new OSM(name, baseUrls)
                }
                else {
                    new OSM()
                }
            } else {
                null
            }
        }

        @Override
        TileRenderer getTileRenderer(Map options, TileLayer tileLayer, List<Layer> layers) {
            null
        }
    }
}
