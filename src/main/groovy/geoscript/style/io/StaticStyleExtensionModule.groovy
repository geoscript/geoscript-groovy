package geoscript.style.io

import geoscript.style.Style

/**
 * A Groovy Extension Module that adds static methods to the Style class.
 * @author Jared Erickson
 */
class StaticStyleExtensionModule {

    /**
     * Read a Style from an SLD String
     * @param style The Style
     * @param sld The SLD String
     * @return A Style
     */
    static Style fromSLD(Style style, String sld) {
        Readers.find("sld").read(sld)
    }

    /**
     * Read a Style from an YSLD String
     * @param style The Style
     * @param sld The YSLD String
     * @return A Style
     */
    static Style fromYSLD(Style style, String sld) {
        Readers.find("ysld").read(sld)
    }

    /**
     * Read a Style from an CSS String
     * @param style The Style
     * @param sld The CSS String
     * @return A Style
     */
    static Style fromCSS(Style style, String sld) {
        Readers.find("css").read(sld)
    }

}
