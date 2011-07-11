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

void createImage(Layer layer, Symbolizer symbolizer, File file) {
    symbolizer.asSLD()
    Map map = new Map()
    layer.style = symbolizer
    map.addLayer(layer)
    map.bounds = layer.bounds.expandBy(20)
    map.render(file)
    map.close()
}

Layer shp = new Shapefile("sld_cookbook_line/sld_cookbook_line.shp")

// Simple Line
createImage(shp, new Stroke("#000000", 3), new File("line_simple.png"))

// Line with border
createImage(shp, new Stroke("#333333", 5, null, "round").zindex(0) + new Stroke("#6699FF", 3, null, "round").zindex(1), new File("line_border.png")) 

// Dashed line
createImage(shp, new Stroke("#0000FF", 3, [5,2]), new File("line_dashed.png"))

// Railroad (hatching)
createImage(shp, new Stroke("#333333", 3) + new Hatch("vertline", new Stroke("#333333", 1), 12).zindex(1), new File("line_railroad.png"))

// Spaced graphic symbols
createImage(shp, new Stroke(null, 0, [4, 6]).shape(new Shape("#666666", 4, "circle").stroke("#333333", 1)), new File("line_spaced_graphics.png"))

// Alternating symbols with dash offsets
createImage(shp, new Stroke("#0000FF", 1, [10,10]).zindex(0) + new Stroke(null, 0, [[5,15],7.5]).shape(new Shape(null, 5, "circle").stroke("#000033",1)).zindex(1), new File("line_alternating_symbols.png"))

// Line with default labels
createImage(shp, new Stroke("#FF0000",1) + new Label("name"), new File("line_default_label.png"))

// Label following line
createImage(shp, new Stroke("#FF0000",1) + new Label("name").linear(0, null, null, false, true), new File("line_label_following.png"))

// Optimized label placement
createImage(shp, new Stroke("#FF0000",1) + new Label("name").linear(0, null, null, false, true, false, 400, 150).maxAngleDelta(90), new File("line_optimized_labels.png"))

// Optimized  and styled label
createImage(shp, new Stroke("#FF0000",1) + new Label("name").linear(0, null, null, false, true, false, 400, 150).maxAngleDelta(90).font(new Font("normal", "bold", 10, "Arial")), new File("line_optimized_styled_labels.png"))

// Attribute based line
createImage(shp, new Stroke("#009933", 2).where("type='local-road'").zindex(1) +
        new Stroke("#0055CC", 3).where("type='secondary'").zindex(2) +
        new Stroke("#FF0000", 6).where("type='highway'").zindex(3), new File("line_attribute_based.png")
)

// Zoom based line
createImage(shp, new Stroke("#009933", 6).range(-1, 180000000) +
    new Stroke("#009933", 4).range(180000000, 360000000) +
    new Stroke("#009933", 2).range(360000000),
    new File("line_zoom_based.png")
)

// Line with alternating star and dots
createImage(shp, new Stroke(color: "#000000", width: 15, join: "round", cap: "round").zindex(0) +
        new Stroke(shape: new Shape(color: "red", size: 10, type: "circle"), dash: [[10,20],0]).zindex(1) +
        new Stroke(shape: new Shape(color: "blue", size: 10, type: "star"), dash: [[10,20],15]).zindex(2),
        new File("line_star_dots.png")
)