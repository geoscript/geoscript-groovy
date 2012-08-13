package geoscript.workspace

import org.geotools.data.DataStore
import org.geotools.data.postgis.PostgisNGDataStoreFactory

/**
 * A PostGIS Workspace connects to a PostGIS database.
 * <p><blockquote><pre>
 * PostGIS postgis = new PostGIS("database", "localhost", "5432", "public", "user", "password")
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class PostGIS extends Database {

    /**
     * Create a new PostGIS Workspace with a name, host, port, schema, user, and password.
     * <p><blockquote><pre>
     * PostGIS postgis = new PostGIS("database", "localhost", "5432", "public", "user", "password")
     * </pre></blockquote></p>
     * @param name The database name
     * @param host The host name
     * @param port The port
     * @param schema The database schema
     * @param user The user name
     * @param password The password
     * @param estimatedExtent Whether to estimate the extent or not
     */
    PostGIS (String name, String host, String port, String schema, String user, String password, boolean estimatedExtent = false) {
        super(createDataStore(name, host, port, schema, user, password, estimatedExtent))
    }
    
    /**
     * Create a new PostGIS with just a database name using defaults for other values.
     * <p><blockquote><pre>
     * PostGIS postgis = new PostGIS("database", user: 'me', password: 'supersecret'
     * </pre></blockquote></p>
     * @param options The options for connecting to a PostGIS database (host, port, schema, user, password, estimatedExtent)
     * @param name The database name
     */
    PostGIS (Map options = [:], String name) {
        this(name, options.get("host","localhost"), options.get("port","5432"), options.get("schema","public"), options.get("user",System.getProperty("user.name")), options.get("password",null), options.get("estimatedExtent",false) as boolean)
    }

    /**
     * Create a new PostGIS DataStore with a name, host, port, schema user, pasoword, and whether to estimate the extent or not
     */
    private static DataStore createDataStore(String name, String host, String port, String schema, String user, String password, boolean estimatedExtent) {
        Map params = [:]
        params.put("database", name)
        params.put("host", host)
        params.put("port", port)
        params.put("schema", schema)
        params.put("user", user)
        params.put("passwd", password)
        params.put("Estimated extends", String.valueOf(estimatedExtent))
        params.put("dbtype", "postgis")
        PostgisNGDataStoreFactory f = new PostgisNGDataStoreFactory()
        f.createDataStore(params)
    }
}
