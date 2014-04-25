import geoscript.feature.Feature
import geoscript.filter.Property
import geoscript.layer.Shapefile
import geoscript.layer.io.GeoRSSWriter

def layer = new Shapefile("states.shp")
def writer = new GeoRSSWriter(
        feedType: "atom",
        geometryType: "simple",
        feedTitle: "United States",
        feedDescription: "A GeoRSS Feed of the United States",
        itemTitle: new Property("STATE_NAME"),
        itemDescription: new Property("STATE_ABBR"),
        itemGeometry: { Feature feature -> feature.geom.centroid}
)
println writer.write(layer)