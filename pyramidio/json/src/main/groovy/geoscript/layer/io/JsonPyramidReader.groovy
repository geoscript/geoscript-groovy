package geoscript.layer.io

import geoscript.geom.Bounds
import geoscript.layer.Grid
import geoscript.layer.Pyramid
import geoscript.proj.Projection
import groovy.json.JsonSlurper

/**
 * Read a Pyramid from a JSON String.
 * @author Jared Erickson
 */
class JsonPyramidReader implements PyramidReader {

    /**
     * Read a Pyramid from a JSON String.
     * @param str The JSON String
     * @return A Pyramid
     */
    @Override
    Pyramid read(String str) {
        JsonSlurper slurper = new JsonSlurper()
        Map json = slurper.parseText(str)
        Projection projection = new Projection(json.proj)
        Bounds bounds = new Bounds(json.bounds.minX, json.bounds.minY, json.bounds.maxX, json.bounds.maxY, projection)
        List<Grid> grids = json.grids.collect { def jsonGrid ->
            new Grid(jsonGrid.z, jsonGrid.width, jsonGrid.height, jsonGrid.xres, jsonGrid.yres)
        }
        Pyramid.Origin origin = Pyramid.Origin.valueOf(json.origin)
        int tileWidth = json.tileSize.width
        int tileHeight = json.tileSize.height
        new Pyramid(proj: projection, bounds: bounds, grids: grids, origin: origin, tileWidth: tileWidth, tileHeight: tileHeight)
    }
}
