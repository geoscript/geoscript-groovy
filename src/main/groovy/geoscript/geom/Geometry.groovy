package geoscript.geom

import org.locationtech.jts.algorithm.construct.LargestEmptyCircle
import org.locationtech.jts.algorithm.construct.MaximumInscribedCircle
import org.locationtech.jts.geom.Geometry as JtsGeometry
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.prep.PreparedGeometryFactory
import org.locationtech.jts.geom.Envelope
import org.locationtech.jts.geom.IntersectionMatrix
import org.locationtech.jts.geom.util.AffineTransformation
import org.locationtech.jts.geom.util.GeometryFixer
import org.locationtech.jts.operation.buffer.BufferParameters
import org.locationtech.jts.operation.buffer.BufferOp
import org.locationtech.jts.awt.FontGlyphReader
import org.locationtech.jts.operation.buffer.OffsetCurve
import org.locationtech.jts.operation.buffer.VariableBuffer
import org.locationtech.jts.operation.overlay.snap.GeometrySnapper
import geoscript.geom.io.Reader
import geoscript.geom.io.Readers
import org.locationtech.jts.geom.PrecisionModel
import org.locationtech.jts.precision.GeometryPrecisionReducer
import org.geotools.geometry.jts.CurvedGeometries
import org.geotools.geometry.jts.OffsetCurveBuilder

/**
 * The base class for other Geometries.
 * @author Jared Erickson
 */
class Geometry {
	
    /**
     * The wrapped JTS Geometry
     */
    JtsGeometry g
	
    /**
     * The JTS GeometryFactory used to create JTS Geometry
     */
    static GeometryFactory factory = new GeometryFactory()

    /**
     * The JTS PreparedGeometryFactory used to create prepared JTS Geometry
     */
    static PreparedGeometryFactory preparedFactory = new PreparedGeometryFactory()

    /**
     * Create a new Geometry wrapping a JTS Geometry
     * @param g The JTS Geometry
     */
    Geometry (JtsGeometry g) {
        this.g = g
    }
	
    /**
     * Round Buffer cap style
     */
    static final int CAP_ROUND = org.locationtech.jts.operation.buffer.BufferParameters.CAP_ROUND

    /**
     * Butt Buffer cap style
     */
    static final int CAP_BUTT = org.locationtech.jts.operation.buffer.BufferParameters.CAP_SQUARE

    /**
     * Square Buffer cap style
     */
    static final int CAP_SQUARE = org.locationtech.jts.operation.buffer.BufferParameters.CAP_SQUARE

    /**
     * Buffer the Geometry by some distance.
     * @param distance The buffer distance 
     * @param quadrantSegments The number of quadrant segments (the default is 8)
     * @param endCapStyle The end cap style (CAP_ROUND is default, also CAP_BUTT, or CAP_SQUARE)
     * @return The buffer Geometry
     */
    Geometry buffer(double distance, int quadrantSegments = 8, int endCapStyle = CAP_ROUND) {
        wrap(g.buffer(distance, quadrantSegments, endCapStyle))
    }

    /**
     * Create a single sided buffer (+ for right side, - for left side)
     * @param distance The buffer distance
     * @param quadrantSegments The number of quadrant segments (the default is 8)
     * @param endCapStyle THe end cap style (CAP_ROUND is default, also CAP_BUTT, or CAP_SQUARE)
     * @return The single sided buffer Geometry
     */
    Geometry singleSidedBuffer(double distance, int quadrantSegments = 8, int endCapStyle = CAP_ROUND) {
        BufferParameters params = new BufferParameters()
        params.singleSided = true
        params.quadrantSegments = quadrantSegments
        params.endCapStyle = endCapStyle
        wrap(BufferOp.bufferOp(g, distance, params))
    }

    /**
     * Buffer the Geometry with variable sizes
     * @param distances A List of buffer distances
     * @return A variable width buffer
     */
    Geometry variableBuffer(List<Double> distances) {
        if (distances.size() == 2) {
            Geometry.wrap(VariableBuffer.buffer(g, distances[0], distances[1]))
        } else if (distances.size() == 3) {
            Geometry.wrap(VariableBuffer.buffer(g, distances[0], distances[1], distances[2]))
        }  else {
            Geometry.wrap(VariableBuffer.buffer(g, distances as double[]))
        }
    }

    /**
     * Calculate an offset curve
     * @param distance The distance to offset
     * @return A Geometry
     */
    Geometry offsetCurve(double distance) {
        Geometry.wrap(OffsetCurve.getCurve(g, distance))
    }

    /**
     * Whether this Geometry contains the other Geometry.
     * @param other The other Geometry
     * @return Whether this Geometry contains the other Geometry
     */
    boolean contains(Geometry other) {
        this.g.contains(other.g)
    }

    /**
     * Calculate the convex hull of this Geometry
     * @return The convex hull of this Geometry
     */
    Geometry getConvexHull() {
        wrap(g.convexHull())
    }

    /**
     * Whether this Geometry is covered by the other Geometry
     * @param The other Geometry
     * @return Whether this Geometry is covered by the other Geometry
     */
    boolean coveredBy(Geometry other) {
        this.g.coveredBy(other.g)
    }

    /**
     * Whether this Geometry covers the other Geometry
     * @param The other Geometry
     * @return Whether this Geometry covers the other Geometry
     */
    boolean covers(Geometry other) {
        this.g.covers(other.g)
    }

    /**
     * Whether this Geometry crosses the other Geometry
     * @param The other Geometry
     * @return Whether this Geometry crosses the other Geometry
     */
    boolean crosses(Geometry other) {
        this.g.crosses(other.g)
    }

    /**
     * Calculate the difference between this Geometry and the other Geometry
     * @param The other Geometry
     * @return The difference between this Geometry and the other Geometry
     */
    Geometry difference(Geometry other) {
        try {
            return wrap(this.g.difference(other.g))
        } catch (org.locationtech.jts.geom.TopologyException e) {
            return wrap(this.g.buffer(0).difference(other.g))
        }
    }

    /**
     * Whether this Geometry is disjoint from the other Geometry
     * @param The other Geometry
     * @return Whether this Geometry is disjoint from the other Geometry
     */
    boolean disjoint(Geometry other) {
        this.g.disjoint(other.g)
    }

    /**
     * Calculate the distance between this Geometry and other Geometry
     * @param The other Geometry
     * @return The distance between this Geometry and other Geometry
     */
    double distance(Geometry other) {
        this.g.distance(other.g)
    }

    /**
     * Get the area of this Geometry
     * @return The area of this Geometry
     */
    double getArea() {
        this.g.area
    }

    /**
     * Get the boundary of this Geometry
     * @return The boundary of this Geometry
     */
    Geometry getBoundary() {
        Geometry.wrap(this.g.boundary)
    }

    /**
     * Calculate the centroid of this Geometry.
     * @return The centroid as a Point of this Geometry
     */
    Point getCentroid() {
        wrap(this.g.centroid)
    }

    /**
     * Get the Array of Coordinates
     * @return The Array of Coordinates
     */
    Coordinate[] getCoordinates() {
        this.g.coordinates
    }

    /**
     * Calculate the envelope of this Geometry
     * @return The Envelope of this Geometry
     */
    Envelope getEnvelope() {
        this.g.envelopeInternal
    }

    /**
     * Get the Bounds of this Geometry
     * @return The Bounds of this Geometry
     */
    Bounds getBounds() {
        Envelope e = getEnvelopeInternal()
        new Bounds(e.minX, e.minY, e.maxX, e.maxY)
    }

    /**
     * Calculate the internal Envelope of this Geometry
     * @return The internal Envelope of this Geometry
     */
    Envelope getEnvelopeInternal() {
        this.g.envelopeInternal
    }

    /**
     * Get the nth Geometry in this Geometry
     * @param n The index of the Geometry
     * @return The nth Geometry
     */
    Geometry getGeometryN(int n) {
        wrap(this.g.getGeometryN(n))
    }

    /**
     * Get the interior Point of this Geometry
     * @return The interior Point of this Geometry
     */
    Point getInteriorPoint() {
        wrap(this.g.getInteriorPoint()) as Point
    }

    /**
     * Create a new Geometry where all LineStrings are offset by the given distance.  Positive distances
     * will offset to the right.  Negative distance will offset to the left.
     * @param distance The offset distance
     * @param quadrantSegments The number of quadrant segements which defaults to 8
     * @return The offset Geometry
     */
    Geometry offset(double distance, int quadrantSegments = 8) {
        OffsetCurveBuilder builder = new OffsetCurveBuilder(distance, quadrantSegments)
        Geometry.wrap(builder.offset(this.g))
    }

    /**
     * Get the length of this Geometry
     * @return The length of this Geometry
     */
    double getLength() {
        this.g.length
    }

    /**
     * Get the dimension of the Geometry
     * @return The dimension
     */
    int getDimension() {
        this.g.dimension
    }

    /**
     * Get the number of Geometries in this Geometry
     * @return The number of Geometries in this Geometry
     */
    int getNumGeometries() {
        this.g.numGeometries
    }

    /**
     * Get a List of all Geometries
     * @return A List of all Geometries
     */
    List<Geometry> getGeometries() {
        List<Geometry> geoms = []
        (0..numGeometries-1).each{index ->
            geoms.add(getGeometryN(index))
        }
        geoms
    }

    /**
     * Get the number of Points in this Geometry
     * @return The number of Points in this Geometry
     */
    int getNumPoints () {
        this.g.numPoints
    }

    /**
     * Get a List of Points that make up this Geometry
     * @return A List of Points
     */
    List<Point> getPoints() {
        this.coordinates.collect{c -> new Point(c.x, c.y)}
    }

    /**
     * Calculate the intersection between this Geometry and the other Geometry
     * @param The other Geometry
     * @return The intersection between this Geometry and the other Geometry
     */
    Geometry intersection(Geometry other) {
        wrap(this.g.intersection(other.g))
    }

    /**
     * Whether this Geometry intersects the other Geometry
     * @param The other Geometry
     * @return Whether this Geometry intersects the other Geometry
     */
    boolean intersects(Geometry other) {
        this.g.intersects(other.g)
    }

    /**
     * Whether this Geometry is empty
     * @return Whether this Geometry is empty
     */
    boolean isEmpty() {
        this.g.isEmpty()
    }

    /**
     * Whether this Geometry is rectangular
     * @return Whether this Geometry is rectangular
     */
    boolean isRectangle() {
        this.g.isRectangle()
    }

    /**
     * Whether this Geometry is simple
     * @return Whether this Geometry is simple
     */
    boolean isSimple() {
        this.g.isSimple()
    }

    /**
     * Whether this Geometry is valid
     * @return Whether this Geometry is valid
     */
    boolean isValid() {
        this.g.isValid()
    }

    /**
     * Get the reason why this Geometry is invalid.
     * @return A textual reason why this Geometry is invalid
     */
    String getValidReason() {
        def op = new org.locationtech.jts.operation.valid.IsValidOp(this.g)
        op.validationError?.message ?: ""
    }

    /**
     * Whether this Geometry is curved
     * @return Whether this Geometry is curved
     */
    boolean isCurved() {
        CurvedGeometries.isCurved(this.g)
    }

    /**
     * Whether this Geometry is within the given distance of the other Geometry
     * @param geom The other Geometry
     * @param distance The distance
     * @return Whether this Geometry is within the given distance of the other Geometry
     */
    boolean isWithinDistance(Geometry geom, double distance) {
        this.g.isWithinDistance(geom.g, distance)
    }

    /**
     * Get a normalized copy of this Geometry
     * @return A new normalized copy of this Geometry
     */
    Geometry getNorm() {
        wrap(g.norm())
    }

    /**
     * Normalize this Geometry
     */
    void normalize() {
        g.normalize()
    }

    /**
     * Whether this Geometry overlaps the other Geometry
     * @param other The other Geometry
     * @return Whether this Geometry overlaps the other Geometry
     */
    boolean overlaps(Geometry other) {
        this.g.overlaps(other.g)
    }

    /**
     * Calculate the D9 Intersection Matrix of this Geometry with the other
     * Geometry.
     * @param other The other Geometry
     * @return The IntersectionMatrix of this Geometry with another Geometry
     */
    IntersectionMatrix relate(Geometry other) {
        this.g.relate(other.g)
    }

    /**
     * Whether this Geometry relates with the other Geometry given a D9 intesection
     * matrix pattern.
     * @param other The other Geometry
     * @param intersectionPattern The intersection pattern
     * @return Whether this Geometry relates with the other Geometry given a D9 intesection
     * matrix pattern.
     */
    boolean relate(Geometry other, String intersectionPattern) {
        this.g.relate(other.g, intersectionPattern)
    }

    /**
     * Calculate the symmetric difference between this Geometry and the other
     * Geometry
     * @param other The other Geometry
     * @return The the symmetric difference between this Geometry and the other Geometry
     */
    Geometry symDifference(Geometry other) {
        wrap(this.g.symDifference(other.g))
    }

    /**
     * Whether this Geometry touches the other Geometry
     * @param other The other Geometry
     * @return Whether this Geometry touches the other Geometry
     */
    boolean touches(Geometry other) {
        this.g.touches(other.g)
    }

    /**
     * Calculate the union of this Geometry
     * @return The union of this Geometry
     */
    Geometry union() {
        wrap(this.g.union())
    }

    /**
     * Calculate the union of this Geometry with the other Geometry
     * @param other The other Geometry
     * @return The union of this Geometry with the other Geometry
     */
    Geometry union(Geometry other) {
        wrap(this.g.union(other.g))
    }

    /**
     * Fix an invalid Geometry
     * @return A new fixed Geometry
     */
    Geometry fix() {
        wrap(GeometryFixer.fix(this.g))
    }

    /**
     * Whether this Geometry is within the other Geometry
     * @param other The other Geometry
     * @return Whether this Geometry is within the other Geometry
     */
    boolean within(Geometry other) {
        this.g.within(other.g)
    }

    /**
     * Smooth this Geometry
     * @param fit The amount of smoothing (between 0 - more and 1 - less)
     * @return A new smoothed Geometry
     */
    Geometry smooth(double fit) {
        Geometry.wrap(org.geotools.geometry.jts.JTS.smooth(this.g, fit))
    }

    /**
     * Get the Minimum Bounding Circle
     * @return The minimum bouding circle as a Geometry
     */
    Geometry getMinimumBoundingCircle() {
        def circle = new org.locationtech.jts.algorithm.MinimumBoundingCircle(g)
        Geometry.wrap(circle.getCircle())
    }

    /**
     * Get the octagonal envelope for this Geometry
     * @return the octagonal envelope for this Geometry
     */
    Geometry getOctagonalEnvelope() {
        def oct = new org.locationtech.jts.geom.OctagonalEnvelope(g)
        Geometry.wrap(oct.toGeometry(factory))
    }

    /**
     * Get the maximum inscribed circle for this Geometry
     * @param tolerance The tolerance which defaults to 1.0
     * @return The maximum inscribed circle
     */
    Geometry getMaximumInscribedCircle(double tolerance = 1.0) {
        MaximumInscribedCircle algorithm = new MaximumInscribedCircle(g, tolerance)
        def radiusLineString = algorithm.getRadiusLine()
        def centerPoint = radiusLineString.getStartPoint()
        Geometry.wrap(centerPoint.buffer(radiusLineString.getLength()))
    }

    /**
     * Get the largest empty circle for this Geometry
     * @param tolerance The tolerance which defaults to 1.0
     * @return The largest empty circle
     */
    Geometry getLargestEmptyCircle(double tolerance = 1.0) {
        LargestEmptyCircle algorithm = new LargestEmptyCircle(g, tolerance)
        def radiusLineString = algorithm.getRadiusLine()
        def centerPoint = radiusLineString.getStartPoint()
        Geometry.wrap(centerPoint.buffer(radiusLineString.getLength()))
    }

    /**
     * Get Delaunay Triangle Diagram for this Geometry
     * @return A Delaunay Triangle Diagram Geometry
     */
    Geometry getDelaunayTriangleDiagram(boolean isConforming = false) {
        def builder;
        if (isConforming) {
            builder = new org.locationtech.jts.triangulate.ConformingDelaunayTriangulationBuilder()
        }
        else {
            builder = new org.locationtech.jts.triangulate.DelaunayTriangulationBuilder()
        }
        builder.setSites(g)
        Geometry.wrap(builder.getTriangles(Geometry.factory))
    }

    /**
     * Get the Voronoi Diagram for this Geometry
     * @return A Voronoi Diagram Geometry
     */
    Geometry getVoronoiDiagram() {
        def builder = new org.locationtech.jts.triangulate.VoronoiDiagramBuilder()
        builder.setSites(g)
        Geometry.wrap(builder.getDiagram(Geometry.factory))
    }

    /**
     * Simplify this Geometry using the Douglas Peucker Simplifier.
     * @param tolerance The distance tolerance
     * @return A simplified Geometry
     */
    Geometry simplify(double tolerance) {
        Geometry.wrap(org.locationtech.jts.simplify.DouglasPeuckerSimplifier.simplify(this.g, tolerance))
    }

    /**
     * Simplify this Geometry preserving topology.
     * @param tolerance The distance tolerance
     * @return A simplified Geometry
     */
    Geometry simplifyPreservingTopology(double tolerance) {
        Geometry.wrap(org.locationtech.jts.simplify.TopologyPreservingSimplifier.simplify(this.g, tolerance))
    }

    /**
     * Densify this Geometry
     * @param distance The distance between coordinates
     * @return A new densified Geometry
     */
    Geometry densify(double distance) {
        Geometry.wrap(org.locationtech.jts.densify.Densifier.densify(this.g, distance))
    }

    /**
     * Get the minimum enclosing rectangle
     * @return The minimum enclosing rectangle
     */
    Geometry getMinimumRectangle() {
        def minDiameter = new org.locationtech.jts.algorithm.MinimumDiameter(this.g)
        Geometry.wrap(minDiameter.minimumRectangle)
    }

    /**
     * Get the minimum diameter of this Geometry as a LineString
     * @return The minimum diameter as a LineString
     */
    Geometry getMinimumDiameter() {
        def minDiameter = new org.locationtech.jts.algorithm.MinimumDiameter(this.g)
        Geometry.wrap(minDiameter.diameter)
    }

    /**
     * Get the minimum clearance of this Geometry as a LineString
     * @return The minimum clearance of this Geometry
     */
    Geometry getMinimumClearance() {
        Geometry.wrap(org.locationtech.jts.precision.MinimumClearance.getLine(g))
    }

    /**
     * Translate the Geometry.
     * @param x The distance in the x direction
     * @param y The distance in the y direction
     * @return The translated Geometry
     */
    Geometry translate(double x, double y) {
        Geometry.wrap(AffineTransformation.translationInstance(x,y).transform(g))
    }
    
    /**
     * Scale the Geometry relative to the origin
     * @param xScale The x direction value
     * @param yScale The y direction value
     */
    Geometry scale(double xScale, double yScale) {
        Geometry.wrap(AffineTransformation.scaleInstance(xScale, yScale).transform(g))
    }

    /**
     * Scale the Geometry relative to the origin
     * @param xScale The x direction value
     * @param yScale The y direction value
     * @param x The x coordinate
     * @param y The y coordinate
     */
    Geometry scale(double xScale, double yScale, double x, double y) {
        Geometry.wrap(AffineTransformation.scaleInstance(xScale, yScale, x, y).transform(g))
    }

    /**
     * Rotate the current Geometry around it's origin by a given angle theta(in radians)
     * @param theta The angle of rotation in radians
     * @return A new rotated Geometry
     */
    Geometry rotate(double theta) {
        Geometry.wrap(AffineTransformation.rotationInstance(theta).transform(g))
    }

    /**
     * Rotate the current Geometry around it's origin by a given angle theta(in radians)
     * @param sin The sin of the angle of rotation in radians
     * @param cos The cos of the angle of rotation in radians
     * @return A new rotated Geometry
     */
    Geometry rotate(double sin, double cos) {
        Geometry.wrap(AffineTransformation.rotationInstance(sin, cos).transform(g))
    }

    /**
     * Rotate the current Geometry around the xy coordinate by a given angle theta(in radians)
     * @param theta The angle of rotation in radians
     * @param x The x coordinate
     * @param y The y coordinate
     * @return A new rotated Geometry
     */
    Geometry rotate(double theta, double x, double y) {
        Geometry.wrap(AffineTransformation.rotationInstance(theta, x, y).transform(g))
    }

    /**
     * Rotate the current Geometry around the xy coordinate by a given angle theta(in radians)
     * @param sin The sin of the angle of rotation in radians
     * @param cos The cos of the angle of rotation in radians
     * @param x The x coordinate
     * @param y The y coordinate
     * @return A new rotated Geometry
     */
    Geometry rotate(double sin, double cos, double x, double y) {
        Geometry.wrap(AffineTransformation.rotationInstance(cos, sin, x, y).transform(g))
    }

    /**
     * Shear the Geometry in X and Y direction
     * @param xShear The distance to shear in the x direction
     * @param yShear The distance to shear in the y direction
     * @return The new sheared Geometry
     */
    Geometry shear(double xShear, double yShear) {
        Geometry.wrap(AffineTransformation.shearInstance(xShear, yShear).transform(g))
    }

    /**
     * Reflect the Geometry about the line (0 0, x y)
     * @param x The x coordinate
     * @param y The y coordinate
     */
    Geometry reflect(double x, double y) {
        Geometry.wrap(AffineTransformation.reflectionInstance(x, y).transform(g))
    }

    /**
     * Reflect the Geometry about the line (x1 y1, x2 y2)
     * @param x1 The x cordinate of the first coordinate
     * @param y1 The y cordinate of the first coordinate
     * @param x2 The x cordinate of the second coordinate
     * @param y2 The y cordinate of the second coordinate
     */
    Geometry reflect(double x1, double y1, double x2, double y2) {
        Geometry.wrap(AffineTransformation.reflectionInstance(x1, y1, x2, y2).transform(g))
    }

    /**
     * Snap this Geometry to the other Geometry within the given distance
     * @param other The other Geometry
     * @param distance The snap distance
     * @return The snapped Geometries as a GeometryCollection
     */
    Geometry snap(Geometry other, double distance) {
        new GeometryCollection(GeometrySnapper.snap(this.g, other.g, distance).collect{Geometry.wrap(it)})
    }

    /**
     * Find the nearest Points between this Geometry and another Geometry
     * @param other The other Geometry
     * @return A List of Points
     */
    List<Point> getNearestPoints(Geometry other) {
        org.locationtech.jts.operation.distance.DistanceOp.nearestPoints(this.g, other.g).collect {c->
            new Point(c.x, c.y)
        }
    }

    /**
     * Reduce the precision of this Geometry.
     * @param options Options can include scale (used when type is 'fixed'), pointwise (whether the reductions occurs
     * pointwise or not), or removecollapsed (whether collapsed geometries should be removed)
     * @param type The precision model type (fixed, floating, or floating_single)
     * @return A new Geometry
     */
    Geometry reducePrecision(Map options = [:], String type = "floating") {
        def precisionModel = null;
        if (type.equalsIgnoreCase("fixed")) {
            precisionModel = new PrecisionModel(options.get("scale",100))
        } else if (type.equalsIgnoreCase("floating")) {
            precisionModel = new PrecisionModel(PrecisionModel.FLOATING)
        } else if (type.equalsIgnoreCase("floating_single")) {
            precisionModel = new PrecisionModel(PrecisionModel.FLOATING_SINGLE)
        } else {
            throw new IllegalArgumentException("Unsupported Precision Model Type: '" + type + "'!");
        }
        def reducer = new GeometryPrecisionReducer(precisionModel)
        reducer.setPointwise(options.get("pointwise", false))
        reducer.setRemoveCollapsedComponents(options.get("removecollapsed", false))
        Geometry.wrap(reducer.reduce(this.g))
    }

    /**
     * Get the sub Geometry at the specified index.  This
     * allows support for Groovy's multiple assignment.
     * <p><code>def p1 = new Point(111,-47)</code></p>
     * <p><code>def (x,y) = p1</code></p>
     * @return The sub Geometry at the specified index
     */
    Object getAt(int index) {
        // Point => X, Y
        if (g instanceof org.locationtech.jts.geom.Point) {
            if (index == 0) {
                return g.x
            } else if (index == 1) {
                return g.y
            }
        }
        // Polygon => Exterior and Interior LinearRing(s)
        else if (g instanceof org.locationtech.jts.geom.Polygon) {
            if (index == 0) {
                return Geometry.wrap(g.exteriorRing)
            } else if (g.numInteriorRing > 0 && index <= g.numInteriorRing) {
                return Geometry.wrap(g.getInteriorRingN(index - 1))
            }
        }
        // GeometryCollection => Geometry
        else if (g instanceof org.locationtech.jts.geom.GeometryCollection) {
            if (index < g.numGeometries) {
                return Geometry.wrap(g.getGeometryN(index))
            }
        }
        // Other Geometry => Point
        else {
            if (index < g.numPoints) {
                def c = g.coordinates[index]
                return new Point(c.x, c.y)
            }
        }
        return null
    }

    /**
     * The string representation
     * @return The string representation
     */
    String toString() {
        return wkt
    }

    /**
     * Whether this Geometry equals another Geometry after
     * they are both normalized
     * @param geometry The other Geometry
     * @return Whether the two normalized Geometries equal
     */
    boolean equalsNorm(Geometry geometry) {
        this.g.equalsNorm(geometry.g)
    }

    /**
     * Whether this Geometry topologically equals another Geometry
     * @param geometry The other Geometry
     * @return Whether the two Geometries are topologically equal
     */
    boolean equalsTopo(Geometry geometry) {
        this.g.equalsTopo(geometry.g)
    }

    /**
     * Whether this Geometry equals another Object
     * @param obj The Object
     * @return Whether this Geometry and the Object are equals
     */
    @Override
    boolean equals(Object obj) {
        if (obj instanceof Geometry) {
            this.g.equals(obj.g)
        } else {
            return false
        }
    }

    /**
     * Calculate this Geometry's hashCode
     * @return The hashCode
     */
    @Override
    int hashCode() {
        this.g.hashCode()
    }

    /**
     * Override the asType method to convert Geometry to a custom value
     * @param type The Class
     * @return The converted Object
     */
    Object asType(Class type) {
        if (type == Point) {
            return centroid
        } else if (type == Bounds) {
            return bounds
        } else {
            return super.asType(type)
        }
    }

    /**
     * Get a PreparedGeometry for this Geometry.
     * @return A PreparedGeometry for this Geometry
     */
    PreparedGeometry prepare() {
        new PreparedGeometry(this)
    }

    /**
     * Get the name of the Geometry type
     * @return The name of the Geometry type
     */
    String getGeometryType() {
        this.g.geometryType
    }

    /**
     * Wrap a JTS Geometry in a geoscript.geom.Geometry
     * @param A JTS Geometry
     * @return A GeoScript Geometry
     */
    static Geometry wrap(JtsGeometry jts) {
        if (jts == null) {
            return null
        } else if (jts instanceof org.locationtech.jts.geom.Point) {
            return new Point(jts)
        }
        else if (jts instanceof org.geotools.geometry.jts.CompoundRing) {
            return new CompoundRing(jts)
        }
        else if (jts instanceof org.geotools.geometry.jts.CompoundCurve) {
            return new CompoundCurve(jts)
        }
        else if (jts instanceof org.geotools.geometry.jts.CircularRing) {
            return new CircularRing(jts)
        }
        else if (jts instanceof org.geotools.geometry.jts.CircularString) {
            return new CircularString(jts)
        }
        else if (jts instanceof org.locationtech.jts.geom.LinearRing) {
            return new LinearRing(jts)
        }
        else if (jts instanceof org.locationtech.jts.geom.LineString) {
            return new LineString(jts)
        }
        else if (jts instanceof org.locationtech.jts.geom.Polygon) {
            return new Polygon(jts)
        }
        else if (jts instanceof org.locationtech.jts.geom.MultiPoint) {
            return new MultiPoint(jts)
        }
        else if (jts instanceof org.locationtech.jts.geom.MultiLineString) {
            return new MultiLineString(jts)
        }
        else if (jts instanceof org.locationtech.jts.geom.MultiPolygon) {
            return new MultiPolygon(jts)
        }
        else if (jts instanceof org.locationtech.jts.geom.GeometryCollection) {
            return new GeometryCollection(jts)
        }
        else {
            return new Geometry(jts)
        }
    }
	
    /**
     * Get a Geometry from a String with an unknown format.
     * @param str The String
     * @return A Geometry or null if the String can't be parsed
     * as a Geometry
     */
    static Geometry fromString(String str) {
        if (str == null || str.trim().length() == 0) {
            return null
        }
        str = str.trim()
        List<Reader> readers = Readers.list()
        Geometry geom = null
        for(def reader in readers) {
            // Don't include the GooglePolylineEncoder because it tries to decode
            // too many strings that it shouldn't
            if (!reader.class.simpleName.equalsIgnoreCase("GooglePolylineEncoder")) {
                try {
                    geom = reader.read(str)
                } catch (Exception ex) { /* Reading failed, try next reader */
                }
                if (geom) {
                    break
                }
            }
        }
        if (!geom) {
            def parts = str.split(",")
            if (parts.length == 4 || parts.length == 5) {
                geom = new Bounds(parts[0] as double, parts[1] as double, 
                    parts[2] as double, parts[3] as double, parts.length > 4 ? parts[4] : null).geometry
            } else if (parts.length == 2) {
                geom = new Point(parts[0] as double, parts[1] as double)
            }
            if (!geom) {
                parts = str.split(" ")
                if (parts.length == 4) {
                    geom = new Bounds(parts[0] as double, parts[1] as double, parts[2] as double, parts[3] as double).geometry
                } else if (parts.length == 2) {
                    geom = new Point(parts[0] as double, parts[1] as double)
                }
            }
        }
        geom
    }

    /**
     * Get a PreparedGeometry for the given Geometry
     * @param g The Geometry
     * @return A PreparedGeometry
     */
    static PreparedGeometry prepare(Geometry g) {
        new PreparedGeometry(g)
    }

    /**
     * Create the given number of randomly located points inside of the given Geometry
     * @param geometry The Geometry that will contain the randomly located points
     * @param number The number of points
     * @return A MultiPoint
     */
    static Geometry createRandomPoints(Geometry geometry, int number) {
        def builder = new org.locationtech.jts.shape.random.RandomPointsBuilder(factory)
        builder.setExtent(geometry.g)
        builder.numPoints = number
        Geometry.wrap(builder.getGeometry())
    }

    /**
     * Create the given number of randomly located points inside of the given Geometry and also constrained by the cells
     * of a grid.  Often more points will be generated that the number given because of the required grid size.
     * @param bounds The Bounds that will contain the randomly located points
     * @param number The number of points
     * @param constrainedToCircle Whether the points should be constrained to a circle or not
     * @param gutterFraction The size of the gutter between cells
     * @return A MultiPoint
     */
    static Geometry createRandomPointsInGrid(Bounds bounds, int number, boolean constrainedToCircle, double gutterFraction) {
        def builder = new org.locationtech.jts.shape.random.RandomPointsInGridBuilder(factory)
        builder.extent = bounds.env
        builder.numPoints = number
        builder.setConstrainedToCircle(constrainedToCircle)
        builder.setGutterFraction(gutterFraction)
        Geometry.wrap(builder.getGeometry())
    }
    
    /**
     * Create a Geometry from a text and font.
     * @param text The text
     * @param fontName The font name
     * @param size The font size
     * @return A Geometry
     */
    static Geometry createFromText(String text, String fontName = FontGlyphReader.FONT_SANSERIF, int size = 24) {
        Geometry.wrap(FontGlyphReader.read(text, fontName, size, factory))
    }
    
    /**
     * Create a Sierpinski Carpet Geometry
     * @param Bounds The Bounds
     * @param numberOfPoints The number of points
     * @return A Geometry
     */
    static Geometry createSierpinskiCarpet(Bounds bounds, int numberOfPoints) {
        def builder = new org.locationtech.jts.shape.fractal.SierpinskiCarpetBuilder(factory)
        builder.extent = bounds.env
        builder.numPoints = numberOfPoints
        Geometry.wrap(builder.geometry)
    }
    
    /**
     * Create a Koch Snowflake Geometry
     * @param Bounds The Bounds
     * @param numberOfPoints The number of points
     * @return A Geometry
     */
    static Geometry createKochSnowflake(Bounds bounds, int numberOfPoints) {
        def builder = new org.locationtech.jts.shape.fractal.KochSnowflakeBuilder(factory)
        builder.extent = bounds.env
        builder.numPoints = numberOfPoints
        Geometry.wrap(builder.geometry)
    }

    /**
     * Perform a cascaded union on a List of Polygons
     * @param polygons The Polygons
     * @return A unioned Geometry
     */
    static Geometry cascadedUnion(List<Polygon> polygons) {
        Geometry.wrap(org.locationtech.jts.operation.union.CascadedPolygonUnion.union(polygons.collect{it.g}))
    }
}
