package geoscript.layer

import geoscript.geom.Bounds
import geoscript.style.Fill
import geoscript.style.Stroke
import geoscript.workspace.Workspace
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import javax.imageio.ImageIO
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage

import static org.junit.Assert.*

/**
 * The TileGenerator Unit Test
 * @author Jared Erickson
 */
class TileGeneratorTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Test
    void generate() {
        Workspace w = Workspace.getWorkspace([url: getClass().getClassLoader().getResource("states.shp")])
        Layer shp = w.get("states")
        shp.style = new Fill("wheat") + new Stroke("navy", 0.1)
        File file = folder.newFolder("states")
        XYZ tiles = new XYZ("states","png",file,Pyramid.createGlobalMercatorPyramid())
        ImageTileRenderer renderer = new ImageTileRenderer(tiles, shp)
        TileGenerator generator = new TileGenerator()
        generator.generate(tiles, renderer, 0, 2)
        assertNotNull tiles.get(0, 0, 0).data
        assertNotNull tiles.get(1, 1, 1).data
        assertNotNull tiles.get(2, 2, 2).data
        tiles.close()
    }

    private static class ColorRenderer implements TileRenderer {
        Color color
        ColorRenderer(Color color) {
            this.color = color
        }
        @Override
        byte[] render(Bounds b) {
            BufferedImage image = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB)
            Graphics2D g2d = image.createGraphics()
            g2d.color = color
            g2d.fillRect(0, 0, 256, 256)
            g2d.dispose()
            ByteArrayOutputStream out = new ByteArrayOutputStream()
            ImageIO.write(image, "png", out)
            out.toByteArray()
        }
    }

    @Test
    void generateMissing() {
        File file = folder.newFolder("color")
        XYZ tiles = new XYZ("color","png",file,Pyramid.createGlobalMercatorPyramid())
        TileGenerator generator = new TileGenerator()
        // Generate all tiles with solid blue color
        generator.generate(tiles, new ColorRenderer(Color.BLUE), 0, 2)
        (0..2).each { int z ->
            tiles.tiles(z).each { ImageTile t ->
                assertNotNull t.data
                assertTrue t.base64String.startsWith("iVBORw0KGgoAAAANSUhEUgAAAQAAAAEACAYAAABccqhmAAACXElEQVR42u3UMQEAAAzCMPyb")
            }
        }
        // Delete zoom level 0
        tiles.delete(tiles.tiles(0))
        // Generate tiles but only the missing tiles (zoom level 0)
        generator.generate(tiles, new ColorRenderer(Color.RED), 0, 2, missingOnly: true)
        // Zoom level 0 tiles should now be present but RED
        tiles.tiles(0).each { ImageTile t ->
            assertNotNull t.data
            assertTrue t.base64String.startsWith("iVBORw0KGgoAAAANSUhEUgAAAQAAAAEACAYAAABccqhmAAACXUlEQVR42u3UMQEAAAiAMPqX1")
        }
        // Zoom level 1 and 2 tiles should still be BLUE
        (1..2).each { int z ->
            tiles.tiles(z).each { ImageTile t ->
                assertNotNull t.data
                assertTrue t.base64String.startsWith("iVBORw0KGgoAAAANSUhEUgAAAQAAAAEACAYAAABccqhmAAACXElEQVR42u3UMQEAAAzCMPyb")
            }
        }
        tiles.close()
    }
}
