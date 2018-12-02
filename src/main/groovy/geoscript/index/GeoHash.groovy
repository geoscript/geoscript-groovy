package geoscript.index

import geoscript.geom.Bounds
import geoscript.geom.Point

/**
 * A GeoHash module.  This is a port of node-geohash (https://github.com/sunng87/node-geohash).
 * @author Jared Erickson
 */
class GeoHash {

    private String BASE32_CODES = "0123456789bcdefghjkmnpqrstuvwxyz"

    private Map BASE32_CODES_DICT = [:]

    private List SIGFIG_HASH_LENGTH = [0, 5, 7, 8, 11, 12, 13, 15, 16, 17, 18]

    /**
     * Create a new GeoHash
     */
    GeoHash() {
        BASE32_CODES.eachWithIndex { String letter, int i ->
            BASE32_CODES_DICT[letter] = i
        }
    }

    /**
     * Encode a Point as a hash string that is 9 characters long
     * @param pt The Point
     * @return A hash string
     */
    String encode(Point pt) {
        encode(pt, 9)
    }

    /**
     * Encode a Point as a hash string
     * @param pt The Point
     * @param autoEncode Whether to auto encode or not.  If false, encode using 9 characters, else calculate
     * the number of characters.
     * @return A hash string
     */
    String encode(Point pt, boolean autoEncode) {
        if (!autoEncode) {
            encode(pt)
        } else {
            int decSigFigsLat = "${pt.y}".split('.')[1].length()
            int decSigFigsLon = "${pt.x}".split('.')[1].length()
            int numberOfSigFigs = Math.max(decSigFigsLat, decSigFigsLon)
            int numberOfChars = SIGFIG_HASH_LENGTH[numberOfSigFigs];
            encode(pt, numberOfChars)
        }
    }

    /**
     * Encode a Point as a hash string
     * @param pt The Point
     * @param numberOfChars The number of characters
     * @return A hash string
     */
    String encode(Point pt, int numberOfChars) {
        List chars = []
        int bits = 0
        int bitsTotal = 0
        int hashValue = 0
        double maxLat = 90
        double minLat = -90
        double maxLon = 180
        double minLon = -180
        double mid
        while (chars.size() < numberOfChars) {
            if (bitsTotal % 2 == 0) {
                mid = (maxLon + minLon) / 2
                if (pt.x > mid) {
                    hashValue = (hashValue << 1) + 1
                    minLon = mid
                } else {
                    hashValue = (hashValue << 1) + 0
                    maxLon = mid
                }
            } else {
                mid = (maxLat + minLat) / 2
                if (pt.y > mid) {
                    hashValue = (hashValue << 1) + 1
                    minLat = mid
                } else {
                    hashValue = (hashValue << 1) + 0
                    maxLat = mid
                }
            }

            bits++
            bitsTotal++
            if (bits == 5) {
                String code = BASE32_CODES[hashValue]
                chars.add(code)
                bits = 0
                hashValue = 0
            }
        }
        chars.join('')
    }

    /**
     * Encode a Point as a hash long using 52 as the bit depth
     * @param pt The Point
     * @return A hash long
     */
    long encodeLong(Point pt) {
        encodeLong(pt, 52)
    }

    /**
     * Encode a Point as a hash long
     * @param pt The Point
     * @param bitDepth The bit depth
     * @return A hash long
     */
    long encodeLong(Point pt, int bitDepth) {
        int bitsTotal = 0
        double maxLat = 90
        double minLat = -90
        double maxLon = 180
        double minLon = -180
        double mid
        long combinedBits = 0
        while (bitsTotal < bitDepth) {
            combinedBits *= 2
            if (bitsTotal % 2 == 0) {
                mid = (maxLon + minLon) / 2
                if (pt.x > mid) {
                    combinedBits += 1
                    minLon = mid
                } else {
                    maxLon = mid
                }
            } else {
                mid = (maxLat + minLat) / 2
                if (pt.y > mid) {
                    combinedBits += 1
                    minLat = mid
                } else {
                    maxLat = mid
                }
            }
            bitsTotal++
        }
        combinedBits
    }

    /**
     * Decode a Point from the hash string
     * @param hashString The hash string
     * @return A Point
     */
    Point decode(String hashString) {
        decodeWithError(hashString).point
    }

    /**
     * Decode a Point from the hash string but also include the error
     * @param hashString The hash string
     * @return A Map containing the point and error with latitude and longitude keys
     */
    private Map decodeWithError(String hashString) {
        Bounds bounds = decodeBounds(hashString)
        double lat = (bounds.minY + bounds.maxY) / 2
        double lon = (bounds.minX + bounds.maxX) / 2
        double latErr = bounds.maxY - lat
        double lonErr = bounds.maxX - lon
        [
                point: new Point(lon, lat),
                error: [
                        latitude : latErr,
                        longitude: lonErr
                ]
        ]
    }

    /**
     * Decode a Bounds from a hash string
     * @param hashString The hash string
     * @return A Bounds
     */
    Bounds decodeBounds(String hashString) {
        boolean isLon = true
        double maxLat = 90
        double minLat = -90
        double maxLon = 180
        double minLon = -180
        double mid
        int i = 0
        int l = hashString.length()
        while (i < l) {
            String code = hashString.charAt(i).toLowerCase()
            int hashValue = BASE32_CODES_DICT[code]
            for (int bits = 4; bits >= 0; bits--) {
                int bit = (hashValue >> bits) & 1
                if (isLon) {
                    mid = (maxLon + minLon) / 2
                    if (bit == 1) {
                        minLon = mid
                    } else {
                        maxLon = mid
                    }
                } else {
                    mid = (maxLat + minLat) / 2
                    if (bit == 1) {
                        minLat = mid
                    } else {
                        maxLat = mid
                    }
                }
                isLon = !isLon
            }
            i++
        }
        new Bounds(minLon, minLat, maxLon, maxLat)
    }

    /**
     * Decode a Bounds from a hash long using 52 as the bit depth
     * @param hashLong The hash long
     * @return A Bounds
     */
    Bounds decodeBounds(long hashLong) {
        decodeBounds(hashLong, 52)
    }

    /**
     * Decode a Bounds from as hash long
     * @param hashLong The hash long
     * @param bitDepth The bit depth
     * @return A Bounds
     */
    Bounds decodeBounds(long hashLong, int bitDepth) {
        double maxLat = 90
        double minLat = -90
        double maxLon = 180
        double minLon = -180
        int step = bitDepth / 2
        for (int i = 0; i < step; i++) {

            int lonBit = getBit(hashLong, ((step - i) * 2) - 1)
            int latBit = getBit(hashLong, ((step - i) * 2) - 2)

            if (latBit == 0) {
                maxLat = (maxLat + minLat) / 2
            } else {
                minLat = (maxLat + minLat) / 2
            }

            if (lonBit == 0) {
                maxLon = (maxLon + minLon) / 2
            } else {
                minLon = (maxLon + minLon) / 2
            }
        }
        new Bounds(minLon, minLat, maxLon, maxLat)
    }

    private int getBit(long bits, int position) {
        (bits / Math.pow(2, position)) as long & 0x01
    }

    /**
     * Decode a Point from a hash long
     * @param hashLong The hash long
     * @return A Point
     */
    Point decode(long hashLong) {
        decodeWithError(hashLong).point
    }

    /**
     * Decode a Point from a hash long with error
     * @param hashLong The hash long
     * @return A Map containing the point and error with latitude and longitude keys
     */
    private Map decodeWithError(long hashLong) {
        decodeWithError(hashLong, 52)
    }

    /**
     * Decode a Point from a hash long with error for a given bit depth
     * @param hashLong The hash long
     * @param bitDepth The bit depth
     * @return A Map containing the point and error with latitude and longitude keys
     */
    private Map decodeWithError(long hashLong, int bitDepth) {
        Bounds bounds = decodeBounds(hashLong, bitDepth)
        double lat = (bounds.minY + bounds.maxY) / 2
        double lon = (bounds.minX + bounds.maxX) / 2
        double latErr = bounds.maxY - lat
        double lonErr = bounds.maxX - lon
        [
                point: new Point(lon, lat),
                error: [
                        latitude : latErr,
                        longitude: lonErr
                ]
        ]
    }

    /**
     * Calculate the neighbor of a hash string for the given direction
     * @param hashString The hash string
     * @param direction The Direction
     * @return A hash string
     */
    String neighbor(String hashString, Direction direction) {
        neighbor(hashString, direction, hashString.length())
    }

    /**
     * Calculate the neighbor of a hash string for the given direction
     * @param hashString The hash string
     * @param direction The Direction
     * @param numberOfChars The number of characters to use while encoding
     * @return A hash string
     */
    String neighbor(String hashString, Direction direction, int numberOfChars) {
        Map result = decodeWithError(hashString)
        Point pt = result.point
        double lat = pt.y + direction.lat() * result.error.latitude * 2
        double lon = pt.x + direction.lon() * result.error.longitude * 2
        encode(new Point(lon, lat), numberOfChars)
    }

    /**
     * Calculate the neighbor of a hash string with a number of steps in the latitude and longitude directions.
     * @param hashString The hash string
     * @param lonStep The number of steps along the longitude axis
     * @param latStep The number of steps along the latitude axis
     * @param numberOfChars The number of characters to use while encoding
     * @return A hash string
     */
    String neighbor(String hashString, int lonStep, int latStep, int numberOfChars) {
        Map result = decodeWithError(hashString)
        Point pt = result.point
        double lat = pt.y + latStep * result.error.latitude * 2
        double lon = pt.x + lonStep * result.error.longitude * 2
        encode(new Point(lon, lat), numberOfChars)
    }

    /**
     * Calculate the neighbor of a hash long in the given Direction
     * @param hashLong The hash long
     * @param direction The Direction
     * @return A hash long
     */
    long neighbor(long hashLong, Direction direction) {
        neighbor(hashLong, direction, 52)
    }

    /**
     * Calculate the neighbor of a hash long in the given Direction
     * @param hashLong The hash long
     * @param direction The Direction
     * @param bitDepth The bit depth to use while encoding
     * @return A hash long
     */
    long neighbor(long hashLong, Direction direction, int bitDepth) {
        Map result = decodeWithError(hashLong, bitDepth)
        Point pt = result.point
        Map error = result.error
        double lat = pt.y + direction.lat() * error.latitude * 2
        double lon = pt.x + direction.lon() * error.longitude * 2
        encodeLong(new Point(lon, lat), bitDepth)
    }

    /**
     * Calculate the neighbor of a hash long with a number of steps in the latitude and longitude directions.
     * @param hashLong The hash long
     * @param lonStep The number of steps along the longitude axis
     * @param latStep The number of steps along the latitude axis
     * @param bitDepth The bit depth to use while encoding
     * @return A hash long
     */
    long neighbor(long hashLong, int lonStep, int latStep, int bitDepth) {
        Map result = decodeWithError(hashLong, bitDepth)
        Point pt = result.point
        Map error = result.error
        double lat = pt.y + latStep * error.latitude * 2
        double lon = pt.x + lonStep * error.longitude * 2
        encodeLong(new Point(lon, lat), bitDepth)
    }

    /**
     * A Direction enum used when calculating neighbors
     */
    static enum Direction {
        NORTH(1, 0),
        NORTHEAST(1, 1),
        EAST(0, 1),
        SOUTHEAST(-1, 1),
        SOUTH(-1, 0),
        SOUTHWEST(-1, -1),
        WEST(0, -1),
        NORTHWEST(1, -1);

        private int lat
        private int lon

        Direction(int lat, int lon) {
            this.lat = lat
            this.lon = lon
        }

        int lat() {
            lat
        }

        int lon() {
            lon
        }
    }

    /**
     * Calculate all of the surrounding neighbors of the given hash string
     * @param hashString The hash string
     * @return A Map of Directions with hash strings
     */
    Map<Direction, String> neighbors(String hashString) {
        neighbors(hashString, hashString.length())
    }

    /**
     * Calculate all of the surrounding neighbors of the given hash string
     * @param hashString The hash string
     * @param numberOfChar The number of characters to use while encoding
     * @return A Map of Directions with hash strings
     */
    Map<Direction, String> neighbors(String hashString, int numberOfChar) {
        Map result = decodeWithError(hashString)
        Point pt = result.point
        double latErr = result.error.latitude * 2
        double lonErr = result.error.longitude * 2
        Map neighbors = [:]
        Direction.values().each { Direction d ->
            double y = pt.y + d.lat() * latErr
            double x = pt.x + d.lon() * lonErr
            neighbors[d] = encode(new Point(x, y), numberOfChar)
        }
        neighbors
    }

    /**
     * Calculate all of the surrounding neighbors of the given hash long
     * @param hashLong The hash long
     * @return A Map of Directions with hash strings
     */
    Map<Direction, Long> neighbors(long hashLong) {
        neighbors(hashLong, 52)
    }

    /**
     * Calculate all of the surrounding neighbors of the given hash long
     * @param hashLong The hash long
     * @param bitDepth The bit depth to use while encoding
     * @return A Map of Directions with hash strings
     */
    Map<Direction, Long> neighbors(long hashLong, int bitDepth) {
        Map result = decodeWithError(hashLong, bitDepth)
        Point pt = result.point
        double latErr = result.error.latitude * 2
        double lonErr = result.error.longitude * 2
        Map neighbors = [:]
        Direction.values().each { Direction d ->
            double lat = pt.y + d.lat() * latErr
            double lon = pt.x + d.lon() * lonErr
            neighbors[d] = encodeLong(new Point(lon, lat), bitDepth)
        }
        neighbors
    }

    /**
     * Calculate all of the hash strings in a Bounds
     * @param bounds The Bounds
     * @return A List of hash strings
     */
    List<String> bboxes(Bounds bounds) {
        bboxes(bounds, 9)
    }

    /**
     * Calculate all of the hash strings in a Bounds
     * @param bounds The Bounds
     * @param numberOfChars The number of characters to use while encoding
     * @return A List of hash strings
     */
    List<String> bboxes(Bounds bounds, int numberOfChars) {
        String hashSouthWest = encode(new Point(bounds.minX, bounds.minY), numberOfChars)
        String hashNorthEast = encode(new Point(bounds.maxX, bounds.maxY), numberOfChars)

        Map result = decodeWithError(hashSouthWest)

        double perLat = result.error.latitude * 2
        double perLon = result.error.longitude * 2

        Bounds boxSouthWest = decodeBounds(hashSouthWest)
        Bounds boxNorthEast = decodeBounds(hashNorthEast)

        long latStep = Math.round((boxNorthEast.minY - boxSouthWest.minY) / perLat)
        long lonStep = Math.round((boxNorthEast.minX - boxSouthWest.minX) / perLon)

        List<String> hashList = []
        for (int lat = 0; lat <= latStep; lat++) {
            for (int lon = 0; lon <= lonStep; lon++) {
                hashList.add(hashList.size(), neighbor(hashSouthWest, lon, lat, numberOfChars))
            }
        }
        hashList
    }

    /**
     * Calculate all of the hash longs in a Bounds
     * @param bounds The Bounds
     * @return A List of hash longs
     */
    List<Long> bboxesLong(Bounds bounds) {
        bboxesLong(bounds, 52)
    }

    /**
     * Calculate all of the hash longs in a Bounds
     * @param bounds The Bounds
     * @params bitDepth The bit depth to use while encoding
     * @return A List of hash longs
     */
    List<Long> bboxesLong(Bounds bounds, int bitDepth) {
        long hashSouthWest = encodeLong(new Point(bounds.minX, bounds.minY), bitDepth)
        long hashNorthEast = encodeLong(new Point(bounds.maxX, bounds.maxY), bitDepth)

        Map result = decodeWithError(hashSouthWest, bitDepth)

        double perLat = result.error.latitude * 2
        double perLon = result.error.longitude * 2

        Bounds boxSouthWest = decodeBounds(hashSouthWest, bitDepth)
        Bounds boxNorthEast = decodeBounds(hashNorthEast, bitDepth)

        long latStep = Math.round((boxNorthEast.minY - boxSouthWest.minY) / perLat)
        long lonStep = Math.round((boxNorthEast.minX - boxSouthWest.minX) / perLon)

        List<String> hashList = []
        for (int lat = 0; lat <= latStep; lat++) {
            for (int lon = 0; lon <= lonStep; lon++) {
                hashList.add(hashList.size(), neighbor(hashSouthWest, lon, lat, bitDepth))
            }
        }
        hashList
    }
}
