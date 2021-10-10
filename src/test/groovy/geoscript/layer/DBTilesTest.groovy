package geoscript.layer

import geoscript.geom.Bounds
import org.apache.commons.dbcp.BasicDataSource
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import javax.sql.DataSource

import static org.junit.jupiter.api.Assertions.*

import javax.imageio.ImageIO
import java.awt.Graphics2D
import java.awt.image.BufferedImage

/**
 * The DBTiles Unit Test
 * @author Jared Erickson
 */
class DBTilesTest {

    @TempDir
    private File folder

    private generate(DBTiles dbtiles) {
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

        TileGenerator generator = new TileGenerator(verbose: false)
        generator.generate(dbtiles, renderer, 0, 2)
    }

    private void read(DBTiles dbtiles) {
        assertEquals("Random", dbtiles.name)
        Raster raster = dbtiles.getRaster(dbtiles.tiles(2))
        assertNotNull raster

        assertNotNull dbtiles.get(2,0,0)
        dbtiles.delete(new ImageTile(2,0,0))
        assertNull dbtiles.get(2,0,0).data
    }

    private metadata(DBTiles dbtiles) {
        Map<String,String> metadata = dbtiles.metadata
        assertEquals("Random", metadata.name)
        assertEquals("baselayer", metadata.type)
        assertEquals("1.0", metadata.version)
        assertEquals("Random Color Tiles", metadata.description)
        assertEquals("png", metadata.format)
        assertEquals("-179.99,-85.0511,179.99,85.0511", metadata.bounds)
        assertEquals("Created with GeoScript", metadata.attribution)

        List stats = dbtiles.tileCounts
        stats.eachWithIndex { Map stat, int index ->
            assertEquals(index, stat.zoom)
            assertEquals(Math.pow(4, index), stat.tiles, 0.1)
            assertEquals(Math.pow(4, index), stat.total, 0.1)
            assertEquals(1.0, stat.percent, 0.1)
        }

        assertEquals 0, dbtiles.minZoom
        assertEquals 2, dbtiles.maxZoom
    }

    @Test void h2() {
        File dbFile = new File(folder, "tiles.db")
        DBTiles dbtiles = new DBTiles("jdbc:h2:${dbFile}","org.h2.Driver", "Random", "Random Color Tiles")
        assertEquals("Random", dbtiles.name)
        generate(dbtiles)
        dbtiles = new DBTiles("jdbc:h2:${dbFile}","org.h2.Driver")
        assertEquals("Random", dbtiles.name)
        read(dbtiles)
    }

    @Test void h2DataSource() {
        File dbFile = new File(folder, "tiles.db")
        DataSource ds = new BasicDataSource()
        ds.url ="jdbc:h2:${dbFile}"
        ds.driverClassName = "org.h2.Driver"
        DBTiles dbtiles = new DBTiles(ds, "Random", "Random Color Tiles")
        assertEquals("Random", dbtiles.name)
        generate(dbtiles)
        dbtiles = new DBTiles(ds)
        assertEquals("Random", dbtiles.name)
        read(dbtiles)
    }

    @Test void h2Metadata() {
        File dbFile = new File(folder, "tiles.db")
        DBTiles dbtiles = new DBTiles("jdbc:h2:${dbFile}","org.h2.Driver", "Random", "Random Color Tiles")
        assertEquals("Random", dbtiles.name)
        generate(dbtiles)
        dbtiles = new DBTiles("jdbc:h2:${dbFile}","org.h2.Driver")
        assertEquals("Random", dbtiles.name)
        metadata(dbtiles)
    }

    @Test void sqlite() {
        File dbFile = new File("tiles.db") //new File(folder, "tiles.db")
        DBTiles dbtiles = new DBTiles("jdbc:sqlite:${dbFile}","org.sqlite.JDBC", "Random", "Random Color Tiles")
        assertEquals("Random", dbtiles.name)
        generate(dbtiles)
        dbtiles = new DBTiles("jdbc:sqlite:${dbFile}","org.sqlite.JDBC")
        assertEquals("Random", dbtiles.name)
        read(dbtiles)
    }

    @Test void sqliteDataSource() {
        File dbFile = new File(folder, "tiles.db")
        DataSource ds = new BasicDataSource()
        ds.url ="jdbc:sqlite:${dbFile}"
        ds.driverClassName = "org.sqlite.JDBC"
        DBTiles dbtiles = new DBTiles(ds, "Random", "Random Color Tiles")
        assertEquals("Random", dbtiles.name)
        generate(dbtiles)
        dbtiles = new DBTiles(ds)
        assertEquals("Random", dbtiles.name)
        read(dbtiles)
    }

    @Test void sqliteMetadata() {
        File dbFile = new File(folder, "tiles.db")
        DBTiles dbtiles = new DBTiles("jdbc:sqlite:${dbFile}","org.sqlite.JDBC", "Random", "Random Color Tiles")
        assertEquals("Random", dbtiles.name)
        generate(dbtiles)
        dbtiles = new DBTiles("jdbc:sqlite:${dbFile}","org.sqlite.JDBC")
        assertEquals("Random", dbtiles.name)
        metadata(dbtiles)
    }

    @Test void lookupWithTileFactories() {
        assertNotNull TileLayerFactories.list().find {
            it instanceof DBTiles.Factory
        }
    }

    @Test void createWithTileFactory() {
        File dbFile = new File(folder, "tiles.db")
        TileLayer dbtiles = TileLayer.getTileLayer("type=dbtiles driver='org.sqlite.JDBC' url='jdbc:sqlite:${dbFile}' name=Random description='Random Color Tiles'")
        generate(dbtiles as DBTiles)
    }

    @Test void getTileRendererWithTileFactory() {
        File dbFile = new File(folder, "tiles.db")
        DBTiles dbtiles = TileLayer.getTileLayer("type=dbtiles driver='org.sqlite.JDBC' url='jdbc:sqlite:${dbFile}' name=States description='The USA'") as DBTiles

        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        TileRenderer renderer = TileLayer.getTileRenderer(dbtiles, new Shapefile(file))
        assertNotNull renderer

        TileGenerator generator = new TileGenerator(verbose: false)
        generator.generate(dbtiles, renderer, 0, 2)

        Raster raster = dbtiles.getRaster(dbtiles.tiles(2))
        assertNotNull raster

        assertNotNull dbtiles.get(2,0,0)
        dbtiles.delete(new ImageTile(2,0,0))
        assertNull dbtiles.get(2,0,0).data
    }

}
