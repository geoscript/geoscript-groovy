package geoscript.carto

import geoscript.render.Map
import org.geotools.renderer.lite.RendererUtilities

import java.awt.Color
import java.awt.Font

/**
 * Adds scale bar to a cartographic document.
 * @author Jared Erickson
 */
class ScaleBarItem extends Item {

    Map map

    Color strokeColor = Color.BLACK

    Color fillColor = Color.WHITE

    float strokeWidth = 1

    float barStrokeWidth = 1

    Color barStrokeColor = Color.BLACK

    Font font = new Font("Arial", Font.PLAIN, 12)

    Color textColor = Color.BLACK

    int border = 5

    Units units = Units.METRIC

    enum Units {
        METRIC,
        US
    }

    /**
     * Create a scale bar from the top left with the given width and height.
     * @param x The number of pixels from the left
     * @param y The number of pixels from the top
     * @param width The width in pixels
     * @param height The height in pixels
     */
    ScaleBarItem(int x, int y, int width, int height) {
        super(x, y, width, height)
    }

    /**
     * Set the Map to use when calculating the map scale
     * @param map The Map
     * @return The ScaleBarItem
     */
    ScaleBarItem map(Map map) {
        this.map = map
        this
    }

    /**
     * Set the Units (US or METRIC) to use
     * @param units Units (US or METRIC)
     * @return The ScaleBarItem
     */
    ScaleBarItem units(Units units) {
        this.units = units
        this
    }

    /**
     * Set stroke Color
     * @param strokeColor The stroke Color
     * @return The ScaleBarItem
     */
    ScaleBarItem strokeColor(Color strokeColor) {
        this.strokeColor = strokeColor
        this
    }

    /**
     * Set the fill Color
     * @param fillColor The fill Color
     * @return The ScaleBarItem
     */
    ScaleBarItem fillColor(Color fillColor) {
        this.fillColor = fillColor
        this
    }

    /**
     * Set the stroke width
     * @param strokeWidth The stroke width
     * @return The ScaleBarItem
     */
    ScaleBarItem strokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth
        this
    }

    /**
     * Set the bar stroke Color
     * @param strokeColor The bar stroke Color
     * @return The ScaleBarItem
     */
    ScaleBarItem barStrokeColor(Color strokeColor) {
        this.barStrokeColor = strokeColor
        this
    }

    /**
     * Set the bar stroke width
     * @param strokeWidth The bar stroke width
     * @return The ScaleBarItem
     */
    ScaleBarItem barStrokeWidth(float strokeWidth) {
        this.barStrokeWidth = strokeWidth
        this
    }

    /**
     * Set the text Color
     * @param color The text Color
     * @return The ScaleBarItem
     */
    ScaleBarItem textColor(Color color) {
        this.textColor = color
        this
    }

    /**
     * Set the Font
     * @param font The Font
     * @return The ScaleBarItem
     */
    ScaleBarItem font(Font font) {
        this.font = font
        this
    }

    /**
     * Set the border padding
     * @param border The border padding
     * @return The ScaleBarItem
     */
    ScaleBarItem border(int border) {
        this.border = border
        this
    }

    /**
     * Calculate the scale bar information
     * @return A ScaleBarInfo instance
     */
    ScaleBarInfo calculateScaleBarInfo() {
        
        double widthInMeters = RendererUtilities.toMeters(map.bounds.width, map.bounds.proj.crs)
        double widthInUnits = widthInMeters
        if (units == ScaleBarItem.Units.US) {
            widthInUnits = org.geotools.measure.Units.METRE.getConverterTo(org.geotools.measure.Units.FOOT).convert(widthInMeters)
        }

        double scaleDenominator = map.scaleDenominator
        double pixelsPerMeter = RendererUtilities.calculatePixelsPerMeterRatio(scaleDenominator, [:])
        String unitForScaleText
        double pixelsPerUnit
        if (units == ScaleBarItem.Units.US) {
            if (widthInUnits > 5280) {
                unitForScaleText = "miles"
                pixelsPerUnit = pixelsPerMeter / 0.000621371
            } else {
                unitForScaleText = "feet"
                pixelsPerUnit = pixelsPerMeter / 3.28084
            }
        } else {
            if (widthInUnits > 1000) {
                unitForScaleText = "km"
                pixelsPerUnit = pixelsPerMeter * 1000
            } else {
                unitForScaleText = "m"
                pixelsPerUnit = pixelsPerMeter
            }
        }

        int scaleBarWidthInPixels = width - border * 2
        double scaleBarWidthInUnits = scaleBarWidthInPixels / pixelsPerUnit

        double orderOfMagnitude = Math.pow(10, Math.floor(Math.log10(scaleBarWidthInUnits)))
        double desiredScaleBarWidthInUnits = Math.round(scaleBarWidthInUnits / orderOfMagnitude) * orderOfMagnitude
        double desiredScaleBarWidthInPixels = Math.round(desiredScaleBarWidthInUnits * pixelsPerUnit)

        new ScaleBarInfo(
            widthInPixels: desiredScaleBarWidthInPixels,
            widthInUnits: desiredScaleBarWidthInUnits,
            unitForScaleText: unitForScaleText
        )
    }

    static class ScaleBarInfo {

        double widthInPixels

        double widthInUnits

        String unitForScaleText

    }

}
