import geoscript.layer.*
import geoscript.proj.*
import geoscript.workspace.*
import geoscript.feature.*
import geoscript.geom.*


// Write Shapefile to GeoJSON 
def shp = new Shapefile("states.shp")
def memory = new Memory()
def schema = shp.schema.changeGeometryType("Point","states_centroids")
def centroidLayer = memory.create(schema)

// Use the Cursor to effeciently loop through each Feature in the Shapefile
Cursor cursor = shp.cursor
while(cursor.hasNext()) {

    // Get the next Feature
    Feature f = cursor.next()

    // Create a Map for the new attributes
    Map attributes = [:]

    // For each attribute in the shapefile
    f.attributes.each{k,v ->

        // If its Geometry, find the centroid
        if (v instanceof Geometry) {
            attributes[k] = v.centroid
        }
        // Else, they stay the same
        else {
            attributes[k] = v
        }
    }

    // Create a new Feature with the new attributes
    Feature feature = schema.feature(attributes, f.id)

    // Add it to the centroid Layer
    centroidLayer.add(feature)
}

// Always remember to close the cursor
cursor.close()

// Write the centroid Layer to a GeoJSON File
centroidLayer.toJSONFile(new File("states.json"))

// Write a web page with OpenLayers
def file = new File("states.html")
def writer = new FileWriter(file)
def builder = new groovy.xml.MarkupBuilder(writer)
builder.html {
    head {
        title "OpenLayers with GeoScript Groovy"
        script (src:"http://openlayers.org/api/OpenLayers.js","")
        script """
            var map;
            function init() {
                map = new OpenLayers.Map("map");
                var layer = new OpenLayers.Layer.WMS("OpenLayers WMS", 
                    "http://vmap0.tiles.osgeo.org/wms/vmap0",
                    {layers: 'basic'} 
                );
                map.addLayer(layer);
                var vectorLayer = new OpenLayers.Layer.Vector("States", {
                    strategies: [new OpenLayers.Strategy.Fixed()],
                    protocol: new OpenLayers.Protocol.HTTP({
                            url: "states.json",
                            format: new OpenLayers.Format.GeoJSON()
                    })
                });
                map.addLayer(vectorLayer);
                map.addControl(new OpenLayers.Control.LayerSwitcher());
                map.addControl(new OpenLayers.Control.MousePosition());
                map.zoomToMaxExtent();
           }
        """
    }
    body(onload:"init()") {
        h1 "OpenLayers with GeoScript Groovy"
        div (id:"map", style:"width:100%;height:100%","")
    }
}
