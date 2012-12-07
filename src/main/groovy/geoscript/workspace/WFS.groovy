package geoscript.workspace

import org.geotools.data.wfs.*

/**
 * A WFS Workspace.
 * http://docs.geotools.org/latest/javadocs/org/geotools/data/wfs/WFSDataStore.html
 * http://docs.geotools.org/latest/javadocs/org/geotools/data/wfs/WFSDataStoreFactory.html
 * @author Jared Erickson
 */
class WFS extends Workspace {

    /**
     * Create a new WFS Workspace with a WFS get capabilities URL and options.
     * Options are optional and include (protocol, username, password, encoding,
     * timeout, bufferSize, tryGzip, lenient, maxFeatures)
     * @param url The get capabilities URL
     * @param options A Map of optional options
     */
    WFS(Map options = [:], String url) {
        super(new WFSDataStoreFactory().createDataStore(createParams(url, options)))
    }

    /**
     * Get the format
     * @return The workspace format name
     */
    String getFormat() {
        return "WFS"
    }

    /**
     * The String representation
     * @return A String representation
     */
    String toString() {
        return "WFS (" + ds.getCapabilitiesURL() + ")"
    }

    /**
     * Create a Map of parameters for the WFSDataStoreFactory.
     * @param url The get capabilities url
     * @param options A Map of options
     * @return A Map with WFSDataStoreFactory keys and values
     */
    private static Map createParams(String url, Map options) {
        Map params = [:]
        params.put("WFSDataStoreFactory:GET_CAPABILITIES_URL", url)
        if (options.containsKey("protocol")){
            params.put("WFSDataStoreFactory:PROTOCOL", options.get("protocol"))
        }
        if (options.containsKey("username")) {
            params.put("WFSDataStoreFactory:USERNAME", options.get("username"))
        }
        if (options.containsKey("password")) {
            params.put("WFSDataStoreFactory:PASSWORD", options.get("password"))
        }
        if (options.containsKey("encoding")) {
            params.put("WFSDataStoreFactory:ENCODING", options.get("encoding"))
        }
        params.put("WFSDataStoreFactory:TIMEOUT", options.get("timeout", 3000))
        params.put("WFSDataStoreFactory:BUFFER_SIZE", options.get("bufferSize",10))
        params.put("WFSDataStoreFactory:TRY_GZIP", options.get("tryGzip",true))
        params.put("WFSDataStoreFactory:LENIENT", options.get("lenient",true))
        if (options.containsKey("maxFeatures")) {
            params.put("WFSDataStoreFactory:MAXFEATURES", options.get("maxFeatures"))
        }
        return params
    }
}