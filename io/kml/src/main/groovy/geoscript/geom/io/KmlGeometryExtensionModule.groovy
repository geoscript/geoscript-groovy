package geoscript.geom.io

import geoscript.geom.Geometry

/**
 * A Groovy Extension Module that adds methods to the Geometry class.
 * @author Jared Erickson
 */
class KmlGeometryExtensionModule {

    /**
     * Get a KML String for this Geometry
     * @param geometry The Geometry
     * @return The KML String
     */
    static String getKml(Geometry geometry) {
        new KmlWriter().write(geometry)
    }

}
