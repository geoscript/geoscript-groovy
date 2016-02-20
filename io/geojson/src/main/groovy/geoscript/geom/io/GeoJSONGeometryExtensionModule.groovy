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
     * @param options Optional named parameters:
     * <ol>
     *     <li> decimals = The number of decimals (defaults to 4) </li>
     * </ol>
     * @return The GeoJSON String
     */
    static String getGeoJSON(Geometry geometry, Map options = [:]) {
        new GeoJSONWriter().write(options, geometry)
    }

}
