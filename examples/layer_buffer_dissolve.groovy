import geoscript.layer.*
import geoscript.feature.*
import geoscript.geom.*
import geoscript.index.*

// This is the Shapefile we want to buffer
Shapefile shp = new Shapefile('states.shp')

// Create a new Schema (based on the Shapefile) but with Polygon Geometry
Schema schema = shp.schema.changeGeometryType('Polygon','states_buffers_dissolved')

// Create a new Layer
Layer bufferLayer = shp.workspace.create(schema)

// The buffer distance
double distance = 2

// Create the SpatialIndex used to quickly find intersecting buffers
SpatialIndex index = new Quadtree()

// Iterate through each Feature using a closure
shp.features.each{f ->

    // Get the geometry's centroid and buffer it
    Geometry geom = f.geom.centroid.buffer(distance)

    // Search the index for intersecting buffers
    List hits = index.query(geom.bounds)
    hits.each{hit ->
        // Make sure the geometry's actually intersect
        // not just the bounds
        if (geom.intersects(hit)) {
                // Remove the original Geometry from the index
                index.remove(hit.bounds, hit)
                // Union the Geometries
                geom = geom.union(hit)
        }
    }

    // Either add the geometry or the unioned geometry
    index.insert(geom.bounds, geom)
}

// Add all Geometries from the spatial index to the output layer
List geoms = index.queryAll()
geoms.each{geom->
    bufferLayer.add([geom])
}