package geoscript.geom

import org.geotools.geometry.jts.ReferencedEnvelope
import com.vividsolutions.jts.geom.Envelope
import geoscript.proj.Projection
import com.vividsolutions.jts.util.GeometricShapeFactory
import com.vividsolutions.jts.geom.util.SineStarFactory

/**
 * A Bounds is an Envelope with a Projection.
 * <p><code>
 * Bounds b = new Bounds(1,2,3,4, new Projection("EPSG:2927"))
 * </p></code>
 * @author Jared Erickson
 */
class Bounds {
	
    /**
     * The GeoTools' wrapped ReferencedEnvelope
     */
    ReferencedEnvelope env
	
    /**
     * Create a new Bounds wrapping a ReferencedEnvelope.
     * <p><code>ReferencedEnvelope e = new ReferencedEnvelope(1,3,2,4,null)</code></p>
     * <p><code>Bounds b = new Bounds(e)</code></p>
     * @param env The ReferencedEnvelope
     */
    Bounds(ReferencedEnvelope env) {
        this.env = env
    }

    /**
     * Create a new Bounds wrapping an Envelope.
     * <p><code>Envelope e = new Envelope(1,3,2,4)</code></p>
     * <p><code>Bounds b = new Bounds(e)</code></p>
     * @param env The ReferencedEnvelope
     */
    Bounds(Envelope env) {
        this(new ReferencedEnvelope(env, null))
    }

    /**
     * Create a new Bounds with minX, minY, maxX, and maxY coordinates.
     * <p><code>Bounds b = new Bounds(1,2,3,4)</code></p>
     * @param minX The left/west most coordinate (minX)
     * @param minY the bottom/minY most coordinate (minY)
     * @param maxX The right/maxX most coordinate (maxX)
     * @param maxY The top/maxY most coordinate (maxY)
     */
    Bounds(double minX, double minY, double maxX, double maxY) {
        this(minX, minY, maxX, maxY, null)
    }
	
    /**
     * Create a new Bounds with minX, minY, maxX, and maxY coordinates
     * and a Projection.
     * <p><code>Bounds b = new Bounds(1,2,3,4, new Projection("EPSG:2927"))</code></p>
     * <p><code>Bounds b = new Bounds(1,2,3,4, "EPSG:2927")</code></p>
     * @param minX The left/minX most coordinate (minX)
     * @param minY the bottom/minY most coordinate (minY)
     * @param maxX The right/maxX most coordinate (maxX)
     * @param maxY The top/maxY most coordinate (maxY)
     * @param proj The Projection can either be a Projection or a String
     */
    Bounds(double minX, double minY, double maxX, double maxY, def proj) {
        this(new ReferencedEnvelope(minX, maxX, minY, maxY, new Projection(proj).crs))
    }

    /**
     * Create a new Bounds from a Point (which can either be the origin/lower left or center) and a width and height
     * @param point The Point origin or center
     * @param width The width
     * @param height The height
     * @param isOrigin Whether the Point is the origin (true) or the center (false)
     */
    Bounds(Point point, double width, double height, boolean isOrigin = true) {
        this(createBounds(point, width, height, isOrigin).env)
    }

    /**
     * Create a new Bounds from a Point (which can either be the origin/lower left or center) and a width and height
     * @param point The Point origin or center
     * @param width The width
     * @param height The height
     * @param isOrigin Whether the Point is the origin (true) or the center (false)
     */
    private static Bounds createBounds(Point point, double width, double height, boolean isOrigin = true) {
        // Lower left
        if (isOrigin) {
            return new Bounds(point.x, point.y, point.x + width, point.y + height)
        }
        // Center
        else {
            return new Bounds(point.x - width / 2, point.y - height / 2, point.x + width / 2, point.y + height / 2)
        }
    }

    /**
     * Get the left/west most coordinate (minX)
     * @return The left/west most coordinate (minX)
     */
    double getMinX() {
        env.minX()
    }

    /**
     * Get the right/east most coordinate (maxX)
     * @return The right/east most coordinate (maxX)
     */
    double getMaxX() {
        env.maxX()
    }

    /**
     * Get the bottom/south most coordinate (minY)
     * @return The bottom/south most coordinate (minY)
     */
    double getMinY() {
        env.minY()
    }

    /**
     * Get the top/north most coordinate (maxY)
     * @return The top/north most coordinate (maxY)
     */
    double getMaxY() {
        env.maxY()
    }

    /**
     * Get the area of this Bounds
     * @return The area
     */
    double getArea() {
        env.area
    }

    /**
     * Get the width
     * @return The width
     */
    double getWidth() {
        env.width
    }

    /**
     * Get the height
     * @return The height
     */
    double getHeight() {
        env.height
    }

    /**
     * Expand the Bounds by the given distance in all directions
     * @param distance The distance
     * @return The modified Bounds
     */
    Bounds expandBy(double distance) {
        env.expandBy(distance)
        this
    }

    /**
     * Expand this Bounds to include another Bounds
     * @param other Another Bounds
     * @return The modified Bounds
     */
    Bounds expand(Bounds other) {
        env.expandToInclude(other.env)
        this
    }

    /**
     * Scales the current Bounds by a specific factor.
     * @param factor The scale factor
     * @return The new scaled Bounds
     */
    Bounds scale(double factor) {
        double w = width * (factor - 1) / 2
        double h = height * (factor - 1) / 2
        new Bounds(minX - w, minY - h, maxX + w, maxY + h)
    }


    /**
     * Get the Projection (if any) or null
     * @return The Projection (if any) or null
     */
    Projection getProj() {
        if (env.coordinateReferenceSystem)
            return new Projection(env.coordinateReferenceSystem)
        else
            return null
    }
	
    /**
     * Reprojects the Bounds
     * @param proj A Projection or String
     * @return The reprojected Bounds
     */
    Bounds reproject(def proj) {
        proj = new Projection(proj)
        new Bounds(env.transform(proj.crs, true))
    }

    /**
     * Convert this Bounds into a Geometry object
     * @return A Geometry
     */
    Geometry getGeometry() {
        Geometry.wrap(Geometry.factory.toGeometry(env))
    }

    /**
     * Convert this Bounds into a Polygon
     * @return A Polygon
     */
    Polygon getPolygon() {
        new Polygon([minX, minY], [minX, maxY], [maxX, maxY], [maxX, minY], [minX, minY])
    }
    
    /**
     * Partitions the bounding box into a set of smaller bounding boxes.
     * @param res The resolution to tile at and should be in range 0-1.
     * @return A List of smaller bounding boxes
     */
    List<Bounds> tile(double res) {
        double dx = width * res
        double dy = height * res
        List bounds = []
        double y = minY
        while (y < maxY) {
            double x = minX
            while (x < maxX) {
                bounds.add(new Bounds(x,y,Math.min(x + dx, maxX), Math.min(y+dy, maxY), proj))
                x += dx
            }
            y += dy
        }
        bounds
    }

    /**
     * Calculate a quad tree for this Bounds between the start and stop levels. The Closure
     * is called for each new Bounds generated.
     * @param start The start level
     * @param stop The stop level
     * @param closure The Closure called for each new Bounds
     */
    void quadTree(int start, int stop, Closure closure) {
        Projection p = getProj()
        for(int level = start; level < stop; level++) {
            double factor = Math.pow(2, level)
            double dx = (this.maxX - this.minX) / factor
            double dy = (this.maxY - this.minY) / factor
            double minx = this.minX
            for(int x = 0; x < factor; ++x) {
                double miny = this.minY
                for(int y = 0; y < factor; ++y) {
                    closure(new Bounds(minx, miny, minx + dx, miny + dy, p))
                    miny += dy
                }
                minx += dx
            }
        }
    }

    /**
     * Get whether the Bounds is empty (width and height are zero)
     * @return Whether the Bounds is empty
     */
    boolean isEmpty() {
       env.empty 
    }
    
    /**
     * Determine whether this Bounds equals another Bounds
     * @param other The other Bounds
     * @return Whether this Bounds and the other Bounds are equal
     */
    boolean equals(Object other) {
        other instanceof Bounds && env.equals(other.env)
    }
    
    /**
     * Determine whether this Bounds contains the other Bounds
     * @param other The other Bounds
     * @return Whether this Bounds contains the other Bounds
     */
    boolean contains(Bounds other) {
        env.contains(other.env)
    }
    
    /**
     * Determine whether this Bounds intersects with the other Bounds
     * @param other The other Bounds
     * @return Whether this Bounds intersects with the other Bounds
     */
    boolean intersects(Bounds other) {
        env.intersects(other.env)
    }
    
    /**
     * Calculate the intersection between this Bounds and the other Bounds
     * @param other The other Bounds
     * @return The intersection Bounds between this and the other Bounds
     */
    Bounds intersection(Bounds other) {
        new Bounds(env.intersection(other.env))
    }

    /**
     * Ensure that the Bounds has a width and height.  Handle vertical and horizontal lines and points.
     * @return A new Bounds with a width and height
     */
    Bounds ensureWidthAndHeight() {
        Bounds b = new Bounds(env)
        if (b.width == 0 || b.height == 0) {
            if (b.height > 0) {
                double h = b.height / 2.0
                b = new Bounds(b.minX - h, b.minY, b.maxX + h, b.maxY, b.proj)
            } else if (b.width > 0) {
                double w = b.width / 2.0
                b = new Bounds(b.minX, b.minY - w, b.maxX, b.maxY + w, b.proj)
            } else {
                def e = new Point(b.minX, b.minY).buffer(0.1).envelopeInternal
                b = new Bounds(e.minX, e.minY, e.maxX, e.maxY, proj)
            }
        }
        return b
    }

    /**
     * Get a value from this Bounds at the given index (minX = 0, minY = 1,
     * maxX = 2, maxY = 3).
     * <p><code>Bounds b = new Bounds(1,2,3,4)</code></p>
     * <p><code>def w = b[0]</code></p>
     * <p><code>def (w,s,e,n) = b</code></p>
     * @return A value from this Bounds or null if the index is greater than 3
     */
    Object getAt(int index) {
        if (index == 0) {
            minX
        } else if (index == 1) {
            minY
        } else if (index == 2) {
            maxX
        } else if (index == 3) {
            maxY
        } else {
            null
        }
    }

    /**
     * Create a rectangle or square based on this Bound's extent with the given number of points and rotation.
     * @param numPoints The number of points
     * @param rotation The rotation angle
     * @return The rectangular Geometry
     */
    Polygon createRectangle(int numPoints = 20, double rotation = 0) {
        Geometry.wrap(createGeometricShapeFactory(numPoints, rotation).createRectangle()) as Polygon
    }

    /**
     * Create an ellipse or circle based on this Bound's extent with the given number of points and rotation.
     * @param numPoints The number of points
     * @param rotation The rotation angle
     * @return The elliptical or circular Geometry
     */
    Polygon createEllipse(int numPoints = 20, double rotation = 0) {
        Geometry.wrap(createGeometricShapeFactory(numPoints, rotation).createEllipse()) as Polygon
    }

    /**
     * Create a squircle based on this Bound's extent with the given number of points and rotation.
     * @param numPoints The number of points
     * @param rotation The rotation angle
     * @return The squircular Geometry
     */
    Polygon createSquircle(int numPoints = 20, double rotation = 0) {
        Geometry.wrap(createGeometricShapeFactory(numPoints, rotation).createSquircle()) as Polygon
    }

    /**
     * Create a super circle based on this Bound's extent with the given number of points and rotation.
     * @param power The positive power
     * @param numPoints The number of points
     * @param rotation The rotation angle
     * @return The super circular Geometry
     */
    Polygon createSuperCircle(double power, int numPoints = 20, double rotation = 0) {
        Geometry.wrap(createGeometricShapeFactory(numPoints, rotation).createSupercircle(power)) as Polygon
    }

    /**
     * Create a LineString arc based on this Bound's extent from the start angle (in radians) for the given angle extent
     * (also in radians) with the given number of points and rotation.
     * @param startAngle The start angle (in radians)
     * @param angleExtent The extent of the angle (in radians)
     * @param numPoints The number of points
     * @param rotation The rotation angle
     * @return The LineString arc
     */
    LineString createArc(double startAngle, double angleExtent, int numPoints = 20, double rotation = 0) {
        Geometry.wrap(createGeometricShapeFactory(numPoints, rotation).createArc(startAngle, angleExtent)) as LineString
    }

    /**
     * Create a Polygon arc based on this Bound's extent from the start angle (in radians) for the given angle extent
     * (also in radians) with the given number of points and rotation.
     * @param startAngle The start angle (in radians)
     * @param angleExtent The extent of the angle (in radians)
     * @param numPoints The number of points
     * @param rotation The rotation angle
     * @return The Polygon arc
     */
    Polygon createArcPolygon(double startAngle, double angleExtent, int numPoints = 20, double rotation = 0) {
        Geometry.wrap(createGeometricShapeFactory(numPoints, rotation).createArcPolygon(startAngle, angleExtent)) as Polygon
    }

    /**
     * Create a sine star based on this Bound's extent with the given number of arms and arm length ratio with the
     * given number of points and rotation.
     * @param numberOfArms The number of arms
     * @param armLengthRatio The arm length ratio
     * @param numPoints The number of points
     * @param rotation The rotation angle
     * @return The sine star Polygon
     */
    Polygon createSineStar(int numberOfArms, double armLengthRatio, int numPoints = 20, double rotation = 0) {
        def shapeFactory = createGeometricShapeFactory(numPoints, rotation, new SineStarFactory())  as SineStarFactory
        shapeFactory.setArmLengthRatio(armLengthRatio)
        shapeFactory.setNumArms(numberOfArms)
        Geometry.wrap(shapeFactory.createSineStar()) as Polygon
    }

    /**
     * Create a GeometricShapeFactory and initialize it with number of points and a rotation.
     * @param numPoints The number of points
     * @param rotation The rotation
     * @param shapeFactory The GeometricShapeFactory
     * @return The initialized GeometricShapeFactory
     */
    private GeometricShapeFactory createGeometricShapeFactory(int numPoints = 100, double rotation = 0.0, GeometricShapeFactory shapeFactory = new GeometricShapeFactory()) {
        shapeFactory.numPoints = numPoints
        shapeFactory.rotation = rotation
        shapeFactory.envelope = env
        shapeFactory
    }

    /**
     * The string representation
     * @return The string representation
     */
    String toString() {
        "(${minX},${minY},${maxX},${maxY}${if (proj != null){',' + proj.id } else {''}})"
    }
}
