import geoscript.layer.Shapefile
import geoscript.filter.Filter

// Get a shapefile
def shp = new Shapefile('states.shp')

// Create a Filter to find Washington State
def filter = new Filter("STATE_NAME='Washington'")

// Get the Feature (there should only be one)
def feature = shp.getFeatures(filter)[0]

// Print out some attributes
println("Name: ${feature.get("STATE_NAME")}")
println("Abbreviation: ${feature.get("STATE_ABBR")}")

// Create a Filter to find North and South Dakota
def bbox = "BBOX (the_geom, -102, 43.5, -100, 47.5)"
def features = shp.getFeatures(bbox)
features.each{f ->
    println("Name: ${f.get("STATE_NAME")}")
    println("Abbreviation: ${f.get("STATE_ABBR")}")
}