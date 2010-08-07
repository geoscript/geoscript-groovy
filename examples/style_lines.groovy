/**
 * GeoScript examples for the line section of the GeoServer SLD Cookbook.
 *
 * http://docs.geoserver.org/stable/en/user/styling/sld-cookbook/index.html
 *
 * To run, download the line shapefile.
 *
 */
import geoscript.layer.*
import geoscript.filter.Filter
import geoscript.geom.Bounds
import geoscript.style.*
import geoscript.map.Map

void createImage(Layer layer, Style style, File file) {
    style.toSLD()
    Map map = new Map()
    layer.style = style
    map.addLayer(layer)
    map.bounds = layer.bounds.expandBy(20)
    map.render(file)
    map.close()
}

Layer shp = new Shapefile("sld_cookbook_line/sld_cookbook_line.shp")

createImage(shp, new Style(new LineSymbolizer(
    strokeColor: "#000000",
    strokeWidth: 3
)), new File("line_simple.png"))

createImage(shp, new Style([
    new LineSymbolizer(
        strokeColor: "#333333",
        strokeWidth: 5,
        strokeLineCap: "round",
        zIndex: 0
    ),
    new LineSymbolizer(
        strokeColor: "#6699FF",
        strokeWidth: 3,
        strokeLineCap: "round",
        zIndex: 1
    )
]), new File("line_border.png"))

createImage(shp, new Style(new LineSymbolizer(
    strokeColor: "#0000FF",
    strokeWidth: 3,
    strokeDashArray: "5 2"
)), new File("line_dashed.png"))

createImage(shp, new Style([
    new LineSymbolizer(
            strokeColor: "#333333",
            strokeWidth: 3
    ),
    new LineSymbolizer(
        graphicStrokeMarkName: "shape://vertline",
        graphicStrokeMarkStrokeColor: "#333333",
        graphicStrokeMarkStrokeWidth: 1,
        graphicStrokeMarkSize: 12
    )
]), new File("line_railroad.png"))

createImage(shp, new Style([
    new LineSymbolizer(
        strokeColor: "#FF0000",
        strokeWidth: 1,
    ),
    new TextSymbolizer(
        label: "name",
        color: "#000000"
    )
]), new File("line_default_label.png"))

createImage(shp, new Style([
    new LineSymbolizer(
        strokeColor: "#FF0000",
        strokeWidth: 1,
    ),
    new TextSymbolizer(
        label: "name",
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
        label: "name",
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
        label: "name",
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


Rule localRoadRule = new Rule(
    symbolizers: [
        new LineSymbolizer(
            strokeColor: "#009933",
            strokeWidth: 2
        )
    ],
    name: "local-road",
    filter: new Filter("type = 'local-road'")
)

Rule secondaryRoadRule = new Rule(
    symbolizers:[
        new LineSymbolizer(
            strokeColor: "#0055CC",
            strokeWidth: 3
        )
    ],
    name: "secondary-road",
    filter: new Filter("type = 'secondary'")
)

Rule highwayRoadRule = new Rule(
    symbolizers: [
        new LineSymbolizer(
            strokeColor: "#550000",
            strokeWidth: 6
        )
    ],
    name: "highway",
    filter: new Filter("type = 'highway'")
)
createImage(shp, new Style([
    localRoadRule,
    secondaryRoadRule,
    highwayRoadRule
]), new File("line_attribute_based.png"));

Rule largeRule = new Rule(
    symbolizers: [
        new LineSymbolizer(
            strokeColor: "#009933",
            strokeWidth: 6
        )
    ],
    name: "Large",
    maxScaleDenominator: 1800000000
)

Rule mediumRule = new Rule(
    symbolizers: [
        new LineSymbolizer(
            strokeColor: "#009933",
            strokeWidth: 4
        )
    ],
    name: "Medium",
    minScaleDenominator: 1800000000,
    maxScaleDenominator: 3600000000
)

Rule smallRule = new Rule(
    symbolizers: [
        new LineSymbolizer(
            strokeColor: "#009933",
            strokeWidth: 2
        )
    ],
    name: "Small",
    minScaleDenominator: 3600000000
)

createImage(shp, new Style([
    largeRule,
    mediumRule,
    smallRule
]), new File("line_zoom.png"))


createImage(shp, new Style([
    new LineSymbolizer(
        strokeColor: "#000000",
        strokeWidth: 15,
        strokeLineJoin: "round",
        strokeLineCap: "round",
        zIndex: 0
    ),
    new LineSymbolizer(
        graphicStrokeMarkName: "circle",
        graphicStrokeMarkStrokeColor: "red",
        graphicStrokeMarkFillColor: "red",
        strokeDashArray: "10 20",
        strokeDashOffset: 0,
        zIndex: 1
    ),
    new LineSymbolizer(
        graphicStrokeMarkName: "star",
        graphicStrokeMarkStrokeColor: "blue",
        graphicStrokeMarkFillColor: "blue",
        strokeDashArray: "10 20",
        strokeDashOffset: 15,
        zIndex: 1
    )
]), new File("line_stardot.png"))
