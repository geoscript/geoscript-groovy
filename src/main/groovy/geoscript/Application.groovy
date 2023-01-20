package geoscript

import groovy.ui.GroovyMain as Script
import groovy.console.ui.Console
import org.apache.groovy.groovysh.Main as Shell
import org.geotools.util.factory.GeoTools
import org.locationtech.jts.JTSVersion

/**
 * The GeoScript Application that can run script, shell, console or version.
 * @author Jared Erickson
 */
class Application {

    /**
     * Run the GeoScript Application
     * @param args The command line arguments.  At least script, shell, or console
     * is required.
     */
    static void main(String[] args) {
        if (args.length < 1) {
            println "Please provide a command (script, shell, or console, version)"
            return
        }
        String command = args[0]
        String[] argsWithoutCommand = (args as List).subList(1, args.length)
        if (command.equalsIgnoreCase("script")) {
            Script.main(argsWithoutCommand)
        }
        else if (command.equalsIgnoreCase("shell")) {
            Shell.main(argsWithoutCommand)
        }
        else if (command.equalsIgnoreCase("console")) {
            Console.main(argsWithoutCommand)
        }
        else if (command.equalsIgnoreCase("version")) {
            println "GeoScript = ${GeoScript.version} Groovy = ${GroovySystem.getVersion()} GeoTools = ${GeoTools.getVersion()} JTS = ${JTSVersion.CURRENT_VERSION.toString()}"
        }
        else {
            println "Unknown command '${command}'"
        }
    }

}
