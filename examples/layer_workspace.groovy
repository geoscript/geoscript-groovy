import geoscript.layer.Shapefile

def shp = new Shapefile('states.shp')
println("Count: ${shp.count()}")
println("Bound: ${shp.bounds()}")

// Use the features property to print each Feature
shp.features.each{f -> println(f)}

// Get the schema
println("Schema: ${shp.schema}")

// Get a field from the schema
def f = shp.schema.field('STATE_NAME')
println("Field name: ${f.name} Field Type: ${f.typ}")

// Every Layer has a workspace which is a collection of layers
def ws = shp.workspace
println("Workspace: ${ws}")
println("Layers: ${ws.names}")

