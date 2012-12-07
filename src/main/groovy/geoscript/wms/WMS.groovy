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
        this(new URL(url))
    }

    /**
     * Create a new WMS object with a get capabilities URL
     * @param url The get capabilities URL
     */
    WMS(URL url) {
        wms = new WebMapServer(url)
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
                layers.add(new WMSLayer(wms, layer))
            }
        }
        layers
    }

    /**
     * Get a WMSLayer by name
     * @param The name
     * @return A WMSLayer or null
     */
    WMSLayer getLayer(String name) {
        WMSLayer wmsLayer = null
        capabilities.layerList.each{layer ->
            if (name.equalsIgnoreCase(layer.name)) {
                wmsLayer = new WMSLayer(wms, layer)
            }
        }
        return wmsLayer
    }

    /**
     * Get a Image from a WMS service.
     * @param A Map of options (width, height, bbox or minX/minY/maxX/maxY, format,
     * srs, transparent, layers).
     * @return A BufferedImage
     */
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
        } else if (bbox instanceof Bounds) {
            minX = bbox.minX
            minY = bbox.minY
            maxX = bbox.maxX
            maxY = bbox.maxY
        } else {
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
            mapRequest.addLayer(layer.toString(),"")
        }
        println mapRequest.finalURL
        def response = wms.issueRequest(mapRequest)
        ImageIO.read(response.inputStream)
    }

    /**
     * Get a legend from a WMS service.
     * @param options A Map of options (width, height, format, exceptions,
     * featureType, layer, rule, scale, sld, sldBody, style)
     * @return A BufferedImage
     */
    BufferedImage getLegend(Map options) {
        def request = wms.createGetLegendGraphicRequest()
        // width
        request.width = options.get("width", 32)
        // height
        request.height = options.get("height", 32)
        // format
        request.format = options.get("format","image/png")
        // exceptions
        if (options.containsKey("exceptions")) {
            request.exceptions = options.get("exceptions")
        }
        // featureType
        if (options.containsKey("featureType")) {
            request.featureType = options.get("featureType")
        }
        // layer
        if (options.containsKey("layer")) {
            request.layer = options.get("layer").toString()
        }
        // rule
        if (options.containsKey("rule")) {
            request.rule = options.get("rule")
        }
        // scale
        if (options.containsKey("scale")) {
            request.scale = options.get("scale")
        }
        // sld
        if (options.containsKey("sld")) {
            request.setSLD(options.get("sld"))
        }
        // sldBody
        if (options.containsKey("sldBody")) {
            request.setSLDBody(options.get("sldBody"))
        }
        // style
        if (options.containsKey("style")) {
            request.style = options.get("style")
        }
        //println("Get Legend: ${request.finalURL}")
        def response = wms.issueRequest(request)
        ImageIO.read(response.inputStream)
    }

    /**
     * Get the name (WMT_MS_Capabilities/Service/Name)
     * @return The name
     */
    String getName() {
        capabilities?.service?.name
    }

    /**
     * Get the title (WMT_MS_Capabilities/Service/Title)
     * @return The title
     */
    String getTitle() {
        capabilities?.service?.title
    }

    /**
     * Get the abstract (WMT_MS_Capabilities/Service/Abstract)
     * @return The abstract
     */
    String getAbstract() {
        capabilities?.service?.get_abstract()
    }

    /**
     * Get a List of keywords
     * @return A List of keywords
     */
    List getKeywords() {
        capabilities?.service?.keywordList.findAll{it != null && it.trim().length() > 0}
    }

    /**
     * Get the online resource URL
     * @return The online resource URL
     */
    URL getOnlineResource() {
        capabilities?.service?.onlineResource
    }

    /**
     * Get the update sequence
     * @return The update sequence
     */
    String getUpdateSequence() {
        capabilities?.updateSequence
    }

    /**
     * Get the version
     * @return The version
     */
    String getVersion() {
        capabilities?.version
    }

    /**
     * Get the GetMap formats
     * @return The GetMap formats
     */
    List getGetMapFormats() {
        capabilities?.request?.getMap?.formats
    }
}