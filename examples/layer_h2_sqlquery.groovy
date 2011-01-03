import geoscript.workspace.*
import geoscript.layer.*
import geoscript.feature.*

// Get the states Shapefile
Shapefile shp = new Shapefile("states.shp")

// Create a new Spatial H2 database
H2 h2 = new H2("states",".")

// Add the states Shapefile as a new table
if (h2.get("states") == null) {
    h2.add(shp, "states")
}

// Add a virtual SQL Query of the states centroids
String sql = "select st_centroid(\"the_geom\") as \"the_geom\", \"STATE_NAME\" FROM \"states\""
Layer statesCentroidLayer = h2.addSqlQuery("states_centroids", sql, new Field("the_geom", "Point", "EPSG:4326"), [])
statesCentroidLayer.features.each{feat->
    println(feat.geom)
}

// Add another virtual SQL Query of the buffered states centroids
String sql2 = "select st_buffer(st_centroid(\"the_geom\"), 500) as \"the_geom\", \"STATE_NAME\" FROM \"states\""
Layer statesCentroidBufferLayer = h2.addSqlQuery("states_centroid_buffers", sql2, "the_geom", "Polygon", 4326, [])
statesCentroidBufferLayer.features.each{feat->
    println(feat.geom)
}

// Close the H2 database
h2.close()

