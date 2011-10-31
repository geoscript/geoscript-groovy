import geoscript.layer.*
import geoscript.feature.*
import geoscript.geom.*

// Get the Shapefile
Shapefile shp = new Shapefile('states.shp')

// Create a new Schema
Schema schema = new Schema('states_spiderdiagram', [
        ['the_geom','LineString','EPSG:4326'],
        ['state','String'],
        ['length','Double']
])

// Create the new Layer
Layer spiderLayer = shp.workspace.create(schema)

// The state we want to create our diagram around
String state = "ND"

// Get the state and it's centroid
Feature feature = shp.getFeatures("STATE_ABBR = '${state}'")[0]
Point centroid = feature.geom.centroid

// Create a line from the state to every other except for itself
spiderLayer.add(shp.getFeatures("STATE_ABBR <> '${state}'").collect{f ->
    String otherState = f.get("STATE_ABBR")
    Point otherCentroid = f.geom.centroid
    LineString line = new LineString([centroid, otherCentroid])
    schema.feature([
       "the_geom": line,
       "state": otherState,
       "length": line.length
    ])
})
