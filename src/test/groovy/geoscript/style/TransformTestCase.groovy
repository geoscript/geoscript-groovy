package geoscript.style

import org.junit.Test
import static org.junit.Assert.*
import geoscript.filter.*
import geoscript.geom.GeometryCollection
import geoscript.layer.Layer
import geoscript.raster.GeoTIFF

/**
 * The Transform Unit Test
 * @author Jared Erickson
 */
class TransformTestCase {

    @Test void constructors() {

        Transform transform = new Transform("centroid(the_geom)")
        assertNotNull transform.function
        assertEquals "Transform(function = centroid([the_geom]))", transform.toString()

        Transform transform1 = new Transform(new Function("myCentroid", {g -> g.centroid}))
        assertNotNull transform1.function
        assertEquals "Transform(function = myCentroid())", transform1.toString()

        Transform transform3 = new Transform(new Function("centroid(the_geom)"))
        assertNotNull transform3.function
        assertEquals "Transform(function = centroid([the_geom]))", transform3.toString()
    }

    @Test void appy() {
        def pointSym = Symbolizer.styleFactory.createPointSymbolizer();
        Transform centroidTransform = new Transform(new Function("myCentroid", {g -> g.centroid}))
        centroidTransform.apply(pointSym)
        assertTrue pointSym.geometry instanceof org.geotools.filter.FunctionImpl

        def textSym = Symbolizer.styleFactory.createTextSymbolizer()
        Transform upperCaseTransform = new Transform(new Function("myUpperCase", {str -> str.toUpperCase()}))
        upperCaseTransform.apply(textSym)
        assertTrue textSym.label instanceof org.geotools.filter.FunctionImpl
    }

    @Test void prepare() {
        def rule = Symbolizer.styleFactory.createRule()
        rule.symbolizers().add(Symbolizer.styleFactory.createPointSymbolizer())
        rule.symbolizers().add(Symbolizer.styleFactory.createTextSymbolizer())

        Transform centroidTransform = new Transform(new Function("myCentroid", {g -> g.centroid}))
        Transform upperCaseTransform = new Transform(new Function("myUpperCase", {str -> str.toUpperCase()}))
        centroidTransform.prepare(rule)
        upperCaseTransform.prepare(rule)

        assertTrue rule.symbolizers()[0].geometry instanceof org.geotools.filter.FunctionImpl
        assertTrue rule.symbolizers()[1].label instanceof org.geotools.filter.FunctionImpl
    }

    @Test void rendering() {

        // Create a Rendering transformation that calculates the minimum rectangle
        // for the entire Layer
        Transform t = new Transform(new Function("minimumRectangle", { Layer layer ->
            def features = layer.features
            def geom = new GeometryCollection(features.collect{f->f.geom})
            def newLayer = new Layer()
            newLayer.add([geom.minimumRectangle])
            newLayer
        }), Transform.RENDERING)

        // Make sure the FeatureTypeStyle's transformation property is set
        // instead of the Symbolizer's geometry or text property
        def fts = Symbolizer.styleFactory.createFeatureTypeStyle()
        def rule = Symbolizer.styleFactory.createRule()
        t.prepare(fts, rule)
        assertNotNull fts.transformation

        // Make sure that the rendering system can handle the resulting transformation
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        def shp1 = new geoscript.layer.Shapefile(file)
        shp1.style = new Stroke("navy",1.0) + t
        def shp2 = new geoscript.layer.Shapefile(file)
        shp2.style = new Stroke("#666666",0.2)
        def shp3 = new geoscript.layer.Shapefile(file)
        shp3.style = new Shape("navy",6,"circle") + new Transform(new Function("centroid(the_geom)"))
        def map = new geoscript.render.Map(layers:[shp1, shp2, shp3], bounds: shp1.bounds.expandBy(1.5))
        File out = File.createTempFile("map",".png")
        println("Rendering Transform: ${out}")
        map.render(out)
        map.close()
    }

    @Test void rasterRendering() {

        Function f = new Function("raster2points", { geoscript.raster.Raster raster ->
            def pointLayer = raster.toPoints()
            println "Point Layer: ${pointLayer}"
            return pointLayer
        }) 
        Transform t = new Transform(f, Transform.RENDERING)

        def fts = Symbolizer.styleFactory.createFeatureTypeStyle()
        def rule = Symbolizer.styleFactory.createRule()
        assertNull fts.transformation
        t.prepare(fts, rule)
        assertNotNull fts.transformation

        File file = new File(getClass().getClassLoader().getResource("raster.tif").toURI())
        assertNotNull(file)

        def raster = new GeoTIFF(file)
        raster.style = new  geoscript.style.ColorMap([[color: "#008000", quantity:70], [color:"#663333", quantity:256]])

        def raster2 = new GeoTIFF(file)
        def sym2 = new Shape("red", 6, "circle") + t
        raster2.style = sym2

        def map = new geoscript.render.Map()
        map.addRaster(raster)
        map.addRaster(raster2)
        File out = File.createTempFile("raster",".png")
        println("renderDemRaster: ${out}")
        map.render(out)
        assertTrue(out.exists())
        map.close()
    }
}
