// Import modules
import geoscript.layer.*
import geoscript.workspace.*

// 1. Explore the states Layer by creating a separate Layer for each state

// Create the output Directory Workspace
File dir = new File("states")
if (!dir.exists()) dir.mkdir()
Workspace out = new Directory(dir)

// Get the States shapefile
Layer shp = new Shapefile("states.shp")

// Save each Feature (or state) to a separate Shapefile
shp.features.each{f->
    Layer state = out.create(f.attributes.get("STATE_ABBR"), shp.schema.fields)
    state + f
}

// 2. Merge the separate state Layers back into one states Layer

// Create workspaces from existing directories
def source = out
def target = new Directory(".")

// Iterate through layers in source workspace
def states
source.names.each{name ->
    // Get the Layer
    def state = source.get(name)
    // Create merge shapefile the first time through
    if (!states) {
        states = target.create("states_merged", state.schema.fields)
    }
    // Iterate through source features to add each target
    state.features.each{f->
        states + f
    }
}
