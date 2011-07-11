/**
 * GeoScript examples for the polygon section of the GeoServer SLD Cookbook.
 *
 * http://docs.geoserver.org/stable/en/user/styling/sld-cookbook/index.html
 * 
 * To run, download the polygon shapefile.
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

Layer shp = new Shapefile("sld_cookbook_polygon/sld_cookbook_polygon.shp")

// Simple Polygon
createImage(shp, new Fill("#000080"), new File("polygon_simple.png"))

// Simple polygon with stroke
createImage(shp, new Fill("#000080") + new Stroke("#FFFFFF", 2), new File("polygon_simple_stroke.png"))

// Transparent polygon
createImage(shp, new Fill("#000080",0.5) + new Stroke("#FFFFFF", 2), new File("polygon_transparent.png"))

// Graphic Fill
createImage(shp, new Fill(null).icon("colorblocks.png", "image/png"), new File("polygon_graphicfill.png"))

// Hatching Fill
createImage(shp, new Fill(null).hatch("times", new Stroke("#990099",1), 16), new File("polygon_hatchingfill.png"))

// Polygon with default label
createImage(shp, new Fill("#40FF40") + new Stroke("#FFFFFF",2) + new Label("name"), new File("polygon_label.png"))

// Label halo
createImage(shp, new Fill("#40FF40") + new Stroke("#FFFFFF",2) + new Label("name").halo(new Fill("#FFFFFF"),3), new File("polygon_label_halo.png"))

// Polygon with styled label
createImage(shp, new Fill("#40FF40") + new Stroke("#FFFFFF",2) + new Label("name").font(new Font("normal","bold", 11, "Arial")).point([0.5,0.5]).fill(new Fill("#000000")).autoWrap(60).maxDisplacement(150), new File("polygon_styled_label.png"))

// Attribute based polygon
createImage(shp, new Fill("#66FF66").where("pop < 200000") + new Fill("#33CC33").where("pop >= 200000 and pop < 500000") + new Fill("#009900").where("pop > 500000") , new File("polygon_attribute_based.png"))

// Zoom based polygon
createImage(shp, (new Fill("#0000CC") + new Stroke("#000000",7) + new Label("name").font(new Font("normal","bold",14,"Arial")).point([0.5,0.5]).fill(new Fill("#FFFFFF"))).range(-1, 100000000) +
    (new Fill("#0000CC") + new Stroke("#000000",4)).range(100000000, 200000000) +
    (new Fill("#0000CC") + new Stroke("#000000",1)).range(200000000)
, new File("polygon_zoom_based.png"))

// Unique value style 
// createImage(shp, Style.createUniqueValuesStyle(shp, "name"), new File("polygon_uniquevalue.png"))

// Graduated color style 
// createImage(shp, Style.createGraduatedStyle(shp, "name", "equalinterval", 4, "Greens"), new File("polygon_graduated.png")) */
