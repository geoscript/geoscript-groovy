import geoscript.layer.*
import geoscript.feature.*

// This is the shapefile we want to add and calculate fields to
Shapefile shp = new Shapefile('states.shp')

// Copy the schema but add id, area, x and y fields
List fields = shp.schema.fields
fields.add(new Field("id","int"))
fields.add(new Field("area","double"))
fields.add(new Field("x","Double"))
fields.add(new Field("y","Double"))
Schema schema = new Schema('states_update', fields)

// Create the new Layer
Layer layer = shp.workspace.create(schema)

// Copy all features to the new Layer
layer.add(shp.features.collect{f->
    Map attributes = [:]
    f.attributes.each{k,v ->
        attributes[k] = v
    }
    schema.feature(attributes, f.id)
})

// Set the id, area, x, and y field value
int id = 1
layer.features.each{f ->
    f.set("id", id++)
    f.set("area", f.geom.area)
    f.set("x", f.geom.centroid.x)
    f.set("y", f.geom.centroid.y)
}

// Persist the updates
layer.update()