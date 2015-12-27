package geoscript.geom.io

import geoscript.geom.Geometry

/**
 * A Groovy Extension Module that adds static methods to the Geometry class.
 * @author Jared Erickson
 */
class GmlStaticGeometryExtensionModule {

    /**
     * Get a Geometry from a GML2 String
     * @param geometry The Geometry
     * @param gml2 A GML2 String
     * @return A Geometry
     */
    static Geometry fromGML2(Geometry geometry, String gml2) {
        new Gml2Reader().read(gml2)
    }

    /**
     * Get a Geometry from a GML3 String
     * @param geometry The Geometry
     * @param gml3 A GML3 String
     * @return A Geometry
     */
    static Geometry fromGML3(Geometry geometry, String gml3) {
        new Gml3Reader().read(gml3)
    }

}
