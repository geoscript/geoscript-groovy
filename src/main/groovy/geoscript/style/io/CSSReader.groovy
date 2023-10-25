package geoscript.style.io

import geoscript.style.Style
import org.geotools.api.style.Style as GtStyle
import org.geotools.styling.css.CssParser
import org.geotools.styling.css.CssTranslator
import org.geotools.styling.css.Stylesheet
import org.parboiled.Parboiled
import org.parboiled.errors.ErrorUtils
import org.parboiled.parserunners.ReportingParseRunner
import org.parboiled.support.ParsingResult

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
        String css = reader.text
        CssParser parser = Parboiled.createParser(CssParser.class)
        ParsingResult result = new ReportingParseRunner(parser.StyleSheet()).run(css)
        if (result.hasErrors()) {
           println ErrorUtils.printParseErrors(result)
        }
        Stylesheet ss = result.parseTreeRoot.value
        CssTranslator translator = new CssTranslator()
        GtStyle style = translator.translate(ss)
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
        GtStyle getGtStyle() {
            style
        }
    }
}

