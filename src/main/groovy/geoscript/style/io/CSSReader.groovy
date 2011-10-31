package geoscript.style.io

import geoscript.style.Style
import org.geoscript.geocss.compat.CSS2SLD
import org.geotools.styling.Style as GtStyle

/**
 * Read a Geoscript Style from a CSS File, InputStream or String
 * <p>
 * <pre><code>
 * Reader reader = new CSSReader()
 * Style style = reader.read("states.css")
 * </code></pre>
 * </p>
 * @author Jared Erickson
 */
class CSSReader implements Reader {

    /**
     * Read a GeoScript Style from a CSS InputStream
     * @param input A CSS InputStream
     * @return A GeoScript Style
     */
    Style read(InputStream input) {
        GtStyle style = CSS2SLD.convert(input)
        new CSSStyle(style)
    }

    /**
     * Read a GeoScript Style from a CSS File
     * @param file A CSS File
     * @return A GeoScript Style
     */
    Style read(File file) {
        read(new FileInputStream(file))
    }
	
    /**
     * Read a GeoScript Style from a CSS String
     * @param str A CSS String
     * @return A GeoScript Style
     */
    Style read(String str) {
        read(new ByteArrayInputStream(str.getBytes("UTF-8")))
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

