package geoscript.proj

import geoscript.geom.Geometry
import geoscript.geom.Bounds
import org.geotools.geometry.jts.GeometryCoordinateSequenceTransformer
import org.geotools.metadata.iso.citation.Citations
import org.geotools.referencing.CRS
import org.opengis.referencing.crs.CoordinateReferenceSystem
import org.opengis.referencing.operation.MathTransform

/**
 * A Projection is a cartographic projection or coordinate reference system.
 * <p>You can create a Projection with an EPSG Code:</p>
 * <p><blockquote><pre>
 * Projection p = new Projection("EPSG:4326")
 * </pre></blockquote></p>
 * <p>Or with WKT:</p>
 * <p><blockquote><pre>
 * Projection p = new Projection("""GEOGCS["GCS_WGS_1984",DATUM["D_WGS_1984",SPHEROID["WGS_1984",6378137,298.257223563]],PRIMEM["Greenwich",0],UNIT["Degree",0.017453292519943295]]""")
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class Projection {

    /**
     * The wrapped GeoTools CoordinateReferenceSystem
     */
    CoordinateReferenceSystem crs

    /**
     * By default GeoTools Referencing assumes yx or lat/lon
     */
    static {
        if (!System.getProperty("org.geotools.referencing.forceXY")) {
            System.setProperty("org.geotools.referencing.forceXY", "true")
        }
    }

    /**
     * Create a new Projection wrapping a GeoTools
     * CoordinateReferenceSystem
     * @param crs The GeoTools CoordinateReferenceSystem
     */
    Projection(CoordinateReferenceSystem crs) {
        this.crs = crs
    }

    /**
     * Create a new Projection from an existing Projection
     * @param p An existing Projection
     */
    Projection(Projection p) {
        this(p.crs)
    }

    /**
     * Create a new Projection from a String
     * @param str A EPSG ID or WTK
     */
    Projection(String str) {
        this(parse(str))
    }

    /**
     * Get the Identifier.
     * This can be a very slow operation.
     * @return The CRS Lookup Identifier
     */
    String getId() {
        // Sometimes CRS.lookupIdentifier returns null
        CRS.lookupIdentifier(crs, true)
    }

    /**
     * Get the SRS Code
     * @param codeOnly Whether to include the code only (defaults to false)
     * @return The SRS code
     */
    String getSrs(boolean codeOnly = false) {
        CRS.toSRS(this.crs, codeOnly)
    }

    /**
     * Get the EPSG code
     * @return The EPSG code
     */
    int getEpsg() {
        CRS.lookupEpsgCode(crs, true)
    }

    /**
     * Get the well known text
     * @param citation The citation (can be epsg, the default, or esri)
     * @param indentation The number of spaces to indent (defaults to 2)
     * @return The well known texts
     */
    String getWkt(String citation = "epsg", int indentation = 2) {
        if (citation.equalsIgnoreCase("esri")) {
            ((org.geotools.referencing.wkt.Formattable)crs).toWKT(Citations.ESRI, indentation)
        } else {
            ((org.geotools.referencing.wkt.Formattable)crs).toWKT(Citations.EPSG, indentation)
        }
    }

    /**
     * Get the extent for this Projection
     * @return A Bounds
     */
    Bounds getBounds() {
        def extent = CRS.getEnvelope(crs)
        if (extent != null) {
            new Bounds(extent.getMinimum(0), extent.getMinimum(1), extent.getMaximum(0), extent.getMaximum(1), this)
        } else {
            null
        }
    }

    /**
     * Get the valid geographic area for this Projection
     * @return A Bounds
     */
    Bounds getGeoBounds() {
        def extent = CRS.getGeographicBoundingBox(crs)
        if (extent != null) {
            new Bounds(extent.westBoundLongitude, extent.southBoundLatitude, extent.eastBoundLongitude, extent.northBoundLatitude, 'epsg:4326')
        } else {
            null
        }
    }

    /**
     * Transform the Geometry to another Projection
     * @param geom The Geometry
     * @param dest The destination Projection
     * @return A new Geometry reprojected from this Projection to the destination Projection
     */
    Geometry transform(Geometry geom, Projection dest) {
        CoordinateReferenceSystem fromCrs = crs
        CoordinateReferenceSystem toCrs = dest.crs
        MathTransform tx = CRS.findMathTransform(fromCrs, toCrs)
        GeometryCoordinateSequenceTransformer gtx = new GeometryCoordinateSequenceTransformer()
        gtx.mathTransform = tx
        Geometry.wrap(gtx.transform(geom.g))
    }

    /**
     * Transform the Geometry to another Projection
     * @param geom The Geometry
     * @param dest The destination Projection string
     * @return A new Geometry reprojected from this Projection to the destination Projection
     */
    Geometry transform(Geometry geom, String prj) {
        transform(geom, new Projection(prj))
    }

    /**
     * The string representation
     * @return The string representation
     */
    String toString() {
        String projId = id
        projId != null ? projId : wkt
    }

    /**
     * Does this Projection equal the other?
     * @return Whether this Projection equal the other?
     */
    @Override
    boolean equals(Object other) {
        if (!(other instanceof Projection)) {
            false
        } else {
            CRS.equalsIgnoreMetadata(crs, other.crs)
        }

    }

    /**
     * Get the hashcode of this Projection
     * @return The hashcode
     */
    @Override
    int hashCode() {
        crs.hashCode()
    }

    /**
     * Try and parse the string as a CRS or WKT
     */
    private static CoordinateReferenceSystem parse(String str) {
        CoordinateReferenceSystem crs
        if (wellKnownProjections.containsKey(str)) {
            crs = CRS.parseWKT(wellKnownProjections[str])
        }
        else {
            try {
                crs = CRS.decode(str)
            }
            catch (Exception ex1) {
                try {
                    crs = CRS.parseWKT(str)
                }
                catch (Exception ex2) {
                    throw new Exception("Unable to determine projection from ${str}!")
                }
            }
        }
        crs
    }

    /**
     * Reproject the Geometry from the source Projection to the destination
     * Projection
     * @param geom The Geometry
     * @param src The Projection source/from
     * @param dest The Projection destination/to
     * @return A new Geometry
     */
    static Geometry transform(Geometry geom, Projection src, Projection dest) {
        src.transform(geom, dest)
    }

    /**
     * Reproject the Geometry from the source Projection to the destination
     * Projection
     * @param geom The Geometry
     * @param src The Projection String source/from
     * @param dest The Projection String destination/to
     * @return A new Geometry
     */
    static Geometry transform(Geometry geom, String src, String dest) {
        new Projection(src).transform(geom, new Projection(dest))
    }


    /**
     * Get a List of all supported Projections.
     * This is currently reallllllly slow...
     * @return A List of all Projections
     */
    static List<Projection> projections() {
        List<Projection> projections = []
        CRS.getSupportedCodes("epsg").each {
            try {
                projections.add(new Projection("EPSG:${it}"))
            }
            catch (Exception ex){
            }
        }
        projections
    }

    /**
     * A Map (name:wkt) of Well Known Projections
     */
    private static final Map wellKnownProjections = [
            "WGS84": 'GEOGCS["WGS84",  DATUM["WGS84",  SPHEROID["WGS84",  6378137.0, 298.257223563]], PRIMEM["Greenwich", 0.0], UNIT["degree",0.017453292519943295], AXIS["Longitude",EAST], AXIS["Latitude",NORTH]]',
            "Aitoff": 'PROJCS["Aitoff", GEOGCS["WGS84",  DATUM["WGS84",  SPHEROID["WGS84",  6378137.0, 298.257223563]], PRIMEM["Greenwich", 0.0], UNIT["degree",0.017453292519943295], AXIS["Longitude",EAST], AXIS["Latitude",NORTH]], PROJECTION["Aitoff"], UNIT["m", 1.0], AXIS["Easting", EAST], AXIS["Northing", NORTH]]',
            "AlbersEqualArea": 'PROJCS["Albers", GEOGCS["WGS84", DATUM["WGS84", SPHEROID["WGS84", 6378137.0, 298.257223563]], PRIMEM["Greenwich", 0.0], UNIT["degree",0.017453292519943295], AXIS["Longitude",EAST], AXIS["Latitude",NORTH]], PROJECTION["Albers_Conic_Equal_Area"], PARAMETER["central_meridian", -88.0], PARAMETER["latitude_of_origin", 14.5], PARAMETER["false_easting", 328.08333333], PARAMETER["false_northing", 192109.19583333], UNIT["feet", 0.304800609601219], PARAMETER["standard_parallel_1", -20.0], PARAMETER["standard_parallel_2", -33.0], AXIS["x",EAST], AXIS["y",NORTH]]',
            "Cassini": 'PROJCS["Cassini_Soldner", GEOGCS["WGS84",  DATUM["WGS84",  SPHEROID["WGS84",  6378137.0, 298.257223563]], PRIMEM["Greenwich", 0.0], UNIT["degree",0.017453292519943295], AXIS["Longitude",EAST], AXIS["Latitude",NORTH]], PROJECTION["Cassini_Soldner"], UNIT["m", 1.0], AXIS["Easting", EAST], AXIS["Northing", NORTH]]',
            "EckertIV": 'PROJCS["Eckert_IV", GEOGCS["WGS84",  DATUM["WGS84",  SPHEROID["WGS84",  6378137.0, 298.257223563]], PRIMEM["Greenwich", 0.0], UNIT["degree",0.017453292519943295], AXIS["Longitude",EAST], AXIS["Latitude",NORTH]], PROJECTION["Eckert_IV"], UNIT["m", 1.0], AXIS["Easting", EAST], AXIS["Northing", NORTH]]',
            "EquidistantConic": 'PROJCS["World_Equidistant_Conic", GEOGCS["WGS84",  DATUM["WGS84",  SPHEROID["WGS84",  6378137.0, 298.257223563]], PRIMEM["Greenwich", 0.0], UNIT["degree",0.017453292519943295], AXIS["Longitude",EAST], AXIS["Latitude",NORTH]] ,PROJECTION["Equidistant_Conic"],PARAMETER["False_Easting",0.0],PARAMETER["False_Northing",0.0],PARAMETER["Central_Meridian",0.0],PARAMETER["Standard_Parallel_1",60.0],PARAMETER["Standard_Parallel_2",60.0],PARAMETER["Latitude_Of_Origin",0.0],UNIT["Meter",1.0]]',
            "LambertConfic": 'PROJCS["Lambert", GEOGCS["WGS84", DATUM["WGS84", SPHEROID["WGS84", 6378137.0, 298.257223563]], PRIMEM["Greenwich", 0.0], UNIT["degree",0.017453292519943295], AXIS["Longitude",EAST], AXIS["Latitude",NORTH]], PROJECTION["Lambert_Conformal_Conic_1SP"], PARAMETER["central_meridian", -50.0], PARAMETER["latitude_of_origin", 30.0], PARAMETER["scale_factor", 1.0], PARAMETER["false_easting", 0.0], PARAMETER["false_northing", 0.0], UNIT["metre",1.0], AXIS["x",EAST], AXIS["y",NORTH]]',
            "Mercator": 'PROJCS["WGS 84 / Pseudo-Mercator", GEOGCS["WGS 84", DATUM["World Geodetic System 1984", SPHEROID["WGS 84", 6378137.0, 298.257223563, AUTHORITY["EPSG","7030"]], AUTHORITY["EPSG","6326"]], PRIMEM["Greenwich", 0.0, AUTHORITY["EPSG","8901"]], UNIT["degree", 0.017453292519943295], AXIS["Geodetic longitude", EAST], AXIS["Geodetic latitude", NORTH], AUTHORITY["EPSG","4326"]], PROJECTION["Popular Visualisation Pseudo Mercator", AUTHORITY["EPSG","1024"]], PARAMETER["semi-minor axis", 6378137.0], PARAMETER["Latitude of false origin", 0.0], PARAMETER["Longitude of natural origin", 0.0], PARAMETER["Scale factor at natural origin", 1.0], PARAMETER["False easting", 0.0], PARAMETER["False northing", 0.0], UNIT["m", 1.0], AXIS["Easting", EAST], AXIS["Northing", NORTH], AUTHORITY["EPSG","3857"]]',
            "Mollweide": 'PROJCS["Mollweide", GEOGCS["WGS84",  DATUM["WGS84",  SPHEROID["WGS84",  6378137.0, 298.257223563]], PRIMEM["Greenwich", 0.0], UNIT["degree",0.017453292519943295], AXIS["Longitude",EAST], AXIS["Latitude",NORTH]], PROJECTION["Mollweide"], UNIT["m", 1.0], AXIS["Easting", EAST], AXIS["Northing", NORTH]]',
            "NAD27": 'GEOGCS["NAD27", DATUM["North American Datum 1927", SPHEROID["Clarke 1866", 6378206.4, 294.9786982138982, AUTHORITY["EPSG","7008"]], TOWGS84[2.478, 149.752, 197.726, 0.526, -0.498, 0.501, 0.685], AUTHORITY["EPSG","6267"]], PRIMEM["Greenwich", 0.0, AUTHORITY["EPSG","8901"]], UNIT["degree", 0.017453292519943295], AXIS["Geodetic longitude", EAST], AXIS["Geodetic latitude", NORTH], AUTHORITY["EPSG","4267"]]',
            "NAD83": 'GEOGCS["NAD83", DATUM["North American Datum 1983", SPHEROID["GRS 1980", 6378137.0, 298.257222101, AUTHORITY["EPSG","7019"]], TOWGS84[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0], AUTHORITY["EPSG","6269"]], PRIMEM["Greenwich", 0.0, AUTHORITY["EPSG","8901"]], UNIT["degree", 0.017453292519943295], AXIS["Geodetic longitude", EAST], AXIS["Geodetic latitude", NORTH], AUTHORITY["EPSG","4269"]]',
            "Robinson": 'PROJCS["Robinson", GEOGCS["WGS84",  DATUM["WGS84",  SPHEROID["WGS84",  6378137.0, 298.257223563]], PRIMEM["Greenwich", 0.0], UNIT["degree",0.017453292519943295], AXIS["Longitude",EAST], AXIS["Latitude",NORTH]], PROJECTION["Robinson"], UNIT["m", 1.0], AXIS["Easting", EAST], AXIS["Northing", NORTH]]',
            "Sinusoidal": 'PROJCS["MODIS Sinusoidal",GEOGCS["WGS 84",DATUM["WGS_1984",SPHEROID["WGS 84",6378137,298.257223563 ] ], PRIMEM["Greenwich",0.0], UNIT["degree",0.01745329251994328 ]],PROJECTION["Sinusoidal"],UNIT["m",1.0] ]',
            "WagnerIV": 'PROJCS["Wagner_IV", GEOGCS["WGS84",  DATUM["WGS84",  SPHEROID["WGS84",  6378137.0, 298.257223563]], PRIMEM["Greenwich", 0.0], UNIT["degree",0.017453292519943295], AXIS["Longitude",EAST], AXIS["Latitude",NORTH]], PROJECTION["Wagner_IV"], UNIT["m", 1.0], AXIS["Easting", EAST], AXIS["Northing", NORTH]]',
            //"WagnerV": 'PROJCS["Wagner_V", GEOGCS["WGS84",  DATUM["WGS84",  SPHEROID["WGS84",  6378137.0, 298.257223563]], PRIMEM["Greenwich", 0.0], UNIT["degree",0.017453292519943295], AXIS["Longitude",EAST], AXIS["Latitude",NORTH]], PROJECTION["Wagner_V"], UNIT["m", 1.0], AXIS["Easting", EAST], AXIS["Northing", NORTH]]',
            "WinkeTripel": 'PROJCS["WinkeTripel", GEOGCS["WGS84",  DATUM["WGS84",  SPHEROID["WGS84",  6378137.0, 298.257223563]], PRIMEM["Greenwich", 0.0], UNIT["degree",0.017453292519943295], AXIS["Longitude",EAST], AXIS["Latitude",NORTH]], PROJECTION["Winkel Tripel"], UNIT["m", 1.0], AXIS["Easting", EAST], AXIS["Northing", NORTH]]',
            "WorldVanderGrintenI": 'PROJCS["World_Van_der_Grinten_I",GEOGCS["GCS_WGS_1984",DATUM["D_WGS_1984",SPHEROID["WGS_1984",6378137,298.257223563]],PRIMEM["Greenwich",0],UNIT["Degree",0.017453292519943295]],PROJECTION["Van_der_Grinten_I"],PARAMETER["False_Easting",0],PARAMETER["False_Northing",0],PARAMETER["Central_Meridian",0],UNIT["Meter",1]]'
    ]
}
