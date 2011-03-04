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
def wfs = new WFS("http://frameworkwfs.usgs.gov/framework/wfs/wfs.cgi", [
    timeout: 9000
])

// Print the Layers
println("Layers:")
wfs.layers.each{layer ->
    println(layer)
}

// Get the 'gubs:GovernmentalUnitCE' Layer and inspect some properties
def layer = wfs.get("gubs:GovernmentalUnitCE")
println("Layer: ${layer.name}")
println("   Format: ${layer.format}")
println("   Projection: ${layer.proj}")
println("   Schema: ${layer.schema}")
println("   # Features: ${layer.count}")
println("   Bounds: ${layer.bounds}")

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
println("Features where instanceName = 'Washington'")
layer.getFeatures("instanceName = 'Washington'").each{f ->
    println("Feature:")
    println("----------------------")
    f.schema.fields.each {fld ->
        if (!fld.isGeometry()) println("  ${fld.name} = ${f.get(fld.name)}")
    }
    println("")
}

// Query the Layer by Bounding Box
def filter = Filter.bbox("geometry", new Bounds(43.636, -73.0442, 44.9487, -72.0159, "EPSG:4269"))
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
def schema = new Schema("wa_govunits", layer.schema.fields)
def waLayer = dir.create(schema)
def features = layer.getFeatures("instanceName = 'Washington'")
features.each{f->
    waLayer.add([
        the_geom: f.geom,
        typeAbbrev: f.get('typeAbbreviation'),
        instanceNa: f.get('instanceName'),
        instanceAl: f.get('instanceAlternateName'),
        officialDe: f.get('officialDescription'),
        instanceCo: f.get('instanceCode'),
        codingSyst: f.get('codingSystemReference'),
        government: f.get('governmentalUnitType'),
        typeDefini: f.get('typeDefinition')

    ])
}
println("Shapefile: ${waLayer.name}")
println("   Bounds: ${waLayer.bounds}")
println("   Projection: ${waLayer.proj}")
println("   Schema: ${waLayer.schema}")