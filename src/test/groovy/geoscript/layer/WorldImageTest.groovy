package geoscript.layer

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import static org.junit.jupiter.api.Assertions.*

/**
 * The WorldImage Unit Test
 * @author Jared Erickson
 */
class WorldImageTest {

    @TempDir
    File folder

    @Test void readWrite() {

        GeoTIFF geotiff = new GeoTIFF(new File(getClass().getClassLoader().getResource("alki.tif").toURI()))
        Raster gtRaster = geotiff.read()

        // GeoTIFF to TIFF + WorldFile
        File worldTiffFile = new File(folder,"raster.tif")
        WorldImage worldImage = new WorldImage(worldTiffFile)
        worldImage.write(gtRaster)
        Raster tifRaster = worldImage.read()
        assertNotNull(tifRaster)

        // GeoTIFF to JPEG + WorldFile
        File worldJpegfFile = new File(folder,"raster.jpeg")
        worldImage = new WorldImage(worldJpegfFile)
        worldImage.write(gtRaster)
        Raster jpgRaster = worldImage.read()
        assertNotNull(jpgRaster)

        // GeoTIFF to PNG + WorldFile
        File worldPngfFile = new File(folder,"raster.png")
        worldImage = new WorldImage(worldPngfFile)
        worldImage.write(gtRaster)
        Raster pngRaster = worldImage.read()
        assertNotNull(pngRaster)
    }

}
