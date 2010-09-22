package geoscript.wms

import geoscript.geom.Bounds
import org.geotools.data.ows.Layer
import org.geotools.data.ows.WMSCapabilities
import org.geotools.data.wms.WebMapServer
import javax.imageio.ImageIO
import java.awt.image.BufferedImage

/**
 * A Web Map Server (WMS) module
 * @author Jared Erickson
 */
class WMS {

    /**
     * The GeoTools WebMapServer
     */
    WebMapServer wms

    /**
     * A cached WMSCapabilities
     */
    private final WMSCapabilities capabilities

    /**
     * Create a new WMS object with a get capabilities URL
     * @param url The get capabilities URL
     */
    WMS(String url) {
        wms = new WebMapServer(new URL(url))
        capabilities = wms.capabilities
    }

    /**
     * Get a List of WMSLayers
     * @return A List of WMSLayers
     */
    List getLayers() {
        List layers = []
        capabilities.layerList.each{layer ->
            if (layer.name != null) {
                layers.add(new WMSLayer(layer))
            }
        }
        layers
    }

    WMSLayer getLayer(String name) {
        WMSLayer wmsLayer = null
        capabilities.layerList.each{layer ->
            if (name.equalsIgnoreCase(layer.name)) {
                wmsLayer = new WMSLayer(layer)
            }
        }
        return wmsLayer
    }

    BufferedImage getMap(Map options) {
        def mapRequest = wms.createGetMapRequest()
        int w = options.get('width', 512)
        int h = options.get('height', 512)
        double minX
        double minY
        double maxX
        double maxY
        def bbox = options.get("bbox")
        if (bbox instanceof String) {
            String[] parts = bbox.split(",")
            minX = parts[0] as double
            minY = parts[1] as double
            maxX = parts[2] as double
            maxY = parts[3] as double
        }
        else if (bbox instanceof Bounds) {
            minX = bbox.l
            minY = bbox.b
            maxX = bbox.r
            maxY = bbox.t
        }
        else {
            minX = options['minX']
            maxX = options['maxX']
            minY = options['minY']
            maxY = options['maxY']
        }
        String format = options.get("format","image/png")
        String srs = options.get('srs', "EPSG:4326")
        boolean isTransprent = options.get("transparent", true)
        List layers = options.get("layers")
        

        mapRequest.setDimensions(w,h)
        mapRequest.SRS = srs
        mapRequest.format = format;
        mapRequest.setBBox(new org.geotools.data.ows.CRSEnvelope(srs, minX, minY, maxX, maxY))
        mapRequest.transparent = isTransprent
        layers.each{layer ->
            mapRequest.addLayer(layer,"")
        }
        def response = wms.issueRequest(mapRequest)
        ImageIO.read(response.inputStream)
    }

    BufferedImage getLegend() {

    }

    void getFeatureInfo() {

    }

    void describeLayer() {

    }

    void getStyles() {
        
    }

}

