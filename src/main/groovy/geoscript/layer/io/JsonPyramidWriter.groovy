package geoscript.layer.io

import geoscript.layer.Grid
import geoscript.layer.Pyramid
import groovy.json.JsonBuilder

/**
 * Write a Pyramid to a JSON String.
 * @author Jared Erickson
 */
class JsonPyramidWriter implements PyramidWriter {

    /**
     * Write a Pyramid to a JSON String.
     * @param pyramid The Pyramid
     * @return A JSON String
     */
    @Override
    String write(Pyramid pyramid) {
        JsonBuilder builder = new JsonBuilder()
        builder {
            proj "${pyramid.proj}"
            bounds {
                minX pyramid.bounds.minX
                minY pyramid.bounds.minY
                maxX pyramid.bounds.maxX
                maxY pyramid.bounds.maxY
            }
            origin pyramid.origin
            tileSize {
                width pyramid.tileWidth
                height pyramid.tileHeight
            }
            grids pyramid.grids.collect { Grid g ->
                builder {
                    z g.z
                    width g.width
                    height g.height
                    xres g.xResolution
                    yres g.yResolution
                }
            }
        }
        builder.toPrettyString()
    }
}
