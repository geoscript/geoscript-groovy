import geoscript.layer.*
import geoscript.feature.*
import geoscript.geom.*

// Get the Shapefile
Shapefile shp = new Shapefile('states.shp')

// Create a new Schema
Schema schema = new Schema('states_minimum_bounding_circle', [['the_geom','Polygon','EPSG:4326']])

// Create the new Layer
Layer layer = shp.workspace.create(schema)

// Collect Geometries from the Shapefile
List geoms = shp.features.collect{f->f.geom}

// Create a GeometryCollection from the List of Geometries
GeometryCollection geomCol = new GeometryCollection(geoms)

// Get the Minimum Bounding Circle from the GeometryCollection
Geometry circleGeom = geomCol.minimumBoundingCircle

// Add the Minimum Bounding Circle Geometry as a Feature
layer.add(schema.feature([circleGeom]))
