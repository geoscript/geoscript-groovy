/**
 * GeoScript examples for the point section of the GeoServer SLD Cookbook.
 *
 * http://docs.geoserver.org/stable/en/user/styling/sld-cookbook/index.html
 * 
 * To run, download the point shapefile.
 *
 */
import geoscript.layer.*
import geoscript.style.*
import geoscript.map.Map

void createImage(Layer layer, Symbolizer symbolizer, File file) {
    Map map = new Map()
    layer.style = symbolizer
    map.addLayer(layer)
    map.bounds = layer.bounds.expandBy(20)
    map.render(file)
    map.close()
}

Layer shp = new Shapefile("sld_cookbook_point/sld_cookbook_point.shp")

// Simple point
createImage(shp, new Shape("#FF0000", 6, "circle"), new File("point_simple.png"))

// Simple point with stroke
createImage(shp, new Shape("#FF0000", 6, "circle").stroke("#000000",2), new File("point_simple_stroke.png"))

// Simple point with true type font
createImage(shp, new Shape("navy", 18, "ttf://Wingdings#0xF054"), new File("point_simple_ttf.png"))

// Rotated square
createImage(shp, new Shape("#009900", 12, "square", 1.0, 45), new File("point_rotated_square.png"))

// Transparent triangle
createImage(shp, new Shape("#009900", 12, "triangle", 0.2).stroke("#000000",2), new File("point_transparent_triangle.png"))

// Point as graphic
createImage(shp, new Icon("smileyface.png", "image/png"), new File("point_graphic.png"))

// Point as graphic with SVG
createImage(shp, new Icon("accommodation_camping.svg", "image/svg", 12), new File("point_graphic_svg.png"))

// Point with default label
createImage(shp, new Shape("#FF0000", 6, "circle") + new Label("name").fill(new Fill("#000000")), new File("point_label.png"))

// Point with styled label
createImage(shp, new Shape("#FF0000", 6, "circle") + new Label("name").font(new Font("normal","bold",12,"Arial")).fill(new Fill("#000000")).point([0.5,0.5],[0,5]), new File("point__styled_label.png"))

// Point with rotated label
createImage(shp, new Shape("#FF0000", 6, "circle") + new Label("name").font(new Font("normal","bold",12,"Arial")).fill(new Fill("#000000")).point([0.5,0.5],[0,5], -45), new File("point_label_rotated.png"))

// Attribute-based point
createImage(shp, new Shape("#0033CC", 8, "circle").where("pop < 50000") + new Shape("#0033CC", 12, "circle").where("pop >= 50000 AND pop < 100000") + new Shape("#0033CC", 16, "circle").where("pop >= 100000"), new File("point_attribute_based.png"))

// Zoom-based point
createImage(shp, new Shape("#CC3300", 8, "circle").range(0, 160000000) + new Shape("#CC3300", 12, "circle").range(160000000,320000000) + new Shape("#CC3300", 16, "circle").range(320000000), new File("point_zoom.png"))