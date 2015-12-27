package geoscript.geom.io

import geoscript.geom.Geometry

/**
 * A Groovy Extension Module that adds static methods to the Geometry class.
 * @author Jared Erickson
 */
class GeoJSONStaticGeometryExtensionModule {

    /**
     * Get a Geometry from a GeoJSON String
     * @param geometry The Geometry
     * @param geoJSON A GeoJSON String
     * @return A Geometry
     */
    static Geometry fromGeoJSON(Geometry geometry, String geoJSON) {
        new GeoJSONReader().read(geoJSON)
    }

}
