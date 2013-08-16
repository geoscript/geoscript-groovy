import geoscript.layer.*
import geoscript.geom.Bounds
import javax.imageio.ImageIO

// Create a connection to a WMS server
WMS wms = new WMS("http://localhost:8080/geoserver/ows?service=wms&version=1.1.1&request=GetCapabilities")

// Print out some metadata
println "Name: ${wms.name}"
println "Title: ${wms.title}"
println "Abstract: ${wms.abstract}"
println "Keywords: ${wms.keywords.join(',')}"
println "Online Resource: ${wms.onlineResource}"
println "Update Sequence: ${wms.updateSequence}"
println "Version: ${wms.version}"
println "Formats: ${wms.getMapFormats.join(',')}"
println "Layers:"
wms.layers.each{layer ->
    println "   Name: ${layer.name}"
    println "      Title: ${layer.title}"
    println "      Srs: ${layer.srses}"
    println "      Styles: ${layer.styles}"
    layer.styles.each{style ->
        println "         Name: ${style.name}"
        println "            Title: ${style.title}"
        println "            Abstract: ${style.abstract}"
    }
    println "      Queryable: ${layer.queryable}"
    println "      Max Scale: ${layer.maxScale}"
    println "      Min Scale: ${layer.minScale}"
    println "      Parent: ${layer.parent}"
    println "      Children: ${layer.children}"
    println "      Bounds: ${layer.bounds}"
    println "      LatLon Bounds: ${layer.latLonBounds}"
}
// Get a WMS Layer
println "Get Layer 'world:borders': ${wms.getLayer('world:borders').name}"

// Get an Image for a Layer
def image = wms.getImage("world:borders")
ImageIO.write(image,"png",new File("world.png"))

// Get an Image for Layers
image = wms.getImage(["world:cities","world:borders"])
ImageIO.write(image,"png",new File("world_cities.png"))

// Get an Image for Layers with custom styles
image = wms.getImage([[name: "world:urbanareas1_1", style: "point"], [name: "world:urbanareas1_1", style: "heatmap"]])
ImageIO.write(image,"png",new File("world_urbanareas.png"))

// Get an Image for Layers and a custom Bounds
image = wms.getImage(["medford:hospitals","medford:citylimits"], bounds: new Bounds(-122.87999, 42.29600,-122.81312, 42.35004,"EPSG:4326"))
ImageIO.write(image,"png",new File("medford.png"))

// Get a Raster for Layer
def raster = wms.getRaster("world:borders")
ImageIO.write(raster.image,"png",new File("raster_world.png"))

// Get a Raster for two Layers
raster = wms.getRaster(["world:cities","world:borders"])
ImageIO.write(raster.image,"png",new File("raster_world_cities.png"))

// Get a Raster for two Layers with custom styles
raster = wms.getRaster([[name: "world:urbanareas1_1", style: "point"], [name: "world:urbanareas1_1", style: "heatmap"]])
ImageIO.write(raster.image,"png",new File("raster_world_urbanareas.png"))

// Get a Raster for two Layers and a custom Bounds
raster = wms.getRaster(["medford:hospitals","medford:citylimits"], bounds: new Bounds(-122.87999, 42.29600,-122.81312, 42.35004,"EPSG:4326"))
ImageIO.write(raster.image,"png",new File("raster_medford.png"))

// Get a Legend
def legend = wms.getLegend("world:borders")
ImageIO.write(legend,"png",new File("legend_world.png"))

legend = wms.getLegend(wms.getLayer("world:cities"))
ImageIO.write(legend,"png",new File("legend_cities.png"))

// Use WMSLayer in a Map
def map = new geoscript.render.Map(
    layers: [new WMSLayer(wms, ["world:borders","world:cities"])]
)
map.render(new File("map_world.png"))

// Combine WMS with a Shapefile
def states = new Shapefile("states.shp")
map = new geoscript.render.Map(
        layers: [new WMSLayer(wms, ["world:borders"]),states]
)
map.render(new File("map_world_states.png"))