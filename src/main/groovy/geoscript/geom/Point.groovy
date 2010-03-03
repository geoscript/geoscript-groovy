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
     */
    Point(double x, double y) {
        super(Geometry.factory.createPoint(new Coordinate(x,y)))
    }

    MultiPoint plus(Point point) {
        new MultiPoint(*[this, point])
    }

}