package geoscript.layer

import geoscript.geom.Bounds
import geoscript.style.Fill
import geoscript.style.Stroke
import org.geotools.mbtiles.MBTilesFile
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import javax.imageio.ImageIO

import static org.junit.Assert.*

/**
 * The MBTiles Unit Test
 * @author Jared Erickson
 */
class MBTilesTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Test void createTiles() {
        File file = folder.newFile("states.mbtiles")
        Shapefile shp = new Shapefile(new File(getClass().getClassLoader().getResource("states.shp").toURI()))
        shp.style = new Fill("wheat") + new Stroke("navy", 0.1)
        MBTiles mbtiles = new MBTiles(file)
        mbtiles.create("states","A map of the united states")
        mbtiles.generate(shp, 0, 2)
        assertTrue(file.exists())
        assertTrue(file.length() > 0)
        MBTilesFile tf = new MBTilesFile(file)
        assertEquals(0, tf.minZoom())
        assertEquals(2, tf.maxZoom())
        assertEquals(21, tf.numberOfTiles())
    }

    @Test void readFormat() {
        MBTiles mbtiles = new MBTiles(new File(getClass().getClassLoader().getResource("states.mbtiles").toURI()))
        Bounds bounds = new Bounds(-179.999999, -85.0511, 179.999999, 85.0511, "EPSG:4326")
        // Read with bounds and size
        Raster raster = mbtiles.read(bounds, [500, 500])
        File file1 = folder.newFile("states1.png")
        ImageIO.write(raster.image, "PNG", file1)
        assertTrue(file1.exists())
        assertTrue(file1.length() > 0)
        // Read with default bounds and size
        raster = mbtiles.read()
        File file2 = folder.newFile("states2.png")
        ImageIO.write(raster.image, "PNG", file2)
        assertTrue(file2.exists())
        assertTrue(file2.length() > 0)
        // Read with smaller bounds and larger size
        raster = mbtiles.read(new Bounds(-124.73142200000001,24.955967,-66.969849,49.371735,"EPSG:4326"), [800,800])
        File file3 = folder.newFile("states3.png")
        ImageIO.write(raster.image, "PNG", file3)
        assertTrue(file3.exists())
        assertTrue(file3.length() > 0)
    }

}
