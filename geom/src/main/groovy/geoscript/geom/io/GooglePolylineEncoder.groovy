package geoscript.geom.io

import geoscript.geom.Geometry
import geoscript.geom.LineString
import geoscript.geom.Point

/**
 * A Google Polyline Encoder/Decoder (https://developers.google.com/maps/documentation/utilities/polylinealgorithm).
 * Based on https://github.com/mapbox/polyline.
 * @author Jared Erickson
 */
class GooglePolylineEncoder implements Reader, Writer {

    /**
     * Encode a LineString
     * @param lineString The LineString
     * @param precision The precision (defaults to 5)
     * @return The encoded String
     */
    String write (Geometry geometry, int precision = 5) {
        if (!(geometry instanceof LineString)) {
            throw new IllegalArgumentException("The GooglePolylineEncoder can only encode LineStrings!")
        }
        LineString lineString = geometry as LineString
        StringBuilder output = new StringBuilder()
        if (!lineString.empty) {
            double factor = Math.pow(10, precision)
            List<Point> points = lineString.points
            output.append(encodeNum(points[0].y, factor))
            output.append(encodeNum(points[0].x, factor))
            (1..<points.size()).each { int i ->
                Point a = points[i]
                Point b = points[i - 1]
                output.append(encodeNum(a.y - b.y, factor))
                output.append(encodeNum(a.x - b.x, factor))
            }
        }
        output.toString()
    }

    /**
     * Encode a coordinate
     * @param coordinate The coordinate
     * @param factor The factor
     * @return The encoded number as a string
     */
    private String encodeNum(double coordinate, double factor) {
        int coordinateInt = Math.round(coordinate * factor) as int
        coordinateInt <<= 1
        if (coordinateInt < 0) {
            coordinateInt = ~coordinateInt
        }
        StringBuilder output = new StringBuilder()
        while (coordinateInt >= 0x20) {
            output.append(Character.toChars((0x20 | (coordinateInt & 0x1f)) + 63))
            coordinateInt >>= 5
        }
        output.append(String.valueOf(Character.toChars(coordinateInt + 63)))
        output.toString()
    }

    /**
     * Decode the encoded String as a LineString
     * @param str The encoded String
     * @param precision The precision which defaults to 5
     * @return A LineString
     */
    Geometry read(String str, int precision = 5) {

        List<Point> points = []
        double factor = Math.pow(10, precision)

        int index = 0
        int strLength = str.length()

        int latitude = 0
        int longitude = 0
        int latitudeChange
        int longitudeChange

        while (index < strLength) {

            // Latitude
            int c = 0
            int shift = 0
            int result = 0
            while(true) {
                c = Character.codePointAt(str, index++) - 63
                result |= (c & 0x1f) << shift
                shift += 5
                if (c < 0x20) {
                    break
                }
            }
            latitudeChange = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1))

            // Longitude
            shift = 0
            result = 0
            while(true) {
                c = Character.codePointAt(str, index++) - 63
                result |= (c & 0x1f) << shift
                shift += 5
                if (c < 0x20) {
                    break
                }
            }
            longitudeChange = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1))

            latitude += latitudeChange
            longitude += longitudeChange

            points.add(new Point(longitude / factor,latitude / factor))
        }

        new LineString(points)
    }
}
