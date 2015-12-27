package geoscript.layer

import geoscript.layer.io.GeoJSONWriter
import geoscript.layer.io.KmlWriter
import geoscript.layer.io.MvtWriter
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue

/**
 * The VectorTileRenderer Unit Test
 * @author Jared Erickson
 */
class VectorTileRendererTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Test
    void renderGeoJson() {
        Shapefile shp = new Shapefile(new File(getClass().getClassLoader().getResource("states.shp").toURI()))
        List fields = [shp.schema.get("STATE_NAME")]
        File dir = folder.newFolder("states")
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
        File dir = folder.newFolder("states")
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
        File dir = folder.newFolder("states")
        Pyramid pyramid = Pyramid.createGlobalMercatorPyramid()
        geoscript.layer.io.Writer writer = new MvtWriter()
        VectorTileRenderer renderer = new VectorTileRenderer(writer, shp, fields)
        byte[] data = renderer.render(pyramid.bounds(new Tile(5, 5, 20)))
        assertNotNull data
        assertTrue data.length > 0
    }
}
