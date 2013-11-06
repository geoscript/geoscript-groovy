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

        GeoTIFF geotiff = new GeoTIFF()
        Raster gtRaster = geotiff.read(new File(getClass().getClassLoader().getResource("alki.tif").toURI()))

        WorldImage worldImage = new WorldImage()

        // GeoTIFF to TIFF + WorldFile
        File worldTiffFile = folder.newFile("raster.tif")
        worldImage.write(gtRaster, worldTiffFile)
        Raster tifRaster = worldImage.read(worldTiffFile)
        assertNotNull(tifRaster)

        // GeoTIFF to JPEG + WorldFile
        File worldJpegfFile = folder.newFile("raster.jpeg")
        worldImage.write(gtRaster, worldJpegfFile)
        Raster jpgRaster = worldImage.read(worldJpegfFile)
        assertNotNull(jpgRaster)

        // GeoTIFF to PNG + WorldFile
        File worldPngfFile = folder.newFile("raster.png")
        worldImage.write(gtRaster, worldPngfFile)
        Raster pngRaster = worldImage.read(worldPngfFile)
        assertNotNull(pngRaster)
    }

}
