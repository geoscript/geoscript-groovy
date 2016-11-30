package geoscript.layer.io

import geoscript.layer.Layer
import org.apache.commons.codec.binary.Base64

/**
 * A Mapnik Vector Tile Reader
 * @author Jared Erickson
 */
class MvtReader implements Reader {

    /**
     * Read a Layer from a MVT URL
     * @param url The MVT URL
     * @return A GeoScript Layer
     */
    Layer read(URL url) {
        Layer layer
        url.withInputStream {InputStream input ->
            layer = read(input)
        }
        layer
    }

    /**
     * Read a GeoScript Layer from an InputStream
     * @param input An InputStream
     * @return A GeoScript Layer
     */
    @Override
    Layer read(InputStream input) {
        Mvt.read(input)
    }

    /**
     * Read a GeoScript Layer from a File
     * @param file A File
     * @return A GeoScript Layer
     */
    @Override
    Layer read(File file) {
        Mvt.read(file)
    }

    /**
     * Read a GeoScript Layer from a Base64 Encoded String
     * @param str A String
     * @return A GeoScript Layer
     */
    @Override
    Layer read(String str) {
        InputStream input = new ByteArrayInputStream(Base64.decodeBase64(str.getBytes("UTF-8")))
        Layer layer = Mvt.read(input)
        input.close()
        layer
    }

}
