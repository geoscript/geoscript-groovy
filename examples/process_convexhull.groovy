import geoscript.layer.Layer
import geoscript.layer.Shapefile
import geoscript.process.Process
import geoscript.geom.GeometryCollection

// Get our input layer
def statesShp = new Shapefile("states.shp")

// Define our new custom Process
Process p = new Process("convexhull", "Create a convexhull around the features",
        // Inputs
        [features: geoscript.layer.Cursor],
        // Outputs
        [result: geoscript.layer.Cursor],
        // Closure
        { inputs ->
            def geoms = new GeometryCollection(inputs.features.collect{f -> f.geom})
            def output = new Layer()
            output.add([geoms.convexHull])
            [result: output]
        }
)

// Execute the Process
Map results = p.execute(["features": statesShp.cursor])

// Save the results to a new Shapefile
def statesConvexHullShp = statesShp.workspace.create("states_process_convexhull", [["the_geom", "Polygon", "EPSG:4326"]])
statesConvexHullShp.add(results.result)