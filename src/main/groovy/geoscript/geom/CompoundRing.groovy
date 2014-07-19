package geoscript.geom

import com.vividsolutions.jts.geom.LineString as JtsLineString
import org.geotools.geometry.jts.CompoundRing as GtCompoundRing
import org.geotools.geometry.jts.CurvedGeometryFactory

/**
 * A CompoundRing is a connected set of CircularStrings and LineStrings
 * @author Jared Erickson
 */
class CompoundRing extends LinearRing {

    /**
     * Create a CompoundRing from a GeoTools CompoundRing
     * @param cr The GeoTools CompoundRing
     */
    CompoundRing(GtCompoundRing cr) {
        super(cr)
    }

    /**
     * Create a CompoundRing from a List of repeated LineStrings or CircularStrings
     * @param options The optional named parameters
     * <ul>
     *     <li>tolerance = The distance tolerance used to linearize the curve. Defaults to Double.MAX_VALUE. </li>
     * </ul>
     * @param lineStrings The List of repeated LineStrings or CircularStrings
     */
    CompoundRing(Map options = [:], LineString... lineStrings) {
        this(create(lineStrings.collect{it}, options.get("tolerance", Double.MAX_VALUE)))
    }

    /**
     * Create a CompoundRing from a List of LineStrings or CircularStrings
     * @param options The optional named parameters
     * <ul>
     *     <li>tolerance = The distance tolerance used to linearize the curve. Defaults to Double.MAX_VALUE. </li>
     * </ul>
     * @param lineStrings The List of LineStrings or CircularStrings
     */
    CompoundRing(Map options = [:], List<LineString> lineStrings) {
        this(create(lineStrings, options.get("tolerance", Double.MAX_VALUE)))
    }

    /**
     * Create a GeoTools CompoundRing from a List of GeoScript LineStrings or CircularStrings
     * @param lineStrings The List of GeoScript LineStrings or CircularStrings
     * @param tolerance The distance tolerance used to linearize the curve.
     * @return A GeoTools CompoundRing
     */
    private static create(List<LineString> lineStrings, double tolerance) {
        CurvedGeometryFactory cgf = new CurvedGeometryFactory(tolerance)
        new GtCompoundRing(lineStrings.collect{it.g as JtsLineString}, cgf, tolerance)
    }

    /**
     * Get the curved WKT
     * @return The curved WKT
     */
    String getCurvedWkt() {
        (g as GtCompoundRing).toCurvedText()
    }

    /**
     * Get the original LineStrings or CircularStrings (not linearized)
     * @return The original LineStrings or CircularStrings
     */
    List<LineString> getComponents() {
        (g as GtCompoundRing).components.collect{Geometry.wrap(it)}
    }

    /**
     * Get the linearized Geometry
     * @return The linearized Geometry
     */
    Geometry getLinear() {
        Geometry.wrap((g as GtCompoundRing).linearize())
    }

}
