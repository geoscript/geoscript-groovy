package geoscript.layer

import geoscript.geom.Bounds
import geoscript.proj.Projection

/**
 * A UTFGrid TileLayer
 * @author Jared Erickson
 */
class UTFGrid extends TileLayer<Tile> {

    /**
     * The UTFGrid directory
     */
    private final File dir

    /**
     * The cached internal Pyramid
     */
    private Pyramid pyramid

    /**
     * Create a UTFGrid from a existing directory
     * @param file The existing File
     */
    UTFGrid(File dir) {
        this.dir = dir
        this.proj = new Projection("EPSG:3857")
        this.bounds = new Bounds(-179.99, -85.0511, 179.99, 85.0511, "EPSG:4326").reproject(this.proj)
        this.name = dir.name
        this.pyramid = Pyramid.createGlobalMercatorPyramid()
        this.pyramid.origin = Pyramid.Origin.TOP_LEFT
    }

    @Override
    Pyramid getPyramid() {
        this.pyramid
    }

    @Override
    Tile get(long z, long x, long y) {
        File file = new File(new File(new File(this.dir, String.valueOf(z)), String.valueOf(x)), "${y}.grid.json")
        new Tile(z, x, y, file.exists() ? file.bytes : null)
    }

    @Override
    void put(Tile t) {
        File file = new File(new File(new File(this.dir, String.valueOf(t.z)), String.valueOf(t.x)), "${t.y}.grid.json")
        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }
        file.withOutputStream { out ->
            out.write(t.data)
        }
    }

    @Override
    void close() throws IOException {
        // Do Nothing!
    }

}
