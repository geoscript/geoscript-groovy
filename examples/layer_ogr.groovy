import geoscript.workspace.OGR

println "Is OGR available? ${OGR.isAvailable()}"
println "Drivers:"
OGR.getDrivers().eachWithIndex { String driver, int i ->
    println "   ${i}). ${driver}"
}