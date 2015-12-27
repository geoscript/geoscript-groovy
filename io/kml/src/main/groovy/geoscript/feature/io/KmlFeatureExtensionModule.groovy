package geoscript.feature.io

import geoscript.feature.Feature

/**
 * A Groovy Extension Module that adds methods to the Feature class.
 * @author Jared Erickson
 */
class KmlFeatureExtensionModule {

    /**
     * Get this Feature as a KML Placemark
     * @param feature The Feature
     * @param options The named parameters
     * <ul>
     *     <li>format = Whether to format the KML or not (default = false)</li>
     *     <li>xmldecl = Whether to include the XML declaration (default = false)</li>
     * </ul>
     * @return A KML Placemark String
     */
    static String getKml(Feature feature, Map options = [:]) {
        def writer = new geoscript.feature.io.KmlWriter()
        writer.write(options, feature)
    }

}
