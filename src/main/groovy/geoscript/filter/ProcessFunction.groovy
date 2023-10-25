package geoscript.filter

import geoscript.process.Process
import org.geotools.feature.NameImpl

/**
 * A Function that wraps a Process for rendering transformations.
 * @author Jared Erickson
 */
class ProcessFunction extends Function {

    /**
     * Create a Rendering Transformation Function from a Process and a variable List of Functions.
     * @param process The GeoScript Process
     * @param functions A variable List of Functions
     */
    ProcessFunction(Process process, Function... functions) {
        super(createProcessFunction(process, functions))
    }

    /**
     * Create a Rendering Transformation Function from a Process and a variable List of Functions.
     * @param process The GeoScript Process
     * @param functions A variable List of Functions
     */
    private static org.geotools.api.filter.expression.Function createProcessFunction(geoscript.process.Process process, Function... functions) {
        def pff = new org.geotools.process.function.ProcessFunctionFactory()
        def names = process.name.split(":")
        def nm = new NameImpl(names[0], names[1])
        def f = pff.function(nm, functions.collect{it.expr}, null)
        f
    }

}
