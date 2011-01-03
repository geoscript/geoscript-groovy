import geoscript.layer.*
import geoscript.feature.*
import geoscript.geom.*

// This is the Shapefile we want to process
Shapefile shp = new Shapefile('states.shp')

// Create a new Schema (based on the Shapefile) but with Polygon Geometry
Schema schema = shp.schema.changeGeometryType('Polygon','states_minboundingcircles')

// Create a new Layer
Layer layer = shp.workspace.create(schema)
layer.add(shp.features.collect{f->

    // Create a Map for the new attributes
    Map attributes = [:]

    // For each attribute in the shapefile
    f.attributes.each{k,v ->

        // If its Geometry, get the minimum bounding circle 
        if (v instanceof Geometry) {
            attributes[k] = v.minimumBoundingCircle
        }
        // Else, they stay the same
        else {
            attributes[k] = v
        }
    }

    // Create a new Feature with the new attributes
    schema.feature(attributes, f.id)
})
