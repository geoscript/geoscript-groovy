package geoscript.geom.io

import geoscript.geom.Geometry

/**
 * A Groovy Extension Module that adds methods to the Geometry class.
 * @author Jared Erickson
 */
class GpxGeometryExtensionModule {

    /**
     * Get a GPX String for this Geometry
     * @param geometry The Geometry
     * @return The GPX String
     */
    static String getGpx(Geometry geometry) {
        new GpxWriter().write(geometry)
    }

}
