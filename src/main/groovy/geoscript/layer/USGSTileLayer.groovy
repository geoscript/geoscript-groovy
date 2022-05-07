package geoscript.layer

/**
 * A USGS National Map ImageTileLayer that is read only.
 * @author Jared Erickson
 */
class USGSTileLayer extends ImageTileLayer {

    /**
     * The base url
     */
    private final String baseUrl

    /**
     * The global web mercator Pyramid
     */
    private Pyramid pyramid

    /**
     * Create a new USGS ImageTileLayer
     * @param name The name
     * @param baseUrl The base URL
     */
    USGSTileLayer(String name, String baseUrl) {
        this.name = name
        this.baseUrl = baseUrl
        this.pyramid = Pyramid.createGlobalMercatorPyramid()
        this.pyramid.origin = Pyramid.Origin.TOP_LEFT
        this.bounds = pyramid.bounds
        this.proj = pyramid.proj
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
        URL url = new URL("${baseUrl}${baseUrl.endsWith("/") ? '' : '/'}${z}/${y}/${x}")
        URLConnection urlConnection = url.openConnection()
        urlConnection.setRequestProperty("User-Agent", "GeoScript Groovy")
        InputStream inputStream = urlConnection.inputStream
        try {
            tile.data = inputStream.bytes
        } finally {
            inputStream.close()
        }
        tile
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

    static USGSTileLayer createTopo() {
        new USGSTileLayer("USGSTopo","https://basemap.nationalmap.gov/arcgis/rest/services/USGSTopo/MapServer/tile")
    }

    static USGSTileLayer createShadedRelief() {
        new USGSTileLayer("USGSShadedRelief","https://basemap.nationalmap.gov/arcgis/rest/services/USGSShadedReliefOnly/MapServer/tile")
    }

    static USGSTileLayer createImagery() {
        new USGSTileLayer("USGSImagery", "https://basemap.nationalmap.gov/arcgis/rest/services/USGSImageryOnly/MapServer/tile")
    }

    static USGSTileLayer createImageryTopo() {
        new USGSTileLayer("USGSImageryTopo","https://basemap.nationalmap.gov/arcgis/rest/services/USGSImageryTopo/MapServer/tile")
    }

    static USGSTileLayer createHydro() {
        new USGSTileLayer("USGSHydro","https://basemap.nationalmap.gov/arcgis/rest/services/USGSHydroCached/MapServer/tile")
    }

    /**
     * Get a USGS ImageTileLayer with a well known name.
     * @param name The well known name
     * @return A USGS ImageTileLayer or null
     */
    static USGSTileLayer getWellKnown(String name) {
        if (!name) {
            null
        } else if (name.equalsIgnoreCase("usgs-topo")) {
            createTopo()
        } else if (name.equalsIgnoreCase("usgs-shadedrelief")) {
            createShadedRelief()
        } else if (name.equalsIgnoreCase("usgs-imagery")) {
            createImagery()
        } else if (name.equalsIgnoreCase("usgs-imagerytopo")) {
            createImageryTopo()
        } else if (name.equalsIgnoreCase("usgs-hydro")) {
            createHydro()
        }
    }

    /**
     * The USGS TileLayerFactory
     */
    static class Factory extends TileLayerFactory<USGSTileLayer> {

        @Override
        USGSTileLayer create(String paramsStr) {
            Map params = [:]
            if (paramsStr in ['usgs-topo', 'usgs-shadedrelief', 'usgs-imagery', 'usgs-imagerytopo', 'usgs-hydro']) {
                params["type"] = "usgs"
                params["name"] = paramsStr
                create(params)
            } else {
                super.create(paramsStr)
            }
        }

        @Override
        USGSTileLayer create(Map params) {
            String type = params.get("type","").toString()
            if (type.equalsIgnoreCase("usgs")) {
                String name = params.get("name", "USGS")
                if (params.get("url")) {
                    String baseUrl = params.get("url")
                    new USGSTileLayer(name, baseUrl)
                }
                else if (USGSTileLayer.getWellKnown(name)) {
                    USGSTileLayer.getWellKnown(name)
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
