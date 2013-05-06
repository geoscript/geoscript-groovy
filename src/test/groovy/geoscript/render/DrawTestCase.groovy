package geoscript.render

import org.junit.Test
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

    @Test void drawGeometry() {
        Symbolizer sym = new Stroke('black', 2) + new Fill('gray',0.75)
        Geometry geom = new Point(0,0).buffer(0.2)
        File file = File.createTempFile("draw_geometry_",".png")
        println "Drawing Geometry: ${file}"
        draw(geom, style: sym, bounds: geom.bounds.scale(1.1), size: [250,250], out: file, format: "png")
    }

    @Test void drawGeometries() {
        Symbolizer sym = new Stroke('navy', 0.1)
        Geometry geom = new Point(0,0)
        List geometries = (1..10).collect{geom.buffer(it)}
        File file = File.createTempFile("draw_geometries_",".png")
        println "Drawing Geometries: ${file}"
        draw(geometries, style: sym, bounds: new GeometryCollection(geometries).bounds.scale(1.1), size: [250,250], out: file, format: "png")
    }

    @Test void drawFeature() {
        Schema schema  = new Schema("shapes",[new Field("geom","Polygon"), new Field("name", "String")])
        Feature feature = new Feature([new LineString([0,0],[1,1]).bounds.polygon, "square"], "0",  schema)
        Symbolizer sym = new Stroke('navy', 0.1)
        File file = File.createTempFile("draw_feature_",".png")
        println "Drawing Feature: ${file}"
        draw(feature, style: sym, bounds: feature.geom.bounds.scale(1.1), size: [250,250], out: file, format: "png")
    }

    @Test void drawLayer() {
        Symbolizer sym = new Stroke('black', 0.1) + new Fill('gray', 0.75)
        File shpFile = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Layer layer = new Shapefile(shpFile)
        layer.style = sym
        File file = File.createTempFile("draw_layer_",".png")
        println "Drawing Layer: ${file}"
        draw(layer, bounds: layer.bounds.scale(1.1), size: [250,250], out: file, format: "png")
    }

    @Test void drawRaster() {
        File tifFile = new File(getClass().getClassLoader().getResource("alki.tif").toURI())
        GeoTIFF geoTIFF = new GeoTIFF()
        Raster raster = geoTIFF.read(tifFile)
        File file = File.createTempFile("draw_raster_",".png")
        println "Drawing Raster: ${file}"
        draw(raster, bounds: raster.bounds.scale(1.1), size: [250,250], out: file, format: "png")
    }

    @Test void drawLayerToPdf() {
        Symbolizer sym = new Stroke('black', 0.1) + new Fill('gray', 0.75)
        File shpFile = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Layer layer = new Shapefile(shpFile)
        layer.style = sym
        File file = File.createTempFile("draw_layer_",".pdf")
        println "Drawing Layer: ${file}"
        draw(layer, bounds: layer.bounds.scale(1.1), size: [250,250], out: file, format: "pdf")
    }

    @Test void drawGeometryToImage() {
        Symbolizer sym = new Stroke('black', 2) + new Fill('gray',0.75)
        Geometry geom = new Point(0,0).buffer(0.2)
        File file = File.createTempFile("draw_geometry_",".png")
        println "Drawing Geometry to Image: ${file}"
        def image = drawToImage(geom, style: sym, bounds: geom.bounds.scale(1.1), size: [250,250])
        assertNotNull(image)
        ImageIO.write(image, "png", file)
    }

    @Test void drawGeometriesToImage() {
        Symbolizer sym = new Stroke('navy', 0.1)
        Geometry geom = new Point(0,0)
        List geometries = (1..10).collect{geom.buffer(it)}
        File file = File.createTempFile("draw_geometries_",".png")
        println "Drawing Geometries to Image: ${file}"
        def image = drawToImage(geometries, style: sym, bounds: new GeometryCollection(geometries).bounds.scale(1.1), size: [250,250])
        assertNotNull(image)
        ImageIO.write(image, "png", file)
    }

    @Test void drawFeatureToImage() {
        Schema schema  = new Schema("shapes",[new Field("geom","Polygon"), new Field("name", "String")])
        Feature feature = new Feature([new LineString([0,0],[1,1]).bounds.polygon, "square"], "0",  schema)
        Symbolizer sym = new Stroke('navy', 0.1)
        File file = File.createTempFile("draw_feature_",".png")
        println "Drawing Feature to Image: ${file}"
        def image = drawToImage(feature, style: sym, bounds: feature.geom.bounds.scale(1.1), size: [250,250])
        assertNotNull(image)
        ImageIO.write(image, "png", file)
    }

    @Test void drawLayerToImage() {
        Symbolizer sym = new Stroke('black', 0.1) + new Fill('gray', 0.75)
        File shpFile = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Layer layer = new Shapefile(shpFile)
        layer.style = sym
        File file = File.createTempFile("draw_layer_",".png")
        println "Drawing Layer to Image: ${file}"
        def image = drawToImage(layer, bounds: layer.bounds.scale(1.1), size: [250,250])
        assertNotNull(image)
        ImageIO.write(image, "png", file)
    }

    @Test void drawRasterToImage() {
        File tifFile = new File(getClass().getClassLoader().getResource("alki.tif").toURI())
        GeoTIFF geoTIFF = new GeoTIFF()
        Raster raster = geoTIFF.read(tifFile)
        File file = File.createTempFile("draw_raster_",".png")
        println "Drawing Raster to Image: ${file}"
        def image = drawToImage(raster, bounds: raster.bounds.scale(1.1), size: [250,250])
        assertNotNull(image)
        ImageIO.write(image, "png", file)
    }
}
