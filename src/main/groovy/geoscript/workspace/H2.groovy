package geoscript.workspace

import org.geotools.data.DataStore
import org.geotools.data.h2.H2DataStoreFactory

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
     * Create a new H2 Workspace from a database file
     * @param file The H2 database file
     */
    H2 (File file) {
        this(file.name, file.parentFile)
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
        params.put("database", new File(dir,name).absolutePath)
        params.put("dbtype", "h2")
        H2DataStoreFactory h2f = new H2DataStoreFactory()
        h2f.createDataStore(params)
    }
}


