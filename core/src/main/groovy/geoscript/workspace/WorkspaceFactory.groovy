package geoscript.workspace

import org.geotools.data.DataStore
import org.geotools.data.DataStoreFinder

/**
 * A WorkspaceFactory creates Workspaces from connection parameters
 * @param < T > The Workspace created by this WorkspaceFactory
 */
abstract class WorkspaceFactory<T extends Workspace> {

    /**
     * Create a Workspace from a connection string.
     * @param str The connection string
     * @return A Workspace or null
     */
    T create(String str) {
        Map params = getParametersFromString(str)
        create(params)
    }

    /**
     * Create a Workspace from a Map of connection parameters
     * @param params The connection parameters
     * @return A Workspace or null
     */
    T create(Map params) {
        DataStore ds = DataStoreFinder.getDataStore(params)
        if (ds) {
            create(ds)
        } else {
            null
        }
    }

    /**
     * Create a Workspace wrapping the GeoTools DataStore.  Return null if
     * this Workspace does not wrap the DataStore
     * @param dataStore The GeoTools DataStore
     * @return A Workspace or null
     */
    abstract T create(DataStore dataStore)

    /**
     * Get a map of connection parameters from a connection string.
     * @param str The connection string
     * @return A Map of connection parameters
     */
    Map getParametersFromString(String str) {
        Map params = [:]
        if (str.contains("=") && !str.toLowerCase().startsWith("http")) {
            params.putAll(Workspace.getParameters(str))
        }
        params
    }
}