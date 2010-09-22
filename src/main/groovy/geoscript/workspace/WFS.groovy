package geoscript.workspace

import org.geotools.data.wfs.*

/**
 * A WFS Workspace
 * @author Jared Erickson
 */
class WFS extends Workspace {

    /**
     * Create a new WFS Workspace with a WFS get capabilities URL and optional options.
     * Options include:
     * 1. protocol
     * 2. username
     * 3. password
     * 4. encoding
     * 5. timeout
     * 6. bufferSize
     * 7. tryGzip
     * 8. lenient
     * 9. maxFeatures
     * @param url The get capabilities URL
     * @param options A Map of optional options
     */
    WFS(String url, Map options = [:]) {
        super(new WFSDataStoreFactory().createDataStore(createParams(url, options)))
    }

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

