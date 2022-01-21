package geoscript.geom.io

import geoscript.geom.Geometry

/**
 * A Groovy Extension Module that adds methods to the Geometry class.
 * @author Jared Erickson
 */
class GeometryExtensionModule {

    /**
     * Get the WKT of the Geometry
     * @param geometry The Geometry
     * @return The WKT of this Geometry
     */
    static String getWkt(Geometry geometry) {
        new WktWriter().write(geometry)
    }

    /**
     * Get the WKB of the Geometry
     * @param geometry The Geometry
     * @return The WKB hex string of this Geometry
     */
    static String getWkb(Geometry geometry) {
        new WkbWriter().write(geometry)
    }

    /**
     * Get the WKB of the Geometry
     * @param geometry The Geometry
     * @return The WKB byte array of this Geometry
     */
    static byte[] getWkbBytes(Geometry geometry) {
        new WkbWriter().writeBytes(geometry)
    }

    /**
     * Get a KML String for this Geometry
     * @param geometry The Geometry
     * @return The KML String
     */
    static String getKml(Geometry geometry) {
        new KmlWriter().write(geometry)
    }

    /**
     * Get a GeoJSON String for this Geometry
     * @param geometry The Geometry
     * @param options Optional named parameters:
     * <ol>
     *     <li> decimals = The number of decimals (defaults to 4) </li>
     * </ol>
     * @return The GeoJSON String
     */
    static String getGeoJSON(Geometry geometry, Map options = [:]) {
        new GeoJSONWriter().write(options, geometry)
    }

    /**
     * Get a GML 2 String for this Geometry
     * @param geometry The Geometry
     * @return The GML 2 String
     */
    static String getGml2(Geometry geometry) {
        new Gml2Writer().write(geometry)
    }

    /**
     * Get a GML 3 String for this Geometry
     * @param geometry The Geometry
     * @return The GML 3 String
     */
    static String getGml3(Geometry geometry) {
        new Gml3Writer().write(geometry)
    }

    /**
     * Get a GPX String for this Geometry
     * @param geometry The Geometry
     * @return The GPX String
     */
    static String getGpx(Geometry geometry) {
        new GpxWriter().write(geometry)
    }

    /**
     * Get a Geobuf hex string for this Geometry
     * @param geometry The Geometry
     * @return The Geobuf hex string
     */
    static String getGeobuf(Geometry geometry) {
        new GeobufWriter().write(geometry)
    }

    /**
     * Get a Geobuf byte array for this Geometry
     * @param geometry The Geometry
     * @return The Geobuf byte array
     */
    static byte[] getGeobufBytes(Geometry geometry) {
        new GeobufWriter().writeBytes(geometry)
    }

    /**
     * Get a GeoYaml String for this Geometry
     * @param geometry The Geometry
     * @return A GeoYaml String
     */
    static String getYaml(Geometry geometry) {
        return new YamlWriter().write(geometry)
    }

}
