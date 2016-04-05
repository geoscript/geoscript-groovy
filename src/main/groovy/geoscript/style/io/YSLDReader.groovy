package geoscript.style.io

import geoscript.style.Style
import org.geotools.ysld.Ysld

/**
 * Read a Geoscript Style from a YSLD File, InputStream or String
 * <p><blockquote><pre>
 * Reader reader = new YSLDReader()
 * Style style = reader.read("states.yml")
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class YSLDReader implements Reader {

    /**
     * Read a GeoScript Style from an InputStream
     * @param input An InputStream
     * @return A GeoScript Style
     */
    @Override
    Style read(InputStream input) {
        new YsldStyle(Ysld.parse(input))
    }

    /**
     * Read a GeoScript Style from a File
     * @param file A File
     * @return A GeoScript Style
     */
    @Override
    Style read(File file) {
        new YsldStyle(Ysld.parse(input))
    }

    /**
     * Read a GeoScript Style from a String
     * @param str A String
     * @return A GeoScript Style
     */
    @Override
    Style read(String str) {
        new YsldStyle(Ysld.parse(input))
    }

    /**
     * A simple GeoScript Style that wraps a GeoTools Style
     */
    private static class YsldStyle implements Style {
        private final org.geotools.styling.Style style
        YsldStyle(org.geotools.styling.Style style) {
            this.style = style
        }
        org.geotools.styling.Style getGtStyle() {
            style
        }
    }
}
