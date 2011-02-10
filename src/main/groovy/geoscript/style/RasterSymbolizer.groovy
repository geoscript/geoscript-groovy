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
