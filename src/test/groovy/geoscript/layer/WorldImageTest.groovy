package geoscript.layer

import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.*

/**
 * The WorldImage Unit Test
 * @author Jared Erickson
 */
class WorldImageTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Test void readWrite() {

        GeoTIFF geotiff = new GeoTIFF(new File(getClass().getClassLoader().getResource("alki.tif").toURI()))
        Raster gtRaster = geotiff.read()

        // GeoTIFF to TIFF + WorldFile
        File worldTiffFile = folder.newFile("raster.tif")
        WorldImage worldImage = new WorldImage(worldTiffFile)
        worldImage.write(gtRaster)
        Raster tifRaster = worldImage.read()
        assertNotNull(tifRaster)

        // GeoTIFF to JPEG + WorldFile
        File worldJpegfFile = folder.newFile("raster.jpeg")
        worldImage = new WorldImage(worldJpegfFile)
        worldImage.write(gtRaster)
        Raster jpgRaster = worldImage.read()
        assertNotNull(jpgRaster)

        // GeoTIFF to PNG + WorldFile
        File worldPngfFile = folder.newFile("raster.png")
        worldImage = new WorldImage(worldPngfFile)
        worldImage.write(gtRaster)
        Raster pngRaster = worldImage.read()
        assertNotNull(pngRaster)
    }

}
