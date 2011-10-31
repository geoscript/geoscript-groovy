package geoscript.render

import geoscript.geom.Bounds
import geoscript.layer.Shapefile
import geoscript.proj.Projection
import geoscript.raster.GeoTIFF
import geoscript.raster.Raster
import geoscript.style.Fill
import geoscript.style.Stroke
import org.junit.Test
import static org.junit.Assert.*

/**
 * The Map UnitTest
 * @author Jared Erickson
 */
class MapTestCase {

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

        File out = File.createTempFile("map",".png")
        println("renderToImage: ${out}")
        javax.imageio.ImageIO.write(image, "png", out);
        assertTrue(out.exists())
        map.close()
    }

    @Test void renderRasterToImage() {
        File file = new File(getClass().getClassLoader().getResource("alki.tif").toURI())
        assertNotNull(file)

        Raster raster = new GeoTIFF(file)

        Map map = new Map()
        map.proj = new Projection("EPSG:2927")
        map.addRaster(raster)
        map.bounds = raster.bounds
        def image = map.renderToImage()
        assertNotNull(image)

        File out = File.createTempFile("raster",".png")
        println("renderRasterToImage: ${out}")
        javax.imageio.ImageIO.write(image, "png", out);
        assertTrue(out.exists())
        map.close()
    }

    @Test void renderDemRaster() {
        File file = new File(getClass().getClassLoader().getResource("raster.tif").toURI())
        assertNotNull(file)

        Raster raster = new GeoTIFF(file)
        raster.style = new  geoscript.style.ColorMap([[color: "#008000", quantity:70], [color:"#663333", quantity:256]])

        Map map = new Map()
        map.addRaster(raster)
        def image = map.renderToImage()
        assertNotNull(image)

        File out = File.createTempFile("raster",".png")
        println("renderDemRaster: ${out}")
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

        File out = File.createTempFile("map",".png")
        println("renderToImageWithMapNoProjection: ${out}")
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

        File out = File.createTempFile("map",".png")
        println("renderToImageWithMapBoundsNoProjection: ${out}")
        javax.imageio.ImageIO.write(image, "png", out);
        assertTrue(out.exists())
        map.close()
    }

    @Test void renderToFile() {

        File out = File.createTempFile("map",".png")
        println("renderToFile: ${out}")

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
        File f = File.createTempFile("map",".png")
        println("renderToOutputStream: ${f}")
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
        File f = File.createTempFile("map",".pdf")
        println("renderToPdf: ${f}")

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
        File f = File.createTempFile("map",".svg")
        println("renderToSvg: ${f}")

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
        File f = File.createTempFile("map",".jpeg")
        println("renderToJpeg: ${f}")

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
        File f = File.createTempFile("map",".gif")
        println("renderToGif: ${f}")

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
        assertEquals(shp.bounds.l, map.bounds.l, 0.01)
        assertEquals(shp.bounds.r, map.bounds.r, 0.01)
        assertEquals(shp.bounds.t, map.bounds.t, 0.01)
        assertEquals(shp.bounds.b, map.bounds.b, 0.01)
        assertEquals(shp.bounds.proj.id, map.bounds.proj.id)
    }
}