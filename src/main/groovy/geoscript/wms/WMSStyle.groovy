package geoscript.wms

import org.geotools.data.ows.StyleImpl

/**
 * A WMSStyle.
 * @author Jared Erickson
 */
class WMSStyle {

    /**
     * The wrapped GeoTools WMS Style
     */
    StyleImpl style

    /**
     * Create a WMSStyle from a GeoTools StyleImpl
     * @param style The GeoTools StyleImpl
     */
    WMSStyle(StyleImpl style) {
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
