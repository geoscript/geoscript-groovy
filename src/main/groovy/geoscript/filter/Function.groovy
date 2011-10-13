package geoscript.filter

import org.opengis.filter.expression.Function as GtFunction

import org.geotools.factory.CommonFactoryFinder
import org.geotools.factory.FactoryIteratorProvider
import org.geotools.factory.GeoTools
import org.geotools.filter.FunctionFactory
import org.geotools.filter.FunctionImpl
import org.opengis.filter.expression.Literal
import org.opengis.feature.type.Name

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
class Function extends Expression {

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
        super(f)
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
     * Create a new Function from a Closure
     * @param name The name of the new Function
     * @param closure The Closure
     */
    Function(String name, Closure closure) {
        this(new ClosureFunction(name, closure))
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
	
    /**
     * A GeoTools Function that delegates to a Groovy Closure.
     */
    private static class ClosureFunction extends FunctionImpl {
        private final Closure closure
        ClosureFunction(String name, Closure closure) {
            setName(name)
            this.closure = closure
            functionFactory.functions.add(this)
        }
        def evaluate(def obj) {
            closure(obj)
        }
        String toString() {
            "${name}()"
        }
    }

    /**
     * A GeoScript FunctionFactory
     */
    private static final GeoScriptFunctionFactory functionFactory = new GeoScriptFunctionFactory()

    /**
     * A GeoScript FactoryIteratorProvider
     */
    private static final GeoScriptFactoryIteratorProvider provider = new GeoScriptFactoryIteratorProvider()

    /**
     * Add the ability to dynamically create and register custom Functions
     */
    static {
        GeoTools.addClassLoader(provider.class.classLoader)
        GeoTools.addFactoryIteratorProvider(provider)
        // If a custom function is called before a standard function,
        // the standard functions (through CQL) can't be found.
        org.geotools.filter.text.ecql.ECQL.toExpression("centroid(the_geom)")
    }

    /**
     * The GeoScript FunctionFactory
     */
    private static class GeoScriptFunctionFactory implements FunctionFactory {
        List functions = []
        GtFunction function(String name, List args, Literal fallback) {
            functions.find{func -> func.name.equals(name) }
        }
        GtFunction function(Name name, List args, Literal fallback) {
            functions.find{func -> func.name.equals(name.localPart)}
        }
        List getFunctionNames() {
            functions.collect{func -> func.name}
        }
    }

    /**
     * The GeoScript FactoryIterator Provider
     */
    private static class GeoScriptFactoryIteratorProvider implements FactoryIteratorProvider {
        Iterator iterator(Class category) {
            if(FunctionFactory.class == category) {
                return [functionFactory].iterator()
            } else {
                return null
            }
        }
    }
}

