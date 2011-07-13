import geoscript.layer.Layer
import geoscript.layer.Shapefile
import geoscript.feature.Feature
import geoscript.filter.Filter

// Create a new Shapefile from the States Shapefile
Shapefile shp = new Shapefile('states.shp')
Layer mtShp = shp.workspace.add(shp, "states_mt")

// Create a Filter and get a single Feature
Filter isMt = new Filter("STATE_ABBR EQ 'MT'")
Feature mt = mtShp.getFeatures(isMt)[0]

// Make the Geometry bigger
mt.set("the_geom", mt.geom.buffer(1))

// Create a bbox Filter
Filter nearMt = Filter.bbox("the_geom", mt.geom.bounds)

// Subtract MT geometry from adjacent states
mtShp.getFeatures(nearMt.and(isMt.not)).each{state ->
    state.set("the_geom", state.geom.difference(mt.geom))
}

// Persist all modified features
mtShp.update()