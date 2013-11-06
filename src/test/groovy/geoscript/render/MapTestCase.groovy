package geoscript.render

import geoscript.geom.Bounds
import geoscript.layer.Shapefile
import geoscript.proj.Projection
import geoscript.layer.GeoTIFF
import geoscript.layer.Raster
import geoscript.style.Fill
import geoscript.style.Stroke
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.*

/**
 * The Map UnitTest
 * @author Jared Erickson
 */
class MapTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()
    
    @Test void proj() {
        Map map = new Map();
        map.proj = new Projection("EPSG:2927")
        assertEquals("EPSG:2927", map.proj.id)
        map.proj = "EPSG:4326"
        assertEquals("EPSG:4326", map.proj.id)
    }

    @Test void layer() {
        Map map = new Map()
        assertEquals(0, map.layers.size())
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        assertNotNull(file)
        Shapefile shp = new Shapefile(file)
        assertNotNull(shp)
        map.addLayer(shp)
        assertEquals(1, map.layers.size())
        map.layers = [shp]
        assertEquals(1, map.layers.size())
        map.close()
    }

    @Test void renderToImage() {
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        assertNotNull(file)

        Shapefile shp = new Shapefile(file)
        assertNotNull(shp)

        Map map = new Map()
        map.proj = new Projection("EPSG:2927")
        map.addLayer(shp)
        map.bounds = shp.bounds
        def image = map.renderToImage()
        assertNotNull(image)

        File out = folder.newFile("map.png")
        javax.imageio.ImageIO.write(image, "png", out);
        assertTrue(out.exists())
        map.close()
    }

    @Test void renderRasterToImage() {
        File file = new File(getClass().getClassLoader().getResource("alki.tif").toURI())
        assertNotNull(file)

        GeoTIFF geoTIFF = new GeoTIFF()
        Raster raster = geoTIFF.read(file)

        Map map = new Map()
        map.proj = new Projection("EPSG:2927")
        map.addRaster(raster)
        map.bounds = raster.bounds
        def image = map.renderToImage()
        assertNotNull(image)

        File out = folder.newFile("raster.png")
        javax.imageio.ImageIO.write(image, "png", out);
        assertTrue(out.exists())
        map.close()
    }

    @Test void renderDemRaster() {
        File file = new File(getClass().getClassLoader().getResource("raster.tif").toURI())
        assertNotNull(file)

        GeoTIFF geoTIFF = new GeoTIFF()
        Raster raster = geoTIFF.read(file)
        raster.style = new  geoscript.style.ColorMap([[color: "#008000", quantity:70], [color:"#663333", quantity:256]])

        Map map = new Map()
        map.addRaster(raster)
        def image = map.renderToImage()
        assertNotNull(image)

        File out = folder.newFile("raster.png")
        javax.imageio.ImageIO.write(image, "png", out);
        assertTrue(out.exists())
        map.close()
    }

    @Test void renderToImageWithMapNoProjection() {
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        assertNotNull(file)

        Shapefile shp = new Shapefile(file)
        assertNotNull(shp)

        Map map = new Map()
        map.addLayer(shp)
        map.bounds = shp.bounds
        def image = map.renderToImage()
        assertNotNull(image)

        File out = folder.newFile("map.png")
        javax.imageio.ImageIO.write(image, "png", out);
        assertTrue(out.exists())
        map.close()
    }

    @Test void renderToImageWithMapBoundsNoProjection() {
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        assertNotNull(file)

        Shapefile shp = new Shapefile(file)
        assertNotNull(shp)

        Map map = new Map()
        map.addLayer(shp)
        map.bounds = new Bounds(-126, 45.315, -116, 50.356)
        def image = map.renderToImage()
        assertNotNull(image)

        File out = folder.newFile("map.png")
        javax.imageio.ImageIO.write(image, "png", out);
        assertTrue(out.exists())
        map.close()
    }

    @Test void renderToFile() {
        File out = folder.newFile("map.png")
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        assertNotNull(file)
        Shapefile shp = new Shapefile(file)
        assertNotNull(shp)

        Map map = new Map()
        map.proj = new Projection("EPSG:2927")
        map.addLayer(shp)
        map.bounds = shp.bounds
        def image = map.render(out)
        assertTrue(out.exists())
        map.close()
    }

    @Test void renderToOutputStream() {
        File f = folder.newFile("map.png")
        FileOutputStream out = new FileOutputStream(f)

        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        assertNotNull(file)
        Shapefile shp = new Shapefile(file)
        assertNotNull(shp)

        Map map = new Map()
        map.proj = new Projection("EPSG:2927")
        map.addLayer(shp)
        map.bounds = shp.bounds
        def image = map.render(out)
        out.close()
        assertTrue(f.exists())
        map.close()
    }

    @Test void renderToPdf() {
        File f = folder.newFile("map.pdf")

        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        assertNotNull(file)
        Shapefile shp = new Shapefile(file)
        assertNotNull(shp)
        shp.style = new Fill("white") + new Stroke("#CCCCCC", 0.1)

        Map map = new Map(type:"pdf", layers:[shp])
        map.addLayer(shp)
        def image = map.render(f)
        assertTrue(f.exists())
        map.close()
    }

    @Test void renderToSvg() {
        File f = folder.newFile("map.svg")

        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        assertNotNull(file)
        Shapefile shp = new Shapefile(file)
        assertNotNull(shp)
        shp.style = new Fill("white") + new Stroke("#CCCCCC", 0.1)

        Map map = new Map(type:"svg", layers:[shp])
        def image = map.render(f)
        assertTrue(f.exists())
        map.close()
    }

    @Test void renderToJpeg() {
        File f = folder.newFile("map.jpeg")

        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        assertNotNull(file)
        Shapefile shp = new Shapefile(file)
        assertNotNull(shp)
        shp.style = new Fill("white") + new Stroke("#CCCCCC", 0.1)

        Map map = new Map(type:"jpeg", layers: [shp])
        def image = map.render(f)
        assertTrue(f.exists())
        map.close()
    }

    @Test void renderToGif() {
        File f = folder.newFile("map.gif")

        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        assertNotNull(file)
        Shapefile shp = new Shapefile(file)
        assertNotNull(shp)
        shp.style = new Fill("white") + new Stroke("#CCCCCC", 0.1)

        Map map = new Map(type:"gif", layers: [shp])
        def image = map.render(f)
        assertTrue(f.exists())
        map.close()
    }

    @Test void getScaleDenominator() {
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        assertNotNull(file)
        Shapefile shp = new Shapefile(file)
        assertNotNull(shp)

        Map map = new Map()
        map.proj = new Projection("EPSG:2927")
        map.addLayer(shp)
        map.bounds = shp.bounds
        assertEquals(38273743.41534821, map.scaleDenominator, 0.01)
    }

    @Test void getBounds() {
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        assertNotNull(file)
        Shapefile shp = new Shapefile(file)
        assertNotNull(shp)

        Map map = new Map()
        map.proj = new Projection("EPSG:2927")
        map.addLayer(shp)
        map.bounds = shp.bounds
        assertEquals(shp.bounds.minX, map.bounds.minX, 0.01)
        assertEquals(shp.bounds.maxX, map.bounds.maxX, 0.01)
        assertEquals(shp.bounds.minY, map.bounds.minY, 0.01)
        assertEquals(shp.bounds.maxY, map.bounds.maxY, 0.01)
        assertEquals(shp.bounds.proj.id, map.bounds.proj.id)
    }
}