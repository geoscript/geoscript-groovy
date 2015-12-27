package geoscript.layer

import geoscript.geom.Bounds

/**
 * An Image TileRenderer that uses a geoscript.render.Map
 * @author Jared Erickson
 */
class ImageTileRenderer implements TileRenderer {

    /**
     * The Map used to render images
     */
    private geoscript.render.Map map

    /**
     * Create an ImageTileRenderer
     * @param options The optional named parameters
     * <ul>
     *    <li>imageType = The image type.  Defaults to PNG.</li>
     * </ul>
     * @param tileLayer The TileLayer
     * @param layers A Layer or a List of Layers
     */
    ImageTileRenderer(Map options = [:], TileLayer tileLayer, def layers) {
        this.map  = new geoscript.render.Map(
                fixAspectRatio: false,
                proj: tileLayer.proj,
                width: tileLayer.pyramid.tileWidth,
                height: tileLayer.pyramid.tileHeight,
                type: options.get("imageType", "png"),
                layers: layers instanceof List ? layers : [layers],
                bounds: tileLayer.bounds
        )
    }

    @Override
    byte[] render(Bounds b) {
        map.bounds = b
        def out = new ByteArrayOutputStream()
        map.render(out)
        out.close()
        out.toByteArray()
    }
}
