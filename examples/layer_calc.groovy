import geoscript.layer.*
import geoscript.feature.*
import geoscript.geom.*

// This is the shapefile we want to add and calculate fields to
Shapefile shp = new Shapefile('states.shp')

// Copy the schema but add id, area, x and y fields
List fields = shp.schema.fields
fields.add(new Field("id","int"))
fields.add(new Field("area","double"))
fields.add(new Field("x","Double"))
fields.add(new Field("y","Double"))
Schema schema = new Schema('states_calc', fields)

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

// Update the Are field
int id = 1
layer.update(schema.get('id'), {f->id++})
layer.update(schema.get('area'), {f->f.geom.area})
layer.update(schema.get('x'), {f->f.geom.centroid.x})
layer.update(schema.get('y'), {f->f.geom.centroid.y})