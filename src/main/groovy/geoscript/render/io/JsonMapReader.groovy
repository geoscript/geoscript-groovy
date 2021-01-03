package geoscript.render.io

import geoscript.geom.Bounds
import geoscript.layer.Renderables
import geoscript.render.Map as GMap
import groovy.json.JsonSlurper

/**
 * Read a Map from a JSON String.
 * <pre>
 * {
 *    "width": 400,
 *    "height": 400,
 *    "type": "png",
 *    "backgroundColor": "blue",
 *    "proj": "EPSG:4326",
 *    "bounds": {
 *        "minX": -135.911779,
 *        "minY": 36.993573,
 *        "maxX": -96.536779,
 *        "maxY": 51.405899
 *    },
 *    "layers": [
 *        {"layertype": "layer", "file": "states.shp"}
 *    ]
 * }
 * </pre>
 * @author Jared Erickson
 */
class JsonMapReader implements MapReader {

    @Override
    String getName() {
        "json"
    }

    @Override
    GMap read(String str) {
        JsonSlurper jsonSlurper = new JsonSlurper()
        Map values = jsonSlurper.parseText(str)
        read(values)
    }

    /**
     * Read a GeoScript Map from a map of values
     * @param values A Map of values
     * @return A GeoScript Map
     */
    GMap read(Map values) {
        GMap map = new GMap(
                width: values.get("width", 600),
                height: values.get("height", 400),
                type: values.get("type", "png"),
                backgroundColor: values.get("backgroundColor"),
                fixAspectRatio: values.get("fixAspectRation", true),
                layers: Renderables.getRenderables(values.get("layers"))
        )
        if (values.get("proj")) {
            map.proj = values.get("proj")
        }
        if (values.get("bounds")) {
            Map bounds = values.get("bounds")
            map.bounds = new Bounds(bounds.minX, bounds.minY, bounds.maxX, bounds.maxY, bounds?.proj)
        }
        map
    }

}
