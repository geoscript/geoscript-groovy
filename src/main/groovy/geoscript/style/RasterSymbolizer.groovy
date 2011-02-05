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
     * Create a RasterSymbolizer from a ColorMap of values
     * @param values A List of Maps with color, opatcity, quantity, and label keys
     * @return A RasterSymbolizer
     */
    public static RasterSymbolizer createColorMap(List<Map> values) {
        def colorMap = Style.styleFactory.createColorMap();
        values.each {value ->
            def entry = Style.styleFactory.createColorMapEntry()
            if (value.containsKey("color")) entry.color = Style.filterFactory.literal(value.color)
            if (value.containsKey("opacity")) entry.opacity = Style.filterFactory.literal(value.opacity)
            if (value.containsKey("quantity")) entry.quantity = Style.filterFactory.literal(value.quantity)
            if (value.containsKey("label")) entry.label = Style.filterFactory.literal(value.label)
            colorMap.addColorMapEntry(entry)
        }
        def sym = Style.styleFactory.defaultRasterSymbolizer
        sym.colorMap = colorMap
        new RasterSymbolizer(sym)
    }

}
