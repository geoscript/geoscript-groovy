package geoscript.layer

import geoscript.FileUtil
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import static org.junit.jupiter.api.Assertions.*

/**
 * The UTFGrid Unit Test
 * @author Jared Erickson
 */
class UTFGridTest {

    @TempDir
    File folder

    @Test void generate() {
        Shapefile shp = new Shapefile(new File(getClass().getClassLoader().getResource("states.shp").toURI()))
        File dir = FileUtil.createDir(folder, "states")
        UTFGrid utf = new UTFGrid(dir)
        assertNotNull utf.pyramid

        UTFGridTileRenderer renderer = new UTFGridTileRenderer(utf,shp,[shp.schema.get("STATE_NAME")])
        TileGenerator generator = new TileGenerator()
        generator.generate(utf, renderer, 0, 2)

        [
                new File(dir, "0/0/0.grid.json"),
                new File(dir, "1/0/0.grid.json"),
                new File(dir, "1/1/0.grid.json"),
                new File(dir, "1/1/1.grid.json"),
                new File(dir, "1/0/1.grid.json"),
                new File(dir, "2/0/0.grid.json"),
                new File(dir, "2/0/1.grid.json"),
                new File(dir, "2/0/2.grid.json"),
                new File(dir, "2/0/3.grid.json"),
                new File(dir, "2/1/0.grid.json"),
                new File(dir, "2/1/1.grid.json"),
                new File(dir, "2/1/2.grid.json"),
                new File(dir, "2/1/3.grid.json"),
                new File(dir, "2/2/0.grid.json"),
                new File(dir, "2/2/1.grid.json"),
                new File(dir, "2/2/2.grid.json"),
                new File(dir, "2/2/3.grid.json"),
                new File(dir, "2/3/0.grid.json"),
                new File(dir, "2/3/1.grid.json"),
                new File(dir, "2/3/2.grid.json"),
                new File(dir, "2/3/3.grid.json")
        ].each { File f ->
            assertTrue f.exists()
        }
        // Read
        (0..2).each { int z ->
            utf.tiles(z).each { Tile t ->
                assertEquals z, t.z
                assertTrue t.x in [0l, 1l, 2l, 3l]
                assertTrue t.y in [0l, 1l, 2l, 3l]
                assertNotNull t.data
            }
        }
        // Delete
        Tile tile = utf.get(0,0,0)
        assertNotNull tile.data
        utf.delete(tile)
        tile = utf.get(0,0,0)
        assertNull tile.data
    }
}
