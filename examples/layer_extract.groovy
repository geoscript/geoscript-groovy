import geoscript.layer.*
import geoscript.feature.*
import geoscript.geom.*
import geoscript.filter.Filter

// Get the shapefile
Shapefile shp = new Shapefile('states.shp')

// Create our Filter (Field names are case sensitive)
Filter filter = new Filter("STATE_ABBR = 'WA'")

// Get Washington State
Feature feature = shp.getFeatures(filter)[0]

// Create our new Schema
Schema schema = new Schema("state_wa", shp.schema.fields)

// Create a new Shapefile
Layer layer = shp.workspace.create(schema)

// Add the Washington State Feature
layer.add(feature)
