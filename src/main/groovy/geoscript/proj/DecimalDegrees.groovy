package geoscript.proj

import geoscript.geom.Point

import java.text.DecimalFormat
import java.text.NumberFormat

/**
 * A DecimalDegrees class that can parse and format DD, DMS, and DDM.
 * @author Jared Erickson
 */
class DecimalDegrees {

    /**
     * The latitude
     */
    final double latitude

    /**
     * The longitude
     */
    final double longitude

    /**
     * A Map of unicode glyphs
     */
    private static final Map<String, String> glyphs = [
        degree: "\u00B0",
        minute: "\'",
        second: '"'
    ]

    /**
     * A Map of NumberFormats
     */
    private static final Map<String, NumberFormat> formats = [
        zero: new DecimalFormat("#"),
        four: new DecimalFormat("0.0000")
    ]

    /**
     * Create a new DecimalDegrees from a longitude and latitude
     * @param longitude The longitude
     * @param latitude The latitude
     */
    DecimalDegrees(double longitude, double latitude) {
        this.latitude = latitude
        this.longitude = longitude
    }

    /**
     * Create a new DecimalDegrees from a Point (which should be in EPSG:4326)
     * @param point The Point
     */
    DecimalDegrees(Point point) {
        this(point.x, point.y)
    }

    /**
     * Create a new DecimalDegrees from separate longitude and latitude strings
     * @param longitude The longitude string
     * @param latitude The latitude string
     */
    DecimalDegrees(String longitude, String latitude) {
        this(parseString(longitude), parseString(latitude))
    }

    /**
     * Create a new DecimalDegrees from a string
     * @param str The string
     */
    DecimalDegrees(String str) {
        this(parseCompleteString(str))
    }

    /**
     * Parse a string which contains both longitude and latitude
     * @param str The string which contains both longitude and latitude
     * @return A Point
     */
    private static Point parseCompleteString(String str) {
        // The list of delimeters to try
        List delimeters = [","," ","\\|","\\t"]
        // Find the first delimeter that splits the string
        // into two parts
        String delimeter = delimeters.find{d ->
            if (str.split(d).size() == 2) {
                return d
            }
        }
        def parts = str.split(delimeter)
        new Point(parseString(parts[0].trim()), parseString(parts[1].trim()))
    }

    /**
     * Parse the string into a double
     * @param str The string
     * @return A double
     */
    private static double parseString(String str) {

        // Divide and trim each word in the string
        def parts = str.split(" ").collect{it.trim()}

        // DMS with glyphs
        if (parts.size() >= 3 && str.contains(glyphs.degree) && str.contains(glyphs.minute) && str.contains(glyphs.second)) {
            int d = str.indexOf(glyphs.degree)
            int m = str.indexOf(glyphs.minute)
            int s = str.indexOf(glyphs.second)
            double degrees = Double.parseDouble(str.substring(0, d))
            double minutes = Double.parseDouble(str.substring(d + 1, m))
            double seconds = Double.parseDouble(str.substring(m + 1, s))
            double dd = degrees + (minutes / 60) + (seconds / 3600)
            if (parts.size() > 3) {
                String dir = parts[3].toUpperCase()
                if (dir.equals("S") || dir.equals("W")) {
                    dd = -dd
                }
            }
            return dd
        }
        // DMS with characters
        else if (parts.size() >= 3 && parts[0].endsWith("d") && parts[1].endsWith("m") && parts[2].endsWith("s")) {
            double degrees = Double.parseDouble(parts[0].substring(0,parts[0].size() - 1))
            double minutes = Double.parseDouble(parts[1].substring(0,parts[1].size() - 1))
            double seconds = Double.parseDouble(parts[2].substring(0,parts[2].size() - 1))
            double dd = degrees + (minutes / 60) + (seconds / 3600)
            if (parts.size() > 3) {
                String dir = parts[3].toUpperCase()
                if (dir.equals("S") || dir.equals("W")) {
                    dd = -dd
                }
            }
            return dd
        }
        // DDM with glyphs  (122° 31.5372' W, 47° 12.7213' N)
        else if (parts.size() >= 2 && parts[0].endsWith(glyphs.degree) && parts[1].endsWith(glyphs.minute)) {
            double degrees = Double.parseDouble(parts[0].substring(0, parts[0].size() - 1))
            double minutes = Double.parseDouble(parts[1].substring(0, parts[1].size() - 1))
            double dd = degrees + (minutes / 60)
            if (parts.size() > 2) {
                String dir = parts[2].toUpperCase()
                if (dir.equals("S") || dir.equals("W")) {
                    dd = -dd
                }
            }
            return dd
        }
        // DDM with characters  (122d 31.5372m W, 47d 12.7213m N)
        else if (parts.size() >= 2 && parts[0].endsWith("d") && parts[1].endsWith("m")) {
            double degrees = Double.parseDouble(parts[0].substring(0, parts[0].size() - 1))
            double minutes = Double.parseDouble(parts[1].substring(0, parts[1].size() - 1))
            double dd = degrees + (minutes / 60)
            if (parts.size() > 2) {
                String dir = parts[2].toUpperCase()
                if (dir.equals("S") || dir.equals("W")) {
                    dd = -dd
                }
            }
            return dd
        }
        // Just a number
        else {
            return Double.parseDouble(str)
        }

    }

    /**
     * Convert a decimal degrees into a Map of degrees, minutes, seconds
     * @param n The decimal degrees number
     * @return A Map of degrees, minutes, seconds keys
     */
    private Map ddToDms(double n) {
        List nums1 = splitNumber(n)
        int degrees = nums1[0]
        List nums2 = splitNumber(nums1[1] * 60)
        int minutes = nums2[0]
        double seconds = nums2[1] * 60
        [degrees: degrees, minutes: minutes, seconds: seconds]
    }

    /**
     * Get the DecimalDegrees longitude and latitude in degrees, minutes, seconds (DMS).
     * @return A Map with longitude and latitude keys which contains degrees, minutes, seconds keys
     */
    Map getDms() {
        [longitude: ddToDms(longitude), latitude: ddToDms(latitude)]
    }

    /**
     * Convert the DecimalDegrees to a DMS string
     * @param useGlyphs Whether to use glyphs (true, default) or characters (false)
     * @return The DecimalDegrees to a DMS string
     */
    String toDms(boolean useGlyphs = true) {
        Map dmsMap = getDms()
        "${formats.zero.format(dmsMap.longitude.degrees)}${useGlyphs ? glyphs.degree : 'd'} ${formats.zero.format(dmsMap.longitude.minutes)}${useGlyphs ? glyphs.minute : 'm'} ${formats.four.format(dmsMap.longitude.seconds)}${useGlyphs ? glyphs.second : 's'} W, " +
        "${formats.zero.format(dmsMap.latitude.degrees)}${useGlyphs ? glyphs.degree : 'd'} ${formats.zero.format(dmsMap.latitude.minutes)}${useGlyphs ? glyphs.minute : 'm'} ${formats.four.format(dmsMap.latitude.seconds)}${useGlyphs ? glyphs.second : 's'} N"
    }

    /**
     * Get the DecimalDegrees longitude and latitude in decimal degree minutes (DDM).
     * @return A Map with longitude and latitude keys which contains degrees and minutes
     */
    Map getDdm() {
        [longitude: ddToDdm(longitude), latitude: ddToDdm(latitude)]
    }

    /**
     * Convert a decimal degrees into a Map of decimal degrees minutes
     * @param n The decimal degrees number
     * @return A Map of degrees and minutes
     */
    private Map ddToDdm(double n) {
        List nums1 = splitNumber(n)
        int degrees = nums1[0]
        double minutes = nums1[1] * 60
        [degrees: degrees, minutes: minutes]
    }

    /**
     * Convert the DecimalDegrees to a DDM string
     * @param useGlyphs Whether to use glyphs (true, default) or characters (false)
     * @return The DecimalDegrees to a DDM string
     */
    String toDdm(boolean useGlyphs = true) {
        Map dmsMap = getDms()
        "${formats.zero.format(dmsMap.longitude.degrees)}${useGlyphs ? glyphs.degree : 'd'} ${formats.four.format(splitNumber(longitude)[1] * 60)}${useGlyphs ? glyphs.minute : 'm'} W, " +
        "${formats.zero.format(dmsMap.latitude.degrees)}${useGlyphs ? glyphs.degree : 'd'} ${formats.four.format(splitNumber(latitude)[1] * 60)}${useGlyphs ? glyphs.minute : 'm'} N"
    }

    /**
     * Get the DecimalDegrees as a Point
     * @return A Point
     */
    Point getPoint() {
        new Point(longitude, latitude)
    }

    /**
     * Split a number into integer and fraction
     * @param n The number
     * @return A List with integer and fraction
     */
    private List splitNumber(double n) {
        String str = String.valueOf(n)
        int p = str.indexOf(".")
        if (p > -1) {
            [str.substring(0, p) as int, str.substring(p) as double]
        } else {
            [Integer.parseInt(n), 0.0]
        }
    }
}
