package geoscript.geom

import com.vividsolutions.jts.geom.MultiLineString as JtsMultiLineString
import com.vividsolutions.jts.geom.LineString as JtsLineString
import com.vividsolutions.jts.geom.PrecisionModel
import com.vividsolutions.jts.noding.snapround.GeometryNoder
import com.vividsolutions.jts.operation.linemerge.LineMerger
import com.vividsolutions.jts.operation.polygonize.Polygonizer as JtsPolygonizer

/**
 * A MultiLineString Geometry.
 * <p>You can create a MultiLineString from a variable List of {@link LineString}:</p>
 * <p><blockquote><pre>
 * MultiLineString m = new MultiLineString(new LineString([1,2],[3,4]), new LineString([5,6],[7,8]))
 * </pre></blockquote></p>
 * <p>Or from a variable List of List of Doubles:</p>
 * <p><blockquote><pre>
 * MultiLineString m = new MultiLineString([[1,2],[3,4]], [[5,6],[7,8]])
 * </pre></blockquote></p>
 * <p>Or from a List of {@link LineString}s:</p>
 * <p><blockquote><pre>
 * MultiLineString m = new MultiLineString([new LineString([1,2],[3,4]), new LineString([5,6],[7,8])])
 * </pre></blockquote></p>
 * <p>Or from a List of List of List of Doubles:</p>
 * <p><blockquote><pre>
 * MultiLineString m = new MultiLineString([[[1,2],[3,4]], [[5,6],[7,8]]])
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class MultiLineString extends GeometryCollection {

    /**
     * Create a MultiLineString that wraps a JTS MultiLineString
     * @param multiLineString The JTS MultiLineString
     */
    MultiLineString(JtsMultiLineString multiLineString) {
        super(multiLineString)
    }

    /**
     * Create a MultiLineString from a variable List of LineStrings
     * <p><blockquote><pre>
     * MultiLineString m = new MultiLineString(new LineString([1,2],[3,4]), new LineString([5,6],[7,8]))
     * </pre></blockquote></p>
     * @param lineString A variable List of LineStrings
     */
    MultiLineString(LineString... lineStrings) {
        this(create(lineStrings))
    }

    /**
     * Create a MultiLineString from a variable List of List of Doubles
     * <p><blockquote><pre>
     * MultiLineString m = new MultiLineString([[1,2],[3,4]], [[5,6],[7,8]])
     * </pre></blockquote></p>
     * @param lineString A variable List of List of Doubles
     */
    MultiLineString(List<List<Double>>... lineStrings) {
        this(create(lineStrings))
    }

    /**
     * Create a MultiLineString from a List of LineString or a List of List of Doubles
     * <p><blockquote><pre>
     * MultiLineString m = new MultiLineString([new LineString([1,2],[3,4]), new LineString([5,6],[7,8])])
     * MultiLineString m = new MultiLineString([[[1,2],[3,4]], [[5,6],[7,8]]])
     * </pre></blockquote></p>
     * @param lineString Either a List of List of Doubles or a List of LineStrings
     */
    MultiLineString(List lineStrings) {
        this(create(lineStrings))
    }

    /**
     * Add a LineString to this MultiLineString to create another MultiLineString
     * <p><blockquote><pre>
     * def m1 = new MultiLineString(new LineString([1,2],[3,4]), new LineString([5,6],[7,8]))
     * def m2 = m1 + new LineString([11,12],[13,14])
     *
     * MULTILINESTRING ((1 2, 3 4), (5 6, 7 8), (11 12, 13 14))
     * </pre></blockquote></p>
     * @param line A LineString
     * @return A new MultiLineString with this LineString and the other
     */
    MultiLineString plus(LineString line) {
        List<LineString> lines = []
        if (!empty) {
            (0..numGeometries-1).each{index ->
                lines.add(getGeometryN(index))
            }
        }
        lines.add(line)
        new MultiLineString(lines)
    }

    /**
     * Node the LineStrings in this MultiLineString
     * @param numberOfDecimalPlaces The number of decimal places to use in the PrecisionModel
     * @return A new MultiLineString
     */
    MultiLineString node(int numberOfDecimalPlaces = 5) {
        def pm = new PrecisionModel(numberOfDecimalPlaces)
        def noder = new GeometryNoder(pm)
        def nodedLines = noder.node(this.geometries.collect{line -> line.g})
        new MultiLineString(nodedLines.collect{line -> new LineString(line)})
    }

    /**
     * Merge the LineStrings of this MultiLineString together
     * @return A new MultiLineString
     */
    MultiLineString merge() {
        def merger = new LineMerger()
        this.geometries.each{line -> merger.add(line.g) }
        new MultiLineString(merger.mergedLineStrings.collect{line -> new LineString(line)})
    }

    /**
     * Polygonize the LineStrings of this MultiLineString
     * @return A MultiPolygon
     */
    MultiPolygon polygonize() {
        def polygonizer = new JtsPolygonizer()
        this.geometries.each{line -> polygonizer.add(line.g)}
        def polygons = polygonizer.polygons
        new MultiPolygon(polygons.collect{p -> new Polygon(p)})
    }

    /**
     * Polygonize the LineStrings of this MultiLineString and return a Map
     * with polygons, cutEdges, dangles, and invalidRingLines.
     * @return A Map with polygons, cutEdges, dangles, and invalidRingLines
     */
    Map polyzonizeFull() {
        Map results = [:]
        def polygonizer = new JtsPolygonizer()
        this.geometries.each{line -> polygonizer.add(line.g)}
        results.polygons = new MultiPolygon(polygonizer.polygons.collect{p -> new Polygon(p)})
        results.cutEdges = new MultiLineString(polygonizer.cutEdges.collect{l -> new LineString(l)})
        results.dangles = new MultiLineString(polygonizer.dangles.collect{l -> new LineString(l)})
        results.invalidRingLines = new MultiLineString(polygonizer.invalidRingLines.collect{l -> new LineString(l)})
        results
    }

    /**
     * Create a MultiLineString from a List of List of Doubles
     */
    private static JtsMultiLineString create(List<List<Double>>... lineStrings) {
        List<LineString> l = lineStrings.collect{line -> new LineString(line)} as List<LineString>;
        create(*l)
    }

    /**
     * Create a MultiLineString from a List of LineStrings
     */
    private static JtsMultiLineString create(LineString... lineStrings) {
        Geometry.factory.createMultiLineString(lineStrings.collect{
                LineString lineString -> lineString.g
            }.toArray() as JtsLineString[])
    }

    /**
     * Create a MultiLineString from a List of LineStrings
     */
    private static JtsMultiLineString create(List lineStrings) {
        Geometry.factory.createMultiLineString(lineStrings.collect{l ->
                (l instanceof LineString) ? l.g : new LineString(l).g
            }.toArray() as JtsLineString[])
    }
}
