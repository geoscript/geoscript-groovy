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

}
