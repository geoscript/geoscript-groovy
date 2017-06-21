package geoscript.geom.io

import geoscript.geom.Geometry
import org.geotools.geopkg.geom.GeoPkgGeomWriter

/**
 * Write a Geometry to a GeoPackage Geometry byte array or hex string
 * @author Jared Erickson
 */
class GeoPackageWriter implements Writer {

    private final GeoPkgGeomWriter geoPkgGeomWriter

    /**
     * Create a new GeoPackageWriter
     * @param options Optional named parameters
     * <ol>
     *     <li>writeEnvelope = Whether to include the envelope or not (default is true)</li>
     * </ol>
     */
    GeoPackageWriter(Map options = [:]) {
        GeoPkgGeomWriter.Configuration config = new GeoPkgGeomWriter.Configuration()
        config.writeEnvelope = options.get("writeEnvelope", true) as Boolean
        this.geoPkgGeomWriter = new GeoPkgGeomWriter(config)
    }

    /**
     * Write the Geometry to WKB hex String
     * @param geom The Geometry
     * @return A WKB hex String
     */
    String write(Geometry geom) {
        writeBytes(geom).encodeHex().toString()
    }

    /**
     * Write the Geometry to WKB byte array
     * @param geom The Geometry
     * @return A WKB byte array
     */
    byte[] writeBytes(Geometry geom) {
        geoPkgGeomWriter.write(geom.g)
    }

}
