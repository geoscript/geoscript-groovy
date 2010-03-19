package geoscript.workspace

import java.io.File
import org.geotools.data.DataStore
import org.geotools.data.h2.H2DataStoreFactory

/**
 * A H2 Workspace connects to a spatially enabled H2 database.
 */
class H2 extends Workspace {

    /**
     * Create a new H2 Workspace with a name and directory
     * @param name The name of the database
     * @param dir The File containing the database
     */
    H2 (String name, File dir) {
        super(createDataStore(name, dir))
    }

    /**
     * Create a new H2 Workspace with a name and directory
     * @param name The name of the database
     * @param dir The File containing the database
     */
    H2 (String name, String dir) {
        this(name, new File(dir).absoluteFile)
    }

    /**
     * Create a new H2 Workspace with a name and directory
     */
    private static DataStore createDataStore(String name, File dir) {
        Map params = new java.util.HashMap()
        params.put("database", new File(dir,name).absolutePath)
        params.put("dbtype", "h2")
        H2DataStoreFactory h2f = new H2DataStoreFactory()
        h2f.createDataStore(params)
    }
}


