package geoscript.geom.io

import geoscript.geom.Geometry

/**
 * A Groovy Extension Module that adds static methods to the Geometry class.
 * @author Jared Erickson
 */
class GeoRSSStaticGeometryExtensionModule {

    /**
     * Get a Geometry from a GeoRSS String
     * @param geometry The Geometry
     * @param georss A GeoRSS String
     * @return A Geometry
     */
    static Geometry fromGpx(Geometry geometry, String georss) {
        new GeoRSSReader().read(georss)
    }

}
