package geoscript.geom.io

import geoscript.geom.Geometry

/**
 * A Groovy Extension Module that adds methods to the Geometry class.
 * @author Jared Erickson
 */
class GmlGeometryExtensionModule {

    /**
     * Get a GML 2 String for this Geometry
     * @param geometry The Geometry
     * @return The GML 2 String
     */
    static String getGml2(Geometry geometry) {
        new Gml2Writer().write(geometry)
    }

    /**
     * Get a GML 3 String for this Geometry
     * @param geometry The Geometry
     * @return The GML 3 String
     */
    static String getGml3(Geometry geometry) {
        new Gml3Writer().write(geometry)
    }

}
