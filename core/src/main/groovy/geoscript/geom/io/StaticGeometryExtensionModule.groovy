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

}
