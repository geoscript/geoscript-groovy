import geoscript.geom.Bounds
import geoscript.layer.MBTiles
import geoscript.layer.Raster
import geoscript.layer.TileGenerator
import geoscript.layer.TileRenderer
import javax.imageio.ImageIO
import java.awt.Graphics2D
import java.awt.image.BufferedImage

TileRenderer renderer = new TileRenderer() {
    @Override
    byte[] render(Bounds b) {
        BufferedImage image = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB)
        Graphics2D g2d = image.createGraphics()
        g2d.color = geoscript.filter.Color.randomPastel.asColor()
        g2d.fillRect(0, 0, 256, 256)
        g2d.dispose()
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        ImageIO.write(image, "png", out)
        out.toByteArray()
    }
}

File file = new File("colors.mbtiles")
MBTiles mbtiles = new MBTiles(file, "colors", "Random color tiles")

TileGenerator generator = new TileGenerator(verbose: true)
generator.generate(mbtiles, renderer, 0, 4)

File dir = new File("randomcolors")
dir.mkdir()

(0..3).each{int zoom ->
    Raster raster = mbtiles.getRaster(mbtiles.tiles(zoom))
    ImageIO.write(raster.image, "png", new File(dir, "${zoom}.png"))
}

mbtiles.close()