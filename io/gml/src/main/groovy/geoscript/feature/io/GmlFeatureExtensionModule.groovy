package geoscript.feature.io

import geoscript.feature.Feature

/**
 * A Groovy Extension Module that adds methods to the Feature class.
 * @author Jared Erickson
 */
class GmlFeatureExtensionModule {

    /**
     * Get this Feature as a GML String
     * @param feature The Feature
     * @param options The named parameters
     * <ul>
     *     <li>version = The version 2, 3, or 3.2</li>
     *     <li>format = Whether to pretty print or not</li>
     *     <li>bounds = Whether to include Feature Bounds or not</li>
     *     <li>xmldecl = Whether to include XML declaration or not</li>
     *     <li>nsprefix = The XML namespace prefix</li>
     * </ul>
     * @return A GML String
     */
    static String getGml(Feature feature, Map options = [:]) {
        double version = options.get("version", 2)
        boolean format = options.get("format", true)
        boolean bounds = options.get("bounds", false)
        boolean xmldecl = options.get("xmldecl", false)
        String nsprefix = options.get("nsprefix", "gsf")
        def writer = new geoscript.feature.io.GmlWriter()
        writer.write(feature, version, format, bounds, xmldecl, nsprefix)
    }

}
