package geoscript.proj

import geoscript.geom.Geometry
import org.geotools.geometry.jts.GeometryCoordinateSequenceTransformer
import org.geotools.referencing.CRS
import org.opengis.referencing.crs.CoordinateReferenceSystem
import org.opengis.referencing.operation.MathTransform

/**
 * A Projection
 */
class Projection {

    /**
     * The wrapped GeoTools CoordinateReferenceSystem
     */
    CoordinateReferenceSystem crs

    /**
     * Create a new Projection wrapping a GeoTools
     * CoordinateReferenceSystem
     */
    Projection(CoordinateReferenceSystem crs) {
        this.crs = crs
    }

    /**
     * Create a new Projection from an existing Projection
     */
    Projection(Projection p) {
        this(p.crs)
    }

    /**
     * Create a new Projection from a String
     */
    Projection(String str) {
        this(parse(str))
    }

    /**
     * Get the Identifier
     */
    String getId() {
        return CRS.lookupIdentifier(crs, true)
    }

    /**
     * Get the well known text
     */
    String getWkt() {
        return crs.toString()
    }

    /**
     * Transform the Geometry to another Projection
     */
    Geometry transform(Geometry geom, Projection dest) {
        CoordinateReferenceSystem fromCrs = crs
        CoordinateReferenceSystem toCrs = dest.crs
        MathTransform tx = CRS.findMathTransform(fromCrs, toCrs)
        GeometryCoordinateSequenceTransformer gtx = new GeometryCoordinateSequenceTransformer()
        gtx.mathTransform = tx
        return Geometry.wrap(gtx.transform(geom.g))
    }

    /**
     * The string representation
     */
    String toString() {
        return id
    }

    /**
     * Does this Projection equal the other?
     */
    boolean equals(Object other) {
        if (!(other instanceof Projection))
        return false;
        return CRS.equalsIgnoreMetadata(crs, other.crs)
    }

    /**
     * Try and parse the string as a CRS or WKT
     */
    private static CoordinateReferenceSystem parse(String str) {
        CoordinateReferenceSystem crs
        try {
            crs = CRS.decode(str)
        }
        catch(Exception ex1) {
            try {
                crs = CRS.parseWKT(str)
            }
            catch(Exception ex2) {
                throw new Exception("Unable to determine projection from ${str}!")
            }
        }
        return crs
    }

    /**
     * Reproject the Geometry from the source Projection to the destintation
     * Projection
     */
    static Geometry transform(Geometry geom, Projection src, Projection dest) {
        return src.transform(geom, dest)
    }

    /**
     * Reproject the Geometry from the source Projection to the destintation
     * Projection
     */
    static Geometry transform(Geometry geom, String src, String dest) {
        return new Projection(src).transform(geom, new Projection(dest))
    }


    /**
     * Get a List of all supported Projections
     */
    static List<Projection> projections() {
        List<Projection> projections = []
        CRS.getSupportedCodes("epsg").foreach{
            try {
                p = new Projection("EPSG:${it}")
                projectsion.add(p)
            }
            catch (Exception ex){
            }
        }
        return projections
    }
	
}