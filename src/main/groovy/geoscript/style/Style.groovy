package geoscript.style

import org.geotools.styling.Style as GtStyle

/**
 * The Style interface
 */
interface Style {

    /**
     * Get a GeoTools Style
     * @return A GeoTools Style
     */
    GtStyle getGtStyle()

}