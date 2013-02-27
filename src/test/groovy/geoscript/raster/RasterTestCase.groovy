package geoscript.raster

import geoscript.geom.*
import geoscript.proj.Projection
import org.junit.Test
import static org.junit.Assert.*
import geoscript.layer.Layer
import geoscript.layer.Shapefile
import geoscript.workspace.Directory
import org.geotools.gce.geotiff.GeoTiffFormat
import geoscript.workspace.Memory
import geoscript.feature.Field

/**
 * The Raster unit test
 */
class RasterTestCase {
	
    @Test void raster() {
        File file = new File(getClass().getClassLoader().getResource("alki.tif").toURI())
        assertNotNull(file)

        GeoTIFF geoTIFF = new GeoTIFF()
        Raster raster = geoTIFF.read(file)
        assertNotNull(raster)

        assertEquals("GeoTIFF", geoTIFF.format)
        assertEquals("EPSG:2927", raster.proj.id)

        Bounds bounds = raster.bounds
        assertEquals(1166191.0260847565, bounds.minX, 0.0000000001)
        assertEquals(1167331.8522748263, bounds.maxX, 0.0000000001)
        assertEquals(822960.0090852415, bounds.minY, 0.0000000001)
        assertEquals(824226.3820666744, bounds.maxY, 0.0000000001)
        assertEquals("EPSG:2927", bounds.proj.id)

        def (double w, double h) = raster.size
        assertEquals(760, w, 0.1)
        assertEquals(843, h, 0.1)

        List<Band> bands = raster.bands
        assertEquals(3, bands.size())
        assertEquals("RED_BAND", bands[0].toString())
        assertEquals("GREEN_BAND", bands[1].toString())
        assertEquals("BLUE_BAND", bands[2].toString())

        def (int bw, int bh) = raster.blockSize
        assertEquals(761, bw)
        assertEquals(3, bh)

        def (double pw, double ph) = raster.pixelSize
        assertEquals(1.5010870921970836, pw, 0.000000000001)
        assertEquals(1.5022218047840352, ph, 0.000000000001)
    }

    @Test void crop() {
        File file = new File(getClass().getClassLoader().getResource("alki.tif").toURI())
        assertNotNull(file)

        GeoTIFF geoTIFF = new GeoTIFF()
        Raster raster = geoTIFF.read(file)
        assertNotNull(raster)

        Bounds bounds = new Bounds(1166191.0260847565, 822960.0090852415, 1166761.4391797914, 823593.1955759579)
        Raster cropped = raster.crop(bounds)
        assertNotNull(cropped)
        assertEquals(bounds.minX, cropped.bounds.minX, 1d)
        assertEquals(bounds.maxX, cropped.bounds.maxX, 1d)
        assertEquals(bounds.minY, cropped.bounds.minY, 1d)
        assertEquals(bounds.maxY, cropped.bounds.maxY, 1d)
    }

    @Test void reproject() {
        File file = new File(getClass().getClassLoader().getResource("alki.tif").toURI())
        assertNotNull(file)

        GeoTIFF geoTIFF = new GeoTIFF()
        Raster raster = geoTIFF.read(file)
        assertNotNull(raster)
        assertEquals("EPSG:2927", raster.proj.id)

        Raster reprojected = raster.reproject(new Projection("EPSG:4326"))
        assertNotNull(reprojected)
        assertEquals("EPSG:4326", reprojected.proj.id)
    }

    @Test void write() {
        // Read a GeoTIFF
        File file = new File(getClass().getClassLoader().getResource("alki.tif").toURI())
        assertNotNull(file)
        GeoTIFF geoTIFF = new GeoTIFF()
        Raster raster = geoTIFF.read(file)
        assertNotNull(raster)
        assertEquals("EPSG:2927", raster.proj.id)

        // Reproject it to 4326
        Raster reprojected = raster.reproject(new Projection("EPSG:4326"))
        assertNotNull(reprojected)
        assertEquals("EPSG:4326", reprojected.proj.id)

        // Write the reprojected GeoTIFF to a file
        File file1 = File.createTempFile("reprojected_raster",".tif")
        println(file1)
        geoTIFF.write(reprojected, file1)

        // Read the written reprojected GeoTIFF
        Raster raster2 = geoTIFF.read(file1)
        assertNotNull(raster2)
        assertEquals("EPSG:4326", raster2.proj.id)
    }

    @Test void evalulate() {
        File file = new File(getClass().getClassLoader().getResource("alki.tif").toURI())
        assertNotNull(file)
        GeoTIFF geoTIFF = new GeoTIFF()
        Raster raster = geoTIFF.read(file)
        assertNotNull(raster)

        def value = raster.evaluate(new Point(1166761.4391797914, 823593.1955759579))
        assertEquals(-30, value[0])
        assertEquals(-22, value[1])
        assertEquals(-46, value[2])
    }

    @Test void add() {
        Bounds bounds = new Bounds(0, 0, 7, 5, "EPSG:4326")
        List data1 = [
            [0,0,0,0,0,0,0],
            [0,1,1,1,1,1,0],
            [0,1,2,3,2,1,0],
            [0,1,1,1,1,1,0],
            [0,0,0,0,0,0,0]
        ]
        Raster raster1 = new Raster(data1, bounds)

        List data2 = [
            [1,1,1,1,1,1,1],
            [1,2,2,2,2,2,1],
            [1,2,3,4,3,2,1],
            [1,2,2,2,2,2,1],
            [1,1,1,1,1,1,1]
        ]
        Raster raster2 = new Raster(data2, bounds)

        Raster raster3 = raster1 + raster2
        assertEquals 1, raster3.evaluate(new Point(0.5,0.5))[0], 0.1
        assertEquals 3, raster3.evaluate(new Point(1.5,1.5))[0], 0.1
        assertEquals 5, raster3.evaluate(new Point(2.5,2.5))[0], 0.1
        assertEquals 7, raster3.evaluate(new Point(3.5,2.5))[0], 0.1
    }

    @Test void addConstant() {
        Bounds bounds = new Bounds(0, 0, 7, 5, "EPSG:4326")
        List data1 = [
                [0,0,0,0,0,0,0],
                [0,1,1,1,1,1,0],
                [0,1,2,3,2,1,0],
                [0,1,1,1,1,1,0],
                [0,0,0,0,0,0,0]
        ]
        Raster raster1 = new Raster(data1, bounds)
        Raster raster2 = raster1 + 5.0
        assertEquals 5, raster2.evaluate(new Point(0.5,0.5))[0], 0.1
        assertEquals 6, raster2.evaluate(new Point(1.5,1.5))[0], 0.1
        assertEquals 7, raster2.evaluate(new Point(2.5,2.5))[0], 0.1
        assertEquals 8, raster2.evaluate(new Point(3.5,2.5))[0], 0.1
    }

    @Test void multiply() {
        Bounds bounds = new Bounds(0, 0, 7, 5, "EPSG:4326")
        List data1 = [
            [0,0,0,0,0,0,0],
            [0,1,1,1,1,1,0],
            [0,1,2,3,2,1,0],
            [0,1,1,1,1,1,0],
            [0,0,0,0,0,0,0]
        ]
        Raster raster1 = new Raster(data1, bounds)

        List data2 = [
            [1,1,1,1,1,1,1],
            [1,2,2,2,2,2,1],
            [1,2,3,4,3,2,1],
            [1,2,2,2,2,2,1],
            [1,1,1,1,1,1,1]
        ]
        Raster raster2 = new Raster(data2, bounds)

        Raster raster3 = raster1 * raster2
        assertEquals 0, raster3.evaluate(new Point(0.5,0.5))[0], 0.1
        assertEquals 2, raster3.evaluate(new Point(1.5,1.5))[0], 0.1
        assertEquals 6, raster3.evaluate(new Point(2.5,2.5))[0], 0.1
        assertEquals 12, raster3.evaluate(new Point(3.5,2.5))[0], 0.1
    }

    @Test void multiplyConstant() {
        Bounds bounds = new Bounds(0, 0, 7, 5, "EPSG:4326")
        List data1 = [
                [0,0,0,0,0,0,0],
                [0,1,1,1,1,1,0],
                [0,1,2,3,2,1,0],
                [0,1,1,1,1,1,0],
                [0,0,0,0,0,0,0]
        ]
        Raster raster1 = new Raster(data1, bounds)
        Raster raster2 = raster1 * 5.0

        assertEquals 0, raster2.evaluate(new Point(0.5,0.5))[0], 0.1
        assertEquals 5, raster2.evaluate(new Point(1.5,1.5))[0], 0.1
        assertEquals 10, raster2.evaluate(new Point(2.5,2.5))[0], 0.1
        assertEquals 15, raster2.evaluate(new Point(3.5,2.5))[0], 0.1
    }

    @Test void divide() {

        Bounds bounds = new Bounds(0, 0, 7, 5, "EPSG:4326")
        List data1 = [
                [0,0,0,0,0,0,0],
                [0,1,1,1,1,1,0],
                [0,1,2,3,2,1,0],
                [0,1,1,1,1,1,0],
                [0,0,0,0,0,0,0]
        ]
        Raster raster1 = new Raster(data1, bounds)

        List data2 = [
                [1,1,1,1,1,1,1],
                [1,2,2,2,2,2,1],
                [1,2,3,4,3,2,1],
                [1,2,2,2,2,2,1],
                [1,1,1,1,1,1,1]
        ]
        Raster raster2 = new Raster(data2, bounds)

        Raster raster3 = raster1 / raster2

        assertEquals 0, raster3.evaluate(new Point(0.5,0.5))[0], 0.1
        assertEquals 0.5, raster3.evaluate(new Point(1.5,1.5))[0], 0.1
        assertEquals 0.6667, raster3.evaluate(new Point(2.5,2.5))[0], 0.1
        assertEquals 0.75, raster3.evaluate(new Point(3.5,2.5))[0], 0.1
    }

    @Test void divideConstant() {
        Bounds bounds = new Bounds(0, 0, 7, 5, "EPSG:4326")
        List data1 = [
                [0,0,0,0,0,0,0],
                [0,1,1,1,1,1,0],
                [0,1,2,3,2,1,0],
                [0,1,1,1,1,1,0],
                [0,0,0,0,0,0,0]
        ]
        Raster raster1 = new Raster(data1, bounds)
        Raster raster2 = raster1 / 1.2

        assertEquals 0, raster2.evaluate(new Point(0.5,0.5))[0], 0.1
        assertEquals 0.833, raster2.evaluate(new Point(1.5,1.5))[0], 0.1
        assertEquals 1.666, raster2.evaluate(new Point(2.5,2.5))[0], 0.1
        assertEquals 2.5, raster2.evaluate(new Point(3.5,2.5))[0], 0.1
    }

    @Test void minus() {

        Bounds bounds = new Bounds(0, 0, 7, 5, "EPSG:4326")
        List data1 = [
                [0,0,0,0,0,0,0],
                [0,1,1,1,1,1,0],
                [0,1,2,3,2,1,0],
                [0,1,1,1,1,1,0],
                [0,0,0,0,0,0,0]
        ]
        Raster raster1 = new Raster(data1, bounds)
        println "Raster 1: ${raster1.size}"

        List data2 = [
                [1,1,1,1,1,1,1],
                [1,2,2,2,2,2,1],
                [1,2,3,4,3,2,1],
                [1,2,2,2,2,2,1],
                [1,1,1,1,1,1,1]
        ]
        Raster raster2 = new Raster(data2, bounds)
        println "Raster 2: ${raster2.size}"

        Raster raster3 = raster1 - raster2

        assertEquals(-1.0, raster3.evaluate(new Point(0.5,0.5))[0], 0.1)
        assertEquals(-1.0, raster3.evaluate(new Point(1.5,1.5))[0], 0.1)
        assertEquals(-1.0, raster3.evaluate(new Point(2.5,2.5))[0], 0.1)
        assertEquals(-1.0, raster3.evaluate(new Point(3.5,2.5))[0], 0.1)
    }

    @Test void minusConstant() {
        Bounds bounds = new Bounds(0, 0, 7, 5, "EPSG:4326")
        List data1 = [
                [0,0,0,0,0,0,0],
                [0,1,1,1,1,1,0],
                [0,1,2,3,2,1,0],
                [0,1,1,1,1,1,0],
                [0,0,0,0,0,0,0]
        ]
        Raster raster1 = new Raster(data1, bounds)
        Raster raster2 = raster1 - 0.5

        assertEquals(-0.5, raster2.evaluate(new Point(0.5,0.5))[0], 0.1)
        assertEquals 0.5, raster2.evaluate(new Point(1.5,1.5))[0], 0.1
        assertEquals 1.5, raster2.evaluate(new Point(2.5,2.5))[0], 0.1
        assertEquals 2.5, raster2.evaluate(new Point(3.5,2.5))[0], 0.1
    }

    @Test void minusFromConstant() {
        Bounds bounds = new Bounds(0, 0, 7, 5, "EPSG:4326")
        List data1 = [
                [0,0,0,0,0,0,0],
                [0,1,1,1,1,1,0],
                [0,1,2,3,2,1,0],
                [0,1,1,1,1,1,0],
                [0,0,0,0,0,0,0]
        ]
        Raster raster1 = new Raster(data1, bounds)
        Raster raster2 = raster1.minusFrom(6)

        assertEquals 6, raster2.evaluate(new Point(0.5,0.5))[0], 0.1
        assertEquals 5, raster2.evaluate(new Point(1.5,1.5))[0], 0.1
        assertEquals 4, raster2.evaluate(new Point(2.5,2.5))[0], 0.1
        assertEquals 3, raster2.evaluate(new Point(3.5,2.5))[0], 0.1
    }

    @Test void contours() {
        Bounds bounds = new Bounds(0, 0, 7, 5, "EPSG:4326")
        List data = [
            [0,0,0,0,0,0,0],
            [0,1,1,1,1,1,0],
            [0,1,2,3,2,1,0],
            [0,1,1,1,1,1,0],
            [0,0,0,0,0,0,0]
        ]
        Raster raster = new Raster(data, bounds)
        Layer layer = raster.contours(0, 0.25, true, true, bounds)
        assertNotNull layer
        assertTrue layer.count > 0

        layer = raster.contours(0, [0.25,0.5,0.75], true, true, bounds)
        assertNotNull layer
        assertTrue layer.count > 0
    }

    @Test void toPolygons() {
        Bounds bounds = new Bounds(0, 0, 7, 5, "EPSG:4326")
        List data = [
            [0,0,0,0,0,0,0],
            [0,1,1,1,1,1,0],
            [0,1,2,3,2,1,0],
            [0,1,1,1,1,1,0],
            [0,0,0,0,0,0,0]
        ]
        Raster raster = new Raster(data, bounds)
        Layer layer = raster.toPolygons(0, true, bounds)
        assertNotNull layer
        assertTrue layer.count > 0

        layer = raster.toPolygons(0, true, bounds, [-1,0], [[min: 1, max: 3]])
        assertNotNull layer
        assertTrue layer.count > 0
    }

    @Test void toPoints() {
        Bounds bounds = new Bounds(0, 0, 7, 5, "EPSG:4326")
        List data = [
            [0,0,0,0,0,0,0],
            [0,1,1,1,1,1,0],
            [0,1,2,3,2,1,0],
            [0,1,1,1,1,1,0],
            [0,0,0,0,0,0,0]
        ]
        Raster raster = new Raster(data, bounds)
        Layer layer = raster.toPoints()
        assertNotNull layer
        assertTrue layer.count > 0
    }

    @Test void scale() {
        File file = new File(getClass().getClassLoader().getResource("raster.tif").toURI())
        GeoTIFF geoTIFF = new GeoTIFF()
        Raster raster = geoTIFF.read(file)
        Raster scaled = raster.scale(10, 10)
        assertNotNull scaled
    }

    @Test void zonalStatistics() {
        Bounds bounds = new Bounds(0, 0, 7, 5, "EPSG:4326")
        List data = [
            [0,0,0,0,0,0,0],
            [0,1,1,1,1,1,0],
            [0,1,2,3,2,1,0],
            [0,1,1,1,1,1,0],
            [0,0,0,0,0,0,0]
        ]
        Raster raster = new Raster(data, bounds)
        Layer zones = new Memory().create("zones", [new Field("geom","Geometry","EPSG:4326")])
        bounds.tile(0.5).each{b -> zones.add([b.geometry])}
        Layer stats = raster.zonalStatistics(0, zones)
        assertEquals 4, stats.count
        stats.features.each{f ->
            assertNotNull f.geom
            assertNotNull f.get("count")
            assertNotNull f.get("min")
            assertNotNull f.get("max")
            assertNotNull f.get("sum")
            assertNotNull f.get("avg")
            assertNotNull f.get("stddev")
            assertNull f.get("classification")
        }
    }

    @Test void createFromList() {

        List data = [
                [0,0,0,0,0,0,0],
                [0,1,1,1,1,1,0],
                [0,1,2,3,2,1,0],
                [0,1,1,1,1,1,0],
                [0,0,0,0,0,0,0]
        ]
        Bounds bounds = new Bounds(0, 0, 7, 5, "EPSG:4326")

        Raster raster = new Raster(data, bounds)
        assertNotNull raster
        assertNotNull raster.bounds
        assertEquals 0, raster.evaluate(new Point(0.5,0.5))[0], 0.1
        assertEquals 1, raster.evaluate(new Point(1.5,1.5))[0], 0.1
        assertEquals 2, raster.evaluate(new Point(2.5,2.5))[0], 0.1
        assertEquals 3, raster.evaluate(new Point(3.5,2.5))[0], 0.1
    }
}