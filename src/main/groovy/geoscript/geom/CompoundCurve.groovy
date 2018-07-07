package geoscript.geom

import org.locationtech.jts.geom.LineString as JtsLineString
import org.geotools.geometry.jts.CompoundCurve as GtCompoundCurve
import org.geotools.geometry.jts.CurvedGeometryFactory

/**
 * A CompoundCurve is a connected set of CircularStrings and LineStrings
 * @author Jared Erickson
 */
class CompoundCurve extends LineString {

    /**
     * Create a CompoundCurve from a GeoTools CompoundCurve
     * @param cc The GeoTools CompoundCurve
     */
    CompoundCurve(GtCompoundCurve cc) {
        super(cc)
    }

    /**
     * Create a CompoundCurve from a List of repeated LineStrings or CircularStrings
     * @param options The optional named parameters
     * <ul>
     *     <li>tolerance = The distance tolerance used to linearize the curve. Defaults to Double.MAX_VALUE. </li>
     * </ul>
     * @param lineStrings The List of repeated LineStrings or CircularStrings
     */
    CompoundCurve(Map options = [:], LineString... lineStrings) {
        this(create(lineStrings.collect{it}, options.get("tolerance", Double.MAX_VALUE)))
    }

    /**
     * Create a CompoundCurve from a List of LineStrings or CircularStrings
     * @param options The optional named parameters
     * <ul>
     *     <li>tolerance = The distance tolerance used to linearize the curve. Defaults to Double.MAX_VALUE. </li>
     * </ul>
     * @param lineStrings The List of LineStrings or CircularStrings
     */
    CompoundCurve(Map options = [:], List<LineString> lineStrings) {
        this(create(lineStrings, options.get("tolerance", Double.MAX_VALUE)))
    }

    /**
     * Create a GeoTools CompoundCurve from a List of GeoScript LineStrings or CircularStrings
     * @param lineStrings The List of GeoScript LineStrings or CircularStrings
     * @param tolerance The distance tolerance used to linearize the curve.
     * @return A GeoTools CompoundCurve
     */
    private static create(List<LineString> lineStrings, double tolerance) {
        CurvedGeometryFactory cgf = new CurvedGeometryFactory(tolerance)
        new GtCompoundCurve(lineStrings.collect{it.g as JtsLineString}, cgf, tolerance)
    }

    /**
     * Get the curved WKT
     * @return The curved WKT
     */
    String getCurvedWkt() {
        (g as GtCompoundCurve).toCurvedText()
    }

    /**
     * Get the original LineStrings or CircularStrings (not linearized)
     * @return The original LineStrings or CircularStrings
     */
    List<LineString> getComponents() {
       (g as GtCompoundCurve).components.collect{Geometry.wrap(it)}
    }

    /**
     * Get the linearized Geometry
     * @return The linearized Geometry
     */
    Geometry getLinear() {
        Geometry.wrap((g as GtCompoundCurve).linearize())
    }

}
