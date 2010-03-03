package geoscript.workspace

import org.geotools.data.DataStore
import org.geotools.data.postgis.PostgisNGDataStoreFactory

/**
 * A PostGIS Workspace
 */
class PostGIS extends Workspace {

    /**
     * Create a new PostGIS Workspace with a name, host, port, schema user, and passowrd
     */
    PostGIS (String name, String host, String port, String schema, String user, String password) {
        super(createDataStore(name, host, port, schema, user, password))
    }

    /**
     * Create a new PostGIS DataStore with a name, host, port, schema user, and passowrd
     */
    private static DataStore createDataStore(String name, String host, String port, String schema, String user, String password) {
        Map params = [:]
        params.put("database", name)
        params.put("host", host)
        params.put("port", port)
        params.put("schema", schema)
        params.put("user", user)
        params.put("passwd", password)
        params.put("dbtype", "postgis")
        PostgisNGDataStoreFactory f = new PostgisNGDataStoreFactory()
        f.createDataStore(params)
    }


}