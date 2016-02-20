package geoscript.feature.io

import geoscript.feature.Feature

/**
 * A Groovy Extension Module that adds methods to the Feature class.
 * @author Jared Erickson
 */
class FeatureExtensionModule {

    /**
     * Get this Feature as a GeoJSON String
     * @param feature The Feature
     * @param options Optional named parameters:
     * <ol>
     *     <li> decimals = The number of decimals (defaults to 4) </li>
     *     <li> encodeFeatureBounds = Whether to encode Feature Bounds (default is false) </li>
     *     <li> encodeFeatureCollectionBounds = Whether to encode FeatureCollection Bounds (default is false) </li>
     *     <li> encodeFeatureCollectionCRS = Whether to encode FeatureCollection CRS (default is false) </li>
     *     <li> encodeFeatureCRS = Whether to encode Feature CRS (default is false) </li>
     *     <li> encodeNullValues = Whether to encode null values (default is false) </li>
     * </ol>
     * @return The GeoJSON String
     */
    static String getGeoJSON(Feature feature, Map options = [:]) {
        def writer = new geoscript.feature.io.GeoJSONWriter()
        writer.write(options, feature)
    }

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

    /**
     * Get this Feature as a Geobuf hex string
     * @param feature The Feature
     * @param options The optional named parameters
     * <ul>
     *     <li> precision = The maximum precision (defaults to 6) </li>
     *     <li> dimension = The supported geometry coordinates dimension (defaults to 2) </li>
     * </ul>
     * @return A Geobuf hex string
     */
    static String getGeobuf(Feature feature, Map options = [:]) {
        def writer = new geoscript.feature.io.GeobufWriter(options)
        writer.write(feature)
    }

    /**
     * Get this Feature as a Geobuf byte array
     * @param feature The Feature
     * @param options The optional named parameters
     * <ul>
     *     <li> precision = The maximum precision (defaults to 6) </li>
     *     <li> dimension = The supported geometry coordinates dimension (defaults to 2) </li>
     * </ul>
     * @return A Geobuf byte arary
     */
    static byte[] getGeobufBytes(Feature feature, Map options = [:]) {
        def writer = new geoscript.feature.io.GeobufWriter(options)
        writer.writeBytes(feature)
    }

}
