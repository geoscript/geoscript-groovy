package geoscript.layer

/**
 * A Groovy Extension Module that adds WMS methods to the Map.
 * @author Jared Erickson
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
