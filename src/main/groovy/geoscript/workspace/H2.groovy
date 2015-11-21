package geoscript.workspace

import org.geotools.data.DataStore
import org.geotools.data.h2.H2DataStoreFactory
import org.geotools.data.h2.H2JNDIDataStoreFactory
import org.geotools.jdbc.JDBCDataStore

/**
 * A H2 Workspace connects to a spatially enabled H2 database.
 * <p><blockquote><pre>
 * H2 h2 = new H2("acme", "target/h2")
 * Layer layer = h2.create('widgets',[new Field("geom", "Point"), new Field("name", "String")])
 * layer.add([new Point(1,1), "one"])
 * layer.add([new Point(2,2), "two"])
 * layer.add([new Point(3,3), "three"])
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class H2 extends Database {

    /**
     * Create a new H2 Workspace with a name and directory
     * @param name The name of the database
     * @param dir The File containing the database
     */
    H2(String name, File dir) {
        super(createDataStore(name, dir))
    }

    /**
     * Create a new H2 Workspace with a name and directory
     * @param name The name of the database
     * @param dir The File containing the database
     */
    H2(String name, String dir) {
        this(name, new File(dir).absoluteFile)
    }

    /**
     * Create a new H2 Workspace from a database file
     * @param file The H2 database file
     */
    H2(File file) {
        this(file.name, file.parentFile)
    }

    /**
     * Create a new H2 Workspace with a name, host, port, schema, user, and password.
     * @param database The database name
     * @param host The host
     * @param port The port
     * @param schema The schema
     * @param user The user name
     * @param password The password
     */
    H2(String database, String host, String port, String schema, String user, String password) {
        super(createDataStore(database, host, port, schema, user, password))
    }

    /**
     * Create a new H2 Workspace
     * @param options The optional named parameters
     * <ul>
     *     <li>host = The host (defaults to localhost)</li>
     *     <li>port = The port (defaults to an empty string)</li>
     *     <li>schema = The schema (defaults to null)</li>
     *     <li>user = The user name (defaults to sa)</li>
     *     <li>password = The password (defaults to an empty string)</li>
     * </ul>
     * @param database The database name (or JNDI reference name)
     */
    H2(Map options = [:], String database) {
        this(database, options.get("host", "localhost"), options.get("port", ""), options.get("schema"),
                options.get("user", "sa"), options.get("password", ""))
    }

    /**
     * Create a new H2 Workspace from a GeoTools JDBCDataStore
     * @param ds The GeoTools JDBCDataStore
     */
    H2(JDBCDataStore ds) {
        super(ds)
    }

    /**
     * Get the format
     * @return The workspace format name
     */
    @Override
    String getFormat() {
        return "H2"
    }

    /**
     * Create a new H2 Workspace with a name and directory
     */
    private static DataStore createDataStore(String name, File dir) {
        Map params = new java.util.HashMap()
        params.put("database", new File(dir, name).absolutePath)
        params.put("dbtype", "h2")
        H2DataStoreFactory h2f = new H2DataStoreFactory()
        h2f.createDataStore(params)
    }

    /**
     * Create a new H2 Workspace with a TCP connections
     */
    private static DataStore createDataStore(String database, String host, String port, String schema, String user, String password) {
        def h2f
        Map params = new java.util.HashMap()
        params.put("dbtype", "h2")
        if (database.startsWith("java:comp/env/")) {
            h2f = new H2JNDIDataStoreFactory()
            params.put("jndiReferenceName", database)
            params.put("schema", schema)
        } else {
            h2f = new H2DataStoreFactory()
            params.put("database", database)
            params.put("schema", schema)
            params.put("host", host)
            params.put("port", port)
            params.put("user", user)
            params.put("passwd", password)
        }
        h2f.createDataStore(params)
    }

    /**
     * The H2 WorkspaceFactory
     */
    static class Factory extends WorkspaceFactory<H2> {

        @Override
        Map getParametersFromString(String str) {
            Map params = [:]
            if (!str.contains("=") && str.endsWith(".db")) {
                params.put("dbtype", "h2")
                params.put("database", new File(str).absolutePath)
            } else {
                params = super.getParametersFromString(str)
            }
            params
        }

        @Override
        H2 create(DataStore dataStore) {
            H2 h2 = null
            if (dataStore instanceof org.geotools.jdbc.JDBCDataStore) {
                def jdbcds = dataStore as org.geotools.jdbc.JDBCDataStore
                if (jdbcds.dataStoreFactory instanceof org.geotools.data.h2.H2DataStoreFactory ||
                    jdbcds.dataStoreFactory instanceof org.geotools.data.h2.H2JNDIDataStoreFactory) {
                    h2 = new H2(dataStore)
                }
            }
            h2
        }
    }

}


