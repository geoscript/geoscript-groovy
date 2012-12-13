/**
 * The WMS module includes support for getMap and getLegend requests and Map integration
 * with just enough metadata from GetCapabilities to explore available layers and styles.
 */

// Import the WMS module
import geoscript.wms.WMS

// Import other classes
import geoscript.render.Map
import geoscript.style.*
import geoscript.layer.Shapefile
import javax.imageio.ImageIO

// Connect to the MassGIS WMS Server
def wms = new WMS("http://localhost:8080/geoserver/wms?REQUEST=GetCapabilities&version=1.1.0")

// Let's explore some metadata
println("WMS: ${wms.name}")
println("   Title: ${wms.title}")
println("   Version: ${wms.version}")
println("   Abstract: ${wms.abstract}")
println("   Keywords: ${wms.keywords.join(',')}")
println("   URL: ${wms.onlineResource}")

// Let's display the layers available
println("Layers:")
wms.layers.eachWithIndex{layer,i ->
    println("   ${i}). ${layer}")
}

// We want the towns layer (aka massgis:GISDATA.TOWNS_POLYM)
def layer = wms.getLayer("world:borders")

// Let's look at some Layer properties
println("Layer: ${layer.name}")
println("   Title: ${layer.title}")
println("   Queryable? ${layer.queryable}")
println("   Min Scale: ${layer.scaleDenominatorMin}")
println("   Max Scale: ${layer.scaleDenominatorMax}")
println("   Lat/Lon Bounds: ${layer.latLonBounds}")
println("   Bounds: ${layer.bounds.size()}")
layer.bounds.each{bound->
    println("       ${bound}")
}
println("   Styles: ${layer.styles.size()}")
layer.styles.each{style->
    println("       ${style}")
}
println("   SRS(es): ${layer.srs.size()}")
layer.srs[0..5].each{srs->
    println("       ${srs}")
}

// Get an image
def image = wms.getMap([
    bbox: layer.bounds[0],
    srs: "EPSG:4326",
    layers: [layer.name]
])
ImageIO.write(image, "PNG", new File("world_borders.png"))

// Get the legend
def legend = wms.getLegend([
    layer: layer.name
])
ImageIO.write(legend, "PNG", new File("world_borders_legend.png"))

// Create a Map combining a shapefile with a WMS layer
def file = new File("states.shp")
def shp = new Shapefile(file)
shp.style = new Fill("steelblue",0.5) + new Stroke("wheat")
def map = new Map(
    width: 512,
    height: 512,
    layers: [layer, shp],
    proj: "EPSG:4326",
    bounds: layer.latLonBounds,
    fixAspectRatio: true
)
map.render("world_borders_states.png")
map.close()
