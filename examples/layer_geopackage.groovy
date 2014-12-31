import geoscript.feature.Feature
import geoscript.layer.Layer
import geoscript.layer.Shapefile
import geoscript.proj.Projection
import geoscript.workspace.GeoPackage
import geoscript.workspace.Workspace

Workspace geopkg = new GeoPackage("geopkg.gpkg")
try {
    // Get the States Shapefile
    File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
    Shapefile shp = new Shapefile(file)

    // Add states shapefile to the GeoPackage database
    Layer l = geopkg.add(shp, 'states')
    println "Polygon Layer has ${l.count} features:"
    geopkg.get('states').eachFeature { Feature f ->
        println "${f['STATE_ABBR']} = ${f['STATE_NAME']} at ${f.geom.centroid}"
    }

    // Add the centroids of each state to the GeoPackage database
    Layer l2 = geopkg.add(shp.transform("state_centroids", [
            geom: "centroid(the_geom)",
            abbr: "STATE_ABBR",
            name: "STATE_NAME"
    ]))
    println "Point Layer has ${l.count} features:"
    geopkg.get('state_centroids').eachFeature { Feature f ->
        println "${f['abbr']} = ${f['name']} at ${f.geom.centroid}"
    }

    // Add a reprojected Layer
    Layer l3 = geopkg.add(shp.reproject(new Projection("EPSG:2927"), "states2927"))
    println "Projected Layer has ${l3.count} features:"
    geopkg.get('states2927').eachFeature { Feature f ->
        println "${f['STATE_ABBR']} = ${f['STATE_NAME']} at ${f.geom.centroid}"
    }

} finally {
    geopkg.close()
}