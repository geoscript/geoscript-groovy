package geoscript.geom

import org.geotools.geometry.jts.CircularRing as GtCircularRing
import org.geotools.geometry.jts.CurvedGeometryFactory

/**
 * A CircularRing is a closed CircularString.
 * @author Jared Erickson
 */
class CircularRing extends LinearRing {

    /**
     * Create a CircularRing from a GeoTools CircularRing
     * @param circularRing The GeoTools CircularRing
     */
    CircularRing(GtCircularRing circularRing) {
        super(circularRing)
    }

    /**
     * Create a CircularRing from a List of repeated Points
     * @param options The optional named parameters
     * <ul>
     *     <li>tolerance = The distance tolerance used to linearize the curve. Defaults to Double.MAX_VALUE. </li>
     * </ul>
     * @param points The List of repeated Points
     */
    CircularRing(Map options = [:], Point...points) {
        this(create(points.collect{it}, options.get("tolerance", Double.MAX_VALUE)))
    }

    /**
     * Create a CircularRing from a repeated List of doubles
     * @param options The optional named parameters
     * <ul>
     *     <li>tolerance = The distance tolerance used to linearize the curve. Defaults to Double.MAX_VALUE. </li>
     * </ul>
     * @param coordinates The repeated List of doubles
     */
    CircularRing(Map options = [:], List<Double>... coordinates) {
        this(create(coordinates.collect{List<Double> coord ->
            new Point(coord[0], coord[1])
        }, options.get("tolerance", Double.MAX_VALUE)))
    }

    /**
     * Create a CircularRing from a List of double Lists or Points
     * @param options The optional named parameters
     * <ul>
     *     <li>tolerance = The distance tolerance used to linearize the curve. Defaults to Double.MAX_VALUE. </li>
     * </ul>
     * @param coordinates The List of double Lists of Points
     */
    CircularRing(Map options = [:], List coordinates) {
        this(create(coordinates.collect{ coord ->
            if (coord instanceof Point) {
                coord as Point
            } else {
                new Point(coord[0], coord[1])
            }
        }, options.get("tolerance", Double.MAX_VALUE)))
    }
    
    /**
     * Create a GeoTools CircularRing from a List of GeoScript Points.
     * @param points A List of GeoScript Points
     * @param tolerance The tolerance for linearizing Geometries
     * @return A GeoTools CircularRing
     */
    private static GtCircularRing create(List<Point> points, double tolerance) {
        double[] coords = new double[points.size() * 2]
        points.eachWithIndex { Point pt, int i ->
            int c = i * 2
            coords[c] = pt.x
            coords[c + 1] = pt.y
        }
        if (coords.length < 5) {
            throw new IllegalArgumentException("CircularRing must contain two or more Arcs (5 or more points)!")
        }
        CurvedGeometryFactory cgf = new CurvedGeometryFactory(tolerance)
        new GtCircularRing(coords, cgf, tolerance)
    }

    /**
     * Get the curved WKT
     * @return The curved WKT
     */
    String getCurvedWkt() {
        (g as GtCircularRing).toCurvedText()
    }

    /**
     * Get the original control Points (not the linearized Points)
     * @return The original control Points
     */
    List<Point> getControlPoints() {
        List<Point> points = []
        double[] d = (g as GtCircularRing).controlPoints
        (0..d.length - 1).step(2).collect { i ->
            new Point(d[i], d[i+1])
        }
    }
    
    /**
     * Get the linear Geometry
     * @param The linear Geometry
     */
    Geometry getLinear() {
        Geometry.wrap((g as GtCircularRing).linearize())
    }

}
