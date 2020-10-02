package geoscript

import groovy.ui.GroovyMain as Script
import groovy.ui.Console
import org.codehaus.groovy.tools.shell.Main as Shell

/**
 * The GeoScript Application that can run scripts, shell, or console.
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
            println "Please provide a command (script, shell, or console)"
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
        else {
            println "Unknown command '${command}'"
        }
    }

}
