package geoscript.layer

import org.junit.Test
import static org.junit.Assert.*

/**
 * The WorldImage Unit Test
 * @author Jared Erickson
 */
class WorldImageTest {

    @Test void readWrite() {

        GeoTIFF geotiff = new GeoTIFF()
        Raster gtRaster = geotiff.read(new File(getClass().getClassLoader().getResource("alki.tif").toURI()))

        WorldImage worldImage = new WorldImage()

        // GeoTIFF to TIFF + WorldFile
        File worldTiffFile = File.createTempFile("raster",".tif")
        worldImage.write(gtRaster, worldTiffFile)
        Raster tifRaster = worldImage.read(worldTiffFile)
        assertNotNull(tifRaster)

        // GeoTIFF to JPEG + WorldFile
        File worldJpegfFile = File.createTempFile("raster",".jpeg")
        worldImage.write(gtRaster, worldJpegfFile)
        Raster jpgRaster = worldImage.read(worldJpegfFile)
        assertNotNull(jpgRaster)

        // GeoTIFF to PNG + WorldFile
        File worldPngfFile = File.createTempFile("raster",".png")
        worldImage.write(gtRaster, worldPngfFile)
        Raster pngRaster = worldImage.read(worldPngfFile)
        assertNotNull(pngRaster)
    }

}
