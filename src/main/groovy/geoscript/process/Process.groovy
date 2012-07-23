package geoscript.process

import org.geotools.process.Process as GtProcess
import org.geotools.process.ProcessFactory
import org.geotools.feature.NameImpl
import org.opengis.util.InternationalString
import org.opengis.feature.type.Name
import org.geotools.process.Processors
import org.geotools.util.SimpleInternationalString
import org.geotools.factory.FactoryIteratorProvider
import org.geotools.data.Parameter
import java.awt.RenderingHints
import org.opengis.util.ProgressListener
import org.geotools.factory.GeoTools
import geoscript.layer.Layer
import geoscript.geom.Bounds

/**
 * A Process is a way of packaging spatial algorithms.  You can create a GeoScript Process by name and get access
 * to all of the built in GeoTool's Processes:
 * <code><pre>
 * def p = new Process("gs:Bounds")
 * Map results = p.execute(["features": layer])
 * </pre></code>
 * Or you can create a new Process using a Groovy Closure:
 * <code><pre>
 * Process p = new Process("convexhull",
 *   "Create a convexhull around the features",
 *   [features: geoscript.layer.Cursor],
 *   [result: geoscript.layer.Cursor],
 *   { inputs ->
 *       def geoms = new GeometryCollection(inputs.features.collect{f -> f.geom})
 *       def output = new Layer()
 *       output.add([geoms.convexHull])
 *       [result: output]
 *   }
 * )
 * Map results = p.execute(["features": layer])
 * </pre></code>
 * @author Jared Erickson
 */
class Process {

    /**
     * The underlying GeoTools Process
     */
    GtProcess process

    /**
     * The cached Name of the GeoTools Process
     */
    private NameImpl name

    /**
     * The cached GeoTools ProcessFactory
     */
    private ProcessFactory factory;

    /**
     * Create a Process by name.  If the namespace is not given, "geoscript" is the default.
     * @param name The name of the Process
     */
    Process(String name) {
        this.name = createName(name)
        this.process = Processors.createProcess(this.name)
        this.factory = Processors.createProcessFactory(this.name)
    }

    /**
     * Create a new Process with a Groovy Closure
     * @param name The name of the Process
     * @param description The description
     * @param inputs The input parameters
     * @param outputs The output results
     * @param closure The Groovy Closure
     */
    Process(String name, String description, Map inputs, Map outputs, Closure closure) {
        this.name = createName(name)
        registerProcess(this.name.localPart, description, inputs, outputs, closure)
        this.process = Processors.createProcess(this.name)
        this.factory = Processors.createProcessFactory(this.name)
    }

    /**
     * Create a NameImpl from a String
     * @param str The String
     * @return A NameImpl
     */
    private NameImpl createName(String str) {
        String namespace = "geoscript"
        String name = str
        def parts = str.split(":")
        if (parts.length == 2) {
            namespace = parts[0]
            name = parts[1]
        }
        new NameImpl(namespace, name)
    }
    
    /**
     * Register a Process with the GeoScript ProcessFactory
     * @param name The name of the Process
     * @param description The description
     * @param inputs The input parameters
     * @param outputs The output results
     * @param closure The Groovy Closure
     */
    static void registerProcess(String name, String description, Map inputs, Map outputs, Closure closure) {
        processFactory.cache.put(name, new ClosureProcessInfo(name:  name, description:  description,
                parameters: inputs, results: outputs, closure: closure))
    }

    /**
     * Get the name
     * @return The name
     */
    String getName() {
        name.toString()
    }

    /**
     * Get the title
     * @return The title
     */
    String getTitle() {
        factory.getTitle(name).toString()
    }

    /**
     * Get the description
     * @return The description
     */
    String getDescription() {
        factory.getDescription(name).toString()
    }

    /**
     * Get the version
     * @return The version
     */
    String getVersion() {
        factory.getVersion(name)
    }

    /**
     * Get the Map of input parameters.
     * @return The Map of input parameters
     */
    Map getParameters() {
        def params = factory.getParameterInfo(name)
        // Convert GeoTools classes to GeoScript classes
        params.collectEntries {key, value ->
            [key, convertGeoToolsToGeoScriptClass(value.type)]
        }
    }

    /**
     * Get the Map of output results.
     * @return The Map of output results
     */
    Map getResults() {
        def results = factory.getResultInfo(name, [:])
        // Convert GeoTools classes to GeoScript classes
        results.collectEntries {key, value ->
            [key, convertGeoToolsToGeoScriptClass(value.type)]
        }
    }

    /**
     * Execute this Process with the given parameters
     * @param params The input parameters
     * @return A Map or results
     */
    Map execute(Map params) {
        boolean isClosure = process instanceof ClosureProcess
        // If this is Closure based Process we need to convert to GeoScript classes
        // otherwise it's a GeoTools Process
        def paramInfo = isClosure ? getParameters() : factory.getParameterInfo(name)
        def inputs = params.collectEntries {key, value ->
            def p = isClosure ? paramInfo[key] : paramInfo[key].type
            def v = Process.convert(value, p)
            [key, v]
        }
        def results = process.execute(inputs, null)
        // We always want to convert the results to GeoScript objects
        def resultInfo = getResults()
        results.collectEntries {key, value ->
            def v = Process.convert(value, resultInfo[key])
            [key, v]
        }
    }

    /**
     * The string representation
     * @return The string representation
     */
    String toString() {
        name.toString()
    }

    /**
     * Get a List of the Process names
     * @return A List of Process names
     */
    static List getProcessNames() {
        def processes = []
        def processFactories = Processors.getProcessFactories();
        def iterator = processFactories.iterator();
        while (iterator.hasNext()) {
            def factory = iterator.next()
            def nameIterator = factory.getNames().iterator()
            while(nameIterator.hasNext()) {
                def p = nameIterator.next()
                processes.add(p.toString())
            }
        }
        return processes
    }

    /**
     * The ClosureProcess is a GeoTools Process that uses a Groovy Closure
     * @author Jared Erickson
     */
    private static class ClosureProcess implements GtProcess {

        /**
         * The Name
         */
        private final Name name

        /**
         * The Closure
         */
        private final Closure closure

        /**
         * Create a new ClosureProcess with a Name and Closure
         * @param name The Name
         * @param closure The Closure
         */
        ClosureProcess(Name name, Closure closure) {
            this.name = name
            this.closure = closure
        }

        /**
         * Execute this Process
         * @param input The Map of input parameters
         * @param monitor The ProgressListener which defaults to null
         * @return A Map of results
         */
        Map<String, Object> execute(Map<String, Object> input, ProgressListener monitor = null) {
            def factory = Processors.createProcessFactory(name)
            def paramsInfo = factory.getParameterInfo(name)
            def resultsInfo = factory.getResultInfo(name,input)
            // Turn all input parameters into GeoScript objects
            def params = input.collectEntries{key, value ->
                def p = paramsInfo[key]
                def v = Process.convert(value, Process.convertGeoToolsToGeoScriptClass(p.type))
                [key, v]
            }
            Map results = closure.call(params)
            // Turn all output results into GeoTools objects
            results.collectEntries{key, value ->
                def r = resultsInfo[key]
                def v = Process.convert(value, r.type)
                [key,v]
            }
        }
    }

    /**
     * A POGO that holds Process related metadata
     */
    private static class ClosureProcessInfo {
        String name
        String title
        String description
        String version = "1.0.0"
        Map parameters
        Map results
        Closure closure
        boolean supportsProcess = false
        boolean available = true
    }

    /**
     * The GeoScript ProcessFactory
     * @author Jared Erickson
     */
    private static class GeoScriptProcessFactory implements ProcessFactory {

        /**
         * A cache of Process's name and it's ClosureProcessInfo
         */
        private Map<String, ClosureProcessInfo> cache = new HashMap<String, ClosureProcessInfo>()

        /**
         * Find a ClosureProcessInfo in the cache by Name
         * @param name The Name
         * @return A ClosureProcessInfo
         */
        private ClosureProcessInfo findProcess(Name name) {
            cache.get(name.localPart)
        }

        /**
         * Create a new ClosureProcess
         * @param name The Name of the ClosureProcess
         * @return A ClosureProcess
         */
        GtProcess create(Name name) {
            new ClosureProcess(name, findProcess(name).closure)
        }

        /**
         * Get the title of this ProcessFactory
         * @return The title of this ProcessFactory
         */
        InternationalString getTitle() {
            new SimpleInternationalString("GeoScript Processes")
        }

        /**
         * Get whether this ProcessFactory is available
         * @return Whether this ProcessFactory is available
         */
        boolean isAvailable() {
            return true
        }

        /**
         * Get implementation hints for this ProcessFactory.
         * @return The implementation hints for this ProcessFactory.
         */
        Map<RenderingHints.Key, ?> getImplementationHints() {
            return null
        }

        /**
         * Get the Names of Processes in this ProcessFactory
         * @return A Set of Names
         */
        Set<Name> getNames() {
            new HashSet<Name>(cache.keySet().collect{name ->
                new NameImpl("geoscript", name)
            })
        }

        /**
         * Get the title of the Process
         * @param name The Name of the Process
         * @return The title
         */
        InternationalString getTitle(Name name) {
            new SimpleInternationalString(findProcess(name)?.name)
        }

        /**
         * Get the description of the Process
         * @param name The Name of the Process
         * @return The description
         */
        InternationalString getDescription(Name name) {
            new SimpleInternationalString(findProcess(name)?.description)
        }

        /**
         * Get whether the Process supports progress
         * @param name The Name of the Process
         * @return Whether the Process supports progress
         */
        boolean supportsProgress(Name name) {
            findProcess(name)?.supportsProgress
        }

        /**
         * Get the version of the Process
         * @param name The Name of the Process
         * @return The version
         */
        String getVersion(Name name) {
            findProcess(name)?.version
        }

        /**
         * Get the Map of input Parameters for the Process
         * @param name The Name of the Process
         * @return The Map of input Parameters
         */
        Map<String, Parameter<?>> getParameterInfo(Name name) {
            def params = new HashMap<String, Parameter<?>>();
            findProcess(name)?.parameters.entrySet().each{Map.Entry e ->
                def key = e.key
                // Convert all GeoScript classes to GeoTools classes
                def value = convertGeoScriptToGeoToolsClass(e.value as Class)
                params[key] = new Parameter(key, value, new SimpleInternationalString(key), new SimpleInternationalString(key))
            }
            params
        }

        /**
         * Get a Map of the result Parameters for the Process
         * @param name The Name of the Process
         * @param parameters The input parameters
         * @return The Map of the result Parameters
         * @throws IllegalArgumentException if an illegal argument is passed
         */
        Map<String, Parameter<?>> getResultInfo(Name name, Map<String, Object> parameters) throws IllegalArgumentException {
            def params = new HashMap<String, Parameter<?>>()
            findProcess(name)?.results.entrySet().each{Map.Entry e ->
                def key = e.key
                // Convert all GeoScript classes to GeoTools classes
                def value = convertGeoScriptToGeoToolsClass(e.value as Class)
                params[key] = new Parameter(key, value, new SimpleInternationalString(key), new SimpleInternationalString(key))
            }
            params
        }
    }

    /**
     * The GeoScriptProcessFactory
     */
    private static final GeoScriptProcessFactory processFactory = new GeoScriptProcessFactory()

    /**
     * The GeoScriptFactoryIteratorProvider
     */
    private static final GeoScriptFactoryIteratorProvider provider = new GeoScriptFactoryIteratorProvider()

    /**
     * The GeoScriptFactoryIteratorProvider
     */
    private static class GeoScriptFactoryIteratorProvider implements FactoryIteratorProvider {
        Iterator iterator(Class category) {
            if(ProcessFactory.class == category) {
                return [processFactory].iterator()
            } else {
                return null
            }
        }
    }

    /**
     * Add the ability to dynamically create and register custom Functions
     */
    static {
        GeoTools.addClassLoader(provider.class.classLoader)
        GeoTools.addFactoryIteratorProvider(provider)
    }

    /**
     * Convert GeoScript classes to a corresponding GeoTools class
     * @param geoScriptClass The GeoScript class
     * @return The Class
     */
    static Class convertGeoScriptToGeoToolsClass(Class geoScriptClass) {
        if (geoscript.geom.Geometry.isAssignableFrom(geoScriptClass)) {
            return com.vividsolutions.jts.geom.Geometry
        } else if (geoscript.geom.Bounds.isAssignableFrom(geoScriptClass)) {
            return org.geotools.geometry.jts.ReferencedEnvelope
        } else if (geoscript.layer.Layer.isAssignableFrom(geoScriptClass)) {
            return org.geotools.feature.FeatureCollection
        } else if (geoscript.layer.Cursor.isAssignableFrom(geoScriptClass)) {
            return org.geotools.feature.FeatureCollection
        } else {
            return geoScriptClass
        }
    }

    /**
     * Convert a GeoTools Class to a correspondng GeoScript Class
     * @param geoToolsClass The GeoTools Class
     * @return The Class
     */
    static Class convertGeoToolsToGeoScriptClass(Class geoToolsClass) {
        if (com.vividsolutions.jts.geom.Geometry.isAssignableFrom(geoToolsClass)) {
            return geoscript.geom.Geometry
        } else if (org.geotools.geometry.jts.ReferencedEnvelope.isAssignableFrom(geoToolsClass)) {
            return geoscript.geom.Bounds
        } else if (org.geotools.feature.FeatureCollection.isAssignableFrom(geoToolsClass)) {
            return geoscript.layer.Cursor
        } else {
            return geoToolsClass
        }
    }

    /**
     * Convert the source Object to the target Class if possible. This is used to convert GeoScript and GeoTools objects
     * back and forth
     * @param source The source Object
     * @param target The target Class
     * @return The source Object as the target Class (if possible)
     */
    static Object convert(Object source, Class target) {
        // Geometry and Geometry
        if (com.vividsolutions.jts.geom.Geometry.isAssignableFrom(target) && geoscript.geom.Geometry.isInstance(source)) {
            return (source as geoscript.geom.Geometry).g
        }
        else if (geoscript.geom.Geometry.isAssignableFrom(target) && com.vividsolutions.jts.geom.Geometry.isInstance(source)) {
            return geoscript.geom.Geometry.wrap(source as com.vividsolutions.jts.geom.Geometry)
        }
        // ReferencedEnvelope and Bounds
        else if (org.geotools.geometry.jts.ReferencedEnvelope.isAssignableFrom(target) && geoscript.geom.Bounds.isInstance(source)) {
            return (source as geoscript.geom.Bounds).env
        }
        else if (geoscript.geom.Bounds.isAssignableFrom(target) && org.geotools.geometry.jts.ReferencedEnvelope.isInstance(source)) {
            return new Bounds(source as org.geotools.geometry.jts.ReferencedEnvelope)
        }
        // FeatureCollection and Layer
        else if ( org.geotools.feature.FeatureCollection.isAssignableFrom(target) && geoscript.layer.Layer.isInstance(source)) {
            return (source as geoscript.layer.Layer).fs.features
        }
        else if (geoscript.layer.Layer.isAssignableFrom(target) && org.geotools.feature.FeatureCollection.isInstance(source)) {
            return new Layer(source as org.geotools.feature.FeatureCollection)
        }
        // FeatureCollection and Cursor
        else if (org.geotools.feature.FeatureCollection.isAssignableFrom(target) && geoscript.layer.Cursor.isInstance(source)) {
            return (source as geoscript.layer.Cursor).col
        }
        else if (geoscript.layer.Cursor.isAssignableFrom(target) && org.geotools.feature.FeatureCollection.isInstance(source)) {
            return new geoscript.layer.Cursor(source as org.geotools.feature.FeatureCollection)
        }
        // Just return an unconverted Object
        else {
            return source
        }
    }
}
