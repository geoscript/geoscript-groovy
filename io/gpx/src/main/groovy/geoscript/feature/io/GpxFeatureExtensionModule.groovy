package geoscript.feature.io

import geoscript.feature.Feature

/**
 * A Groovy Extension Module that adds methods to the Feature class.
 * @author Jared Erickson
 */
class GpxFeatureExtensionModule {

    /**
     * Get this Feature as a GPX String
     * @param feature The Feature
     * @param options The named parameters
     * <ul>
     *     <li>version = The GPX version (defaults to 1.1)</li>
     *     <li>includeAttributes = Whether to include attributes (defaults to false)</li>
     *     <li>attributeNamespace = The attribute namespace (prefix=url)</li>
     *     <li>elevation = The elevation filter, closure, or value</li>
     *     <li>time = The time elevation filter, closure, or value</li>
     *     <li>name = The name elevation filter, closure, or value</li>
     *     <li>description = The description elevation filter, closure, or value</li>
     *     <li>type = The type elevation filter, closure, or value</li>
     * </ul>
     * @return A KML Placemark String
     */
    static String getGpx(Feature feature, Map options = [:]) {
        def writer = new geoscript.feature.io.GpxWriter(options)
        writer.write(feature)
    }

}
