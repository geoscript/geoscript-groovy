package geoscript.layer

import geoscript.AssertUtil
import geoscript.proj.Projection
import org.apache.commons.io.input.ReaderInputStream
import org.geotools.factory.GeoTools
import org.geotools.factory.Hints
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.assertNotNull

/**
 * The ArcGrid Unit Test
 * @author Jared Erickson
 */
class ArcGridTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Test
    void readFromFile() {
        File file = new File(getClass().getClassLoader().getResource("raster.asc").toURI())
        assertNotNull(file)
        ArcGrid arcGrid = new ArcGrid(file)
        Raster raster = arcGrid.read()
        assertNotNull(raster)
    }

    @Test
    void readFromGrassFile() {
        File file = new File(getClass().getClassLoader().getResource("grass.arx").toURI())
        assertNotNull(file)
        ArcGrid arcGrid = new ArcGrid(file)
        Raster raster = arcGrid.read()
        assertNotNull(raster)
    }

    @Test
    void readFromString() {
        File file = new File(getClass().getClassLoader().getResource("raster.asc").toURI())
        assertNotNull(file)
        ArcGrid arcGrid = new ArcGrid(file.text)
        Raster raster = arcGrid.read()
        assertNotNull(raster)
    }

    @Test
    void readFromInputStream() {
        File file = new File(getClass().getClassLoader().getResource("raster.asc").toURI())
        assertNotNull(file)
        ArcGrid arcGrid = new ArcGrid(new ReaderInputStream(new StringReader(file.text)))
        Raster raster = arcGrid.read(new Projection("EPSG:4326"))
        assertNotNull(raster)
    }

    @Test
    void readFromInputStreamWithHints() {
        File file = new File(getClass().getClassLoader().getResource("raster.asc").toURI())
        assertNotNull(file)
        ArcGrid arcGrid = new ArcGrid(new ReaderInputStream(new StringReader(file.text)))
        Hints hints = GeoTools.getDefaultHints()
        hints.put(Hints.DEFAULT_COORDINATE_REFERENCE_SYSTEM, new Projection("EPSG:4326").crs)
        Raster raster = arcGrid.read(hints)
        assertNotNull(raster)
    }

    @Test
    void writeToFile() {
        File file = new File(getClass().getClassLoader().getResource("raster.asc").toURI())
        assertNotNull(file)
        ArcGrid arcGrid = new ArcGrid(file)
        Raster raster = arcGrid.read()
        assertNotNull(raster)
        File destFile = folder.newFile("raster.asc")
        ArcGrid destArcGrid = new ArcGrid(destFile)
        destArcGrid.write(raster)
        String str = destFile.text
        AssertUtil.assertStringsEqual("""NCOLS 4
NROWS 6
XLLCORNER 0.0
YLLCORNER 0.0
CELLSIZE 50.0
NODATA_VALUE -9999.0
-9999.0 -9999.0 5.0 2.0
-9999.0 20.0 100.0 36.0
3.0 8.0 35.0 10.0
32.0 42.0 50.0 6.0
88.0 75.0 27.0 9.0
13.0 5.0 1.0 -9999.0
""", str)

    }

    @Test
    void writeToString() {
        File file = new File(getClass().getClassLoader().getResource("raster.asc").toURI())
        assertNotNull(file)
        ArcGrid arcGrid = new ArcGrid(file)
        Raster raster = arcGrid.read()
        assertNotNull(raster)
        String str = arcGrid.writeToString(raster)
        assertNotNull(str)
        AssertUtil.assertStringsEqual("""NCOLS 4
NROWS 6
XLLCORNER 0.0
YLLCORNER 0.0
CELLSIZE 50.0
NODATA_VALUE -9999.0
-9999.0 -9999.0 5.0 2.0
-9999.0 20.0 100.0 36.0
3.0 8.0 35.0 10.0
32.0 42.0 50.0 6.0
88.0 75.0 27.0 9.0
13.0 5.0 1.0 -9999.0
""", str)
    }

    @Test
    void writeToGrassString() {
        File file = new File(getClass().getClassLoader().getResource("raster.asc").toURI())
        assertNotNull(file)
        ArcGrid arcGrid = new ArcGrid(file)
        Raster raster = arcGrid.read()
        assertNotNull(raster)
        String str = arcGrid.writeToString(raster, "grass")
        assertNotNull(str)
        AssertUtil.assertStringsEqual("""NORTH: 300.0
SOUTH: 0.0
EAST: 200.0
WEST: 0.0
ROWS: 6
COLS: 4
* * 5.0 2.0
* 20.0 100.0 36.0
3.0 8.0 35.0 10.0
32.0 42.0 50.0 6.0
88.0 75.0 27.0 9.0
13.0 5.0 1.0 *
""", str)
    }
}
