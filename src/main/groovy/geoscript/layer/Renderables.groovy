package geoscript.layer

import geoscript.style.RasterSymbolizer
import geoscript.style.Style
import geoscript.style.Symbolizer
import geoscript.style.io.Readers
import geoscript.workspace.Workspace
import groovy.util.logging.Log

/**
 * Get map layers or Rendables from a List of Maps or Strings.
 *
 * Maps or Strings can contain a layertype, layername, layerprojection, and style properties.
 *
 * layertype values include: layer, raster, or tile
 *
 * For layer layertype, you can use the same key value pairs used to specify a Workspace.
 *
 * For raster layertype, you specify a source=file key value pair.
 *
 * For tile layertype, you use the same key value pairs used to specify a tile layer.
 *
 * Examples:
 * <ol>
 *     <li> layertype=layer dbtype=geopkg database=/Users/user/Desktop/countries.gpkg layername=countries style=/Users/user/Desktop/countries.sld</li>
 *     <li> layertype=layer file=/Users/user/Desktop/geoc/polygons.csv layername=polygons style=/Users/user/Desktop/geoc/polygons.sld</li>
 *     <li> layertype=layer file=/Users/user/Desktop/geoc/points.properties style=/Users/user/Desktop/geoc/points.sld</li>
 *     <li> layertype=layer file=/Users/user/Projects/geoc/src/test/resources/polygons.shp</li>
 *     <li> layertype=layer directory=/Users/user/Projects/geoc/src/test/resources/points.properties layername=points</li>
 *     <li> layertype=raster source=rasters/earth.tif</li>
 *     <li> layertype=tile file=world.mbtiles</li>
 *     <li> layertype=tile type=geopackage file=states.gpkg</li>
 *  </ol>
 */
@Log
class Renderables {

    /**
     * Get a List of Renderable Map Layers from a List of Map Layer Strings
     * @param layers A List of Map Layer Strings
     * @return A List of Renderable Map Layers
     */
    static List<Renderable> getRenderables(List layers) {
        List<Renderable> renderables = []
        if (layers) {
            layers.each { Object layerDef ->
                log.info "Adding ${layerDef} to the Map!"
                Map layerMap = [:]
                if (layerDef instanceof Map) {
                    layerMap.putAll(layerDef as Map)
                } else {
                    layerMap.putAll(getParams(layerDef as String))
                }
                Renderable renderable = getRenderable(layerMap)
                if (renderable) {
                    renderables.add(renderable)
                } else {
                    log.warning "No Map Layer found for ${layerMap}!"
                }
            }
        }
        renderables
    }

    private static Renderable getRenderable(Map params) {
        log.info "getRenderable(${params})"
        Renderable renderable
        String layerType = params.get("layertype")
        String layerName = params.get("layername")
        String style = params.get("style")
        log.info "Layer Type = ${layerType} Layer Name = ${layerName} Style = ${style}"
        if (layerType.equalsIgnoreCase("layer")) {
            Workspace workspace = Workspace.getWorkspace(params)
            if (workspace) {
                log.info "Workspace = ${workspace.format}"
                Layer layer = workspace.get(layerName ?: workspace.names[0])
                log.info "Layer = ${layer}"
                if (params.layerprojection) {
                    layer.proj = params.layerprojection
                }
                if (style) {
                    layer.style = getStyle(layer, style)
                }
                renderable = layer
            } else if (params.containsKey("file")) {
                log.info "Reading layer from File = ${params['file']}"
                File file = new File(params.get("file"))
                if (file.exists()) {
                    // Try to use a Workspace first
                    try {
                        workspace = Workspace.getWorkspace(file.absolutePath)
                        if (workspace) {
                            log.info "Workspace = ${workspace.format}"
                            Layer layer = workspace.get(layerName ?: file.name)
                            log.info "Layer = ${layer}"
                            if (params.layerprojection) {
                                layer.proj = params.layerprojection
                            }
                            if (style) {
                                layer.style = getStyle(layer, style)
                            }
                            renderable = layer
                        }
                    } catch(Exception ex) {
                        // Just try the Layer Readers
                    }
                    // Then try to use a Layer Reader
                    if (!renderable) {
                        geoscript.layer.io.Readers.list().each {
                            try {
                                Layer layer = it.read(file)
                                if (layer) {
                                    log.info "Reading Layer using ${it.class.simpleName}"
                                    if (style) {
                                        layer.style = getStyle(layer, style)
                                    }
                                    renderable = layer
                                    return
                                }
                            } catch (Exception e2) {
                            }
                        }
                    }
                }
            }
        } else if (layerType.equalsIgnoreCase("raster")) {
            Format format = Format.getFormat(params.get("source"))
            if (format) {
                log.info "Format = ${format}"
                Raster raster
                if (layerName) {
                    log.info "Reading Raster for ${layerName}"
                    raster = format.read(layerName)
                } else {
                    log.info "Reading Raster"
                    raster = format.read()
                }
                if (raster) {
                    if (style) {
                        raster.style = getStyle(raster, style)
                    }
                    renderable = raster
                } else {
                    log.warning "Unable to read Raster from Format!"
                }
            } else {
                log.warning "Unable to Find Raster Format for ${params}"
            }
        } else if (layerType.equalsIgnoreCase("tile")) {
            TileLayer tileLayer = TileLayer.getTileLayer(params)
            if (tileLayer) {
                log.info "TileLayer = ${tileLayer.name}"
                renderable = tileLayer
            } else if (params.containsKey("file")) {
                File file = new File(params.get("file"))
                log.info "Load TileLayer from File = ${file}"
                if (file.exists()) {
                    tileLayer = TileLayer.getTileLayer(file.absolutePath)
                    if (tileLayer) {
                        log.info "TileLayer = ${tileLayer.name}"
                        renderable = tileLayer
                    }
                }
            }
            else {
                log.warning "Unable to find TileLayer from ${params}"
            }
        } else {
            log.info "UNKNOWN layertype='${layerType}'!"
        }
        renderable
    }

    private static Map getParams(String str) {
        Map params = [:]
        str.split("[ ]+(?=([^\']*\'[^\']*\')*[^\']*\$)").each {
            def parts = it.split("[=]+(?=([^\']*\'[^\']*\')*[^\']*\$)")
            def key = parts[0].trim()
            if ((key.startsWith("'") && key.endsWith("'")) ||
                    (key.startsWith("\"") && key.endsWith("\""))) {
                key = key.substring(1, key.length() - 1)
            }
            def value = parts[1].trim()
            if ((value.startsWith("'") && value.endsWith("'")) ||
                    (value.startsWith("\"") && value.endsWith("\""))) {
                value = value.substring(1, value.length() - 1)
            }
            params.put(key, value)
        }
        params
    }

    private static Style getStyle(Layer layer, String styleStr) {
        Style style = Symbolizer.getDefault(layer.schema.geom.typ)
        getStyle(style, styleStr)
    }

    private static Style getStyle(Raster raster, String styleStr) {
        Style style = new RasterSymbolizer()
        getStyle(style, styleStr)
    }

    private static Style getStyle(Style defaultStyle, String styleStr) {
        Style style = null
        File file = new File(styleStr)
        if (file.exists()) {
            if (file.name.endsWith(".sld")) {
                style = Readers.find("sld").read(file)
            } else if (file.name.endsWith(".css")){
                style = Readers.find("css").read(file)
            } else if (file.name.endsWith(".txt")){
                style = Readers.find("simple").read(file)
            } else if (file.name.endsWith(".yml") || file.name.endsWith(".ysld")){
                style = Readers.find("ysld").read(file)
            }
        }
        if (!style) {
            geoscript.style.io.Readers.list().each { geoscript.style.io.Reader reader ->
                try {
                    style = reader.read(styleStr)
                    return style
                }
                catch (Exception ex) {
                }
            }
        }
        if (!style) {
            style = defaultStyle
        }
        style
    }


}
