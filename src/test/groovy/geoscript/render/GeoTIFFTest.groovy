package geoscript.render

import geoscript.layer.Layer
import geoscript.layer.Shapefile
import geoscript.style.Fill
import geoscript.style.Stroke
import org.geotools.image.test.ImageAssert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import javax.imageio.ImageIO

import static org.junit.jupiter.api.Assertions.*

/**
 * The GeoTIFF Unit Test
 * @author Scott Bortman
 */
class GeoTIFFTest {

    @TempDir
    File folder

    private File getFile(String resource) {
        return new File(getClass().getClassLoader().getResource(resource).toURI())
    }

    @Test
    void renderToImage() {
        File shpFile = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Layer layer = new Shapefile(shpFile)
        layer.style = new Stroke('black', 0.1) + new Fill('gray', 0.75)
        Map map = new Map(layers: [layer], backgroundColor: "white")
        GeoTIFF geotiff = new GeoTIFF()
        def img = geotiff.render(map)
        assertNotNull(img)
        File file = new File(folder,"image.tif")
        ImageIO.write(img, "TIFF", file)
        assertTrue file.exists()
        assertTrue file.length() > 0
       ImageAssert.assertEquals(getFile("geoscript/render/geotiff_to_image.tif"), ImageIO.read(file), 100)
    }

    @Test
    void renderToOutputStream() {
        File shpFile = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Layer layer = new Shapefile(shpFile)
        layer.style = new Stroke('black', 0.1) + new Fill('gray', 0.75)
        Map map = new Map(layers: [layer], backgroundColor: "white")
        GeoTIFF geotiff = new GeoTIFF()
        File file = new File(folder,"image.tif")
        OutputStream out = new FileOutputStream(file)
        geotiff.render(map, out)
        out.close()
        assertTrue file.exists()
        assertTrue file.length() > 0
       ImageAssert.assertEquals(getFile("geoscript/render/geotiff_to_image.tif"), ImageIO.read(file), 100)
    }

    @Test void getImageType() {
        assertEquals "geotiff", new GeoTIFF().imageType
    }
}
