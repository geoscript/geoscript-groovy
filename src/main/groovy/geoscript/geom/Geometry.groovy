package geoscript.geom

import com.vividsolutions.jts.geom.Geometry as JtsGeometry
import com.vividsolutions.jts.geom.GeometryFactory
import com.vividsolutions.jts.geom.prep.PreparedGeometryFactory
import com.vividsolutions.jts.io.WKTReader
import com.vividsolutions.jts.io.WKBReader
import com.vividsolutions.jts.io.WKBWriter
import com.vividsolutions.jts.geom.Envelope
import com.vividsolutions.jts.geom.IntersectionMatrix

/**
 * The base class for other Geomtries
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
     * The JTS WKTReader
     */
    private static WKTReader wktReader = new WKTReader()

    /**
     * The JTS WKBWriter
     */
    private static WKBWriter wkbWriter = new WKBWriter()

    /**
     * The JTS WKBReader
     */
    private static WKBReader wkbReader = new WKBReader()

    /**
     * Create a new Geometry wrapping a JTS Geometry
     */
    Geometry (JtsGeometry g) {
        this.g = g
    }
	

    /**
     * Buffer cap styles
     */
    static final int CAP_ROUND = com.vividsolutions.jts.operation.buffer.BufferOp.CAP_ROUND
    static final int CAP_BUTT = com.vividsolutions.jts.operation.buffer.BufferOp.CAP_BUTT
    static final int CAP_SQUARE = com.vividsolutions.jts.operation.buffer.BufferOp.CAP_SQUARE

    /**
     * Buffer the Geometry by some distance
     */
    Geometry buffer(double distance, int quadrantSegments = 8, int endCapStyle = CAP_ROUND) {
        wrap(g.buffer(distance, quadrantSegments, endCapStyle))
    }

    boolean contains(Geometry other) {
        this.g.contains(other.g)
    }

    Geometry convexHull() {
        wrap(g.convexHull())
    }

    boolean coveredBy(Geometry other) {
        this.g.coveredBy(other.g)
    }

    boolean covers(Geometry other) {
        this.g.covers(other.g)
    }

    boolean crosses(Geometry other) {
        this.g.crosses(other.g)
    }

    Geometry difference(Geometry other) {
        wrap(this.g.difference(other.g))
    }

    boolean disjoint(Geometry other) {
        this.g.disjoin(other.g)
    }

    double distance(Geometry other) {
        this.g.distance(other.g)
    }

    double getArea() {
        this.g.area
    }

    double getBoundary() {
        this.g.boundary
    }

    Point getCentroid() {
        wrap(this.g.centroid)
    }

    Envelope getEnvelope() {
        this.g.envelope
    }

    Envelope getEnvelopeInternal() {
        this.g.envelopeInternal
    }

    Geometry getGeometryN(int n) {
        wrap(this.g.getGeometryN(n))
    }

    Point getInteriorPoint() {
        wrap(this.g.getInteriorPoint())
    }

    double getLength() {
        this.g.length
    }

    int getNumGeometries() {
        this.g.numGeometries
    }

    int getNumPoints () {
        this.g.numPoints
    }

    Geometry intersection(Geometry other) {
        wrap(this.g.intersection(other.g))
    }

    boolean intersects(Geometry other) {
        this.g.intersects(other.g)
    }

    boolean isEmpty() {
        this.g.isEmpty()
    }

    boolean isRectangle() {
        this.g.isRectangle()
    }
    
    boolean isSimple() {
        this.g.isSimple()
    }

    boolean isValid() {
        this.g.isValid()
    }

    boolean isWithinDistance(Geometry geom, double distance) {
        this.g.isWithinDistance(geom.g, distance)
    }

    boolean overlaps(Geometry other) {
        this.g.overlaps(other.g)
    }

    IntersectionMatrix relate(Geometry other) {
        this.g.relate(other.g)
    }
    
    boolean relate(Geometry other, String intersectionPattern) {
        this.g.relate(other.g, intersectionPattern)
    }

    Geometry symDifference(Geometry other) {
        wrap(this.g.symDifference(other.g))
    }

    boolean touches(Geometry other) {
        this.g.touches(other.g)
    }

    Geometry union() {
        wrap(this.g.union())
    }

    Geometry union(Geometry other) {
        wrap(this.g.union(other.g))
    }

    boolean within(Geometry other) {
        this.g.within(other.g)
    }

    /**
     * Get the WKT of the Geometry
     */
    String wkt() {
        return g.toText()
    }

    /**
     * Get the WKT of the Geometry
     */
    String getWkt() {
        g.toText()
    }
    
    /**
     * Get the WKB of the Geometry
     */
    byte[] getWkb() {
        wkbWriter.write(g)
    }

    /**
     * The string reprensentation
     */
    String toString() {
        return wkt
    }

    /**
     * Get a PreparedGeometry for this Geometry
     */
    PreparedGeometry prepare() {
        new PreparedGeometry(this)
    }

    /**
     * Wrap a JTS Geometry in a geoscript.geom.Geometry
     */
    static Geometry wrap(JtsGeometry jts) {
        if (jts instanceof com.vividsolutions.jts.geom.Point) {
            return new Point(jts)
        }
        else if (jts instanceof com.vividsolutions.jts.geom.LineString) {
            return new LineString(jts)
        }
        else if (jts instanceof com.vividsolutions.jts.geom.LinearRing) {
            return new LinearRing(jts)
        }
        else if (jts instanceof com.vividsolutions.jts.geom.Polygon) {
            return new Polygon(jts)
        }
        else if (jts instanceof com.vividsolutions.jts.geom.MultiPoint) {
            return new MultiPoint(jts)
        }
        else if (jts instanceof com.vividsolutions.jts.geom.MultiLineString) {
            return new MultiLineString(jts)
        }
        else if (jts instanceof com.vividsolutions.jts.geom.MultiPolygon) {
            return new MultiPolygon(jts)
        }
        else {
            return new Geometry(jts)
        }
    }
	
    /**
     * Get a Geometry from WKT
     */
    static Geometry fromWKT(String wkt) {
        wrap(wktReader.read(wkt))
    }

    /**
     * Get a Geometry from WKB
     */
    static Geometry fromWKB(byte[] wkb) {
        wrap(wkbReader.read(wkb))
    }

    /**
     * Get a PreparedGeometry for the given Geometry
     */
    static PreparedGeometry prepare(Geometry g) {
        new PreparedGeometry(g)
    }
}
