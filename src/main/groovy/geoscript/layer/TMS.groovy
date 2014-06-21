package geoscript.layer

/**
 * A Tile Map Service (TMS) TileLayer.  A TMS TileLayer can read or write Tiles
 * if given a File directory but it is ready only if given a URL.
 * @author Jared Erickson
 */
class TMS extends TileLayer {

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
     * The image type (png, jpeg, gif)
     */
    String imageType

    /**
     * Create a TMS TileLayer for a File directory
     * @param name The name of the TMS TileLayer
     * @param imageType The image type (png, jpeg, gif)
     * @param fileOrUrl The File directory or URL
     * @param pyramid The Pyramid
     */
    TMS(String name, String imageType, String fileOrUrl, Pyramid pyramid) {
        this.name = name
        this.imageType = imageType
        if (fileOrUrl.startsWith("http")) {
            this.url = new URL(fileOrUrl)
        } else {
            this.dir = new File(fileOrUrl)
        }
        this.pyramid = pyramid
        this.bounds = pyramid.bounds
        this.proj = pyramid.proj
    }

    /**
     * Create a TMS TileLayer for a File directory
     * @param name The name of the TMS TileLayer
     * @param imageType The image type (png, jpeg, gif)
     * @param dir The File directory
     * @param pyramid The Pyramid
     */
    TMS(String name, String imageType, File dir, Pyramid pyramid) {
        this.name = name
        this.imageType = imageType
        this.dir = dir
        this.pyramid = pyramid
        this.bounds = pyramid.bounds
        this.proj = pyramid.proj
    }

    /**
     * Create a TMS TileLayer with a base URL
     * @param name The name of the TMS TileLayer
     * @param imageType The image type (png, jpeg, gif)
     * @param url The base URL
     * @param pyramid The Pyramid
     */
    TMS(String name, String imageType, URL url, Pyramid pyramid) {
        this.name = name
        this.imageType = imageType
        this.url = url
        this.pyramid = pyramid
        this.bounds = pyramid.bounds
        this.proj = pyramid.proj
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
        Tile tile = new Tile(z, x, y)
        if (dir) {
            File file = new File(new File(new File(this.dir, String.valueOf(z)), String.valueOf(x)), "${y}.${imageType}")
            if (file.exists()) {
                tile.data = file.bytes
            }
        } else {
            URL tileUrl = new URL("${url.toString()}/${z}/${x}/${y}.${imageType}")
            tileUrl.withInputStream {input ->
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
        if(!dir) {
            throw new IllegalArgumentException("TMS with URL are ready only!")
        }
        File file = new File(new File(new File(this.dir, String.valueOf(t.z)), String.valueOf(t.x)), "${t.y}.${imageType}")
        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }
        file.withOutputStream {out ->
            out.write(t.data)
        }
    }

    /**
     * Close the TileLayer
     */
    @Override
    void close() throws IOException {
        // Do nothing
    }

}
