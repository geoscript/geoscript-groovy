package geoscript.style

import java.awt.Color
import org.geotools.brewer.color.BrewerPalette
import org.geotools.brewer.color.ColorBrewer
import org.geotools.brewer.color.StyleGenerator

/**
 * A set of Color Utilities
 * @author Jared Erickson
 */
class ColorUtil {

    /**
     * Get a Color from an Object.  Handles CSS names (red, wheat),
     * hexadecimals Strings (#00FF00, #FFF), and RGB String ("255,255,0"), list ([0,255,0]), and map ([r: 255, g: 255, b: 0]).
     * @param color A Object convertable to a Color
     * @return a Color or null
     */
    static Color getColor(def color) {
        // Color: Color.BLACK, new Color(255,255,255)
        if (color instanceof Color) {
            return color
        }
        // RGB as String: "255,255,255"
        else if (color instanceof String && color.split(",").length >= 3) {
            String[] parts = color.split(",")
            int r = parts[0] as int
            int g = parts[1] as int
            int b =  parts[2] as int
            int a = parts.length > 3 ? parts[3] as int : 0
            return new Color(r, g, b, a)
        }
        // Hexadecimal: #00ff00
        else if (color instanceof String && color.startsWith("#")) {
            // #00ff00
            if (color.length() == 7) {
                return Color.decode(color)
            }
            // #0f0
            else if (color.length() == 4) {
                String hex = "#" + color[1] * 2 + color[2] * 2 + color[3] * 2
                return Color.decode(hex)
            }
            // Oops
            else {
                return null
            }
        }
        // CSS Color Names: wheat, black, navy
        else if (color instanceof String && colorNameMap.containsKey(color.toLowerCase())){
            return Color.decode(colorNameMap.get(color.toLowerCase()))
        }
        // RGB as List: [255,255,255,0.1]
        else if (color instanceof List && color.size() >= 3) {
            int r = color[0] as int
            int g = color[1] as int
            int b =  color[2] as int
            int a = color.size > 3 ? color[3] as int : 0
            return new Color(r, g, b, a)
        }
        // RGB as Map [r:255,g:255,b:0,a:125]
        else if (color instanceof Map && color.containsKey("r") && color.containsKey("g") && color.containsKey("b")) {
            int r = color.r as int
            int g = color.g as int
            int b =  color.b as int
            int a = color.containsKey('a') ? color.a as int : 0
            return new Color(r, g, b, a)
        }
        else {
            return null
        }
    }

    /**
     * Convert a Color to a hex color string
     * @param color The color value
     * @return A hex color string
     */
    static String toHex(def color) {
        if (color instanceof String && color.startsWith("#")) {
            return color
        } else if (color instanceof Color) {
            return "#${Integer.toHexString((color.getRGB() & 0xffffff) | 0x1000000).substring(1)}"
        } else {
            def c = getColor(color)
            if (c != null) {
                return toHex(getColor(color))
            } else {
                return null
            }
        }
    }

    /**
     * Generate a random color
     * @return A Color
     */
    static Color getRandom() {
        def random = new java.util.Random()
        int red = random.nextInt(256)
        int green = random.nextInt(256)
        int blue = random.nextInt(256)
        new Color(red,green,blue)
    }

    /**
     * Get a random pastel color
     * @return A Color
     */
    static Color getRandomPastel() {
        java.util.Random random = new java.util.Random()
        int i = 128
        int r = random.nextInt(i)
        int g = random.nextInt(i)
        int b = random.nextInt(i)
        new Color(r,g,b)
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
     * be found.  The number of Colors is constrained by the number of maximum
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
}
