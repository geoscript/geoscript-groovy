package geoscript.filter

import org.geotools.factory.CommonFactoryFinder
import org.opengis.filter.expression.Function as GtFunction

/**
 * A GeoScript Function either wraps an existing GeoTools Function or an CQL String.
 * <p>You can create a Function from CQL:</p>
 * <p><code>
 * Function f = new Function("centroid(the_geom)")
 * </code></p>
 * See http://docs.geoserver.org/2.0.x/en/user/filter/function_reference.html for more
 * details.
 * @author Jared Erickson
 */
class Function {

    /**
     * The GeoTools CommonFactoryFinder for finding GeoTools Functions
     */
    private static def ff = CommonFactoryFinder.getFilterFactory2(null)

    /**
     * The GeoTools Function
     */
    GtFunction function

    /**
     * Create a Function from a GeoTools Function
     * @param f The GeoTools Function
     */
    Function(GtFunction f) {
        function = f
    }

    /**
     * Create a new Function from a CQL compatible string
     * @param str A CQL String
     */
    Function(String str) {
        this(org.geotools.filter.text.ecql.ECQL.toExpression(str))
    }
   
    /**
     * Call the Function with an optional parameter.
     * @param obj The parameter
     * @return The return value
     */
    def call(Object object = null) {
        if (object != null) {
            // A GeoScript Layer == GeoTools FeatureCollection
            if (object instanceof geoscript.layer.Layer) {
                object = object.fs.features
            }
            // A GeoScript Geometry == JTS Geometry
            else if (object instanceof geoscript.geom.Geometry) {
                object = object.g
            }
            // A GeoScript Feature == GeoTools SimpleFeature
            else if (object instanceof geoscript.feature.Feature) {
                object = object.f
            }
        }
        return function.evaluate(object)
    }

    /**
     * The string presentation
     * @return The name of the Function
     */
    String toString() {
        function.toString()
    }
}

