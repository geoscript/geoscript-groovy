import geoscript.layer.*
import geoscript.feature.*
import geoscript.geom.*

// Get the shapefile
Shapefile shp = new Shapefile('states.shp');

// Create a new Schema
Schema schema = new Schema('states_extent', [['the_geom','Polygon','EPSG:4326']])

// Create our new Layer
Layer layer = shp.workspace.create(schema)

// Collect the Geometries
List geoms = shp.features.collect{f->f.geom}

// Create a GeometryCollection from the List of Geometries
GeometryCollection geomCol = new GeometryCollection(geoms)

// Get the Convex Hull from the GeometryCollection
Geometry extentGeom = geomCol.bounds.geometry

// Add the Convex Hull Geometry as a Feature
layer.add(schema.feature([extentGeom]))
