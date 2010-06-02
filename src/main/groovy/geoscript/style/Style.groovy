package geoscript.style

import org.geotools.styling.Style as GtStyle
import org.geotools.styling.SLDParser
import org.geotools.styling.StyleFactory
import org.geotools.styling.StyleBuilder
import org.geotools.styling.SLDTransformer
import org.geotools.styling.UserLayer
import org.geotools.styling.StyledLayerDescriptor
import org.geotools.factory.CommonFactoryFinder
import org.opengis.filter.FilterFactory
import java.awt.Color;

/**
 * A Style
 * @author Jared Erickson
 */
class Style {

    /**
     * The GeoTools Style
     */
    GtStyle style

    /**
     * The StyleBuilder
     */
    static StyleBuilder builder = new StyleBuilder()

    /**
     * The StyleFactory
     */
    static StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null)

    /**
     * The FilterFactory
     */
    static FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(null)

    /**
     * Create a Style wrapping a GeoTools Style
     * @param style The GeoTools Style
     */
    Style(GtStyle style) {
        this.style = style
    }

    /**
     * Create a Style from an SLD File
     * @param file The SLD File
     */
    Style(File file) {
        this(fromFile(file))
    }

    Style() {
        this(builder.createStyle())
    }

    Style(SubStyle subStyle) {
        this(createGtStyleFromSubStyle(subStyle))
    }

    private static createGtStyleFromSubStyle(SubStyle subStyle) {
        GtStyle gtStyle = builder.createStyle()
        gtStyle.featureTypeStyles().add(subStyle.featureTypeStyle)
        gtStyle
    }

    Style(List list) {
        this(createGtStyleFromList(list))
    }

    private static createGtStyleFromList(List list) {
        if (list.size() > 0) {
            def firstItem = list[0]
            if (firstItem instanceof SubStyle) {
                GtStyle style = builder.createStyle()
                style.featureTypeStyles().addAll(list.collect{subStyle ->
                    subStyle.featureTypeStyle
                })
                return style
            }
            else if (firstItem instanceof Rule) {
                return createGtStyleFromSubStyle(new SubStyle(list))
            }
            else if (firstItem instanceof Symbolizer) {
                return createGtStyleFromSubStyle(new SubStyle(new Rule(list)))
            }
        }
        else {
            return builder.createStyle()
        }
    }

    Style(Rule rule) {
        this(createGtStyleFromRule(rule))
    }

    private static createGtStyleFromRule(Rule rule) {
        createGtStyleFromSubStyle(new SubStyle(rule))
    }

    Style(Symbolizer symbolizer) {
        this(createGtStyleFromSymbolizer(symbolizer))
    }

    private static createGtStyleFromSymbolizer(Symbolizer symbolizer) {
        Rule rule = new Rule(symbolizer)
        SubStyle subStyle = new SubStyle(rule)
        createGtStyleFromSubStyle(subStyle)
    }

    /**
     * Get the Style's SubStyles (SLD FeatureTypeStyles)
     * @return The Style's SubStyles
     */
    List<SubStyle> getSubStyles() {
       style.featureTypeStyles().collect{ftStyle -> new SubStyle(ftStyle)}
    }

    String getName() {
        style.name
    }

    void setName(String name) {
        style.name = name
    }

    String getTitle() {
        style.description.title
    }

    void setTitle(String title) {
        style.description.title = title
    }

    String getAbstract() {
        style.description.getAbstract()
    }

    void setAbstract(String abstractStr) {
        style.description.setAbstract(abstractStr)
    }

    String toString() {
        //getName() + " (" + getTitle() + ") " + getAbstract()
        style.toString()
    }

    /**
     * Parse the SLD File and get a GeoTools Style
     */
    private static GtStyle fromFile(File file) {
        SLDParser parser = new SLDParser(styleFactory, file.toURI().toURL())
        GtStyle[] styles = parser.readXML()
        styles[0]
    }

    /**
     * Convert this Style to an SLD document
     * @return An SLD Document
     */
    String toSLD(OutputStream out = System.out) {

        UserLayer userLayer = styleFactory.createUserLayer();
        userLayer.addUserStyle(style);

        StyledLayerDescriptor sld = styleFactory.createStyledLayerDescriptor();
        sld.addStyledLayer(userLayer);

        SLDTransformer styleTransform = new SLDTransformer();
        styleTransform.setIndentation(2);
        styleTransform.transform(sld, out);
    }
   
    /**
     * Get a random (pastel) color
     * @return A Color
     */
    static Color getRandomColor() {
        java.util.Random random = new java.util.Random();
        int r = random.nextInt(256 / 2);
        int g = random.nextInt(256 / 2);
        int b = random.nextInt(256 / 2);
        new Color(r,g,b)
    }

    /**
     * Convert a Color to a hex color string
     * @param The Color
     * @return A hex color string
     */
    static String convertColorToHex(Color color) {
        "#${Integer.toHextString(color.getRGB() & 0x00ffffff)}"
    }
}

