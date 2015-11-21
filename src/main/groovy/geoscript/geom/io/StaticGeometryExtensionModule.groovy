package geoscript.geom.io

import geoscript.geom.Geometry

/**
 * A Groovy Extension Module that adds static methods to the Geometry class.
 * @author Jared Erickson
 */
class StaticGeometryExtensionModule {

    /**
     * Get a Geometry from WKT
     * @param geometry The Geometry
     * @param wkt A WKT String
     * @return A Geometry
     */
    static Geometry fromWKT(Geometry geometry, String wkt) {
        new WktReader().read(wkt)
    }

    /**
     * Get a Geometry from WKB byte array
     * @param geometry The Geometry
     * @param wkb The WKB byte array
     * @return A Geometry
     */
    static Geometry fromWKB(Geometry geometry, byte[] wkb) {
        new WkbReader().read(wkb)
    }

    /**
     * Get a Geometry from WKB hex string
     * @param geometry The Geometry
     * @param wkb The WKB hex string
     * @return A Geometry
     */
    static Geometry fromWKB(Geometry geometry, String wkb) {
        new WkbReader().read(wkb)
    }

    /**
     * Get a Geometry from a KML String
     * @param geometry The Geometry
     * @param kml A KML String
     * @return A Geometry
     */
    static Geometry fromKml(Geometry geometry, String kml) {
        new KmlReader().read(kml)
    }

    /**
     * Get a Geometry from a GeoJSON String
     * @param geometry The Geometry
     * @param geoJSON A GeoJSON String
     * @return A Geometry
     */
    static Geometry fromGeoJSON(Geometry geometry, String geoJSON) {
        new GeoJSONReader().read(geoJSON)
    }

    /**
     * Get a Geometry from a GML2 String
     * @param geometry The Geometry
     * @param gml2 A GML2 String
     * @return A Geometry
     */
    static Geometry fromGML2(Geometry geometry, String gml2) {
        new Gml2Reader().read(gml2)
    }

    /**
     * Get a Geometry from a GML3 String
     * @param geometry The Geometry
     * @param gml3 A GML3 String
     * @return A Geometry
     */
    static Geometry fromGML3(Geometry geometry, String gml3) {
        new Gml3Reader().read(gml3)
    }

    /**
     * Get a Geometry from a GPX String
     * @param geometry The Geometry
     * @param gpx A GPX String
     * @return A Geometry
     */
    static Geometry fromGpx(Geometry geometry, String gpx) {
        new GpxReader().read(gpx)
    }

    /**
     * Get a Geometry from a Geobuf Hex String
     * @param geometry The Geometry
     * @param geobuf A Geobuf Hex String
     * @return A Geometry
     */
    static Geometry fromGeobuf(Geometry geometry, String geobuf) {
        new GeobufReader().read(geobuf)
    }

    /**
     * Get a Geometry from a Geobuf byte array
     * @param geometry The Geometry
     * @param geobuf A Geobuf byte array
     * @return A Geometry
     */
    static Geometry fromGeobuf(Geometry geometry, byte[] geobuf) {
        new GeobufReader().read(geobuf)
    }

}
