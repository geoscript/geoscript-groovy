package geoscript.style.io

import geoscript.style.Style
import org.geoscript.geocss.compat.CSS2SLD
import org.geotools.styling.Style as GtStyle

/**
 * Read a Geoscript Style from a CSS File, InputStream or String
 * <p><blockquote><pre>
 * Reader reader = new CSSReader()
 * Style style = reader.read("states.css")
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class CSSReader implements Reader {

    /**
     * Read a GeoScript Style from a CSS java.io.Reader
     * @param reader A java.io.Reader
     * @return A GeoScript Style
     */
    Style read(InputStream inputStream) {
        read(new InputStreamReader(inputStream))
    }

    /**
     * Read a GeoScript Style from a CSS java.io.Reader
     * @param reader A java.io.Reader
     * @return A GeoScript Style
     */
    Style read(java.io.Reader reader) {
        GtStyle style = CSS2SLD.convert(reader)
        new CSSStyle(style)
    }

    /**
     * Read a GeoScript Style from a CSS File
     * @param file A CSS File
     * @return A GeoScript Style
     */
    Style read(File file) {
        read(new FileReader(file))
    }
	
    /**
     * Read a GeoScript Style from a CSS String
     * @param str A CSS String
     * @return A GeoScript Style
     */
    Style read(String str) {
        read(new StringReader(str))
    }
	
    /**
     * A simple GeoScript Style that wraps a GeoTools Style
     */
    private static class CSSStyle implements Style {
        private final GtStyle style
        CSSStyle(GtStyle style) {
            this.style = style
        }
        GtStyle getStyle() {
            style
        }
    }
}

