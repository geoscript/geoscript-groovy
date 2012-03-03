import geoscript.layer.Shapefile
import geoscript.process.Process

// Get our input layer
def statesShp = new Shapefile("states.shp")

// Get a built in Process
def process = new Process("gs:BufferFeatureCollection")

// Execute the Process
Map results = process.execute(["feature collection": statesShp.cursor, "width of the buffer": 1.2])

// Save the results to a new Shapefile
def statesConvexHullShp = statesShp.workspace.create("states_process_buffer", statesShp.schema.fields)
statesConvexHullShp.add(results.result)