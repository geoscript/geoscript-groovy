package geoscript.style

import geoscript.filter.Filter
import geoscript.feature.Feature
import geoscript.layer.Layer
import geoscript.layer.Cursor
import org.geotools.styling.Style as GtStyle
import org.geotools.styling.Rule as GtRule
import org.geotools.styling.FeatureTypeStyle as GtFeatureTypeStyle
import org.geotools.styling.SLDParser
import org.geotools.styling.StyleFactory
import org.geotools.styling.StyleBuilder
import org.geotools.styling.SLDTransformer
import org.geotools.styling.UserLayer
import org.geotools.styling.StyledLayerDescriptor
import org.geotools.factory.CommonFactoryFinder
import org.opengis.filter.FilterFactory
import java.awt.Color;
import org.geotools.brewer.color.BrewerPalette;
import org.geotools.brewer.color.ColorBrewer;
import org.geotools.brewer.color.StyleGenerator;

/**
 * A Style
 * @author Jared Erickson
 */
class Style {

    /**
     * A List of Rules
     */
    List<Rule> rules = []

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
     * Create a Style from a List of Rules, or Symbolizers
     * @param list A List of Rules, or Symbolizers
     */
    Style(List list) {
        if (list[0] instanceof Rule) {
            this.rules = list
        }
        else if (list[0] instanceof Symbolizer) {
            this.rules = [new Rule(symbolizers: list)]
        }
        else {
            throw new Exception("A Style can only be created from a List of Rules or Symbolizers")
        }
    }

    /**
     * Create a Style from an SLD File
     * @param file The SLD File
     */
    Style(File file) {
        fromFile(file)
    }

    /**
     * Create a Style from a single Rule
     * @param rule The Rule
     */
    Style(Rule rule) {
        this([rule])
    }

     /**
     * Create a Style from a single Symbolizer
     * @param symbolizer The Symbolizer
     */
    Style(Symbolizer symbolizer) {
        this([symbolizer])
    }

    /**
     * Get the GeoTools Style
     * @return The GeoTools Style
     */
    GtStyle getGtStyle() {
        def zIndexes = []
        def lookup = [:]
        rules.each{rule ->
            def symbolizers = rule.symbolizers
            def ruleMap = [:]
            symbolizers.each{symbolizer ->
                int z = symbolizer.zIndex
                if(!ruleMap.containsKey(z)) {
                    ruleMap[z] = new Rule(
                        symbolizers: [],
                        filter: rule.filter,
                        minScaleDenominator: rule.minScaleDenominator,
                        maxScaleDenominator: rule.maxScaleDenominator,
                        name: rule.name,
                        title: rule.title
                    )
                }
                ruleMap[z].symbolizers.add(symbolizer)
            }
            ruleMap.keySet().each{z ->
                if (!lookup.containsKey(z)) {
                    zIndexes.add(z)
                    lookup[z] = []
                }
                lookup[z].add(ruleMap[z])
            }
        }
        def featureTypeStyles = zIndexes.sort().collect{z ->
            def rules = lookup[z]
            Style.builder.createFeatureTypeStyle("Feature", rules.collect{r->
                r.gtRule
            }.toArray() as GtRule[])
        }
        GtStyle style = builder.createStyle()
        style.featureTypeStyles().addAll(featureTypeStyles)
        style
    }

    /**
     * Parse the SLD File and get a GeoTools Style
     */
    private List<Rule> fromFile(File file) {
        rules = []
        SLDParser parser = new SLDParser(styleFactory, file.toURI().toURL())
        GtStyle[] styles = parser.readXML()
        styles[0].featureTypeStyles().eachWithIndex{fts,i ->
            getRulesFromGtFeatureTypeStyle(fts, i).each{r -> rules.add(r)}
        }
    }

    /**
     * Get a List of GeoScript Rules from a GeoTools FeatureTypeStyle at the given index
     * @param fts The GeoTools FeatureTypeStyle
     * @param index The Z index
     * @return a List of GeoScript Rules
     */
    private static List<Rule> getRulesFromGtFeatureTypeStyle(GtFeatureTypeStyle fts, int index = 0) {
        fts.rules().collect {r ->
            Rule rule = new Rule()
            rule.filter = new Filter(r.filter)
            rule.minScaleDenominator = r.minScaleDenominator
            rule.maxScaleDenominator = r.maxScaleDenominator
            rule.name = r.name
            rule.title = r.description.title
            rule.symbolizers = r.symbolizers().collect{s ->
                Symbolizer sym
                if (s instanceof org.geotools.styling.PointSymbolizer) {
                    sym = new PointSymbolizer(s)
                }
                else if (s instanceof org.geotools.styling.LineSymbolizer) {
                    sym = new LineSymbolizer(s)
                }
                else if (s instanceof org.geotools.styling.PolygonSymbolizer) {
                    sym = new PolygonSymbolizer(s)
                }
                else if (s instanceof org.geotools.styling.TextSymbolizer) {
                    sym = new TextSymbolizer(s)
                }
                else {
                    sym = new Symbolizer(s)
                }
                sym.zIndex = index
                sym
            }
            return rule
        }
    }

    /**
     * Convert this Style to an SLD document and write it to the OutputStream
     * @return An SLD Document
     */
    void toSLD(OutputStream out = System.out) {

        GtStyle style = getGtStyle()
        UserLayer userLayer = styleFactory.createUserLayer();
        userLayer.addUserStyle(style);

        StyledLayerDescriptor sld = styleFactory.createStyledLayerDescriptor();
        sld.addStyledLayer(userLayer);

        SLDTransformer styleTransform = new SLDTransformer();
        styleTransform.setIndentation(2);
        styleTransform.transform(sld, out);
    }

    /**
     * Get a default Style for the given geometry type
     * @param geometryType The geometry type
     * @return A Style
     */
    static Style getDefaultStyleForGeometryType(String geometryType) {
        def sym;
        def color = getRandomColor()
        def darkerColor = color.darker()
        if (geometryType.toLowerCase().endsWith("point")) {
            sym = new PointSymbolizer(
                fillColor: convertColorToHex(color),
                strokeColor: convertColorToHex(darkerColor)
            )
        }
        else if (geometryType.toLowerCase().endsWith("linestring") 
            || geometryType.toLowerCase().endsWith("linearring")
            || geometryType.toLowerCase().endsWith("curve")) {
            sym = new LineSymbolizer(
                strokeColor: convertColorToHex(color)
            )
        }
        else if (geometryType.toLowerCase().endsWith("polygon")) {
            sym = new PolygonSymbolizer(
                fillColor: convertColorToHex(color),
                strokeColor: convertColorToHex(darkerColor)
            )
        }
        else {
            sym = [
                new PointSymbolizer(
                    fillColor: convertColorToHex(color),
                    strokeColor: convertColorToHex(darkerColor)
                ),
                new LineSymbolizer(
                    strokeColor: convertColorToHex(color)
                ),
                new PolygonSymbolizer(
                    fillColor: convertColorToHex(color),
                    strokeColor: convertColorToHex(darkerColor)
                )
            ]
        }
        new Style(sym)
    }

    /**
     * Get a random (pastel) color
     * @return A Color
     */
    static Color getRandomColor() {
        java.util.Random random = new java.util.Random();
        int i = 128
        int r = random.nextInt(i);
        int g = random.nextInt(i);
        int b = random.nextInt(i);
        new Color(r,g,b)
    }

    /**
     * Convert a Color to a hex color string
     * @param The Color
     * @return A hex color string
     */
    static String convertColorToHex(Color color) {
        "#${Integer.toHexString((color.getRGB() & 0xffffff) | 0x1000000).substring(1)}"
    }

    /**
     * CSS Color names
     */
    static final Map colorNameMap = [
        aliceblue: "#f0f8ff",
        antiquewhite: "#faebd7",
        aqua: "#00ffff",
        aquamarine: "#7fffd4",
        azure: "#f0ffff",
        beige: "#f5f5dc",
        bisque: "#ffe4c4",
        black: "#000000",
        blanchedalmond: "#ffebcd",
        blue: "#0000ff",
        blueviolet: "#8a2be2",
        brown: "#a52a2a",
        burlywood: "#deb887",
        cadetblue: "#5f9ea0",
        chartreuse: "#7fff00",
        chocolate: "#d2691e",
        coral: "#ff7f50",
        cornflowerblue: "#6495ed",
        cornsilk: "#fff8dc",
        crimson: "#dc143c",
        cyan: "#00ffff",
        darkblue: "#00008b",
        darkcyan: "#008b8b",
        darkgoldenrod: "#b8860b",
        darkgray: "#a9a9a9",
        darkgreen: "#006400",
        darkkhaki: "#bdb76b",
        darkmagenta: "#8b008b",
        darkolivegreen: "#556b2f",
        darkorange: "#ff8c00",
        darkorchid: "#9932cc",
        darkred: "#8b0000",
        darksalmon: "#e9967a",
        darkseagreen: "#8fbc8f",
        darkslateblue: "#483d8b",
        darkslategray: "#2f4f4f",
        darkturquoise: "#00ced1",
        darkviolet: "#9400d3",
        deeppink: "#ff1493",
        deepskyblue: "#00bfff",
        dimgray: "#696969",
        dodgerblue: "#1e90ff",
        firebrick: "#b22222",
        floralwhite: "#fffaf0",
        forestgreen: "#228b22",
        fuchsia: "#ff00ff",
        gainsboro: "#dcdcdc",
        ghostwhite: "#f8f8ff",
        gold: "#ffd700",
        goldenrod: "#daa520",
        gray: "#808080",
        green: "#008000",
        greenyellow: "#adff2f",
        honeydew: "#f0fff0",
        hotpink: "#ff69b4",
        indianred: "#cd5c5c",
        indigo: "#4b0082",
        ivory: "#fffff0",
        khaki: "#f0e68c",
        lavender: "#e6e6fa",
        lavenderblush: "#fff0f5",
        lawngreen: "#7cfc00",
        lemonchiffon: "#fffacd",
        lightblue: "#add8e6",
        lightcoral: "#f08080",
        lightcyan: "#e0ffff",
        lightgoldenrodyellow: "#fafad2",
        lightgrey: "#d3d3d3",
        lightgreen: "#90ee90",
        lightpink: "#ffb6c1",
        lightsalmon: "#ffa07a",
        lightseagreen: "#20b2aa",
        lightskyblue: "#87cefa",
        lightslategray: "#778899",
        lightsteelblue: "#b0c4de",
        lightyellow: "#ffffe0",
        lime: "#00ff00",
        limegreen: "#32cd32",
        linen: "#faf0e6",
        magenta: "#ff00ff",
        maroon: "#800000",
        mediumaquamarine: "#66cdaa",
        mediumblue: "#0000cd",
        mediumorchid: "#ba55d3",
        mediumpurple: "#9370d8",
        mediumseagreen: "#3cb371",
        mediumslateblue: "#7b68ee",
        mediumspringgreen: "#00fa9a",
        mediumturquoise: "#48d1cc",
        mediumvioletred: "#c71585",
        midnightblue: "#191970",
        mintcream: "#f5fffa",
        mistyrose: "#ffe4e1",
        moccasin: "#ffe4b5",
        navajowhite: "#ffdead",
        navy: "#000080",
        oldlace: "#fdf5e6",
        olive: "#808000",
        olivedrab: "#6b8e23",
        orange: "#ffa500",
        orangered: "#ff4500",
        orchid: "#da70d6",
        palegoldenrod: "#eee8aa",
        palegreen: "#98fb98",
        paleturquoise: "#afeeee",
        palevioletred: "#d87093",
        papayawhip: "#ffefd5",
        peachpuff: "#ffdab9",
        peru: "#cd853f",
        pink: "#ffc0cb",
        plum: "#dda0dd",
        powderblue: "#b0e0e6",
        purple: "#800080",
        red: "#ff0000",
        rosybrown: "#bc8f8f",
        royalblue: "#4169e1",
        saddlebrown: "#8b4513",
        salmon: "#fa8072",
        sandybrown: "#f4a460",
        seagreen: "#2e8b57",
        seashell: "#fff5ee",
        sienna: "#a0522d",
        silver: "#c0c0c0",
        skyblue: "#87ceeb",
        slateblue: "#6a5acd",
        slategray: "#708090",
        snow: "#fffafa",
        springgreen: "#00ff7f",
        steelblue: "#4682b4",
        tan: "#d2b48c",
        teal: "#008080",
        thistle: "#d8bfd8",
        tomato: "#ff6347",
        turquoise: "#40e0d0",
        violet: "#ee82ee",
        wheat: "#f5deb3",
        white: "#ffffff",
        whitesmoke: "#f5f5f5",
        yellow: "#ffff00",
        yellowgreen: "#9acd32"
    ]

    /**
     * Get a Color from a String.  Handles CSS names,
     * hexadecimals, and RGB.
     * @param str The String
     * @return a Color or null
     */
    static Color getColor(String str) {
        if (str.startsWith("#")) {
            return Color.decode(str)
        }
        else if (str.split(",").length >= 3) {
            String[] parts = str.split(",")
            int r = parts[0] as int
            int g = parts[1] as int
            int b =  parts[2] as int
            int a = parts.length > 3 ? parts[3] as int : 0
            return new Color(r, g, b, a)
        }
        else if (colorNameMap.containsKey(str.toLowerCase())){
            return Color.decode(colorNameMap.get(str.toLowerCase()))
        }
        return null
    }

    /**
     * Get a hex color String from a hex color string, RGB, or CSS name
     * @param str A color String
     * @return A hex color String
     */
    static String getHexColor(String str) {
        convertColorToHex(getColor(str))
    }

    /**
     * The shared ColorBrewer instance
     */
    private static ColorBrewer colorBrewer;

    /**
     * Initiate and load ColorBrewer if necessary
     */
    private static void loadColorBrewer() {
        if (colorBrewer == null) {
            colorBrewer = new ColorBrewer();
            colorBrewer.loadPalettes();
        }
    }

    /**
     * Get a PaletteType by a string
     * @param type The string type
     * @return A org.geotools.brewer.color.PaletteType
     */
    private static org.geotools.brewer.color.PaletteType getPaletteType(String type) {
        if (type.equalsIgnoreCase("diverging")) {
            return ColorBrewer.DIVERGING
        } else if (type.equalsIgnoreCase("qualitative")) {
            return ColorBrewer.QUALITATIVE
        } else if (type.equalsIgnoreCase("sequential")) {
            return ColorBrewer.SEQUENTIAL
        } else {
            return ColorBrewer.ALL
        }

    }

    /**
     * Get a List of Palette names by type (defaults to All)
     * @param type The type (all, diverging, qualitative, sequential)
     * @return A List of Palette names
     */
    static List getPaletteNames(String type = "all") {
        loadColorBrewer()
        colorBrewer.getPalettes(getPaletteType(type)).collect{palette ->
            palette.name
        }
    }

    /**
     * Get a List of Colors from a Palette by name
     * @param name The Palette name
     * @param count The number of colors
     * @return A List of Colors.  The List will be empty if the Palette can't
     * be found.  The number of Colors is contrained by the number of maximum
     * colors for the given palette.
     */
    static List getPaletteColors(String name, int count = -1) {
        loadColorBrewer()
        def colors = []
        def palette = colorBrewer.getPalette(name)
        if (count == -1) {
            count = palette.maxColors
        }
        if (palette != null) {
            colors.addAll(palette.getColors(Math.min(palette.maxColors, count)).toList())
        }
        colors
    }

    /**
     * Create a graduated Style
     * @param layer The Layer
     * @param field The Field name
     * @param method The classification method (Quantile or EqualInterval)
     * @param number The number of categories
     * @param colors A Palette name, or a List of Colors
     * @param elseMode The else mode (ignore, min, max)
     * @return The graduated Style
     */
    static Style createGraduatedStyle(Layer layer, String field, String method, int number, def colors, String elseMode = "ignore") {

        org.opengis.filter.FilterFactory2 ff = org.geotools.factory.CommonFactoryFinder.getFilterFactory2(null)
        org.opengis.filter.expression.Function function = ff.function(method, ff.property(field), ff.literal(number))
        org.geotools.filter.function.Classifier classifier = (org.geotools.filter.function.Classifier) function.evaluate(layer.fs.features)

        int elseModeInt
        if (elseMode.equalsIgnoreCase("min")) {
            elseModeInt = org.geotools.brewer.color.StyleGenerator.ELSEMODE_INCLUDEASMIN
        } else if (elseMode.equalsIgnoreCase("max")) {
            elseModeInt = org.geotools.brewer.color.StyleGenerator.ELSEMODE_INCLUDEASMAX
        } else {
            elseModeInt = org.geotools.brewer.color.StyleGenerator.ELSEMODE_IGNORE
        }

        // If the colors argument is a String treat it
        // like a Palette
        if (colors instanceof String) {
            colors = getPaletteColors(colors) as java.awt.Color[]
        }
        else {
            colors = colors.collect{c ->
                c instanceof java.awt.Color ? c as Color : Style.getColor(c as String)
            } as java.awt.Color[]
        }

        // Generate the FeatureTypeStyle
        GtFeatureTypeStyle featureTypeStyle = org.geotools.brewer.color.StyleGenerator.createFeatureTypeStyle(
                classifier,
                (org.geotools.filter.Expression) ff.property(field),
                colors,
                "test",
                layer.fs.schema.geometryDescriptor,
                elseModeInt,
                0.5,
                null);

        new Style(getRulesFromGtFeatureTypeStyle(featureTypeStyle))
    }

    /**
     * Create a Style with a Rule for every unique value of a Layer's Field
     * @param layer The Layer
     * @param field The Field name
     * @param colors A Closure (which takes index based on 0 and a value), a Palette name, or a List of Colors
     * @return A Style
     */
    static Style createUniqueValuesStyle(Layer layer, String field, def colors = {index, value -> Style.getRandomColor()}) {

        // Collect the unique values
        Set uniqueValueSet = new HashSet()

        Cursor c = layer.cursor
        while(c.hasNext()) {
            Feature f = c.next()
            uniqueValueSet.add(f.get(field))
        }
        c.close()

        List uniqueValues = new ArrayList(uniqueValueSet)
        Collections.sort(uniqueValues)

        // If the colors argument is a String treat it
        // like a Palette
        if (colors instanceof String) {
            colors = getPaletteColors(colors)
        }

        // Create the list of Rules
        int i = 0
        List rules = uniqueValues.collect{value ->

            // Get the Color
            def color
            if (colors instanceof Closure) {
               color = colors.call(i, value)
            }
            else {
                // Set our counter back to 0 if
                // it exceeds the number of colors
                // in the List
                if (i >= colors.size()) {
                    i = 0
                }
                color = colors[i]
            }

            // Make sure color is a java.awt.Color
            if (color instanceof String) {
                color = getColor(color)
            }

            // Increment our counter
            i++

            // Create the Rule
            new Rule(
                name: "${field} = ${value}",
                title: "${field} = ${value}",
                filter: new Filter(filterFactory.equals(filterFactory.property(field), filterFactory.literal(value))),
                symbolizers: [
                    Symbolizer.getDefaultForGeometryType(layer.schema.geom.typ, color)
                ]
            )
        }

        // Create our Style with a Rule for each unique value
        new Style(rules)
    }

}

