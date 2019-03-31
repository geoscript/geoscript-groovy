import geoscript.layer.Layer
import geoscript.workspace.SpatiaLite

SpatiaLite spatiaLite = new SpatiaLite("naturalearth.sqlite")

println "Layers:"
spatiaLite.layers.each { Layer layer ->
    println "   ${layer.name}"
    println "      # Features = ${layer.count}"
    println "      Schema = ${layer.schema}"
}