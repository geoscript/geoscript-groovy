package geoscript.render

import geoscript.geom.Bounds
import geoscript.layer.Format
import geoscript.layer.Layer
import geoscript.proj.Projection
import geoscript.layer.Raster
import geoscript.style.Fill
import geoscript.style.Stroke
import geoscript.style.io.SLDReader
import geoscript.workspace.Workspace
import org.geotools.image.test.ImageAssert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import javax.imageio.ImageIO

import static org.junit.Assert.*

/**
 * The Map UnitTest
 * @author Jared Erickson
 */
class MapTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    private File getFile(String resource) {
        return new File(getClass().getClassLoader().getResource(resource).toURI())
    }

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
        Workspace w = Workspace.getWorkspace([url: getClass().getClassLoader().getResource("states.shp")])
        Layer layer = w.get("states")
        assertNotNull(layer)
        map.addLayer(layer)
        assertEquals(1, map.layers.size())
        map.layers = [layer]
        assertEquals(1, map.layers.size())
        map.close()
    }

    @Test void renderToImage() {
        Workspace w = Workspace.getWorkspace([url: getClass().getClassLoader().getResource("states.shp")])
        Layer layer = w.get("states")
        assertNotNull(layer)
        layer.style = new SLDReader().read(getClass().getClassLoader().getResource("states.sld").text)

        Map map = new Map()
        map.proj = new Projection("EPSG:2927")
        map.addLayer(layer)
        map.bounds = layer.bounds
        def image = map.renderToImage()
        assertNotNull(image)

        File out = folder.newFile("map.png")
        javax.imageio.ImageIO.write(image, "png", out);
        assertTrue(out.exists())
        map.close()

        ImageAssert.assertEquals(getFile("geoscript/render/map_to_image.png"), ImageIO.read(out), 200)
    }

    @Test void renderRasterToImage() {
        File file = new File(getClass().getClassLoader().getResource("alki.tif").toURI())
        assertNotNull(file)

        Format format = Format.getFormat(file)
        Raster raster = format.read()

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

        ImageAssert.assertEquals(getFile("geoscript/render/map_to_raster.png"), ImageIO.read(out), 100)
    }

    @Test void renderDemRaster() {
        File file = new File(getClass().getClassLoader().getResource("raster.tif").toURI())
        assertNotNull(file)

        Format format = Format.getFormat(file)
        Raster raster = format.read()
        raster.style = new  geoscript.style.ColorMap([[color: "#008000", quantity:70], [color:"#663333", quantity:256]])

        Map map = new Map()
        map.addRaster(raster)
        def image = map.renderToImage()
        assertNotNull(image)

        File out = folder.newFile("raster.png")
        javax.imageio.ImageIO.write(image, "png", out);
        assertTrue(out.exists())
        map.close()

        ImageAssert.assertEquals(getFile("geoscript/render/map_to_dem.png"), ImageIO.read(out), 100)
    }

    @Test void renderToImageWithMapNoProjection() {
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        assertNotNull(file)

        Workspace w = Workspace.getWorkspace([url: getClass().getClassLoader().getResource("states.shp")])
        Layer shp = w.get("states")
        shp.style = new SLDReader().read(getClass().getClassLoader().getResource("states.sld").text)

        Map map = new Map()
        map.addLayer(shp)
        map.bounds = shp.bounds
        def image = map.renderToImage()
        assertNotNull(image)

        File out = folder.newFile("map.png")
        javax.imageio.ImageIO.write(image, "png", out);
        assertTrue(out.exists())
        map.close()

        ImageAssert.assertEquals(getFile("geoscript/render/map_to_image_noproj.png"), ImageIO.read(out), 200)
    }

    @Test void renderToImageWithMapBoundsNoProjection() {
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        assertNotNull(file)

        Workspace w = Workspace.getWorkspace([url: getClass().getClassLoader().getResource("states.shp")])
        Layer shp = w.get("states")
        shp.style = new SLDReader().read(getClass().getClassLoader().getResource("states.sld").text)

        Map map = new Map()
        map.addLayer(shp)
        map.bounds = new Bounds(-126, 45.315, -116, 50.356)
        def image = map.renderToImage()
        assertNotNull(image)

        File out = folder.newFile("map.png")
        javax.imageio.ImageIO.write(image, "png", out);
        assertTrue(out.exists())
        map.close()

        ImageAssert.assertEquals(getFile("geoscript/render/map_to_image_noproj_nobounds.png"), ImageIO.read(out), 200)
    }

    @Test void renderToFile() {
        File out = folder.newFile("map.png")
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        assertNotNull(file)
        Workspace w = Workspace.getWorkspace([url: getClass().getClassLoader().getResource("states.shp")])
        Layer shp = w.get("states")
        shp.style = new SLDReader().read(getClass().getClassLoader().getResource("states.sld").text)

        Map map = new Map()
        map.proj = new Projection("EPSG:2927")
        map.addLayer(shp)
        map.bounds = shp.bounds
        map.render(out)
        assertTrue(out.exists())
        map.close()

        ImageAssert.assertEquals(getFile("geoscript/render/map_to_file.png"), ImageIO.read(out), 200)
    }

    @Test void renderToContinuousFile() {
        File out = folder.newFile("map.png")
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        assertNotNull(file)
        Workspace w = Workspace.getWorkspace([url: getClass().getClassLoader().getResource("states.shp")])
        Layer shp = w.get("states")
        shp.style = new SLDReader().read(getClass().getClassLoader().getResource("states.sld").text)

        Map map = new Map()
        map.width = 400
        map.height = 100
        map.proj = new Projection("EPSG:4326")
        map.addLayer(shp)
        map.bounds = new Bounds(-180, -90, 180, 90, "EPSG:4326")
        map.render(out)
        assertTrue(out.exists())
        map.close()

        ImageAssert.assertEquals(getFile("geoscript/render/map_continuous.png"), ImageIO.read(out), 100)
    }

    @Test void renderToOutputStream() {
        File f = folder.newFile("map.png")
        FileOutputStream out = new FileOutputStream(f)

        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        assertNotNull(file)
        Workspace w = Workspace.getWorkspace([url: getClass().getClassLoader().getResource("states.shp")])
        Layer shp = w.get("states")
        shp.style = new SLDReader().read(getClass().getClassLoader().getResource("states.sld").text)

        Map map = new Map()
        map.proj = new Projection("EPSG:2927")
        map.addLayer(shp)
        map.bounds = shp.bounds
        map.render(out)
        out.close()
        assertTrue(f.exists())
        map.close()

        ImageAssert.assertEquals(getFile("geoscript/render/map_to_out.png"), ImageIO.read(f), 200)
    }

    @Test void renderToJpeg() {
        File f = folder.newFile("map.jpeg")

        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        assertNotNull(file)
        Workspace w = Workspace.getWorkspace([url: getClass().getClassLoader().getResource("states.shp")])
        Layer shp = w.get("states")
        shp.style = new Fill("white") + new Stroke("#CCCCCC", 0.1)

        Map map = new Map(type:"jpeg", layers: [shp])
        map.render(f)
        assertTrue(f.exists())
        map.close()

        ImageAssert.assertEquals(getFile("geoscript/render/map.jpeg"), ImageIO.read(f), 100)
    }

    @Test void renderToGif() {
        File f = folder.newFile("map.gif")

        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        assertNotNull(file)
        Workspace w = Workspace.getWorkspace([url: getClass().getClassLoader().getResource("states.shp")])
        Layer shp = w.get("states")
        shp.style = new Fill("white") + new Stroke("#CCCCCC", 0.1)

        Map map = new Map(type:"gif", layers: [shp])
        map.render(f)
        assertTrue(f.exists())
        map.close()

        // File f2 = folder.newFile("map2.gif")
        // ImageIO.write(ImageIO.read(f), 'gif', f2)
        // ImageAssert.assertEquals(getFile("geoscript/render/map.gif"), ImageIO.read(f2), 100)
    }

    @Test void renderToBase64() {
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        assertNotNull(file)
        Workspace w = Workspace.getWorkspace([url: getClass().getClassLoader().getResource("states.shp")])
        Layer shp = w.get("states")
        shp.style = new Fill("white") + new Stroke("#CCCCCC", 0.1)

        Map map = new Map(type:"base64", layers: [shp])
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        map.render(out)
        map.close()
        byte[] bytes = out.toByteArray()
        assertTrue(bytes.length > 0)
    }

    @Test void getScaleDenominator() {
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        assertNotNull(file)
        Workspace w = Workspace.getWorkspace([url: getClass().getClassLoader().getResource("states.shp")])
        Layer shp = w.get("states")

        Map map = new Map()
        map.proj = new Projection("EPSG:2927")
        map.addLayer(shp)
        map.bounds = shp.bounds
        assertEquals(38273743.41534821, map.scaleDenominator, 0.01)
    }

    @Test void getBounds() {
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        assertNotNull(file)
        Workspace w = Workspace.getWorkspace([url: getClass().getClassLoader().getResource("states.shp")])
        Layer shp = w.get("states")

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
