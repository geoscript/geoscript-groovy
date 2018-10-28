package geoscript.layer

import geoscript.geom.Bounds
import geoscript.layer.WMS.Layer
import geoscript.proj.Projection

/**
 * A WMSLayer is a way to draw one or more WMS layers on a Map.
 * <p><blockquote><pre>
 * def wms = new WMS("http://localhost:8080/geoserver/ows?service=wms&version=1.1.1&request=GetCapabilities")
 * def layer = new WMSLayer(wms, ["world:borders","world:cities"])
 * def map = new Map(layers: [layer])
 * map.render(new File("world_with_cities.png"))
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class WMSLayer implements Renderable {

    /**
     * The WMS connection to a remote server
     */
    private WMS wms

    /**
     * The List of WMS.Layers
     */
    private List<Layer> wmsLayers = []

    /**
     * The Bounds of this WMSLayer
     */
    private Bounds bounds

    /**
     * The Projection of this WMSLayer
     */
    private Projection proj

    /**
     * Create a new WMSLayer
     * @param wms The WMS connection to a remote server
     * @param layers The List of WMS.Layers or layer names
     * @param proj The Projection (EPSG:4326 is the default)
     */
    WMSLayer(WMS wms, def layers, def proj = "EPSG:4326") {
        this.wms = wms
        this.proj = new Projection(proj)
        if (!(layers instanceof List)) {
            layers = [layers]
        }
        layers.each{layer ->
            if (layer instanceof Layer) {
                wmsLayers.add(layer)
            } else {
                def l = wms.getLayer(layer)
                if (l) {
                    wmsLayers.add(l)
                }
            }
        }
    }

    /**
     * Get the List of WMS.Layers
     * @return The List of WMS.Layers
     */
    List<Layer> getLayers() {
        wmsLayers
    }

    /**
     * Get the Bounds
     * @return The Bounds
     */
    Bounds getBounds() {
        if (!this.bounds) {
            wmsLayers.each {l ->
                if (!this.bounds) this.bounds = l.getBounds(this.proj)
                else this.bounds.expand(l.getBounds(this.proj))
            }
        }
        this.bounds
    }

    /**
     * Get the Projection
     * @return The Projection
     */
    Projection getProj() {
        this.proj
    }

    /**
     * The String representation
     * @return
     */
    String toString() {
        "WMS @ ${wms} with ${wmsLayers.join(",")}"
    }

    @Override
    List<org.geotools.map.Layer> getMapLayers(Bounds bounds, List size) {
        def gtWmsLayer = new org.geotools.ows.wms.map.WMSLayer(wms.wms, layers[0].layer)
        (1..<this.layers.size()).each{i ->
            gtWmsLayer.addLayer(this.layers[i].layer)
        }
        [gtWmsLayer]
    }
}
