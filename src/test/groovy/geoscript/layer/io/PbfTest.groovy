package geoscript.layer.io

import geoscript.feature.Feature
import geoscript.feature.Field
import geoscript.feature.Schema
import geoscript.geom.Bounds
import geoscript.geom.Geometry
import geoscript.geom.Point
import geoscript.layer.Layer
import geoscript.layer.Pyramid
import geoscript.layer.Shapefile
import geoscript.layer.Tile
import geoscript.workspace.Memory
import geoscript.workspace.Workspace
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*

/**
 * The Pbf Unit Test
 * @author Jared Erickson
 */
class PbfTest {

    @Test void read() {
        URL url = getClass().getClassLoader().getResource("pbf/1/1/0.pbf")

        Pyramid pyramid = Pyramid.createGlobalMercatorPyramid()
        Bounds bounds = pyramid.bounds(new Tile(1, 1, 0))

        List<Layer> layers = Pbf.read(url.bytes, bounds)
        assertTrue layers.size() > 0
    }

    @Test void writeRead() {
        URL url = getClass().getClassLoader().getResource("states.shp")
        Layer layer = new Shapefile(new File(url.toURI()))

        Pyramid pyramid = Pyramid.createGlobalMercatorPyramid()
        Bounds bounds = pyramid.bounds(new Tile(5, 5, 20))

        byte[] bytes = Pbf.write([layer], bounds)
        assertTrue bytes.length > 0

        Layer pbfLayer = Pbf.read(bytes, bounds)
        assertTrue pbfLayer.count > 0
    }

    @Test void test() {

        Workspace workspace = new Memory()
        Schema schema = new Schema("cities", [
                new Field("geom", "Point", "EPSG:4326"),
                new Field("id", "Integer"),
                new Field("name", "String")
        ])
        Layer layer = workspace.create(schema)
        layer.add([
                geom: new Point(-122.3204, 47.6024),
                id: 1,
                name: "Seattle"
        ])
        layer.add([
                geom: new Point(-122.48416, 47.2619),
                id: 2,
                name: "Tacoma"
        ])

        Pyramid pyramid = Pyramid.createGlobalMercatorPyramid(origin: Pyramid.Origin.TOP_LEFT)
        Tile tile = new Tile(4,2,5)
        Bounds bounds = pyramid.bounds(tile)
        Bounds projectedBounds = bounds.reproject("EPSG:4326")
        Geometry projecteBoundsGeom = projectedBounds.geometry

        byte[] bytes = Pbf.write([layer], bounds)

        List<Layer> layers = Pbf.read(bytes, bounds)
        assertEquals(1, layers.size())
        assertEquals(2, layers[0].count)
    }

}
