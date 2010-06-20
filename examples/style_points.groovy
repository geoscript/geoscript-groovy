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

Layer shp = new Shapefile("sld_cookbook_point/sld_cookbook_point.shp")

createImage(shp, new Style(new PointSymbolizer(
    shape: "circle",
    fillColor: "#FF0000",
    size: 6,
    strokeOpacity: 0
)), new File("point_simple.png"))

createImage(shp, new Style(new PointSymbolizer(
    shape: "circle",
    fillColor: "#FF0000",
    size: 6,
    strokeColor: "#000000",
    strokeWidth: 2
)), new File("point_simple_stroke.png"))

createImage(shp, new Style(new PointSymbolizer(
    shape: "square",
    fillColor: "#009900",
    size: 12,
    rotation: 45,
    strokeOpacity: 0
)), new File("point_rotated_square.png"))

createImage(shp, new Style(new PointSymbolizer(
    shape: "triangle",
    fillColor: "#009900",
    fillOpacity: 0.2,
    size: 12,
    strokeColor: "#000000",
    strokeWidth: 2
)), new File("point_transparent_triangle.png"))

createImage(shp, new Style(new PointSymbolizer(
    graphic: 'smileyface.png'
)), new File("point_graphic.png"))

createImage(shp, new Style([
    new PointSymbolizer(
            shape: "circle",
            fillColor: "#FF0000",
            size: 6,
            strokeOpacity: 0
    ),
    new TextSymbolizer(
        field: shp.schema.field("name"),
        color: "#000000"
    )
]), new File("point_label.png"))


createImage(shp, new Style([
    new PointSymbolizer(
            shape: "circle",
            fillColor: "#FF0000",
            size: 6,
            strokeOpacity: 0
    ),
    new TextSymbolizer(
        field: shp.schema.field("name"),
        color: "#000000",
        fontFamily: "Arial",
        fontSize: 12,
        fontStyle: "normal",
        fontWeight: "bold",
        anchorPointX: 0.5,
        anchorPointY: 0.5,
        displacementX: 0,
        displacementY: 10
    )
]), new File("point_label_styled.png"))

createImage(shp, new Style([
    new PointSymbolizer(
            shape: "circle",
            fillColor: "#FF0000",
            size: 6,
            strokeOpacity: 0
    ),
    new TextSymbolizer(
        field: shp.schema.field("name"),
        color: "#990099",
        fontFamily: "Arial",
        fontSize: 12,
        fontStyle: "normal",
        fontWeight: "bold",
        anchorPointX: 0.5,
        anchorPointY: 0,
        displacementX: 0,
        displacementY: 25,
        rotation: -45
    )
]), new File("point_label_rotated.png"))


Rule smallPopRule = new Rule(new PointSymbolizer(
    shape: "circle",
    size: 8,
    fillColor: "#0033CC",
    strokeOpacity: 0
))
smallPopRule.name = "SmallPop"
smallPopRule.title = "1 to 5000"
smallPopRule.filter = new Filter("pop < 5000")

Rule mediumPopRule = new Rule(new PointSymbolizer(
    shape: "circle",
    size: 12,
    fillColor: "#0033CC",
    strokeOpacity: 0
))
mediumPopRule.name = "MediumPop"
mediumPopRule.title = "5000 to 100000"
mediumPopRule.filter = new Filter("pop >= 5000 AND pop < 100000")

Rule largePopRule = new Rule(new PointSymbolizer(
    shape: "circle",
    size: 16,
    fillColor: "#0033CC",
    strokeOpacity: 0
))
largePopRule.name = "LargePop"
largePopRule.title = "Greater than 100000"
largePopRule.filter = new Filter("pop >= 100000")

Style style =  new Style([
    smallPopRule,
    mediumPopRule,
    largePopRule
])
createImage(shp, style, new File("point_attribute_based.png"))

// Zoom based scales
Rule largeRule = new Rule(new PointSymbolizer(
    shape: "circle",
    size: 12,
    fillColor: "#CC3300",
    strokeOpacity: 0
))
largeRule.name = "Large"
largeRule.maxScale = 160000000

Rule mediumRule = new Rule(new PointSymbolizer(
    shape: "circle",
    size: 8,
    fillColor: "#0033CC",
    strokeOpacity: 0
))
mediumRule.name = "Medium"
mediumRule.minScale = 160000000
mediumRule.maxScale = 320000000

Rule smallRule = new Rule(new PointSymbolizer(
    shape: "circle",
    size: 4,
    fillColor: "#0033CC",
    strokeOpacity: 0
))
smallRule.name = "Small"
smallRule.minScale = 320000000

createImage(shp, new Style([
    smallRule,
    mediumRule,
    largeRule
]), new File("point_zoom.png"))
