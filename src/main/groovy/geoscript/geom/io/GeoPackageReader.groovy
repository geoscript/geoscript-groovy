package geoscript.geom.io

import org.locationtech.jts.geom.Envelope
import geoscript.geom.Bounds
import geoscript.geom.Geometry
import geoscript.proj.Projection
import org.geotools.geometry.jts.ReferencedEnvelope
import org.geotools.geopkg.geom.GeoPkgGeomReader

/**
 * Read GeoPackage encoded Geometries
 * @author Jared Erickson
 */
class GeoPackageReader implements Reader {

    /**
     * Read a Geometry from a String
     * @param str The String
     * @return A Geometry
     */
    Geometry read(String str) {
        read(str.decodeHex())
    }

    /**
     * Read a Geometry from a byte array
     * @param bytes The byte array
     * @return A Geometry
     */
    Geometry read(byte[] bytes) {
        GeoPkgGeomReader reader = new GeoPkgGeomReader(bytes)
        Geometry.wrap(reader.get())
    }

    /**
     * Just read the Bounds from the hex string
     * @param str The hex string
     * @return The Bounds
     */
    Bounds readBounds(String str) {
        readBounds(str.decodeHex())
    }

    /**
     * Just read the Bounds from the byte arrya
     * @param byte[] The byte array
     * @return The Bounds
     */
    Bounds readBounds(byte[] bytes) {
        GeoPkgGeomReader reader = new GeoPkgGeomReader(bytes)
        Envelope env = reader.envelope
        Projection proj = null
        int srid = reader.header.srid
        if (srid > 0) {
            try {
                proj =  new Projection("EPSG:${reader.header.srid}")
            } catch(Exception e) {
                // Do nothing
            }
        }
        new Bounds(env.minX, env.minY, env.maxX, env.maxY, proj)
    }

}
