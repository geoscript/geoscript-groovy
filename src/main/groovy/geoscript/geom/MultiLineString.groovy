package geoscript.geom

import com.vividsolutions.jts.geom.MultiLineString as JtsMultiLineString
import com.vividsolutions.jts.geom.LineString as JtsLineString

/**
 * A MultiLineString
 */
class MultiLineString extends Geometry {

    /**
     * Create a MultiLineString that wraps a JTS MultiLineString
     */
    MultiLineString(JtsMultiLineString multiLineString) {
        super(multiLineString)
    }

    /**
     * Create a MultiLineString from a List of LineStrings
     */
    MultiLineString(LineString... lineStrings) {
        this(create(lineStrings))
    }

    /**
     * Create a MultiLineString from a List of List of Doubles
     */
    MultiLineString(List<List<Double>>... lineStrings) {
        this(create(lineStrings))
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
	
}