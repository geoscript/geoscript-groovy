package geoscript.workspace

import org.geotools.data.DataStore
import org.geotools.geopkg.GeoPkgDataStoreFactory
import org.geotools.jdbc.JDBCDataStore

/**
 * A GeoPackage Workspace.
 * @author Jared Erickson
 */
class GeoPackage extends Database {

    /**
     * Create a new GeoPackage Workspace from a database file
     * @param file The GeoPackage database file
     * @param userName The user name
     * @param password The password
     */
    GeoPackage(File file, String userName = null, String password = null) {
        super(createDataStore(file, userName, password))
    }

    /**
     * Create a new GeoPackage Workspace from a database file
     * @param fileName The GeoPackage database file
     * @param userName The user name
     * @param password The password
     */
    GeoPackage(String fileName, String userName = null, String password = null) {
        this(new File(fileName), userName, password)
    }

    /**
     * Create a new GeoPackage Workspace from a GeoTools JDBCDataStore
     * @param ds The GeoTools JDBCDataStore
     */
    GeoPackage(JDBCDataStore ds) {
        super(ds)
    }

    /**
     * Get the format
     * @return The workspace format name
     */
    @Override
    String getFormat() {
        return "GeoPackage"
    }

    /**
     * Create a new GeoPackage Workspace with a name and directory
     */
    private static DataStore createDataStore(File file, String userName = null, String password = null) {
        Map params = new java.util.HashMap()
        params.put("database", file.absolutePath)
        params.put("dbtype", "geopkg")
        params.put("user", userName)
        params.put("passwd", password)
        GeoPkgDataStoreFactory factory = new GeoPkgDataStoreFactory()
        factory.createDataStore(params)
    }

}
