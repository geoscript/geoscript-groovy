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
     * Get a well known OSM Layer.
     * @param name The name of the well known OSM Layer (stamen-toner, stamen-toner-lite, stamen-watercolor, mapquest-street,
     * mapquest-satellite, or osm)
     * @return An OSM Tile Layer
     */
    static OSM getWellKnownOSM(String name) {
        if (!name) {
            null
        } else if (name.equalsIgnoreCase("stamen-toner")) {
            new OSM("Stamen Toner", [
                    "http://a.tile.stamen.com/toner",
                    "http://b.tile.stamen.com/toner",
                    "http://c.tile.stamen.com/toner",
                    "http://d.tile.stamen.com/toner"
            ])
        } else if (name.equalsIgnoreCase("stamen-toner-lite")) {
            new OSM("Stamen Toner Lite", [
                    "http://a.tile.stamen.com/toner-lite",
                    "http://b.tile.stamen.com/toner-lite",
                    "http://c.tile.stamen.com/toner-lite",
                    "http://d.tile.stamen.com/toner-lite"
            ])
        } else if (name.equalsIgnoreCase("stamen-watercolor")) {
            new OSM("Stamen Watercolor", [
                    "http://a.tile.stamen.com/watercolor",
                    "http://b.tile.stamen.com/watercolor",
                    "http://c.tile.stamen.com/watercolor",
                    "http://d.tile.stamen.com/watercolor"
            ])
        } else if (name.equalsIgnoreCase("stamen-terrain")) {
            new OSM("Stamen Terrain", [
                    "http://a.tile.stamen.com/terrain",
                    "http://b.tile.stamen.com/terrain",
                    "http://c.tile.stamen.com/terrain",
                    "http://d.tile.stamen.com/terrain"
            ])
        } else if (name.equalsIgnoreCase("mapquest-street")) {
            new OSM("MapQuest Street", [
                    "http://otile1.mqcdn.com/tiles/1.0.0/map",
                    "http://otile2.mqcdn.com/tiles/1.0.0/map",
                    "http://otile3.mqcdn.com/tiles/1.0.0/map",
                    "http://otile4.mqcdn.com/tiles/1.0.0/map"
            ])
        } else if (name.equalsIgnoreCase("mapquest-satellite")) {
            new OSM("MapQuest Satellite", [
                    "http://otile1.mqcdn.com/tiles/1.0.0/sat",
                    "http://otile2.mqcdn.com/tiles/1.0.0/sat",
                    "http://otile3.mqcdn.com/tiles/1.0.0/sat",
                    "http://otile4.mqcdn.com/tiles/1.0.0/sat"
            ])
        } else if (name.equalsIgnoreCase("osm")) {
            new OSM()
        } else {
            null
        }
    }

    /**
     * The OSM TileLayerFactory
     */
    static class Factory extends TileLayerFactory<OSM> {

        @Override
        OSM create(String paramsStr) {
            Map params = [:]
            if (paramsStr in ['stamen-toner', 'stamen-toner-lite', 'stamen-watercolor',
                              'mapquest-street', 'mapquest-satellite', 'osm']) {
                params["type"] = "osm"
                params["name"] = paramsStr
                create(params)
            } else {
                super.create(paramsStr)
            }
        }

        @Override
        OSM create(Map params) {
            String type = params.get("type","").toString()
            if (type.equalsIgnoreCase("osm")) {
                String name = params.get("name", "OSM")
                if (params.get("url")) {
                    List baseUrls = [params.get("url")]
                    new OSM(name, baseUrls)
                }
                else if (params.get("urls")) {
                    List baseUrls = params.get("urls").split(",")
                    new OSM(name, baseUrls)
                }
                else if (OSM.getWellKnownOSM(name)) {
                    OSM.getWellKnownOSM(name)
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
