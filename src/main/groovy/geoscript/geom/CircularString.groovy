package geoscript.geom

import org.geotools.geometry.jts.CircularString as GtCircularString
import org.geotools.geometry.jts.CurvedGeometryFactory

/**
 * A CircularString is a collection of connected circular arc segments.
 * @author Jared Erickson
 */
class CircularString extends LineString {

    /**
     * Create a CircularString from a GeoTools CircularString
     * @param circularString The GeoTools CircularString
     */
    CircularString(GtCircularString circularString) {
        super(circularString)
    }

    /**
     * Create a CircularString from a List of repeated Points
     * @param options The optional named parameters
     * <ul>
     *     <li>tolerance = The distance tolerance used to linearize the curve. Defaults to Double.MAX_VALUE. </li>
     * </ul>
     * @param points The List of repeated Points
     */
    CircularString(Map options = [:], Point...points) {
        this(create(points.collect{it}, options.get("tolerance", Double.MAX_VALUE)))
    }

    /**
     * Create a CircularString from a repeated List of doubles
     * @param options The optional named parameters
     * <ul>
     *     <li>tolerance = The distance tolerance used to linearize the curve. Defaults to Double.MAX_VALUE. </li>
     * </ul>
     * @param coordinates The repeated List of doubles
     */
    CircularString(Map options = [:], List<Double>... coordinates) {
        this(create(coordinates.collect{List<Double> coord ->
            new Point(coord[0], coord[1])
        }, options.get("tolerance", Double.MAX_VALUE)))
    }

    /**
     * Create a CircularString from a List of double Lists or Points
     * @param options The optional named parameters
     * <ul>
     *     <li>tolerance = The distance tolerance used to linearize the curve. Defaults to Double.MAX_VALUE. </li>
     * </ul>
     * @param coordinates The List of double Lists of Points
     */
    CircularString(Map options = [:], List coordinates) {
        this(create(coordinates.collect{ coord ->
            if (coord instanceof Point) {
                coord as Point
            } else {
                new Point(coord[0], coord[1])
            }
        }, options.get("tolerance", Double.MAX_VALUE)))
    }
    
    /**
     * Create a GeoTools CircularString from a List of GeoScript Points 
     * @param points A List of GeoScript Points
     * @param tolerance The tolerance used to linearize the curve
     * @return a GeoTools CircularString
     */ 
    private static GtCircularString create(List<Point> points, double tolerance) {
        double[] coords = new double[points.size() * 2]
        points.eachWithIndex { Point pt, int i ->
            int c = i * 2
            coords[c] = pt.x
            coords[c + 1] = pt.y
        }
        CurvedGeometryFactory cgf = new CurvedGeometryFactory(tolerance)
        new GtCircularString(coords, cgf, tolerance)
    }

    /**
     * Get the curved WKT
     * @return The curved WKT
     */
    String getCurvedWkt() {
        (g as GtCircularString).toCurvedText()
    }

    /**
     * Get the original control Points (not the linearized Points)
     * @return The original control Points
     */
    List<Point> getControlPoints() {
        List<Point> points = []
        double[] d = (g as GtCircularString).controlPoints
        (0..d.length - 1).step(2).collect { i ->
            new Point(d[i], d[i+1])
        }
    }
    
    /**
     * Get the linearized Geometry
     * @return The linearized Geometry
     */
    Geometry getLinear() {
        Geometry.wrap((g as GtCircularString).linearize())
    }

}
