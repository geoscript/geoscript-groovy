package geoscript.wms

import org.geotools.data.ows.Layer
import geoscript.style.Style
import geoscript.geom.Bounds
import geoscript.proj.Projection

/**
 *
 * @author jericks
 */
class WMSLayer {

    Layer layer

    WMSLayer(Layer layer) {
        this.layer = layer
    }

    String getName() {
        layer.name
    }

    String getTitle() {
        layer.title
    }

    Set getSrs() {
        layer.srs
    }

    List<Style> getStyles() {
        layer.styles.collect{style ->
            Style.wrapGtStyle(style)
        }
    }

    boolean isQueryable() {
        layer.isQueryable()
    }

    double getScaleDenominatorMax() {
        layer.scaleDenominatorMax
    }

    double getScaleDenominatorMin() {
        layer.scaleDenominatorMin
    }

    WMSLayer getParent() {
        new WMSLayer(layer.parent)
    }

    List getChildren() {
        layer.layerChildren.collect{child ->
            new WMSLayer(child)
        }
    }

    List getBounds() {
        layer.layerBoundingBoxes.collect{bbox ->
            new Bounds(bbox.minX, bbox.minY, bbox.maxX, bbox.maxY, new Projection(bbox.coordinateReferenceSystem))
        }
    }

    Bounds getLatLongBounds() {
        def bbox = layer.latLongBoundingBox
        new Bounds(bbox.minX, bbox.minY, bbox.maxX, bbox.maxY, new Projection(bbox.coordinateReferenceSystem))
    }

    String toString() {
        getName()
    }

}

