package geoscript.style

import org.geotools.styling.Rule
import org.geotools.styling.RasterSymbolizer
import org.geotools.styling.Symbolizer as GtSymbolizer
import org.geotools.styling.SelectedChannelType

/**
 * The ChannelSelection Symbolizer is used to style Rasters.
 * @author Jared Erickson
 */
class ChannelSelection extends Raster {

    /**
     * The red channel name
     */
    String redName

    /**
     * The red ContrastEnhancement
     */
    ContrastEnhancement redContrastEnhancement

    /**
     * The green channel name
     */
    String greenName

    /**
     * The green ContrastEnhancement
     */
    ContrastEnhancement greenContrastEnhancement

    /**
     * The blue channel name
     */
    String blueName

    /**
     * The blue ContrastEnhancement
     */
    ContrastEnhancement blueContrastEnhancement

    /**
     * The gray channel name
     */
    String grayName

    /**
     * The gray ContrastEnhancement
     */
    ContrastEnhancement grayContrastEnhancement

    /**
     * Create a new ChannelSelection
     */
    ChannelSelection() {
    }

    /**
     * Create a new ChannelSelection with red, green, and blue channel names
     * @param redName The red channel name
     * @param greenName The green channel name
     * @param blueName The blue channel name
     */
    ChannelSelection(String redName, String greenName, String blueName) {
        this.redName = redName
        this.greenName = greenName
        this.blueName = blueName
    }

    /**
     * Create a new ChannelSelection with a gray channel name
     * @param grayName The gray channel name
     */
    ChannelSelection(String grayName) {
        this.grayName = grayName
    }

    /**
     * Create a new ChannelSelection from named parameters.
     * @param map A Map of named parameters.
     */
    ChannelSelection(Map map) {
        super()
        map.each{k,v->
            if(this.hasProperty(k as String)){
                this."$k" = v
            }
        }
    }

    /**
     * Set the red channel name and optionally the red ContrastEnhancement
     * @param name The red channel name
     * @param contrastEnhancement The red ContrastEnhancement
     * @return This ChannelSelection
     */
    ChannelSelection red(String name, ContrastEnhancement contrastEnhancement = null) {
        this.redName = name
        this.redContrastEnhancement = contrastEnhancement
        this
    }

    /**
     * Set the green channel name and optionally the green ContrastEnhancement
     * @param name The green channel name
     * @param contrastEnhancement The green ContrastEnhancement
     * @return This ChannelSelection
     */
    ChannelSelection green(String name, ContrastEnhancement contrastEnhancement = null) {
        this.greenName = name
        this.greenContrastEnhancement = contrastEnhancement
        this
    }

    /**
     * Set the blue channel name and optionally the blue ContrastEnhancement
     * @param name The blue channel name
     * @param contrastEnhancement The blue ContrastEnhancement
     * @return This ChannelSelection
     */
    ChannelSelection blue(String name, ContrastEnhancement contrastEnhancement = null) {
        this.blueName = name
        this.blueContrastEnhancement = contrastEnhancement
        this
    }

    /**
     * Set the gray channel name and optionally the gray ContrastEnhancement
     * @param name The gray channel name
     * @param contrastEnhancement The gray ContrastEnhancement
     * @return This ChannelSelection
     */
    ChannelSelection gray(String name, ContrastEnhancement contrastEnhancement = null) {
        this.grayName = name
        this.grayContrastEnhancement = contrastEnhancement
        this
    }

    /**
     * Prepare the GeoTools Rule by applying this Symbolizer
     * @param rule The GeoTools Rule
     */
    @Override
    protected void prepare(Rule rule) {
        super.prepare(rule)
        getGeoToolsSymbolizers(rule, RasterSymbolizer).each{s ->
            apply(s)
        }
    }

    /**
     * Apply this Symbolizer to the GeoTools Symbolizer
     * @param sym The GeoTools Symbolizer
     */
    @Override
    protected void apply(GtSymbolizer sym) {
        super.apply(sym)
        def types = []
        if (grayName) {
            types.add(styleFactory.createSelectedChannelType(grayName, grayContrastEnhancement ? grayContrastEnhancement.createContrastEnhancement() : styleFactory.createContrastEnhancement()))
        } else {
            types.add(styleFactory.createSelectedChannelType(redName, redContrastEnhancement ? redContrastEnhancement.createContrastEnhancement() : styleFactory.createContrastEnhancement()))
            types.add(styleFactory.createSelectedChannelType(greenName, greenContrastEnhancement ? greenContrastEnhancement.createContrastEnhancement() : styleFactory.createContrastEnhancement()))
            types.add(styleFactory.createSelectedChannelType(blueName, blueContrastEnhancement ? blueContrastEnhancement.createContrastEnhancement() : styleFactory.createContrastEnhancement()))
        }
        def channel = styleFactory.createChannelSelection(types as SelectedChannelType[])
        sym.channelSelection = channel
    }

    /**
     * The string representation
     * @return The string representation
     */
    String toString() {
        def values = [:]
        if (grayName) {
            values['grayName'] = grayName
            if (grayContrastEnhancement) values['grayContrastEnhancement'] = grayContrastEnhancement
        } else {
            values['redName'] = redName
            if (redContrastEnhancement) values['redContrastEnhancement'] = redContrastEnhancement
            values['greenName'] = greenName
            if (greenContrastEnhancement) values['greenContrastEnhancement'] = greenContrastEnhancement
            values['blueName'] = blueName
            if (blueContrastEnhancement) values['blueContrastEnhancement'] = blueContrastEnhancement
        }
        buildString("ChannelSelection", values)
    }

}
