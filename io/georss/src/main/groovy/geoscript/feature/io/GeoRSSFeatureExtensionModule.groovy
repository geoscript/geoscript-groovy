package geoscript.feature.io

import geoscript.feature.Feature

/**
 * A Groovy Extension Module that adds methods to the Feature class.
 * @author Jared Erickson
 */
class GeoRSSFeatureExtensionModule {

    /**
     * Get this Feature as a GeoRSS String
     * @param feature The Feature
     * @param options The named parameters
     * <ul>
     *      <li>feedType = The feed type (atom or rss)</li>
     *      <li>geometryType = The geometry type (simple, gml, w3c)</li>
     *      <li>includeAttributes = Whether to include all attributes</li>
     *      <li>attributeNamespace = The namespace for attributes (ogr=http://www.gdal/ogr/)</li>
     *      <li>itemTitle = The item title (Closure, Expression, or String)</li>
     *      <li>itemId = The item id (Closure, Expression, or String)</li>
     *      <li>itemDescription = The item description (Closure, Expression, or String)</li>
     *      <li>itemDate = The item date (Closure, Expression, or String)</li>
     *      <li>itemGeometry = The item geometry (Closure, Expression, or String)</li>
     * </ul>
     * @return A GeoRSS String
     */
    static String getGeoRSS(Feature feature, Map options = [:]) {
        def writer = new geoscript.feature.io.GeoRSSWriter(options)
        writer.write(feature)
    }

}
