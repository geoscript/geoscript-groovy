package geoscript.geom.io

import geoscript.geom.Geometry

/**
 * A Groovy Extension Module that adds methods to the Geometry class.
 * @author Jared Erickson
 */
class GeoJSONGeometryExtensionModule {

    /**
     * Get a GeoJSON String for this Geometry
     * @param geometry The Geometry
     * @return The GeoJSON String
     */
    static String getGeoJSON(Geometry geometry) {
        new GeoJSONWriter().write(geometry)
    }

}
