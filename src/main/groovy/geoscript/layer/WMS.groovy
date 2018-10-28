package geoscript.layer

import geoscript.geom.Bounds
import geoscript.proj.Projection
import org.geotools.data.ows.HTTPClient
import org.geotools.ows.wms.Layer as GtLayer
import org.geotools.data.ows.SimpleHttpClient
import org.geotools.ows.wms.StyleImpl
import org.geotools.ows.wms.WMSCapabilities
import org.geotools.ows.wms.WebMapServer
import javax.imageio.ImageIO
import java.awt.image.BufferedImage

/**
 * The Web Map Server (WMS) module is a connection to a remote WMS server.
 * <p><blockquote><pre>
 * def wms = new WMS("http://localhost:8080/geoserver/ows?service=wms&version=1.1.1&request=GetCapabilities")
 * </pre></blockquote></p>
 * You can use it to get information on the WMS Server just as name, version, formats, layers:
 * <p><blockquote><pre>
 * println "Name: ${wms.name}"
 * println "Version: ${wms.version}"
 * println "Formats: ${wms.getMapFormats.join(',')}"
 * wms.layers.each{layer -> println "${layer.name}" }
 * </pre></blockquote></p>
 * Or you can use it to render images or extract Rasters:
 * <p><blockquote><pre>
 * def image = wms.getImage("world:borders")
 * ImageIO.write(image,"png",new File("world.png"))
 * def raster = wms.getRaster(["world:cities","world:borders"])
 * ImageIO.write(raster.image,"png",new File("raster_world_cities.png"))
 * </pre></blockquote></p>
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
     * The URL
     */
    private final URL url

    /**
     * The cached List of WMS.Layers
     */
    private List<WMS.Layer> cachedLayers

    /**
     * Create a new WMS object with a get capabilities URL
     * @param options Optional named parameters:
     * <ul>
     *     <li> user = The user name for password protected urls</li>
     *     <li> password = The password for password protected urls</li>
     * </ul>
     * @param url The get capabilities URL
     */
    WMS(Map options = [:], String url) {
        this(options, new URL(url))
    }

    /**
     * Create a new WMS object with a get capabilities URL
     * @param options Optional named parameters:
     * <ul>
     *     <li> user = The user name for password protected urls</li>
     *     <li> password = The password for password protected urls</li>
     * </ul>
     * @param url The get capabilities URL
     */
    WMS(Map options = [:], URL url) {
        this.url = url
        if (options.containsKey("user") && options.containsKey("password")) {
            HTTPClient client = new SimpleHttpClient()
            client.user = options.user
            client.password = options.password
            wms = new WebMapServer(url, client)
        } else {
            wms = new WebMapServer(url)
        }
        capabilities = wms.capabilities
    }

    /**
     * The String representation
     * @return
     */
    String toString() {
        url.toString()
    }

    /**
     * Get a Raster from the WMS server
     * @param options Named parameter options may include:
     * <ul>
     *      <li>width: The width of the image (defaults to 512)</li>
     *      <li>height: The height of the image (defaults to 512)</li>
     *      <li>format: The image format (defaults to image/png)</li>
     *      <li>srs: The srs or projection id (defaults to EPSG:4326)</li>
     *      <li>transparent: Whether the image should be transparent or not (defaults to true)</li>
     *      <li>bounds: The Bounds of the image (defaults to the extent of all Layers)</li>
     * </ul>
     * @param layers The Layers to include on the image.  Layers can be a single WMS.Layer, layer name, or
     * map with name and style keys or it can be a List of the previously mentioned values.
     * @return A Raster
     */
    Raster getRaster(Map options = [:], def layers) {
        if (!(layers instanceof List)) {
            layers = [layers]
        }
        List wmsLayers = layers.collect{layer ->
            layer instanceof Layer ? layer : this.getLayer(layer instanceof String ? layer : layer.name)
        }
        Bounds bounds = options.get("bounds",null)
        if (!bounds) {
            wmsLayers.each {layer ->
                if (!bounds) {
                    bounds = layer.latLonBounds
                } else {
                    bounds.expand(layer.latLonBounds)
                }
            }
        }
        options["bounds"] = bounds
        /*WMSCoverageReader reader = new WMSCoverageReader(wms, wmsLayers[0].layer)
        (1..wmsLayers.size()).each{i ->
            reader.addLayer(wmsLayers[i].layer)
        }
        int w = options.get('width', 512)
        int h = options.get('height', 512)
        def c = options.get("backgroundColor",null)
        new Raster(reader.getMap(bounds.env, w, h, c ? new Color(c).asColor() : null))*/
        def image = getImage(options, layers)
        new Raster(image, bounds)

    }

    /**
     * Get a Image from the WMS server.
     * @param options Named parameter options may include:
     * <ul>
     *      <li>width: The width of the image (defaults to 512)</li>
     *      <li>height: The height of the image (defaults to 512)</li>
     *      <li>format: The image format (defaults to image/png)</li>
     *      <li>srs: The srs or projection id (defaults to EPSG:4326)</li>
     *      <li>transparent: Whether the image should be transparent or not (defaults to true)</li>
     *      <li>bounds: The Bounds of the image (defaults to the extent of all Layers)</li>
     *      <li>debug: Show the WMS request url</li>
     * </ul>
     * @param layers The Layers to include on the image.  Layers can be a single WMS.Layer, layer name, or
     * map with name and style keys or it can be a List of the previously mentioned values.
     * @return A BufferedImage
     */
    BufferedImage getImage(Map options = [:], def layers) {
        int w = options.get('width', 512)
        int h = options.get('height', 512)
        String format = options.get("format","image/png")
        String srs = options.get('srs', "EPSG:4326")
        boolean isTransparent = options.get("transparent", true)
        if (!(layers instanceof List)) {
            layers = [layers]
        }

        Bounds bounds = options.get("bounds",null)
        if (!bounds) {
            layers.each {layer ->
                Layer l = layer instanceof Layer ? layer : this.getLayer(layer instanceof String ? layer : layer.name)
                if (!bounds) {
                    bounds = l.getBounds(srs)
                } else {
                    bounds.expand(l.getBounds(srs))
                }
            }
        }

        def mapRequest = wms.createGetMapRequest()
        mapRequest.setDimensions(w,h)
        mapRequest.SRS = srs
        mapRequest.format = format;
        mapRequest.setBBox(new org.geotools.ows.wms.CRSEnvelope(srs, bounds.minX, bounds.minY, bounds.maxX, bounds.maxY))
        mapRequest.transparent = isTransparent
        layers.each{layer ->
            String name
            String style = ""
            if (layer instanceof Map) {
                name = layer.name
                style = layer.get("style","")
            } else {
                name = layer.toString()
            }
            mapRequest.addLayer(name, style)
        }
        if (options.get("debug", false)) {
            println mapRequest.finalURL
        }
        def response = wms.issueRequest(mapRequest)
        ImageIO.read(response.inputStream)
    }

    /**
     * Get a legend from a WMS service.
     * @param options Named parameter options
     * <ul>
     *      <li>width: The width to the image (defaults to 32)</li>
     *      <li>height: The height to the image (defaults to 32)</li>
     *      <li>format: The image format (defaults to image/png)</li>
     *      <li>exceptions: The exceptions WMS GET_LEGEND request value</li>
     *      <li>featureType: The featureType WMS GET_LEGEND request value</li>
     *      <li>rule: The rule WMS GET_LEGEND request value</li>
     *      <li>scale: The scale WMS GET_LEGEND request value</li>
     *      <li>sld: The sld WMS GET_LEGEND request value</li>
     *      <li>sldBody: The sldBody WMS GET_LEGEND request value</li>
     *      <li>style: The style WMS GET_LEGEND request value</li>
     *      <li>debug: Show the WMS request url</li>
     * </ul>
     * @return A BufferedImage
     */
    BufferedImage getLegend(Map options = [:], def layer) {
        def request = wms.createGetLegendGraphicRequest()
        // layer
        request.layer = layer.toString()
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
        if (options.get("debug", false)) {
            println request.finalURL
        }
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

    /**
     * Get a List of WMS.Layers
     * @return A List of WMS.Layers
     */
    List getLayers() {
        if (!this.cachedLayers) {
            this.cachedLayers = []
            capabilities.layerList.each{layer ->
                if (layer.name != null) {
                    this.cachedLayers.add(new Layer(layer))
                }
            }
        }
        this.cachedLayers
    }

    /**
     * Get a WMS.Layer by name
     * @param The name
     * @return A WMS.Layer or null
     */
    Layer getLayer(String name) {
        getLayers().find{layer ->
            if (name.equalsIgnoreCase(layer.name)) {
                return layer
            }
        }
    }

    /**
     * A WMS.Layer
     * @author Jared Erickson
     */
    protected static class Layer {

        /**
         * The wrapped GeoTools WMS Layer
         */
        GtLayer layer

        /**
         * Create a WMS.Layer from a GeoTools WMS Layer
         * @param The GeoTools WebMapServer
         * @param layer The Layer
         */
        Layer(GtLayer layer) {
            this.layer = layer
        }

        /**
         * Get the name of the layer
         * @return The name of the layer
         */
        String getName() {
            layer.name
        }

        /**
         * Get the title of the layer
         * @return The title of the layer
         */
        String getTitle() {
            layer.title
        }

        /**
         * Get the SRSes.
         * @return The SRSes
         */
        List getSrses() {
            def srses = []
            srses.addAll(layer.srs)
            srses
        }

        /**
         * Get the list of Styles
         * @return A list of Styles
         */
        List<Style> getStyles() {
            layer.styles.collect{style ->
                new Style(style)
            }
        }

        /**
         * Is the layer queryable.
         * @return Whether the layer is queryable
         */
        boolean isQueryable() {
            layer.isQueryable()
        }

        /**
         * Get the maximum scale denominator
         * @return The maximum scale denominator
         */
        double getMaxScale() {
            layer.scaleDenominatorMax
        }

        /**
         * Get the minimum scale denominator
         * @return The minimum scale denominator
         */
        double getMinScale() {
            layer.scaleDenominatorMin
        }

        /**
         * Get the parent WMS.Layer
         * @return The parent WMS.Layer
         */
        Layer getParent() {
            layer.parent != null ? new Layer(layer.parent) : null
        }

        /**
         * Get a List of child WMS.Layers
         * @return The list of child WMS.Layers
         */
        List<Layer> getChildren() {
            layer.layerChildren.collect{child ->
                new Layer(child)
            }
        }

        /**
         * Get the List of Bounds
         * @return The List of Bounds
         */
        List getBounds() {
            layer.layerBoundingBoxes.collect{bbox ->
                new Bounds(bbox.minX, bbox.minY, bbox.maxX, bbox.maxY, new Projection(bbox.coordinateReferenceSystem))
            }
        }

        /**
         * Get a Bounds in the given Projection
         * @param proj The Projection
         * @return The Bounds
         */
        Bounds getBounds(def proj) {
            def b
            def env = layer.getBoundingBoxes().get(proj instanceof Projection ? proj.id : proj)
            if (env) {
                b = new Bounds(env.minX, env.minY, env.maxX, env.maxY, proj)
            }
            if (!b) {
                b = getLatLonBounds().reproject(proj)
            }
            b
        }

        /**
         * Get the Bounds in latitude/longitude
         * @return The Bounds in latitude/longitude
         */
        Bounds getLatLonBounds() {
            def bbox = layer.getLatLonBoundingBox()
            if (bbox != null) {
                def crs = new Projection("EPSG:4326")
                try {
                    crs = new Projection(bbox.coordinateReferenceSystem)
                } catch (NullPointerException e) {}
                new Bounds(bbox.minX, bbox.minY, bbox.maxX, bbox.maxY, crs)
            } else {
                null
            }
        }

        /**
         * The string representation
         * @return The string representation
         */
        String toString() {
            getName()
        }
    }

    /**
     * A WMS Style.
     * @author Jared Erickson
     */
    private static class Style {

        /**
         * The wrapped GeoTools WMS Style
         */
        StyleImpl style

        /**
         * Create a Style from a GeoTools StyleImpl
         * @param style The GeoTools StyleImpl
         */
        Style(StyleImpl style) {
            this.style = style
        }

        /**
         * Get the name
         * @return The name
         */
        String getName() {
            style.name
        }

        /**
         * Get the title
         * @return The title
         */
        String getTitle() {
            style.title
        }

        /**
         * Get the abstract
         * @return The abstract
         */
        String getAbstract() {
            style.abstract.toString()
        }

        /**
         * The string representation
         * @return The string representation
         */
        String toString() {
            name
        }
    }
}