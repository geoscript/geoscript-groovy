package geoscript.workspace

import org.geotools.data.DataStore
import org.geotools.data.mysql.MySQLDataStoreFactory

/**
 * A MySQL Workspace
 */
class MySQL extends Workspace {

    /**
     * Create a new MySQL Workspace with a name, host, port, user, and passowrd
     */
    MySQL(String name, String host, String port, String user, String password) {
        super(createDataStore(name, host, port, user, password))
    }

    /**
     * Create a new MySQL DataStore with a name, host, port, user, and passowrd
     */
    private static DataStore createDataStore(String name, String host, String port, String user, String password) {
        Map params = [:]
        params.put("database", name)
        params.put("host", host)
        params.put("port", port)
        params.put("user", user)
        params.put("passwd", password)
        params.put("dbtype", "mysql")
        MySQLDataStoreFactory f = new MySQLDataStoreFactory()
        f.createDataStore(params)
    }


}