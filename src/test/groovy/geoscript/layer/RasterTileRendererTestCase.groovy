package geoscript.layer

import geoscript.geom.Bounds
import geoscript.proj.Projection
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

import static org.junit.Assert.*

class RasterTileRendererTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Test
    void render() {
        // Read a Raster which is in EPSG:4326
        File file = new File(getClass().getClassLoader().getResource("raster.tif").toURI())
        assertNotNull(file)
        GeoTIFF geoTIFF = new GeoTIFF(file)
        Raster raster = geoTIFF.read()
        assertNotNull(raster)

        // Resize and Reproject Raster to Web Mercator
        Projection latLonProj = new Projection("EPSG:4326")
        Projection mercatorProj = new Projection("EPSG:3857")
        Bounds latLonBounds = new Bounds(-179.99, -85.0511, 179.99, 85.0511, latLonProj)
        Raster webMercatorRaster = raster.resample(bbox: latLonBounds).reproject(mercatorProj)

        // Generate a Tile
        RasterTileRenderer tileRenderer = new RasterTileRenderer(webMercatorRaster)
        Pyramid pyramid = Pyramid.createGlobalMercatorPyramid()
        byte[] bytes = tileRenderer.render(pyramid.bounds(new Tile(0,0,0)), size: [256,256])
        assertTrue(bytes.length > 0)

        // Make sure it can be read as an Image
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes))
        File outFile = folder.newFile("raster_0_0_0.jpeg")
        ImageIO.write(image, "JPEG", outFile)
        assertTrue(outFile.exists())
        assertTrue(outFile.length() > 0)
    }

}
