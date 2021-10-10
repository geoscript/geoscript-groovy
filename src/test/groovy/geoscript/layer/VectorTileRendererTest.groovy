package geoscript.layer

import geoscript.layer.io.GeoJSONWriter
import geoscript.layer.io.KmlWriter
import geoscript.layer.io.MvtWriter
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import static org.junit.jupiter.api.Assertions.assertNotNull
import static org.junit.jupiter.api.Assertions.assertTrue

/**
 * The VectorTileRenderer Unit Test
 * @author Jared Erickson
 */
class VectorTileRendererTest {

    @TempDir
    private File folder

    @Test
    void renderGeoJson() {
        Shapefile shp = new Shapefile(new File(getClass().getClassLoader().getResource("states.shp").toURI()))
        List fields = [shp.schema.get("STATE_NAME")]
        File dir = new File(folder, "states")
        dir.mkdir()
        Pyramid pyramid = Pyramid.createGlobalMercatorPyramid()
        geoscript.layer.io.Writer writer = new GeoJSONWriter()
        VectorTileRenderer renderer = new VectorTileRenderer(writer, shp, fields)
        byte[] data = renderer.render(pyramid.bounds(new Tile(5, 5, 20)))
        assertNotNull data
        assertTrue data.length > 0
        String json = new String(data)
        assertTrue json.startsWith("{\"type\":\"FeatureCollection\"")
    }

    @Test
    void renderKml() {
        Shapefile shp = new Shapefile(new File(getClass().getClassLoader().getResource("states.shp").toURI()))
        List fields = [shp.schema.get("STATE_NAME")]
        File dir = new File(folder, "states")
        dir.mkdir()
        Pyramid pyramid = Pyramid.createGlobalMercatorPyramid()
        geoscript.layer.io.Writer writer = new KmlWriter()
        VectorTileRenderer renderer = new VectorTileRenderer(writer, shp, fields)
        byte[] data = renderer.render(pyramid.bounds(new Tile(5, 5, 20)))
        assertNotNull data
        assertTrue data.length > 0
        String kml = new String(data)
        assertTrue kml.startsWith("<kml:kml")
    }

    @Test
    void renderMvt() {
        Shapefile shp = new Shapefile(new File(getClass().getClassLoader().getResource("states.shp").toURI()))
        List fields = [shp.schema.get("STATE_NAME")]
        File dir = new File(folder, "states")
        dir.mkdir()
        Pyramid pyramid = Pyramid.createGlobalMercatorPyramid()
        geoscript.layer.io.Writer writer = new MvtWriter()
        VectorTileRenderer renderer = new VectorTileRenderer(writer, shp, fields)
        byte[] data = renderer.render(pyramid.bounds(new Tile(5, 5, 20)))
        assertNotNull data
        assertTrue data.length > 0
    }
}
