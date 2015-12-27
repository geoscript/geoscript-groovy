package geoscript.geom.io

import geoscript.geom.Geometry

/**
 * A Groovy Extension Module that adds static methods to the Geometry class.
 * @author Jared Erickson
 */
class GeobufStaticGeometryExtensionModule {

    /**
     * Get a Geometry from a Geobuf Hex String
     * @param geometry The Geometry
     * @param geobuf A Geobuf Hex String
     * @return A Geometry
     */
    static Geometry fromGeobuf(Geometry geometry, String geobuf) {
        new GeobufReader().read(geobuf)
    }

    /**
     * Get a Geometry from a Geobuf byte array
     * @param geometry The Geometry
     * @param geobuf A Geobuf byte array
     * @return A Geometry
     */
    static Geometry fromGeobuf(Geometry geometry, byte[] geobuf) {
        new GeobufReader().read(geobuf)
    }

}
