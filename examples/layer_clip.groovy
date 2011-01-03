import geoscript.layer.*
import geoscript.feature.*
import geoscript.geom.*
import geoscript.filter.Filter

// This is the Shapefile we want to clip
Shapefile shp = new Shapefile('states.shp')

// This is the Geometry we will use to clip
Geometry clipGeom = new Bounds(-105, 44, -95, 50).geometry

// Create a new Schema (based on the Shapefile)
Schema schema = new Schema('states_clipped', shp.schema.fields)

// Create a new Layer
Layer clippedLayer = shp.workspace.create(schema)

// Use the Cursor to effeciently loop through each Feature in the Shapefile
Cursor cursor = shp.getCursor(Filter.intersects(clipGeom))
while(cursor.hasNext()) {

    // Get the next Feature
    Feature f = cursor.next()

    // Create a Map for the new attributes
    Map attributes = [:]

    // The flag for whether the current Feature intersects with
    // the clipping Geometry
    boolean addIt = false

    // For each attribute in the shapefile
    f.attributes.each{k,v ->
        // If its Geometry, intersect it with the clip shape
        if (v instanceof Geometry) {
            if (clipGeom.intersects(v)){
                addIt = true
                v = clipGeom.intersection(v)
            }
            attributes[k] = v
        }
        // Else, they stay the same
        else {
            attributes[k] = v
        }
    }

    // Create a new Feature with the new attributes
    Feature feature = schema.feature(attributes, f.id)

    // Add it to the buffer Layer
    if (addIt) {
            clippedLayer.add(feature)
    }
}

// Always remember to close the cursor
cursor.close()
