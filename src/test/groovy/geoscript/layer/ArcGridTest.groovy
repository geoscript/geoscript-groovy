package geoscript.layer

import geoscript.AssertUtil
import geoscript.geom.Bounds
import geoscript.proj.Projection
import org.apache.commons.io.input.ReaderInputStream
import org.geotools.util.factory.GeoTools
import org.geotools.util.factory.Hints
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import static org.junit.jupiter.api.Assertions.*

/**
 * The ArcGrid Unit Test
 * @author Jared Erickson
 */
class ArcGridTest {

    @TempDir
    private File folder

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
        File destFile = new File(folder, "raster.asc")
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

    @Test void setValues() {

        // Create a simple Raster
        Bounds bounds = new Bounds(0, 0, 7, 5, "EPSG:4326")
        List data = [
                [0,0,0,0,0,0,0],
                [0,1,1,1,1,1,0],
                [0,1,2,3,2,1,0],
                [0,1,1,1,1,1,0],
                [0,0,0,0,0,0,0]
        ]
        Raster raster = new Raster(data, bounds)

        // Create a File to hold the Raster
        File file = new File(folder, "dem.asc")
        file.delete()

        // Write the Raster to a ArcGrid File
        Format.getFormat(file).write(raster)

        // Read the Raster from the ArcGrid File
        raster = Format.getFormat(file).read()

        // Check existing value, set new value, check new value
        assertEquals 1, raster.eval([1,1])[0], 0.1
        raster.setValue([1,1],5)
        assertEquals 5, raster.eval([1,1])[0], 0.1

        // Check existing value, set new value, check new value
        assertEquals 3, raster[[3,2]][0], 0.1
        raster[[3,2]] = 10
        assertEquals 10, raster[[3,2]][0], 0.1

        // Write the modified Raster back to File
        Format.getFormat(file).write(raster)

        // Read the Raster from the ArcGrid File
        raster = Format.getFormat(file).read()

        // Check existing values
        assertEquals 5, raster.eval([1,1])[0], 0.1
        assertEquals 10, raster[[3,2]][0], 0.1
    }

    @Test void rasterMath() {

        // Create DSM Raster
        File dsmFile = new File(folder, "DSM_SolarTirol_small.asc")
        dsmFile.delete()
        Bounds bounds = new Bounds(0, 0, 7, 5, "EPSG:4326")
        List data = [
                [10,10,10,10,10,10,10],
                [10,11,11,11,11,11,10],
                [10,11,12,13,12,11,10],
                [10,11,11,11,11,11,10],
                [10,10,10,10,10,10,10]
        ]
        Raster dsmRaster = new Raster(data, bounds)
        Format.getFormat(dsmFile).write(dsmRaster)

        // Create DTM Raster
        File dtmFile = new File(folder, "DTM_SolarTirol_small.asc")
        dtmFile.delete()
        bounds = new Bounds(0, 0, 7, 5, "EPSG:4326")
        data = [
                [0,0,0,0,0,0,0],
                [0,1,1,1,1,1,0],
                [0,1,2,3,2,1,0],
                [0,1,1,1,1,1,0],
                [0,0,0,0,0,0,0]
        ]
        Raster dtmRaster = new Raster(data, bounds)
        Format.getFormat(dtmFile).write(dtmRaster)

        // Create the output CHM Raster
        File chmPath = new File(folder, "CHM.asc")
        chmPath.delete()

        // Read the Rasters
        dtmRaster = Format.getFormat(dtmFile).read()
        dsmRaster = Format.getFormat(dsmFile).read()
        Raster outChmRaster = Format.getFormat(dsmFile).read()

        int cols = dtmRaster.cols
        int rows = dtmRaster.rows
        double NO_VALUE = -9999.0

        for ( row in 0..(rows-1)) {
            for ( col in 0..(cols-1)) {
                List position = [col, row]
                double dtmValue = dtmRaster.getValue(position)
                double dsmValue = dsmRaster.getValue(position)
                if(dtmValue != NO_VALUE && dsmValue != NO_VALUE){
                    double chmValue = dsmValue - dtmValue
                    outChmRaster.setValue(position, chmValue)
                    assertEquals(chmValue, outChmRaster.getValue(position), 0.1)
                }
            }
        }

        Format.getFormat(chmPath).write(outChmRaster)
        outChmRaster = Format.getFormat(chmPath).read()
        assertEquals(10.0, outChmRaster.getValue([1,1]), 0.1)
        assertEquals(10.0, outChmRaster.getValue([3,3]), 0.1)
        assertEquals(10.0, outChmRaster.getValue([1,4]), 0.1)
        assertEquals(10.0, outChmRaster.getValue([2,3]), 0.1)
        assertEquals(10.0, outChmRaster.getValue([4,2]), 0.1)

    }

}
