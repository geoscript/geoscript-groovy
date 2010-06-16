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

    /**
     * Create a default Style
     */
    Style() {
        this(builder.createStyle())
    }

    /**
     * Create a Style with one SubStyle
     * @param subStyle The SubStyle
     */
    Style(SubStyle subStyle) {
        this(createGtStyleFromSubStyle(subStyle))
    }

    /**
     * Create GeoTools Style from a single SubStyle
     * @param subStyle The SubStyle
     * @return A GeoTools Style
     */
    private static GtStyle createGtStyleFromSubStyle(SubStyle subStyle) {
        GtStyle gtStyle = builder.createStyle()
        gtStyle.featureTypeStyles().add(subStyle.featureTypeStyle)
        gtStyle
    }

    /**
     * Create a Style from a List of SubStyles, Rules, or Symbolizers
     * @param list A List of SubStyles, Rules, or Symbolizers
     */
    Style(List list) {
        this(createGtStyleFromList(list))
    }

    /**
     * Create a GeoTools Style from a List of SubStyles, Rules, or Symbolizers
     * @param list A List of SubStyles, Rules, or Symbolizers
     * @return A GeoTools Style
     */
    private static GtStyle createGtStyleFromList(List list) {
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

    /**
     * Create a Style from a single Rule
     * @param rule The Rule
     */
    Style(Rule rule) {
        this(createGtStyleFromRule(rule))
    }

    /**
     * Create a GeoTools Style from a single Rule
     * @param rule The Rule
     * @return a GeoTools Style
     */
    private static GtStyle createGtStyleFromRule(Rule rule) {
        createGtStyleFromSubStyle(new SubStyle(rule))
    }

    /**
     * Create a Style from a Symbolizer
     * @param symbolizer The Symbolizer
     */
    Style(Symbolizer symbolizer) {
        this(createGtStyleFromSymbolizer(symbolizer))
    }

    /**
     * Create a GeoTools Style from a Symbolizer
     * @param symbolizer The Symbolizer
     * @return A GeoTools Style
     */
    private static GtStyle createGtStyleFromSymbolizer(Symbolizer symbolizer) {
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

    /**
     * Get the name
     * @return The name
     */
    String getName() {
        style.name
    }

    /**
     * Set the name
     * @param name The new name
     */
    void setName(String name) {
        style.name = name
    }

    /**
     * Get the title
     * @return The title
     */
    String getTitle() {
        style.description.title
    }

    /**
     * Set the title
     * @param title The new title
     */
    void setTitle(String title) {
        style.description.title = title
    }

    /**
     * Get the abstract
     * @return The abstract
     */
    String getAbstract() {
        style.description.getAbstract()
    }

    /**
     * Set the abstract
     * @return abstractStr The new abstract
     */
    void setAbstract(String abstractStr) {
        style.description.setAbstract(abstractStr)
    }

    /**
     * The string representation
     * @return The string representation
     */
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
     * Convert this Style to an SLD document and write it to the OutputStream
     * @return An SLD Document
     */
    void toSLD(OutputStream out = System.out) {

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

    /**
     * Get a Color from a String.  Handle java.awt.Color names,
     * hexadecimals, and RGB.
     * @param str The String
     * @return a Color or null
     */
    static Color getColor(String str) {
        Map colorNameMap = [
            "black": Color.black,
            "blue": Color.blue,
            "cyan": Color.cyan,
            "darkGray": Color.darkGray,
            "gray": Color.gray,
            "green": Color.green,
            "lightGray": Color.lightGray,
            "magenta": Color.magenta,
            "organge": Color.orange,
            "pink": Color.pink,
            "red": Color.red,
            "white": Color.white,
            "yellow": Color.yellow
        ]
        if (str.startsWith("#")) {
            return Color.decode(str)
        }
        else if (str.split(",") == 3) {
            String[] parts = str.split(",")
            return new Color(parts[0] as int, parts[1] as int, parts[2] as int)
        }
        else if (colorNameMap.containsKey(str.toLowerCase())){
            return colorNameMap.get(str.toLowerCase())
        }
        return null
    }
}

