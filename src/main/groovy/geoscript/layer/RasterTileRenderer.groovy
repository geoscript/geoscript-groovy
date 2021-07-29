package geoscript.layer

import geoscript.geom.Bounds

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

/**
 * Just render a Raster by resampling to the desired Bounds and Size.
 * @author Jared Erickson
 */
class RasterTileRenderer implements TileRenderer {

    private final Raster raster

    RasterTileRenderer(Raster raster) {
        this.raster = raster
    }

    @Override
    byte[] render(Map options = [:], Bounds b) {
        Raster tileRaster = raster.resample(bbox: b, size: [options.get("width",256),options.get("height", 256)])
        BufferedImage image = tileRaster.getBufferedImage()
        def out = new ByteArrayOutputStream()
        ImageIO.write(image, options.get("type", "JPEG"), out)
        out.close()
        out.toByteArray()
    }

}
