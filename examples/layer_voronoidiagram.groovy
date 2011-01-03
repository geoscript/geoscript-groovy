import geoscript.layer.*
import geoscript.feature.*
import geoscript.geom.*

// Get the Shapefile
Shapefile shp = new Shapefile('states.shp')

// Create a new Schema
Schema schema = new Schema('states_voronoi', [['the_geom','MultiPolygon','EPSG:4326']])

// Create the new Layer
Layer diagramLayer = shp.workspace.create(schema)

// Collect Geometries from the Shapefile
List geoms = shp.features.collect{f->f.geom.centroid}

// Create a GeometryCollection from the List of Geometries
GeometryCollection geomCol = new GeometryCollection(geoms)

// Get the Voronoi Diagram from the GeometryCollection
Geometry voronoiGeom = geomCol.voronoiDiagram

// Add the Voronoi Diagram Geometry as a Feature
diagramLayer.add(schema.feature([voronoiGeom]))
