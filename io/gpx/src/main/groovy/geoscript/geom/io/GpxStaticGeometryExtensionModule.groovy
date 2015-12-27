package geoscript.geom.io

import geoscript.geom.Geometry

/**
 * A Groovy Extension Module that adds static methods to the Geometry class.
 * @author Jared Erickson
 */
class GpxStaticGeometryExtensionModule {

    /**
     * Get a Geometry from a GPX String
     * @param geometry The Geometry
     * @param gpx A GPX String
     * @return A Geometry
     */
    static Geometry fromGpx(Geometry geometry, String gpx) {
        new GpxReader().read(gpx)
    }

}
