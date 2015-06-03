import geoscript.feature.Feature
import geoscript.layer.Layer
import geoscript.layer.Shapefile
import geoscript.workspace.OGR

OGR ogr = new OGR("SQLite", new File("states.sqlite").absolutePath)
Layer shp = new Shapefile(new File("states.shp"))
Layer sqlite = ogr.create(shp.cursor)
println "# Features = ${sqlite.count}"
sqlite.eachFeature { Feature f ->
    println "${f.geom} | ${f.attributes}"
}
