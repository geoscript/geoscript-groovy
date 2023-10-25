package geoscript.carto

import geoscript.layer.Layer
import geoscript.layer.Raster
import geoscript.layer.Renderable
import geoscript.style.ColorMap
import geoscript.style.Style
import org.geotools.filter.text.cql2.CQL
import org.geotools.api.style.Rule
import org.geotools.api.style.StyleFactory
import org.geotools.styling.StyleFactoryImpl
import org.geotools.api.filter.Filter

import java.awt.Color
import java.awt.Font
import java.awt.Image

/**
 * Add a Legend to a cartographic document.
 * @author Jared Erickson
 */
class LegendItem extends Item {

    Color backgroundColor = Color.WHITE

    String title = "Legend"

    Font titleFont = new Font("Arial", Font.BOLD, 18)

    Color titleColor = Color.BLACK

    Font textFont = new Font("Arial", Font.PLAIN, 12)

    Color textColor = Color.BLACK

    List<LegendEntry> entries = []

    int legendEntryWidth = 50

    int legendEntryHeight = 30

    int gapBetweenEntries = 10

    String numberFormat = "#.##"

    /**
     * Create a Legend from the top left with the given width and height.
     * @param x The number of pixels from the left
     * @param y The number of pixels from the top
     * @param width The width in pixels
     * @param height The height in pixels
     */
    LegendItem(int x, int y, int width, int height) {
        super(x, y, width, height)
    }

    /**
     * Set the background color
     * @param backgroundColor The background color
     * @return The LegendItem
     */
    LegendItem backgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor
        this
    }

    /**
     * Set the legend title
     * @param title The title
     * @return The LegendItem
     */
    LegendItem title(String title) {
        this.title = title
        this
    }

    /**
     * Set the title Font
     * @param titleFont The title Font
     * @return The LegendItem
     */
    LegendItem titleFont(Font titleFont) {
        this.titleFont = titleFont
        this
    }

    /**
     * Set the title Color
     * @param titleColor The title Color
     * @return The LegendItem
     */
    LegendItem titleColor(Color titleColor) {
        this.titleColor = titleColor
        this
    }

    /**
     * Set the text Font
     * @param textFont The text Font
     * @return The LegendItem
     */
    LegendItem textFont(Font textFont) {
        this.textFont = textFont
        this
    }

    /**
     * Set the text Color
     * @param textColor The text Color
     * @return The LegendItem
     */
    LegendItem textColor(Color textColor) {
        this.textColor = textColor
        this
    }

    /**
     * Set the legend entry width
     * @param legendEntryWidth The legend entry width
     * @return The LegendItem
     */
    LegendItem legendEntryWidth(int legendEntryWidth) {
        this.legendEntryWidth = legendEntryWidth
        this
    }

    /**
     * Set the legen entry height
     * @param legendEntryHeight The legend entry height
     * @return The LegendItem
     */
    LegendItem legendEntryHeight(int legendEntryHeight) {
        this.legendEntryHeight = legendEntryHeight
        this
    }

    /**
     * Set the gap between entries
     * @param gapBetweenEntries The gap between entries
     * @return The LegendItem
     */
    LegendItem gapBetweenEntries(int gapBetweenEntries) {
        this.gapBetweenEntries = gapBetweenEntries
        this
    }

    /**
     * Set the number format
     * @param numberFormat The number format
     * @return The LegendItem
     */
    LegendItem numberFormat(String numberFormat) {
        this.numberFormat = numberFormat
        this
    }

    /**
     * Add a Point legend entry
     * @param title The title
     * @param symbolizer The Point's Symbolizer
     * @return The LegendItem
     */
    LegendItem addPointEntry(String title, Style symbolizer) {
        this.entries.add(new LegendEntry(title: title, type: LegendEntryType.POINT, symbolizer: symbolizer))
        this
    }

    /**
     * Add a Line legend entry
     * @param title The title
     * @param symbolizer The Line's Symbolizer
     * @return The LegendItem
     */
    LegendItem addLineEntry(String title, Style symbolizer) {
        this.entries.add(new LegendEntry(title: title, type: LegendEntryType.LINE, symbolizer: symbolizer))
        this
    }

    /**
     * Add a Polygon legend entry
     * @param title The title
     * @param symbolizer The Polygon's Symbolizer
     * @return The LegendItem
     */
    LegendItem addPolygonEntry(String title, Style symbolizer) {
        this.entries.add(new LegendEntry(title: title, type: LegendEntryType.POLYGON, symbolizer: symbolizer))
        this
    }

    /**
     * Add a Group Entry
     * @param title The title
     * @return The LegendItem
     */
    LegendItem addGroupEntry(String title) {
        this.entries.add(new LegendEntry(title: title, type: LegendEntryType.GROUP))
        this
    }

    /**
     * Add a ColorMap legend entry
     * @param title The title
     * @param symbolizer The Raster's Symbolizer
     * @return The LegendItem
     */
    LegendItem addColorMapEntry(String title, ColorMap colorMap) {
        this.entries.add(new LegendEntry(title: title, type:LegendEntryType.COLORMAP, symbolizer: colorMap))
        this
    }

    /**
     * Add an Image Entry
     * @param title The title
     * @return The LegendItem
     */
    LegendItem addImageEntry(String title, Image image) {
        this.entries.add(new LegendEntry(title: title, type: LegendEntryType.IMAGE, image: image))
        this
    }

    /**
     * Add Legend entries for a Layer
     * @param layer A Layer
     * @return The LegendItem
     */
    LegendItem addLayer(Layer layer) {
        String geometryType = layer.schema.geom.typ.toLowerCase()
        LegendEntryType legendEntryType
        if (geometryType.contains("point")) {
            legendEntryType = LegendEntryType.POINT
        } else if (geometryType.contains("linestring")) {
            legendEntryType = LegendEntryType.LINE
        } else /*if (geometryType.contains("polygon") )*/ {
            legendEntryType = LegendEntryType.POLYGON
        }
        int numberOfRules = countRules(layer.style.gtStyle)
        if (numberOfRules > 1) {
            addGroupEntry(layer.name.capitalize())
        }
        layer.style.gtStyle.featureTypeStyles().each { def fts ->
            fts.rules().each { def rule ->
                String ruleName = rule.name
                Filter filter = rule.filter
                String title = ruleName ?: numberOfRules > 1 ? CQL.toCQL(filter) : layer.name.capitalize()
                Style style = SLDStyle.fromRule(rule)
                if (legendEntryType == LegendEntryType.POINT) {
                    addPointEntry(title, style)
                } else if (legendEntryType == LegendEntryType.LINE) {
                    addLineEntry(title, style)
                }  else if (legendEntryType == LegendEntryType.POLYGON) {
                    addPolygonEntry(title, style)
                }
            }
        }
        this
    }

    /**
     * Add Legend entries for a Raster
     * @param raster The Raster
     * @return The LegendItem
     */
    LegendItem addRaster(Raster raster) {
        Style style = raster.style
        if (style instanceof ColorMap) {
            addColorMapEntry(title, style)
        } else {
            addImageEntry(raster.name.capitalize(), raster.resample(size: [legendEntryWidth, legendEntryHeight]).bufferedImage)
        }
        this
    }

    /**
     * Add Legend entries for a Map
     * @param map The Map
     * @return The LegendItem
     */
    LegendItem addMap(geoscript.render.Map map) {
        map.layers.each { Renderable renderable ->
            if (renderable instanceof Layer) {
                addLayer(renderable as Layer)
            } else if (renderable instanceof Raster) {
                addRaster(renderable as Raster)
            }
        }
        this
    }

    private int countRules(org.geotools.api.style.Style style) {
        int numberOfRules = 0
        style.featureTypeStyles().each { def fts ->
            fts.rules().each { def rule ->
                numberOfRules++
            }
        }
        numberOfRules
    }

    private static class SLDStyle implements Style {
        private final org.geotools.api.style.Style style
        private final static StyleFactory styleFactory = new StyleFactoryImpl()
        SLDStyle(org.geotools.api.style.Style style) {
            this.style = style
        }
        org.geotools.api.style.Style getGtStyle() {
            style
        }
        static SLDStyle fromRule(Rule rule) {
            def style = styleFactory.createStyle()
            def fts = styleFactory.createFeatureTypeStyle()
            def ruleWithoutFilter = styleFactory.createRule()
            ruleWithoutFilter.symbolizers().addAll(rule.symbolizers())
            fts.rules().add(ruleWithoutFilter)
            style.featureTypeStyles().add(fts)
            new SLDStyle(style)
        }
    }

    @Override
    String toString() {
        "LegendItem(" +
            "x = " + x +
            ", y = " + y +
            ", width = " + width +
            ", height = " + height +
            ", backgroundColor = " + backgroundColor +
            ", title = " + title + '\'' +
            ", titleFont = " + titleFont +
            ", titleColor = " + titleColor +
            ", textFont = " + textFont +
            ", textColor = " + textColor +
            ", entries = " + entries +
            ", legendEntryWidth = " + legendEntryWidth +
            ", legendEntryHeight = " + legendEntryHeight +
            ", gapBetweenEntries = " + gapBetweenEntries +
            ", numberFormat = " + numberFormat +
            ')';
    }

    protected static class LegendEntry {
        String title
        LegendEntryType type
        Style symbolizer
        Image image
    }

    protected enum LegendEntryType {
        GROUP,
        POINT,
        LINE,
        POLYGON,
        COLORMAP,
        IMAGE
    }

}
