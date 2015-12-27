package geoscript.layer

import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static junit.framework.Assert.assertEquals

class PyramidTestCase {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder()

    @Test void getFromXml() {
        Pyramid p1 = Pyramid.createGlobalMercatorPyramid()
        String xml = p1.xml
        Pyramid p2 = Pyramid.fromXml(xml)
        assertEquals p1.proj, p2.proj
        assertEquals p1.bounds, p2.bounds
        assertEquals p1.origin, p2.origin
        assertEquals p1.tileWidth, p2.tileWidth
        assertEquals p1.tileHeight, p2.tileHeight
        assertEquals p1.grids, p2.grids
    }

    @Test void fromString() {
        String xml = Pyramid.createGlobalMercatorPyramid().xml
        Pyramid p = Pyramid.fromString(xml)
        assertEquals Pyramid.createGlobalMercatorPyramid(), p
        File f = temporaryFolder.newFile("pyramid.xml")
        f.text = xml
        p = Pyramid.fromString(f.absolutePath)
        assertEquals Pyramid.createGlobalMercatorPyramid(), p
    }

}
