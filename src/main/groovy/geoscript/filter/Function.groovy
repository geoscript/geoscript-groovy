package geoscript.filter

import org.opengis.filter.expression.Function as GtFunction
import org.opengis.filter.expression.Expression as GtExpression

import org.geotools.factory.CommonFactoryFinder
import org.geotools.factory.FactoryIteratorProvider
import org.geotools.factory.GeoTools
import org.geotools.filter.FunctionExpressionImpl
import org.geotools.filter.FunctionFactory
import org.opengis.feature.type.Name
import org.geotools.feature.FeatureCollection
import geoscript.layer.Layer
import org.geotools.coverage.grid.AbstractGridCoverage
import geoscript.raster.Raster
import org.geotools.gce.geotiff.GeoTiffFormat
import org.geotools.filter.function.RenderingTransformation
import org.geotools.data.Query
import org.opengis.coverage.grid.GridGeometry
import org.opengis.filter.expression.Literal
import org.geotools.feature.NameImpl
import org.opengis.filter.capability.FunctionName

/**
 * A GeoScript Function either wraps an existing GeoTools Function or an CQL String.
 * <p>You can create a Function from CQL:</p>
 * <p><blockquote><pre>
 * Function f = new Function("centroid(the_geom)")
 * </pre></blockquote></p>
 * <p>You can also create a Function from CQL and a Closure:</p>
 * <p><blockquote><pre>
 * Function f = new Function("my_centroid(the_geom)", {g-> g.centroid})
 * </pre></blockquote></p>
 * See <a href="http://docs.geoserver.org/2.1.x/en/user/filter/function_reference.html">GeoServer's Function Reference</a> for more
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
     * Create a new Function from a name and one or more Expressions
     * @param name The Function name
     * @param expressions One or more Expressions
     */
    Function(String name, Expression... expressions) {
        this(ff.function(name, *expressions.collect{it.expr}))
    }

    /**
     * Create a new Function from a Closure with one or more Expressions.
     * @param name The name of the new Function
     * @param closure The Closure
     * @param expressions One or more Expressions
     */
    Function(String name, Closure closure, Expression... expressions) {
        this(registerAndCreateFunction(name, closure, expressions))
    }

    /**
     * Create a new Function from CQL and a Closure.
     * @param cql The CQL statement
     * @param closure The Closure
     */
    Function(String cql, Closure closure) {
        this(registerAndCreateFunction(cql, closure))
    }

    /**
     * Create a Rendering Transformation Function from a Process and a variable List of Functions.
     * @param process The GeoScript Process
     * @param functions A variable List of Functions
     */
    Function(geoscript.process.Process process, Function... functions) {
        this(createProcessFunction(process, functions))
    }

    /**
     * Create a Rendering Transformation Function from a Process and a variable List of Functions.
     * @param process The GeoScript Process
     * @param functions A variable List of Functions
     */
    private static GtFunction createProcessFunction(geoscript.process.Process process, Function... functions) {
        def pff = new org.geotools.process.function.ProcessFunctionFactory()
        def names = process.name.split(":")
        def nm = new NameImpl(names[0], names[1])
        def f = pff.function(nm, functions.collect{it.expr}, null)
        f
    }

    /**
     * The string presentation
     * @return The name of the Function
     */
    String toString() {
        function.toString()
    }

    /**
     * Get a List of all Function names
     * @return A List of all Function names
     */
    static List<String> getFunctionNames() {
        List names = []
        CommonFactoryFinder.getFunctionFactories().each { f ->
            f.functionNames.each { fn ->
                names.add((fn instanceof FunctionName) ? fn.functionName.toString() : fn.toString())
            }
        }
        names.sort()
    }

    /**
     * Register a new Function by name with a Closure.
     * @param name The name of the new Function
     * @param closure The Closure
     */
    static void registerFunction(String name, Closure closure) {
        if (!isGeoServerAvailable) {
            functionFactory.cache.put(name, closure)
        }
    }

    /**
     * Register a new Function by name with a Closure and then immediately ask for an instance of the Function
     * with the one or more Expressions
     * @param name The name of the new Function
     * @param closure The Closure
     * @param expressions One or more Expressions
     * @return A GeoTools Function
     */
    private static GtFunction registerAndCreateFunction(String name, Closure closure, Expression... expressions) {
        registerFunction(name, closure)
        ff.function(name, *expressions.collect{it.expr})
    }

    /**
     * Register a new Function with a Closure and a CQL statement.  The name of the new Function is extracted from
     * the CQL statement.
     * @param cql The CQL statement
     * @param closure The Closure
     * @return A GeoTools Function
     */
    private static GtFunction registerAndCreateFunction(String cql, Closure closure) {
        String name = cql.substring(0, cql.indexOf("("))
        registerFunction(name, closure)
        org.geotools.filter.text.ecql.ECQL.toExpression(cql)
    }

    /**
     * A GeoTools Function that delegates to a Groovy Closure.
     */
    private static class ClosureFunction extends FunctionExpressionImpl {

        /**
         * The Groovy Closure
         */
        private final Closure closure

        /**
         * Create a new ClosureFunction.
         * @param name The name of the Function
         * @param closure The Groovy Closure
         * @param args The Expressions
         * @param fallback
         */
        ClosureFunction(String name, Closure closure, List<GtExpression> args, Literal fallback) {
            super(name)
            this.closure = closure
            this.getParameters().addAll(args)
            this.fallbackValue = fallback
        }

        /**
         * Evaluate the Function
         * @param obj The Function argument
         * @return The return value
         */
        def evaluate(def obj) {
            // Evaluate each parameter (set in the constructor)
            def args = getParameters().collect{p ->
                def v = p.evaluate(obj)
                // Wrap GeoTools objects with GeoScript objects
                if (v instanceof com.vividsolutions.jts.geom.Geometry) {
                    v = geoscript.geom.Geometry.wrap(v)
                }
                return v
            } as Object[]

            // Call the Closure
            def result = closure.call(*args)

            // Unwrap GeoScript objects to GeoTools objects
            if (result instanceof geoscript.geom.Geometry) {
                result = result.g
            }
            result
        }

        /**
         * Get the argument count
         * @return The argument count
         */
        int getArgCount() {
            return getParameters().size()
        }
    }

    /**
     * Whether we are embedded in GeoServer or not
     */
    private static final isGeoServerAvailable;

    /**
     * A GeoScript FunctionFactory
     */
    private static final GeoScriptFunctionFactory functionFactory

    /**
     * A GeoScript FactoryIteratorProvider
     */
    private static final GeoScriptFactoryIteratorProvider provider

    /**
     * Add the ability to dynamically create and register custom Functions
     */
    static {
        try {
            Class.forName("org.geoserver.config.GeoServer")
            isGeoServerAvailable = true
        } catch (ClassNotFoundException ex) {
            isGeoServerAvailable = false
        }
        if (!isGeoServerAvailable) {
            functionFactory = new GeoScriptFunctionFactory()
            provider = new GeoScriptFactoryIteratorProvider()
            GeoTools.addClassLoader(provider.class.classLoader)
            GeoTools.addFactoryIteratorProvider(provider)
            // If a custom function is called before a standard function,
            // the standard functions (through CQL) can't be found.
            org.geotools.filter.text.ecql.ECQL.toExpression("centroid(the_geom)")
        }
    }

    /**
     * The GeoScript FunctionFactory
     */
    private static class GeoScriptFunctionFactory implements FunctionFactory {

        /**
         * A cache of Function name's and their Closure
         */
        private Map<String, Closure> cache = new HashMap<String, Closure>()

        /**
         * Look up a GeoTools Function
         * @param name The function name
         * @param args The arguments
         * @param fallback The fallback value
         * @return A ClosureFunction or null
         */
        GtFunction function(String name, List args, Literal fallback) {
            if (cache.containsKey(name)) {
                Closure closure = cache.get(name)
                return new ClosureFunction(name, closure, args, fallback)
            }
            return null
        }

        /**
         * Look up a GeoToos Function.  Delegates to the previous method.
         * @param name The Name
         * @param args The arguments
         * @param fallback The fallback value
         * @return A ClosureFunction or null
         */
        GtFunction function(Name name, List args, Literal fallback) {
            function(name.localPart, args, fallback)
        }

        /**
         * Get the Function names
         * @return The Function names
         */
        List getFunctionNames() {
            cache.keySet().toList()
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

