// import GeoScript modules
import geoscript.layer.*
import geoscript.feature.*
import geoscript.geom.*

// This is the Shapefile we want to extract centroids from
Shapefile shp = new Shapefile('states.shp')

// Create a new Schema (based on the Shapefile) but with Point Geometry
Schema schema = shp.schema.changeGeometryType('Point','states_centroids')

// Create our new centroid Layer
Layer centroidLayer = shp.workspace.create(schema)

// Use the Cursor to effeciently loop through each Feature in the Shapefile
Cursor cursor = shp.cursor
while(cursor.hasNext()) {

    // Get the next Feature
    Feature f = cursor.next()

    // Create a Map for the new attributes
    Map attributes = [:]

    // For each attribute in the shapefile
    f.attributes.each{k,v ->

        // If its Geometry, find the centroid
        if (v instanceof Geometry) {
            attributes[k] = v.centroid
        }
        // Else, they stay the same
        else {
            attributes[k] = v
        }
    }

    // Create a new Feature with the new attributes
    Feature feature = schema.feature(attributes, f.id)

    // Add it to the centroid Layer
    centroidLayer.add(feature)
}

// Always remember to close the cursor
cursor.close()