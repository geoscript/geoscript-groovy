import geoscript.layer.*
import geoscript.filter.Filter
import geoscript.geom.Bounds
import geoscript.style.*
import geoscript.map.Map

void createImage(Layer layer, Style style, File file) {
    //style.toSLD()
    Map map = new Map()
    map.addLayer(layer, style)
    map.render(layer.bounds().expandBy(20), file)
}

Layer shp = new Shapefile("sld_cookbook_polygon/sld_cookbook_polygon.shp")

createImage(shp, new Style(new PolygonSymbolizer(
    fillColor: "#000080",
    strokeOpacity: 0
)), new File("polygon_simple.png"))

createImage(shp, new Style(new PolygonSymbolizer(
    fillColor: "#000080",
    strokeColor: "#FFFFFF",
    strokeWidth: 2
)), new File("polygon_stroke.png"))

createImage(shp, new Style(new PolygonSymbolizer(
    fillColor: "#000080",
    fillOpacity: 0.5,
    strokeColor: "#FFFFFF",
    strokeWidth: 2
)), new File("polygon_transparent.png"))

createImage(shp, new Style(new PolygonSymbolizer(
    graphic: "colorblocks.png",
    strokeOpacity: 0
)), new File("polygon_graphic.png"))

createImage(shp, new Style(new PolygonSymbolizer(
    markName: "shape://times",
    markStrokeColor: "#990099",
    markStrokeWidth: 1,
    strokeOpacity: 0
)), new File("polygon_hatching.png"))

createImage(shp, new Style([
    new PolygonSymbolizer(
            fillColor: "#40FF40",
            strokeColor: "#FFFFFF",
            strokeWidth: 1
    ),
    new TextSymbolizer(
        field: shp.schema.get("name")
    )
]), new File("polygon_default_label.png"))

createImage(shp, new Style([
    new PolygonSymbolizer(
            fillColor: "#40FF40",
            strokeColor: "#FFFFFF",
            strokeWidth: 1
    ),
    new TextSymbolizer(
        field: shp.schema.get("name"),
        haloColor: "#FFFFFF",
        haloRadius: 3
    )
]), new File("polygon_halo_label.png"))

createImage(shp, new Style([
    new PolygonSymbolizer(
            fillColor: "#40FF40",
            strokeColor: "#FFFFFF",
            strokeWidth: 2 
    ),
    new TextSymbolizer(
        field: shp.schema.get("name"),
        fontFamily: "Arial",
        fontSize: 11,
        fontStyle: "normal",
        fontWeight: "bold",
        anchorPointX: 0.5,
        anchorPointY: 0.5,
        color: "#000000",
        autoWrap: 30,
        maxDisplacement: 150
    )
]), new File("polygon_styled_label.png"))

Rule smallPopRule = new Rule(new PolygonSymbolizer(
    fillColor: "#66FF66",
    strokeOpacity: 0
))
smallPopRule.name = "SmallPop"
smallPopRule.title = "Less than 200,000"
smallPopRule.filter = new Filter("pop < 200000")

Rule mediumPopRule = new Rule(new PolygonSymbolizer(
    fillColor: "#33CC33",
    strokeOpacity: 0
))
mediumPopRule.name = "MediumPop"
mediumPopRule.title = "200,000 to 500,000"
mediumPopRule.filter = new Filter("pop >= 200000 and pop < 500000")

Rule largePopRule = new Rule(new PolygonSymbolizer(
    fillColor: "#009900",
    strokeOpacity: 0
))
largePopRule.name = "LargePop"
largePopRule.title = "Greater Than 500,000"
largePopRule.filter = new Filter("pop > 500000")

createImage(shp, new Style([
    smallPopRule,
    mediumPopRule,
    largePopRule
]), new File("polygon_attribute.png"))

Rule largeRule = new Rule([
    new PolygonSymbolizer(
            fillColor: "#0000CC",
            strokeColor: "#000000",
            strokeWidth: 7
    ),
    new TextSymbolizer(
        field: shp.schema.get("name"),
        fontFamily: "Arial",
        fontSize: 14,
        fontStyle: "normal",
        fontWeight: "bold",
        anchorPointX: 0.5,
        anchorPointY: 0.5,
        color: "#FFFFFF"
    )
])
largeRule.name = "Large"
largeRule.maxScale = 100000000

Rule mediumRule = new Rule(new PolygonSymbolizer(
    fillColor: "#0000CC",
    strokeColor: "#000000",
    strokeWidth: 4
))
mediumRule.name = "Medium"
mediumRule.minScale = 100000000
mediumRule.maxScale = 200000000

Rule smallRule = new Rule(new PolygonSymbolizer(
    fillColor: "#0000CC",
    strokeColor: "#000000",
    strokeWidth: 1
))
smallRule.name = "Small"
smallRule.minScale = 200000000

createImage(shp, new Style([
    smallRule,
    mediumRule,
    largeRule
]), new File("polygon_zoom.png"))

