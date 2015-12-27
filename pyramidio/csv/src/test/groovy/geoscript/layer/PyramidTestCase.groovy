package geoscript.layer

import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static junit.framework.Assert.*
class PyramidTestCase {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder()

    @Test void getFromCsv() {
        Pyramid p1 = Pyramid.createGlobalMercatorPyramid()
        String csv = p1.csv
        Pyramid p2 = Pyramid.fromCsv(csv)
        assertEquals p1.proj, p2.proj
        assertEquals p1.bounds, p2.bounds
        assertEquals p1.origin, p2.origin
        assertEquals p1.tileWidth, p2.tileWidth
        assertEquals p1.tileHeight, p2.tileHeight
        assertEquals p1.grids, p2.grids
    }

    @Test void fromString() {
        String csv = Pyramid.createGlobalMercatorPyramid().csv
        Pyramid p = Pyramid.fromString(csv)
        assertEquals Pyramid.createGlobalMercatorPyramid(), p
        File f = temporaryFolder.newFile("pyramid.csv")
        f.text = csv
        p = Pyramid.fromString(f.absolutePath)
        assertEquals Pyramid.createGlobalMercatorPyramid(), p
    }

}
