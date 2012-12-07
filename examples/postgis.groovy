import geoscript.layer.Layer
import geoscript.workspace.PostGIS

// Connect to a PostGIS database
def p = new PostGIS('postgres','localhost','5432','public','postgres','postgres')

// Print the format
println("Format: $p.format")

// Print the layer names
println("Layers:")
p.layers.each{lyr -> println(lyr)}

// Get the first Layer
Layer layer = p.layers[0]
println("Layer: ${layer.name}")
println("Projection : ${layer.proj}")
println("# Features: ${layer.count()}")
println("Bounds: ${layer.bounds()}")

// Print each feature in the first Layer
println("Features:")
layer.features.each{f -> println(f)}

// Get a cursor to iterate over each Feature
println("Features from Cursor:")
def c = layer.cursor
while(c.hasNext()) {
    def f = c.next()
    println(f)
}
c.close()