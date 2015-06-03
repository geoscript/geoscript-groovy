import geoscript.feature.Feature
import geoscript.layer.Layer
import geoscript.layer.Shapefile
import geoscript.workspace.OGR

OGR ogr = new OGR("SQLite", new File("states_spatialite.sqlite").absolutePath)
Layer shp = new Shapefile(new File("states.shp"))
Layer spatialite = ogr.add(shp, options: [
    "SPATIALITE=YES"
])
println "# Features = ${spatialite.count}"
spatialite.eachFeature { Feature f ->
    println "${f.geom} | ${f.attributes}"
}