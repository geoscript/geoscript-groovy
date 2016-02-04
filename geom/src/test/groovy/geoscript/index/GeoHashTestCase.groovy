package geoscript.index

import geoscript.geom.Bounds
import geoscript.geom.Point
import org.junit.Test

import static org.junit.Assert.assertEquals

/**
 * The GeoHash Unit Test
 * @author Jared Erickson
 */
class GeoHashTestCase {

    @Test
    void encodeString() {
        GeoHash geohash = new GeoHash()
        assertEquals "ww8p1r4t8", geohash.encode(new Point(112.5584, 37.8324))
        assertEquals "wte", geohash.encode(new Point(117, 32), 3)
    }

    @Test
    void encodeLong() {
        GeoHash geohash = new GeoHash()
        assertEquals 4064984913515641, geohash.encodeLong(new Point(112.5584, 37.8324))
        assertEquals 63515389273681, geohash.encodeLong(new Point(112.5584, 37.8324), 46)
    }

    @Test
    void decodePointString() {
        GeoHash geohash = new GeoHash()
        assertEquals new Point(112.55838632583618, 37.83238649368286), geohash.decode("ww8p1r4t8")
    }

    @Test
    void decodePointLong() {
        GeoHash geohash = new GeoHash()
        assertEquals new Point(112.55839973688126, 37.83240124583244), geohash.decode(4064984913515641)
    }

    @Test
    void decodeBoundsString() {
        GeoHash geohash = new GeoHash()
        Bounds bounds = geohash.decodeBounds("ww8p1r4t8")
        assertEquals new Bounds(112.55836486816406, 37.83236503601074, 112.5584077835083, 37.83240795135498), bounds
    }

    @Test
    void decodeBoundsLong() {
        GeoHash geohash = new GeoHash()
        Bounds bounds = geohash.decodeBounds(4064984913515641)
        assertEquals new Bounds(112.55839705467224, 37.832399904727936, 112.55840241909027, 37.83240258693695), bounds
    }

    @Test
    void neighborString() {
        GeoHash geohash = new GeoHash()
        assertEquals "dqcjw", geohash.neighbor("dqcjq", GeoHash.Direction.NORTH)
        assertEquals "dqcjj", geohash.neighbor("DQCJQ", GeoHash.Direction.SOUTHWEST)
    }

    @Test
    void neighborLong() {
        GeoHash geohash = new GeoHash()
        assertEquals 1702789520, geohash.neighbor(1702789509, GeoHash.Direction.NORTH, 32)
        assertEquals 27898503327465, geohash.neighbor(27898503327470, GeoHash.Direction.SOUTHWEST, 46)
    }

    @Test
    void neighborsString() {
        GeoHash geohash = new GeoHash()
        Map neighbors = geohash.neighbors('dqcjq')
        assertEquals "dqcjw", neighbors[GeoHash.Direction.NORTH]
        assertEquals "dqcjx", neighbors[GeoHash.Direction.NORTHEAST]
        assertEquals "dqcjr", neighbors[GeoHash.Direction.EAST]
        assertEquals "dqcjp", neighbors[GeoHash.Direction.SOUTHEAST]
        assertEquals "dqcjn", neighbors[GeoHash.Direction.SOUTH]
        assertEquals "dqcjj", neighbors[GeoHash.Direction.SOUTHWEST]
        assertEquals "dqcjm", neighbors[GeoHash.Direction.WEST]
        assertEquals "dqcjt", neighbors[GeoHash.Direction.NORTHWEST]
    }

    @Test
    void neighborsLong() {
        GeoHash geohash = new GeoHash()
        Map neighbors = geohash.neighbors(1702789509, 32)
        assertEquals 1702789520, neighbors[GeoHash.Direction.NORTH]
        assertEquals 1702789522, neighbors[GeoHash.Direction.NORTHEAST]
        assertEquals 1702789511, neighbors[GeoHash.Direction.EAST]
        assertEquals 1702789510, neighbors[GeoHash.Direction.SOUTHEAST]
        assertEquals 1702789508, neighbors[GeoHash.Direction.SOUTH]
        assertEquals 1702789422, neighbors[GeoHash.Direction.SOUTHWEST]
        assertEquals 1702789423, neighbors[GeoHash.Direction.WEST]
        assertEquals 1702789434, neighbors[GeoHash.Direction.NORTHWEST]
    }

    @Test
    void bboxesString() {
        GeoHash geohash = new GeoHash()
        List bboxes = geohash.bboxes(new Bounds(120, 30, 120.0001, 30.0001), 8)
        assertEquals 2, bboxes.size()
        assertEquals "wtm6dtm6", bboxes[0]
        assertEquals "wtm6dtm7", bboxes[1]
    }

    @Test
    void bboxesLong() {
        GeoHash geohash = new GeoHash()
        List bboxes = geohash.bboxesLong(new Bounds(120, 30, 120.0001, 30.0001), 50)
        assertEquals 190, bboxes.size()
        assertEquals 1013309916158361, bboxes[0]
        assertEquals 1013309916159519, bboxes[189]
    }

}
