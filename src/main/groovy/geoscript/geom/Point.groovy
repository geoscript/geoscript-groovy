package geoscript.geom

import com.vividsolutions.jts.geom.Point as JtsPoint
import com.vividsolutions.jts.geom.Coordinate

/**
 * A Point
 */
class Point extends Geometry {
	
    /**
     * Create a Point by wrapping a JTS Point
     */
    Point (JtsPoint p) {
        super(p)
    }
	
    /**
     * Create a Point with an x and y coordinate
     * <p>def p = new Point(111,-47)</p>
     */
    Point(double x, double y) {
        super(Geometry.factory.createPoint(new Coordinate(x,y)))
    }

    /**
     * Add this Point with another to create a MultiPoint
     * <p>def p = new Point(1,2)</p>
     * <p>def m = p + new Point(3,4)</p>
     * <p>MULTIPOINT (1 2, 3 4)</p>
     *
     */
    MultiPoint plus(Point point) {
        new MultiPoint(*[this, point])
    }

}