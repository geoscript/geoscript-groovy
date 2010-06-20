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

Layer shp = new Shapefile("sld_cookbook_line/sld_cookbook_line.shp")

createImage(shp, new Style(new LineSymbolizer(
    strokeColor: "#000000",
    strokeWidth: 3
)), new File("line_simple.png"))

createImage(shp, new Style([
    new SubStyle(new Rule(new LineSymbolizer(
            strokeColor: "#333333",
            strokeWidth: 5,
            strokeLineCap: "round"
    ))),
    new SubStyle(new Rule(new LineSymbolizer(
            strokeColor: "#6699FF",
            strokeWidth: 3,
            strokeLineCap: "round"
            
    )))
]), new File("line_border.png"))

createImage(shp, new Style(new LineSymbolizer(
    strokeColor: "#0000FF",
    strokeWidth: 3,
    strokeDashArray: "5 2"
)), new File("line_dashed.png"))

createImage(shp, new Style([
    new Rule(new LineSymbolizer(
            strokeColor: "#333333",
            strokeWidth: 3
    )),
    new Rule(new LineSymbolizer(
        graphicStrokeMarkName: "shape://vertline",
        graphicStrokeMarkStrokeColor: "#333333",
        graphicStrokeMarkStrokeWidth: 1,
        graphicStrokeMarkSize: 12
    ))
]), new File("line_railroad.png"))

createImage(shp, new Style([
    new LineSymbolizer(
        strokeColor: "#FF0000",
        strokeWidth: 1,
    ),
    new TextSymbolizer(
        field: shp.schema.get("name"),
        color: "#000000"
    )
]), new File("line_default_label.png"))

createImage(shp, new Style([
    new LineSymbolizer(
        strokeColor: "#FF0000",
        strokeWidth: 1,
    ),
    new TextSymbolizer(
        field: shp.schema.get("name"),
        color: "#000000",
        followLine: true
    )
]), new File("line_follow_label.png"))

createImage(shp, new Style([
    new LineSymbolizer(
        strokeColor: "#FF0000",
        strokeWidth: 1,
    ),
    new TextSymbolizer(
        field: shp.schema.get("name"),
        color: "#000000",
        followLine: true,
        maxAngleDelta: 90,
        maxDisplacement: 400,
        repeat: 150
    )
]), new File("line_follow_label_optimized.png"))

createImage(shp, new Style([
    new LineSymbolizer(
        strokeColor: "#FF0000",
        strokeWidth: 1,
    ),
    new TextSymbolizer(
        field: shp.schema.get("name"),
        color: "#000000",
        fontFamily: "Arial",
        fontSize: 10,
        fontStyle: "normal",
        fontWeight: "bold",
        followLine: true,
        maxAngleDelta: 90,
        maxDisplacement: 400,
        repeat: 150
    )
]), new File("line_follow_label_optimized_styled.png"))


Rule localRoadRule = new Rule(new LineSymbolizer(
    strokeColor: "#009933",
    strokeWidth: 2
))
localRoadRule.name = "local-road"
localRoadRule.filter = new Filter("type = 'local-road'")

Rule secondaryRoadRule = new Rule(new LineSymbolizer(
    strokeColor: "#0055CC",
    strokeWidth: 3
))
secondaryRoadRule.name = "secondary-road"
secondaryRoadRule.filter = new Filter("type = 'secondary'")

Rule highwayRoadRule = new Rule(new LineSymbolizer(
    strokeColor: "#550000",
    strokeWidth: 6
))
highwayRoadRule.name = "highway"
highwayRoadRule.filter = new Filter("type = 'highway'")

createImage(shp, new Style([
    new SubStyle(localRoadRule),
    new SubStyle(secondaryRoadRule),
    new SubStyle(highwayRoadRule)
]), new File("line_attribute_based.png"));

Rule largeRule = new Rule(new LineSymbolizer(
    strokeColor: "#009933",
    strokeWidth: 6
))
largeRule.name = "Large"
largeRule.maxScale = 1800000000

Rule mediumRule = new Rule(new LineSymbolizer(
    strokeColor: "#009933",
    strokeWidth: 4 
))
mediumRule.name = "Medium"
mediumRule.minScale = 1800000000
mediumRule.maxScale = 3600000000

Rule smallRule = new Rule(new LineSymbolizer(
    strokeColor: "#009933",
    strokeWidth: 2 
))
smallRule.name = "Small"
smallRule.minScale = 3600000000

createImage(shp, new Style([
    largeRule,
    mediumRule,
    smallRule
]), new File("line_zoom.png"))

