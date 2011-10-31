/**
 * GeoScript examples for the raster section of the GeoServer SLD Cookbook.
 *
 * http://docs.geoserver.org/stable/en/user/styling/sld-cookbook/index.html
 *
 * To run, download the example raster.
 *
 */
import geoscript.raster.GeoTIFF
import geoscript.style.*
import geoscript.render.Map

void createImage(GeoTIFF raster, Symbolizer symbolizer, File file) {
    symbolizer.asSLD()
    Map map = new Map()
    raster.style = symbolizer
    map.addRaster(raster)
    map.bounds = raster.bounds.expandBy(20)
    map.render(file)
    map.close()
}

GeoTIFF raster = new GeoTIFF(new File("sld_cookbook_raster/raster.tif"))

// Two-color gradient
createImage(raster, new ColorMap([[color: "#008000", quantity: 70], [color: "#663333", quantity: 256]]), new File("raster_twocolorgradient.png"))

// Transparent gradient
createImage(raster, new ColorMap([[color: "#008000", quantity: 70], [color: "#663333", quantity: 256]]).opacity(0.3), new File("raster_transparentgradient.png"))

// Brightness and contrast
createImage(raster, new ColorMap([[color: "#008000", quantity: 70], [color: "#663333", quantity: 256]]) + new ContrastEnhancement("normalize", 0.5), new File("raster_brighnessandcontrast.png"))

// Three-color gradient
createImage(raster, new ColorMap([[color: "#0000FF", quantity: 150], [color: "#FFFF00", quantity: 200], [color: "#FF0000", quantity: 250]]), new File("raster_threecolorgradient.png"))

// Alpha Channel
createImage(raster, new ColorMap([[color: "#008800", quantity: 70], [color: "#008000", quantity: 256, opacity: 0]]), new File("raster_alphachannel.png"))

// Discrete Colors
createImage(raster, new ColorMap([[color: "#008000", quantity: 150], [color: "#663333", quantity: 256]], "intervals"), new File("raster_discretecolors.png"))

// Many color gradient
createImage(raster, new ColorMap([
        [color: "#000000", quantity: 95],
        [color: "#0000FF", quantity: 110],
        [color: "#00FF00", quantity: 135],
        [color: "#FF0000", quantity: 160],
        [color: "#FF00FF", quantity: 185],
        [color: "#FFFF00", quantity: 210],
        [color: "#00FFFF", quantity: 235],
        [color: "#FFFFFF", quantity: 256]
]), new File("raster_manycolorgradient.png"))