/**
 * WFS is just another Workspace like a PostGIS database or a Directory full
 * of Shapefiles.
 */

// Import the WFS Workspace
import geoscript.workspace.*

// Other GeoScript Imports
import geoscript.feature.Schema
import geoscript.geom.Bounds
import geoscript.filter.Filter

// Create a WFS Workspace with a GetCapabilities URL
def wfs = new WFS("http://localhost:8080/geoserver/ows?service=wfs&version=1.1.0&request=GetCapabilities", timeout: 9000)

// Print the Layers
println("Layers:")
wfs.layers.each{layer ->
    println(layer)
}

// Get a Layer and inspect some properties
def layer = wfs.get("topp:states")
println("Layer: ${layer.name}")
println("   Format: ${layer.format}")
println("   Projection: ${layer.proj}")
println("   Schema: ${layer.schema}")
println("   # Features: ${layer.count}")
println("   Bounds: ${layer.bounds}")
println("   Geometry Field: ${layer.schema.geom.name}")

// Print all features
println("   Features:")
layer.features.each{f->
    println("Feature:")
    println("----------------------")
    f.schema.fields.each {fld ->
        if (!fld.isGeometry()) println("  ${fld.name} = ${f.get(fld.name)}")
    }
    println("")
}

// Query the Layer by attribute
println("Features where STATE_ABBR = 'WA'")
layer.getFeatures("STATE_ABBR = 'WA'").each{f ->
    println("Feature:")
    println("----------------------")
    f.schema.fields.each {fld ->
        if (!fld.isGeometry()) println("  ${fld.name} = ${f.get(fld.name)}")
    }
    println("")
}

// Query the Layer by Bounding Box
def filter = Filter.bbox("the_geom", new Bounds(43.636, -73.0442, 44.9487, -72.0159, "EPSG:4269"))
println("Features for ${filter}")
layer.getFeatures(filter).each{f ->
    println("Feature:")
    println("----------------------")
    f.schema.fields.each {fld ->
        if (!fld.isGeometry()) println("  ${fld.name} = ${f.get(fld.name)}")
    }
    println("")
}

// Extract a subset from WFS and save it to a Shapefile
def dir = new Directory(".")
def schema = new Schema("wa_state", layer.schema.fields)
def waLayer = dir.create(schema)
def features = layer.getFeatures("STATE_ABBR = 'WA'")
features.each{f->
    waLayer.add([
            the_geom: f.geom,
            STATE_ABBR: f.get('STATE_ABBR'),
    ])
}
println("Shapefile: ${waLayer.name}")
println("   Bounds: ${waLayer.bounds}")
println("   Projection: ${waLayer.proj}")
println("   Schema: ${waLayer.schema}")