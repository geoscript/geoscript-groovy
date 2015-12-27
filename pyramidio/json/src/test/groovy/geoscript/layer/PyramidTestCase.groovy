package geoscript.layer

import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static junit.framework.Assert.assertEquals

class PyramidTestCase {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder()

    @Test void getFromJson() {
        Pyramid p1 = Pyramid.createGlobalMercatorPyramid()
        String json = p1.json
        Pyramid p2 = Pyramid.fromJson(json)
        assertEquals p1.proj, p2.proj
        assertEquals p1.bounds, p2.bounds
        assertEquals p1.origin, p2.origin
        assertEquals p1.tileWidth, p2.tileWidth
        assertEquals p1.tileHeight, p2.tileHeight
        assertEquals p1.grids, p2.grids
    }

    @Test void fromString() {
        String json = Pyramid.createGlobalMercatorPyramid().json
        Pyramid p = Pyramid.fromString(json)
        assertEquals Pyramid.createGlobalMercatorPyramid(), p
        File f = temporaryFolder.newFile("pyramid.json")
        f.text = json
        p = Pyramid.fromString(f.absolutePath)
        assertEquals Pyramid.createGlobalMercatorPyramid(), p
    }

}
