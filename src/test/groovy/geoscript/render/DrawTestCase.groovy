package geoscript.render

import org.geotools.image.test.ImageAssert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.*
import geoscript.style.Symbolizer
import geoscript.style.Stroke
import geoscript.style.Fill
import geoscript.geom.Geometry
import geoscript.geom.Point
import static geoscript.render.Draw.*
import javax.imageio.ImageIO
import geoscript.geom.GeometryCollection
import geoscript.geom.LineString
import geoscript.feature.Field
import geoscript.feature.Feature
import geoscript.feature.Schema
import geoscript.layer.Layer
import geoscript.layer.Shapefile
import geoscript.layer.Raster
import geoscript.layer.GeoTIFF

/**
 * The Draw UnitTest
 * @author Jared Erickson
 */
class DrawTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    private File getFile(String resource) {
        return new File(getClass().getClassLoader().getResource(resource).toURI())
    }

    @Test void drawGeometry() {
        Symbolizer sym = new Stroke('black', 2) + new Fill('gray',0.75)
        Geometry geom = new Point(0,0).buffer(0.2)
        File file = folder.newFile("draw_geometry.png")
        draw(geom, style: sym, bounds: geom.bounds.scale(1.1), size: [250,250], out: file, format: "png", backgroundColor: "white")
        assertTrue file.exists()
        assertTrue file.length() > 0
        File expectedFile = getFile("geoscript/render/draw_geometry.png")
        ImageAssert.assertEquals(expectedFile, ImageIO.read(file), 100)
    }

    @Test void drawGeometries() {
        Symbolizer sym = new Stroke('navy', 0.1)
        Geometry geom = new Point(0,0)
        List geometries = (1..10).collect{geom.buffer(it)}
        File file = folder.newFile("draw_geometries.png")
        draw(geometries, style: sym, bounds: new GeometryCollection(geometries).bounds.scale(1.1), size: [250,250], out: file, format: "png")
        assertTrue file.exists()
        assertTrue file.length() > 0
        File expectedFile = getFile("geoscript/render/draw_geometries.png")
        ImageAssert.assertEquals(expectedFile, ImageIO.read(file), 100)
    }

    @Test void drawFeature() {
        Schema schema  = new Schema("shapes",[new Field("geom","Polygon"), new Field("name", "String")])
        Feature feature = new Feature([new LineString([0,0],[1,1]).bounds.polygon, "square"], "0",  schema)
        Symbolizer sym = new Stroke('navy', 0.1)
        File file = folder.newFile("draw_feature.png")
        draw(feature, style: sym, bounds: feature.geom.bounds.scale(1.1), size: [250,250], out: file, format: "png")
        assertTrue file.exists()
        assertTrue file.length() > 0
        File expectedFile = getFile("geoscript/render/draw_feature.png")
        ImageAssert.assertEquals(expectedFile, ImageIO.read(file), 100)
    }

    @Test void drawLayer() {
        Symbolizer sym = new Stroke('black', 0.1) + new Fill('gray', 0.75)
        File shpFile = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Layer layer = new Shapefile(shpFile)
        layer.style = sym
        File file = folder.newFile("draw_layer.png")
        draw(layer, bounds: layer.bounds.scale(1.1), size: [250,250], out: file, format: "png")
        assertTrue file.exists()
        assertTrue file.length() > 0
        File expectedFile = getFile("geoscript/render/draw_layer.png")
        ImageAssert.assertEquals(expectedFile, ImageIO.read(file), 100)
    }

    @Test void drawRaster() {
        File tifFile = new File(getClass().getClassLoader().getResource("alki.tif").toURI())
        GeoTIFF geoTIFF = new GeoTIFF(tifFile)
        Raster raster = geoTIFF.read()
        File file = folder.newFile("draw_raster.png")
        draw(raster, bounds: raster.bounds.scale(1.1), size: [250,250], out: file, format: "png")
        assertTrue file.exists()
        assertTrue file.length() > 0
        File expectedFile = getFile("geoscript/render/draw_raster.png")
        ImageAssert.assertEquals(expectedFile, ImageIO.read(file), 100)
    }

    @Test void drawLayerToPdf() {
        Symbolizer sym = new Stroke('black', 0.1) + new Fill('gray', 0.75)
        File shpFile = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Layer layer = new Shapefile(shpFile)
        layer.style = sym
        File file = folder.newFile("draw_layer.pdf")
        draw(layer, bounds: layer.bounds.scale(1.1), size: [250,250], out: file, format: "pdf")
        assertTrue file.exists()
        assertTrue file.length() > 0
    }

    @Test void drawGeometryToImage() {
        Symbolizer sym = new Stroke('black', 2) + new Fill('gray',0.75)
        Geometry geom = new Point(0,0).buffer(0.2)
        File file = folder.newFile("draw_geometry.png")
        def image = drawToImage(geom, style: sym, bounds: geom.bounds.scale(1.1), size: [250,250])
        assertNotNull(image)
        ImageIO.write(image, "png", file)
        assertTrue file.exists()
        assertTrue file.length() > 0
        File expectedFile = getFile("geoscript/render/draw_geometry_to_image.png")
        ImageAssert.assertEquals(expectedFile, image, 100)
    }

    @Test void drawGeometriesToImage() {
        Symbolizer sym = new Stroke('navy', 0.1)
        Geometry geom = new Point(0,0)
        List geometries = (1..10).collect{geom.buffer(it)}
        File file = folder.newFile("draw_geometries.png")
        def image = drawToImage(geometries, style: sym, bounds: new GeometryCollection(geometries).bounds.scale(1.1), size: [250,250])
        assertNotNull(image)
        ImageIO.write(image, "png", file)
        assertTrue file.exists()
        assertTrue file.length() > 0
        File expectedFile = getFile("geoscript/render/draw_geometries_to_image.png")
        ImageAssert.assertEquals(expectedFile, image, 100)
    }

    @Test void drawFeatureToImage() {
        Schema schema  = new Schema("shapes",[new Field("geom","Polygon"), new Field("name", "String")])
        Feature feature = new Feature([new LineString([0,0],[1,1]).bounds.polygon, "square"], "0",  schema)
        Symbolizer sym = new Stroke('navy', 0.1)
        File file = folder.newFile("draw_feature.png")
        def image = drawToImage(feature, style: sym, bounds: feature.geom.bounds.scale(1.1), size: [250,250])
        assertNotNull(image)
        ImageIO.write(image, "png", file)
        assertTrue file.exists()
        assertTrue file.length() > 0
        File expectedFile = getFile("geoscript/render/draw_feature_to_image.png")
        ImageAssert.assertEquals(expectedFile, image, 100)
    }

    @Test void drawLayerToImage() {
        Symbolizer sym = new Stroke('black', 0.1) + new Fill('gray', 0.75)
        File shpFile = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Layer layer = new Shapefile(shpFile)
        layer.style = sym
        File file = folder.newFile("draw_layer.png")
        def image = drawToImage(layer, bounds: layer.bounds.scale(1.1), size: [250,250])
        assertNotNull(image)
        ImageIO.write(image, "png", file)
        assertTrue file.exists()
        assertTrue file.length() > 0
        File expectedFile = getFile("geoscript/render/draw_layer_to_image.png")
        ImageAssert.assertEquals(expectedFile, image, 100)
    }

    @Test void drawRasterToImage() {
        File tifFile = new File(getClass().getClassLoader().getResource("alki.tif").toURI())
        GeoTIFF geoTIFF = new GeoTIFF(tifFile)
        Raster raster = geoTIFF.read()
        File file = folder.newFile("draw_raster.png")
        def image = drawToImage(raster, bounds: raster.bounds.scale(1.1), size: [250,250])
        assertNotNull(image)
        ImageIO.write(image, "png", file)
        assertTrue file.exists()
        assertTrue file.length() > 0
        File expectedFile = getFile("geoscript/render/draw_raster_to_image.png")
        ImageAssert.assertEquals(expectedFile, image, 100)
    }
}
