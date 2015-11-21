package geoscript.workspace

import org.geotools.data.DataStore
import org.geotools.data.mysql.MySQLDataStoreFactory
import org.geotools.data.mysql.MySQLJNDIDataStoreFactory
import org.geotools.jdbc.JDBCDataStore

/**
 * A MySQL Workspace connects to a MySQL database.
 * <p><blockquote><pre>
 * MySQL mysql = new MySQL("world","localhost","5432","uzr","pass")
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class MySQL extends Database {

    /**
     * Create a new MySQL Workspace with a name, host, port, user, and password
     * @param name The name of the database
     * @param host The host name
     * @param port The port
     * @param user The user name
     * @param password The password
     */
    MySQL(String name, String host, String port, String user, String password) {
        super(createDataStore(name, host, port, user, password))
    }

    /**
     * Create a new MySQL Workspace
     * @param options The options
     * <ul>
     *     <li>host = The host name (defaults to localhost)</li>
     *     <li>port = The port (defaults to 3306)</li>
     *     <li>user = The user name (defaults to the system user name)</li>
     *     <li>password = The password (defaults to blank)</li>
     * </ul>
     * @param name The database or JNDI name
     */
    MySQL(Map options = [:], String name) {
        super(createDataStore(name, options.get("host", "localhost"), options.get("port", 3306),
                options.get("user", System.getProperty("user.name")), options.get("password")))
    }

    /**
     * Create a new MySQL Workspace from a GeoTools JDBCDataStore
     * @param ds The GeoTools JDBCDataStore
     */
    MySQL(JDBCDataStore ds) {
        super(ds)
    }

    /**
     * Get the format
     * @return The workspace format name
     */
    String getFormat() {
        return "MySQL"
    }

    /**
     * Create a new MySQL DataStore with a name, host, port, user, and password
     */
    private static DataStore createDataStore(String name, String host, String port, String user, String password) {
        def f
        Map params = [:]
        if (name.startsWith("java:comp/env/")) {
            f = new MySQLJNDIDataStoreFactory()
            params.put("jndiReferenceName", name)
        } else {
            f = new MySQLDataStoreFactory()
            params.put("database", name)
            params.put("host", host)
            params.put("port", port)
            params.put("user", user)
            params.put("passwd", password)
        }
        params.put("dbtype", "mysql")
        f.createDataStore(params)
    }

    /**
     * The MySQL WorkspaceFactory
     */
    static class Factory extends WorkspaceFactory<MySQL> {

        @Override
        MySQL create(DataStore dataStore) {
            MySQL mysql = null
            if (dataStore instanceof org.geotools.jdbc.JDBCDataStore) {
                def jdbcds = dataStore as org.geotools.jdbc.JDBCDataStore
                if (jdbcds.dataStoreFactory instanceof org.geotools.data.mysql.MySQLDataStoreFactory ||
                    jdbcds.dataStoreFactory instanceof org.geotools.data.mysql.MySQLJNDIDataStoreFactory) {
                    mysql = new MySQL(dataStore)
                }
            }
            mysql
        }
    }
}