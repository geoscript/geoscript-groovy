package geoscript.workspace

import java.io.File
import org.geotools.data.DataStore
import org.geotools.data.spatialite.SpatiaLiteDataStoreFactory

/**
 * A SpatiaLite Workspace connects to a SpatiaLite database.
 */
class SpatiaLite extends Workspace {

    /**
     * Create a new SpatiaLite Workspace from a name and directory
     * @param name The name of the database
     * @param dir The File directory containing the database
     */
    SpatiaLite(String name, File dir) {
        super(createDataStore(name, dir))
    }

    /**
     * Create a new SpatiaLite Workspace from a name and directory
     * @param name The name of the database
     * @param dir The directory name containing the database
     */
    SpatiaLite(String name, String dir) {
        this(name, new File(dir).absolutePath)
    }

    /**
     * Create a new SpatiaLite DataStore from a name and directory
     */
    private static DataStore createDataStore(String name, File dir) {
        Map params = [:]
        params.put("database", new File(dir,name).absolutePath)
        params.put("dbtype", "spatialite")
        SpatiaLiteDataStoreFactory f = new SpatiaLiteDataStoreFactory()
        f.createDataStore(params)
    }
}