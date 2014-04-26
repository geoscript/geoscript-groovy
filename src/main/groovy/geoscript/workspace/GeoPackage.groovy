package geoscript.workspace

import org.geotools.data.DataStore
import org.geotools.geopkg.GeoPkgDataStoreFactory

/**
 * A GeoPackage Workspace.
 * @author Jared Erickson
 */
class GeoPackage extends Database {

    /**
     * Create a new GeoPackage Workspace with a name and directory
     * @param name The name of the database
     * @param dir The File containing the database
     */
    GeoPackage(String name, File dir, String userName = null, String password = null) {
        super(createDataStore(name, dir, userName, password))
    }

    /**
     * Create a new GeoPackage Workspace with a name and directory
     * @param name The name of the database
     * @param dir The File containing the database
     */
    GeoPackage(String name, String dir, String userName = null, String password = null) {
        this(name, new File(dir).absoluteFile, userName, password)
    }

    /**
     * Create a new GeoPackage Workspace from a database file
     * @param file The GeoPackage database file
     */
    GeoPackage(File file, String userName = null, String password = null) {
        this(file.name, file.parentFile, userName, password)
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
    private static DataStore createDataStore(String name, File dir, String userName = null, String password = null) {
        Map params = new java.util.HashMap()
        params.put("database", new File(dir, name).absolutePath)
        params.put("dbtype", "geopkg")
        params.put("user", userName)
        params.put("passwd", password)
        GeoPkgDataStoreFactory factory = new GeoPkgDataStoreFactory()
        factory.createDataStore(params)
    }

}
