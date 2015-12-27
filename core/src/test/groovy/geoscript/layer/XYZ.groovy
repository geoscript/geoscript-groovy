package geoscript.layer

/**
 * A TileLayer only for testing
 */
class XYZ extends ImageTileLayer {

    File dir

    URL url

    Pyramid pyramid

    String imageType

    XYZ(String name, String imageType, String fileOrUrl, Pyramid pyramid) {
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


    XYZ(String name, String imageType, File dir, Pyramid pyramid) {
        this.name = name
        this.imageType = imageType
        this.dir = dir
        this.pyramid = pyramid
        this.bounds = pyramid.bounds
        this.proj = pyramid.proj
    }

    XYZ(String name, String imageType, URL url, Pyramid pyramid) {
        this.name = name
        this.imageType = imageType
        this.url = url
        this.pyramid = pyramid
        this.bounds = pyramid.bounds
        this.proj = pyramid.proj
    }

    @Override
    ImageTile get(long z, long x, long y) {
        ImageTile tile = new ImageTile(z, x, y)
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

    @Override
    void put(ImageTile t) {
        if(!dir) {
            throw new IllegalArgumentException("XYZ with URL are ready only!")
        }
        File file = new File(new File(new File(this.dir, String.valueOf(t.z)), String.valueOf(t.x)), "${t.y}.${imageType}")
        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }
        file.withOutputStream {out ->
            out.write(t.data)
        }
    }

    @Override
    void delete(ImageTile t) {
        if(!dir) {
            throw new IllegalArgumentException("XYZ with URL are ready only!")
        }
        File file = new File(new File(new File(this.dir, String.valueOf(t.z)), String.valueOf(t.x)), "${t.y}.${imageType}")
        if (file.exists()) {
            file.delete()
        }
    }

    @Override
    void close() throws IOException {
    }
}
