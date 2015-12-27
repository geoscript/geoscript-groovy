package geoscript.layer

import geoscript.render.Map

/**
 * Created by jericks on 11/16/15.
 */
class WMSMapExtensionModule {

    /**
     * Add a WMS Layer
     * @param wms The WMS Layer
     */
    static void addWMSLayer(Map map, WMS wms) {
        map.layers.add(wms)
    }

}
