/**
 * GeoScript examples for the point section of the GeoServer SLD Cookbook.
 *
 * http://docs.geoserver.org/stable/en/user/styling/sld-cookbook/index.html
 * 
 * To run, download the point shapefile.
 *
 */
import geoscript.layer.*
import geoscript.filter.Filter
import geoscript.geom.Bounds
import geoscript.style.*
import geoscript.map.Map

void createImage(Layer layer, Style style, File file) {
    Map map = new Map()
    layer.style = style
    map.addLayer(layer)
    map.bounds = layer.bounds.expandBy(20)
    map.render(file)
    map.close()
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
        label: "name",
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
        label: "name",
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
        label: "name",
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


Rule smallPopRule = new Rule(
    symbolizers: [
        new PointSymbolizer(
            shape: "circle",
            size: 8,
            fillColor: "#0033CC",
            strokeOpacity: 0
        )
    ],
    filter:  new Filter("pop < 5000")
)

Rule mediumPopRule = new Rule(
    symbolizers: [
        new PointSymbolizer(
            shape: "circle",
            size: 12,
            fillColor: "#0033CC",
            strokeOpacity: 0
        )
    ],
    filter: new Filter("pop >= 5000 AND pop < 100000")
)

Rule largePopRule = new Rule(
    symbolizers: [
         new PointSymbolizer(
            shape: "circle",
            size: 16,
            fillColor: "#0033CC",
            strokeOpacity: 0
        )
    ],
    filter: new Filter("pop >= 100000")
)

Style style =  new Style([
    smallPopRule,
    mediumPopRule,
    largePopRule
])
createImage(shp, style, new File("point_attribute_based.png"))

// Zoom based scales
Rule largeRule = new Rule(
    symbolizers: [
        new PointSymbolizer(
            shape: "circle",
            size: 12,
            fillColor: "#CC3300",
            strokeOpacity: 0
        )
    ],
    name: "Large",
    maxScaleDenominator: 160000000
)

Rule mediumRule = new Rule(
    symbolizers: [
        new PointSymbolizer(
            shape: "circle",
            size: 8,
            fillColor: "#0033CC",
            strokeOpacity: 0
        )
    ],
    name: "Medium",
    minScaleDenominator: 160000000,
    maxScaleDenominator: 320000000
)

Rule smallRule = new Rule(
    symbolizers: [
        new PointSymbolizer(
            shape: "circle",
            size: 4,
            fillColor: "#0033CC",
            strokeOpacity: 0
        )
    ],
    name: "Small",
    minScaleDenominator: 320000000
)
createImage(shp, new Style([
    smallRule,
    mediumRule,
    largeRule
]), new File("point_zoom.png"))
