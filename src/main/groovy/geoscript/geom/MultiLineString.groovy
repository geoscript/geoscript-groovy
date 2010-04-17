package geoscript.geom

import com.vividsolutions.jts.geom.MultiLineString as JtsMultiLineString
import com.vividsolutions.jts.geom.LineString as JtsLineString

/**
 * A MultiLineString Geometry.
 * <p>You can create a MultiLineString from a variable List of LineString:</p>
 * <code>MultiLineString m = new MultiLineString(new LineString([1,2],[3,4]), new LineString([5,6],[7,8]))</code>
 * <p>Or from a variable List of List of Doubles:</p>
 * <code>MultiLineString m = new MultiLineString([[1,2],[3,4]], [[5,6],[7,8]])</code>
 * <p>Or from a List of LineStrings:</p>
 * <code>MultiLineString m = new MultiLineString([new LineString([1,2],[3,4]), new LineString([5,6],[7,8])])</code>
 * <p>Or from a List of List of List of Doubles:</p>
 * <code>MultiLineString m = new MultiLineString([[[1,2],[3,4]], [[5,6],[7,8]]])</code>
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
     * <p><code>MultiLineString m = new MultiLineString(new LineString([1,2],[3,4]), new LineString([5,6],[7,8]))</code></p>
     * @param lineString A variable List of LineStrings
     */
    MultiLineString(LineString... lineStrings) {
        this(create(lineStrings))
    }

    /**
     * Create a MultiLineString from a variable List of List of Doubles
     * <p><code>MultiLineString m = new MultiLineString([[1,2],[3,4]], [[5,6],[7,8]])</code></p>
     * @param lineString A variable List of List of Doubles
     */
    MultiLineString(List<List<Double>>... lineStrings) {
        this(create(lineStrings))
    }

    /**
     * Create a MultiLineString from a List of LineString or a List of List of Doubles
     * <p><code>MultiLineString m = new MultiLineString([new LineString([1,2],[3,4]), new LineString([5,6],[7,8])])</code></p>
     * <p><code>MultiLineString m = new MultiLineString([[[1,2],[3,4]], [[5,6],[7,8]]])</code></p>
     * @param lineString Either a List of List of Doubles or a List of LineStrings
     */
    MultiLineString(List lineStrings) {
        this(create(lineStrings))
    }

    /**
     * Add a LineString to this MultiLineString to create another MultiLineString
     * <p><code>def m1 = new MultiLineString(new LineString([1,2],[3,4]), new LineString([5,6],[7,8]))</code></p>
     * <p><code>def m2 = m1 + new LineString([11,12],[13,14])</code></p>
     * <p><code>MULTILINESTRING ((1 2, 3 4), (5 6, 7 8), (11 12, 13 14))</code></p>
     * @param line A LineString
     * @return A new MultiLineString with this LineString and the other
     */
    MultiLineString plus(LineString line) {
        List<LineString> lines = []
        (0..numGeometries-1).each{index ->
            lines.add(getGeometryN(index))
        }
        lines.add(line)
        new MultiLineString(lines)
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