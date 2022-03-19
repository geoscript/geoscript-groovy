package geoscript.style.io

import geoscript.style.Style

/**
 * A Groovy Extension Module that adds static methods to the Style class.
 * @author Jared Erickson
 */
class StyleExtensionModule {

    /**
     * Get a SLD String from a Style
     * @param style The Style
     * @return A SLD String
     */
    static String getSld(Style style) {
        Writers.find("sld").write(style)
    }

    /**
     * Get a YSLD String from a Style
     * @param style The Style
     * @return A YSLD String
     */
    static String getYsld(Style style) {
        Writers.find("ysld").write(style)
    }

}
