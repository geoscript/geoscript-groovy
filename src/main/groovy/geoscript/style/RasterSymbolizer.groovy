package geoscript.style

import geoscript.raster.Raster
import org.geotools.styling.SLD

/**
 * A Symbolizer for Rasters.
 * http://docs.geotools.org/latest/javadocs/org/geotools/styling/RasterSymbolizer.html
 * http://docs.geotools.org/latest/javadocs/org/geotools/styling/SLD.html
 * http://docs.geotools.org/latest/javadocs/org/geotools/styling/ColorMapEntry.html
 * http://docs.geotools.org/stable/userguide/examples/imagelab.html
 * http://docs.geotools.org/latest/javadocs/org/geotools/styling/StyleFactory.html
 *      createColorMap()
 *      createColorMapEntry()
 *      shadedRelief(reliefFactory, brightnessOnly)
 */
class RasterSymbolizer extends Symbolizer {
    
    /**
     * Create a new RasterSymbolizer from a GeoTools RasterSymbolizer
     * @param symbolizer The GeoTools RasterSymbolizer
     */
    RasterSymbolizer(org.geotools.styling.RasterSymbolizer symbolizer) {
        super(symbolizer)
    }
	
	/**
	 * Create a default RasterSymbolizer
	 */
    RasterSymbolizer() {
        this(Style.builder.createRasterSymbolizer())
    }
   
	/**
	 * Set the opacity (0-1)
	 * @param opacity The opacity
	 */
    void setOpacity(double opacity) {
        symbolizer.opacity = Style.filterFactory.literal(opacity)
    }

	/**
	 * Get the opacity
	 * @return The opacity
	 */
    double getOpacity() {
        SLD.opacity(symbolizer)
    }

    /**
     * Get the overlap behavious
     * @return The overlap (LATEST_ON_TOP, EARLIEST_ON_TOP, AVERAGE or RANDOM)
     */
    String getOverlap() {
        symbolizer.overlap?.toString()
    }

    /**
     * Set the overlap behavior
     * @param overlap The overlap (LATEST_ON_TOP, EARLIEST_ON_TOP, AVERAGE or RANDOM)
     */
    void setOverlap(String overlap) {
        symbolizer.overlap = Style.filterFactory.literal(overlap.toUpperCase())
    }

    /**
     * Set the shaded relief
     * @param brightnessOnly Whether to apply to current layer (false) or other layers (true)
     * @param reliefFactor The amount of exaggeration (55 gives reasonable results for DEMs)
     */
    void setShadedRelief(boolean brightnessOnly = false, double reliefFactor = 1.0) {
        symbolizer.shadedRelief = Style.styleFactory.createShadedRelief(Style.filterFactory.literal(reliefFactor))
        symbolizer.shadedRelief.brightnessOnly = brightnessOnly
    }

    /**
     * Set the shaded relief brightness only flag
     * @param brightnessOnly The shaded relief brighness only flag
     */
    void setShadedReliefBrightnessOnly(boolean brightnessOnly) {
        if (symbolizer.shadedRelief == null) {
             symbolizer.shadedRelief = Style.styleFactory.createShadedRelief(Style.filterFactory.literal(1.0))
        }
        symbolizer.shadedRelief.brightnessOnly = brightnessOnly
    }

    /**
     * Set the shaded relief exaggeration factor
     * @param reliefFactor The shaded relief exaggeration factor
     */
    void setShadedReliefFactor(double reliefFactor) {
        if (symbolizer.shadedRelief == null) {
             symbolizer.shadedRelief = Style.styleFactory.createShadedRelief(Style.filterFactory.literal(reliefFactor))
        } else {
            symbolizer.shadedRelief.reliefFactor = Style.filterFactory.literal(reliefFactor)
        }
    }

    /**
     * Get the shaded relief brightness only flag (can be null if the symbolizer doesn't contain
     * shaded relief)
     * @return The shaded relief brightness only flag (null, true or false)
     */
    Boolean getShadedReliefBrightnessOnly() {
        symbolizer?.shadedRelief?.brightnessOnly
    }

    /**
     * Get the shaded relief exaggeration factor (can be null if the symbolizer doesn't contain shaded relief)
     * @return The shaded relief exaggeration factor (null or some number)
     */
    Double getShadedReliefFactor() {
        symbolizer?.shadedRelief?.reliefFactor?.value
    }
    
    /**
     * Set the contrast enhancement method (histogram or normalize)
     * @param method The contrast enhancement method (histogram or normalize)
     */
    void setContrastEnhancementMethod(String method) {
        def gammaValue = getContrastEnhancementGammaValue()
        if (gammaValue == null) gammaValue = 1.0
        def contrastMethod = org.opengis.style.ContrastMethod.NORMALIZE
        if (method.equalsIgnoreCase("histogram")) contrastMethod = org.opengis.style.ContrastMethod.HISTOGRAM
        symbolizer.contrastEnhancement = Style.styleFactory.contrastEnhancement(Style.filterFactory.literal(gammaValue), contrastMethod)
    }

    /**
     * Get the contrast enhancement method.
     * @return The contrast enhancement method (normalize, histogram, or null)
     */
    String getContrastEnhancementMethod() {
        def value = symbolizer?.contrastEnhancement?.method
        if (value != null) {
            if (value == org.opengis.style.ContrastMethod.NORMALIZE) {
                return "normalize"
            } else {
                return "histogram"
            }
        } else {
            return null
        }
    }

    /**
     * Set the constrast enhancement gamma value
     * @param The gamma value.  > 1 to brighten, 1 no change, < 1 to dim
     */
    void setContrastEnhancementGammaValue(double value) {
        if (symbolizer.contrastEnhancement == null) {
            symbolizer.contrastEnhancement = Style.styleFactory.contrastEnhancement(Style.filterFactory.literal(value), org.opengis.style.ContrastMethod.NORMALIZE) 
        } else {
            symbolizer.contrastEnhancement.gammaValue = Style.filterFactory.literal(value)
        }
    }

    /**
     * Get the contrast enhancement gamma value.
     * @return The gamma value which can be null
     */
    Double getContrastEnhancementGammaValue() {
        symbolizer?.contrastEnhancement?.gammaValue?.value
    }

    /**
     * Set the image outline
     * @param symbolizer The LineSymbolizer or PolygonSymbolizer
     */
    void setImageOutline(Symbolizer sym) {
        assert sym instanceof LineSymbolizer || sym instanceof PolygonSymbolizer
        symbolizer.imageOutline = sym.symbolizer
    }

    /**
     * Get the image outline.
     * @return A LineSymbolizer, a PolygonSymbolizer or null
     */
    Symbolizer getImageOutline() {
        def imageOutline = symbolizer.imageOutline
        if (imageOutline != null) {
            if (imageOutline instanceof org.geotools.styling.LineSymbolizer) {
                return new LineSymbolizer(imageOutline)
            } else if (imageOutline instanceof org.geotools.styling.PolygonSymbolizer) {
                return new PolygonSymbolizer(imageOutline)
            }
        } else {
            return null
        }
    }

	/**
	 * Create a gray scale RasterSymbolizer for the given band.
	 * @param band The index of the band to use
	 * @return A RasterSymbolizer
	 */
	public static RasterSymbolizer createGrayscale(int band) {
		def ce = Style.styleFactory.contrastEnhancement(Style.filterFactory.literal(1.0), org.opengis.style.ContrastMethod.NORMALIZE)
		def sct = Style.styleFactory.createSelectedChannelType(String.valueOf(band), ce)
		def sel = Style.styleFactory.channelSelection(sct)
		def sym = Style.styleFactory.defaultRasterSymbolizer
		sym.channelSelection = sel
		new RasterSymbolizer(sym)
	}

    /**
     * Create a RGB RasterSymbolizer for the given Raster
     * @param raster The Raster
     * @return A RasterSymbolizer
     */
	public static RasterSymbolizer createRGB(Raster raster) {
		def channelNum = [1,2,3]
		def names = raster.bands.collect{it.toString()}
		names.eachWithIndex{name, i ->
			if (name.matches("red.*")) {
				channelNum[0] = i + 1
			} else if (name.matches("green.*")) {
				channelNum[1] = i + 1
			} else if (name.matches("blue.*")) {
				channelNum[2] = i + 1
			}
		}
		def ce = Style.styleFactory.contrastEnhancement(Style.filterFactory.literal(1.0), org.opengis.style.ContrastMethod.NORMALIZE)
		def sct = (0..2).collect{i ->
			Style.styleFactory.createSelectedChannelType(String.valueOf(channelNum[i]), ce)
		}
		def sel = Style.styleFactory.channelSelection(sct[0], sct[1], sct[2])
        def sym = Style.styleFactory.defaultRasterSymbolizer
        sym.channelSelection = sel
        new RasterSymbolizer(sym)
	}

    /**
     * Set the RGB channel selection.
     * @param channels The RGB channels
     */
    void setChannelSelection(Map channels) {
        def channelTypeMap = [:]
        channels.each{channel->
            String name = channel.key
            Map value = channel.value
            String band = value.name
            def contrastEnhancement
            if (value.containsKey("contrastEnhancement")) {
                Map ce = value.contrastEnhancement
                def gammaValue = ce.containsKey("gammaValue") ? ce.gammaValue : 1.0
                def methodType = ce.containsKey("method") ? ce.method : org.opengis.style.ContrastMethod.NORMALIZE
                def method = methodType.equalsIgnoreCase("normalize") ? org.opengis.style.ContrastMethod.NORMALIZE : org.opengis.style.ContrastMethod.HISTOGRAM
                contrastEnhancement = Style.styleFactory.contrastEnhancement(Style.filterFactory.literal(gammaValue), method)
            } else {
                contrastEnhancement = Style.styleFactory.contrastEnhancement(Style.filterFactory.literal(1.0), org.opengis.style.ContrastMethod.NORMALIZE)
            }
            channelTypeMap[name] = Style.styleFactory.createSelectedChannelType(band, contrastEnhancement)
        }
        if (channelTypeMap.containsKey("gray")) {
            symbolizer.channelSelection = Style.styleFactory.channelSelection(channelTypeMap["gray"])
        } else {
            symbolizer.channelSelection = Style.styleFactory.channelSelection(channelTypeMap["red"], channelTypeMap["green"], channelTypeMap["blue"])
        }
    }

    /**
     * Get the channel selection as a Map
     * @return A Map representing the bands in the channel selection
     */
    Map getChannelSelection() {
        if (symbolizer.channelSelection != null && (symbolizer.channelSelection.grayChannel != null || symbolizer.channelSelection.getRGBChannels() != null && symbolizer.channelSelection.getRGBChannels()[0] != null)) {
            def contrastToMap = {contrast ->
                if (contrast != null) {
                    return [
                        method: (contrast.method == org.opengis.style.ContrastMethod.NORMALIZE) ? "normalize" : "histogram",
                        gammaValue: contrast.gammaValue.value
                    ]
                } else {
                    return [:]
                }
            }
            if (symbolizer.channelSelection.grayChannel != null) {
                return ["gray": ["name": symbolizer.channelSelection.grayChannel.channelName, "contrastEnhancement": contrastToMap(symbolizer.channelSelection.grayChannel.contrastEnhancement)]]
            } else {
                def channels = symbolizer.channelSelection.getRGBChannels()
                def channelMap = [
                    "red":   ["name": channels[0].channelName, "contrastEnhancement": contrastToMap(channels[0].contrastEnhancement)],
                    "green": ["name": channels[1].channelName, "contrastEnhancement": contrastToMap(channels[1].contrastEnhancement)],
                    "blue":  ["name": channels[2].channelName, "contrastEnhancement": contrastToMap(channels[2].contrastEnhancement)]
                ]
                return channelMap
            }
        } else {
            return null
        }
    }

    /**
     * Create a RasterSymbolizer from a ColorMap of values.
     * <p><code>def sym = RasterSymbolizer.createColorMap([</code></p>
     * <p><code>   [color: "#ffffff", opacity: 0.54, quantity: 100, label: "#1"],</code></p>
     * <p><code>   [color: "#ffffff", opacity: 0.54, quantity: 100],</code></p>
     * <p><code>   [color: "#ffffff", quantity: 100]</code></p>
     * <p><code>])</code></p>
     * @param values A List of Maps with color, opatcity, quantity, and label keys
     * @return A RasterSymbolizer
     */
    public static RasterSymbolizer createColorMap(List<Map> values) {
        def sym = Style.styleFactory.defaultRasterSymbolizer
        sym.colorMap = generateColorMap(values)
        new RasterSymbolizer(sym)
    }

    /**
     * Generate a GeoTool's ColorMap from A List of Maps with color, opacity, quantity, and label keys
     * @param values A List of Maps
     * @return A GeoTool's ColorMap
     */
    private static org.geotools.styling.ColorMap generateColorMap(List<Map> values) {
        def colorMap = Style.styleFactory.createColorMap();
        values.each {value ->
            def entry = Style.styleFactory.createColorMapEntry()
            if (value.containsKey("color")) entry.color = Style.filterFactory.literal(value.color)
            entry.opacity = Style.filterFactory.literal(value.containsKey("opacity") ? value.opacity : 1.0)
            if (value.containsKey("quantity")) entry.quantity = Style.filterFactory.literal(value.quantity)
            if (value.containsKey("label")) entry.label = Style.filterFactory.literal(value.label)
            colorMap.addColorMapEntry(entry)
        }
        colorMap
    }

    /**
     * Get the ColorMap as a List of Maps with color, opacity, quantity, and label keys.
     * @return A List of Maps or null
     */
    List<Map> getColorMap() {
        if (symbolizer.colorMap) {
            return symbolizer.colorMap.colorMapEntries.collect {e ->
                [color: e.color?.value, opacity: e.opacity?.value, quantity: e.quantity?.value, label: e.label?.value?.toString()]
            }
        } else {
            return null
        }
    }

    /**
     * Set the ColorMap using a List of Maps with color, opacity, quantity, label keys.
     * <p><code>def sym = new RasterSymbolizer()</code></p>
     * <p><code>sym.colorMap = [</code></p>
     * <p><code>   [quantity: 100, color: "#ff0000"],</code></p>
     * <p><code>   [quantity: 200, color: "#00ff00", label: "Level 200"],</code></p>
     * <p><code>   [quantity: 300, color: "#0000ff", label: "Level 300", opacity: 0.25]</code></p>
     * <p><code>]</code></p>
     * @param values A List of Maps
     */
    void setColorMap(List<Map> values) {
        symbolizer.colorMap = generateColorMap(values)
    }

    /**
     * Add to the ColorMap.
     * <p><code>def sym = new RasterSymbolizer()</code></p>
     * <p><code>sym.addToColorMap(100, "#ff0000")</code></p>
     * <p><code>sym.addToColorMap(200, "#00ff00","Level 200")</code></p>
     * <p><code>sym.addToColorMap(300, "#0000ff", "Level 300", 0.25)</code></p>
     * @param quantity The quantity
     * @param color The color
     * @param label The label
     * @param opacity The opacity (0-1)
     */
    void addToColorMap(def quantity, String color, String label = null, double opacity = 1.0) {
        if (symbolizer.colorMap == null) {
            symbolizer.colorMap = Style.styleFactory.createColorMap()
        }
        def entry = Style.styleFactory.createColorMapEntry()
        if (color) entry.color = Style.filterFactory.literal(color)
        if (opacity) entry.opacity = Style.filterFactory.literal(opacity)
        if (quantity) entry.quantity = Style.filterFactory.literal(quantity)
        if (label) entry.label = Style.filterFactory.literal(label)
        symbolizer.colorMap.addColorMapEntry(entry)
    }
}
