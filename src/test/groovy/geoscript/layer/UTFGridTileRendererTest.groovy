package geoscript.layer

import geoscript.FileUtil
import groovy.json.JsonSlurper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import static org.junit.jupiter.api.Assertions.*

/**
 * The UTFGridTileRenderer Unit Test
 * @author Jared Erickson
 */
class UTFGridTileRendererTest {

    @TempDir
    File folder

    @Test
    void render() {
        Shapefile shp = new Shapefile(new File(getClass().getClassLoader().getResource("states.shp").toURI()))
        File dir = FileUtil.createDir(folder, "states")
        UTFGrid utf = new UTFGrid(dir)
        UTFGridTileRenderer renderer = new UTFGridTileRenderer(utf, shp, [shp.schema.get("STATE_NAME")])
        byte[] data = renderer.render(utf.pyramid.bounds(utf.get(0, 0, 0)))
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
