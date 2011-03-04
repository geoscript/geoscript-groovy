package geoscript.wms

import org.geotools.data.ows.Layer
import org.geotools.data.wms.WebMapServer
import geoscript.geom.Bounds
import geoscript.proj.Projection

/**
 * A WMSLayer
 * @author Jared Erickson
 */
class WMSLayer {

    /**
     * The wrapped GeoTools WMS Layer
     */
    Layer layer

    /**
     * The GeoTools WebMapServer
     */
    WebMapServer wms

    /**
     * Create a WMSLayer from a GeoTools WMS Layer
     * @param The GeoTools WebMapServer
     * @param layer The Layer
     */
    WMSLayer(WebMapServer wms, Layer layer) {
        this.wms = wms
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
    List getSrs() {
        def srses = []
        srses.addAll(layer.srs)
        srses
    }

    /**
     * Get the list of Styles
     * @return A list of Styles
     */
    List<WMSStyle> getStyles() {
        layer.styles.collect{style ->
            new WMSStyle(style)
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
    double getScaleDenominatorMax() {
        layer.scaleDenominatorMax
    }

    /**
     * Get the minimum scale denominator
     * @return The minimum scale denominator
     */
    double getScaleDenominatorMin() {
        layer.scaleDenominatorMin
    }

    /**
     * Get the parent WMSLayer
     * @return The parent WMSLayer
     */
    WMSLayer getParent() {
        new WMSLayer(wms, layer.parent)
    }

    /**
     * Get a List of child WMSLayers
     * @return The list of child WMSLayers
     */
    List getChildren() {
        layer.layerChildren.collect{child ->
            new WMSLayer(wms, child)
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