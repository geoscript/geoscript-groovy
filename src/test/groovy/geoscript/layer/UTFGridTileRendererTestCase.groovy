package geoscript.layer

import geoscript.style.Fill
import geoscript.style.Stroke
import groovy.json.JsonSlurper
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.*

/**
 * The UTFGridTileRenderer Unit Test
 * @author Jared Erickson
 */
class UTFGridTileRendererTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Test void render() {
        Shapefile shp = new Shapefile(new File(getClass().getClassLoader().getResource("states.shp").toURI()))
        File dir = folder.newFolder("states")
        UTFGrid utf = new UTFGrid(dir)
        UTFGridTileRenderer renderer = new UTFGridTileRenderer(utf,shp,[shp.schema.get("STATE_NAME")])
        byte[] data = renderer.render(utf.pyramid.bounds(utf.get(0,0,0)))
        assertNotNull data
        assertTrue data.length > 0
        String str = new String(data)
        def json = new JsonSlurper().parseText(str)
        assertNotNull json
        assertNotNull json['grid']
        assertNotNull json['keys']
        assertNotNull json['data']
    }

}
