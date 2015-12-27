package geoscript.geom.io

import geoscript.geom.Geometry

/**
 * A Groovy Extension Module that adds methods to the Geometry class.
 * @author Jared Erickson
 */
class GeobufGeometryExtensionModule {

    /**
     * Get a Geobuf hex string for this Geometry
     * @param geometry The Geometry
     * @return The Geobuf hex string
     */
    static String getGeobuf(Geometry geometry) {
        new GeobufWriter().write(geometry)
    }

    /**
     * Get a Geobuf byte array for this Geometry
     * @param geometry The Geometry
     * @return The Geobuf byte array
     */
    static byte[] getGeobufBytes(Geometry geometry) {
        new GeobufWriter().writeBytes(geometry)
    }

}
