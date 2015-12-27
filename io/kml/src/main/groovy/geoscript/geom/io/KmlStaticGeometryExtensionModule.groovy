package geoscript.geom.io

import geoscript.geom.Geometry

/**
 * A Groovy Extension Module that adds static methods to the Geometry class.
 * @author Jared Erickson
 */
class KmlStaticGeometryExtensionModule {

    /**
     * Get a Geometry from a KML String
     * @param geometry The Geometry
     * @param kml A KML String
     * @return A Geometry
     */
    static Geometry fromKml(Geometry geometry, String kml) {
        new KmlReader().read(kml)
    }

}
