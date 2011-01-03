import geoscript.layer.*
import geoscript.feature.*
import geoscript.geom.*

// This is the Shapefile we want to buffer
Shapefile shp = new Shapefile('states.shp')

// Create a new Schema (based on the Shapefile) but with Polygon Geometry
Schema schema = shp.schema.changeGeometryType('Polygon','states_buffers')

// Create a new Layer
Layer bufferLayer = shp.workspace.create(schema)

// The buffer distance
double distance = 2

// Iterate through each Feature using a closure
shp.features.each{f ->

    // Create a Map for the new attributes
    Map attributes = [:]

    // For each attribute in the shapefile
    f.attributes.each{k,v ->

        // If its Geometry, buffer it
        if (v instanceof Geometry) {
            attributes[k] = v.centroid.buffer(distance)
        }
        // Else, they stay the same
        else {
            attributes[k] = v
        }
    }

    // Create a new Feature with the new attributes
    Feature feature = schema.feature(attributes, f.id)

    // Add it to the buffer Layer
    bufferLayer.add(feature)
}
