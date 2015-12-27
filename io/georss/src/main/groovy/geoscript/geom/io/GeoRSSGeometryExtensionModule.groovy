package geoscript.geom.io

import geoscript.geom.Geometry

/**
 * A Groovy Extension Module that adds methods to the Geometry class.
 * @author Jared Erickson
 */
class GeoRSSGeometryExtensionModule {

    /**
     * Get a GeoRSS String for this Geometry
     * @param geometry The Geometry
     * @return The GPX String
     */
    static String getGeoRSS(Geometry geometry) {
        new GeoRSSWriter().write(geometry)
    }

}
