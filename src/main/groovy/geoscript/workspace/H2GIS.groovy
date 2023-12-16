package geoscript.workspace

import org.geotools.data.DataStore
import org.geotools.jdbc.JDBCDataStore
import org.geotools.jdbc.JDBCDataStoreFactory
import org.geotools.util.logging.Logging
import org.h2gis.geotools.H2GISDataStoreFactory

import java.sql.Connection
import java.sql.SQLException
import java.util.logging.Logger

/**
 * A H2GIS Workspace connects to a spatially enabled H2GIS database.
 * <p><blockquote><pre>
 * H2GIS H2GIS = new H2GIS("acme", "target/H2GIS")
 * Layer layer = H2GIS.create('widgets',[new Field("geom", "Point"), new Field("name", "String")])
 * layer.add([new Point(1,1), "one"])
 * layer.add([new Point(2,2), "two"])
 * layer.add([new Point(3,3), "three"])
 * </pre></blockquote></p>
 * @author Jared Erickson
 * @author Erwan Bocher (CNRS)
 */
class H2GIS extends Database {


    /**
     * The Logger
     */
    private static final Logger LOGGER = Logging.getLogger("geoscript.workspace.H2GIS");

    /**
     * Create a new H2GIS Workspace with a name and directory
     * @param name The name of the database
     * @param dir The File containing the database
     */
    H2GIS(String name, File dir) {
        super(createDataStore(name, dir))
    }

    /**
     * Create a new H2GIS Workspace with a name and directory
     * @param name The name of the database
     * @param dir The File containing the database
     */
    H2GIS(String name, String dir) {
        this(name, new File(dir).absoluteFile)
    }


    /**
     * Create a new H2GIS Workspace from a database file
     * @param file The H2 database file
     */
    H2GIS(File file) {
        this(file.name, file.parentFile)
    }

    /**
     * Create a new H2GIS Workspace with a name, host, port, schema, user, and password.
     * @param database The database name
     * @param host The host
     * @param port The port
     * @param schema The schema
     * @param user The user name
     * @param password The password
     */
    H2GIS(String database, String host, String port, String schema, String user, String password) {
        super(createDataStore(database, host, port, schema, user, password))
    }

    /**
     * Create a new H2GIS Workspace
     * @param options The named parameters
     * <ul>
     *     <li>host = The host (defaults to localhost)</li>
     *     <li>port = The port (defaults to an empty string)</li>
     *     <li>schema = The schema (defaults to null)</li>
     *     <li>user = The user name (defaults to sa)</li>
     *     <li>password = The password (defaults to an empty string)</li>
     * </ul>
     * @param database The database name (or JNDI reference name)
     */
    H2GIS(Map options, String database) {
        this(database, options.get("host", "localhost"), options.get("port", ""), options.get("schema"),
                options.get("user", "sa"), options.get("password", ""))
    }

    /**
     * Create a new H2GIS Workspace
     *
     * @param database the name of the database
     */
    H2GIS(String database) {
        this(new File(database))
    }

    /**
     * Create a new H2GIS Workspace from a GeoTools JDBCDataStore
     * @param ds The GeoTools JDBCDataStore
     */
    H2GIS(JDBCDataStore ds) {
        super(ds)
    }

    /**
     * Get the format
     * @return The workspace format name
     */
    @Override
    String getFormat() {
        return "H2GIS"
    }

    /**
     * Load the H2GIS Network function in the current H2GIS DataSource.
     *
     * @return True if the functions have been successfully loaded, false otherwise.
     */
    boolean addNetworkFunctions() {
        Connection connection = getDataSource().getConnection();
        if (connection == null) {
            LOGGER.error("Cannot load the H2GIS Network extension.\n");
            return false;
        }
        try {
            NetworkFunctions.load(connection);
        } catch (SQLException e) {
            LOGGER.error("Cannot load the H2GIS Network extension.\n", e);
            return false;
        }
        return true;
    }

    /**
     * Create a new H2GIS Workspace with a name and directory
     */
    private static DataStore createDataStore(String name, File dir) {
        HashMap params = new HashMap<>();
        params.put(JDBCDataStoreFactory.DATABASE.key, new File(dir, name).absolutePath)
        params.put(JDBCDataStoreFactory.DBTYPE.key, "h2gis")
        params.put(JDBCDataStoreFactory.FETCHSIZE.key, 100)
        H2GISDataStoreFactory h2gisf = new H2GISDataStoreFactory()
        h2gisf.createDataStore(params)
    }

    /**
     * Create a new H2GIS Workspace with a TCP connections
     */
    private static DataStore createDataStore(String database, String host, String port, String schema, String user, String password) {
        Map params = new java.util.HashMap()
        params.put("dbtype", "h2gis")
        params.put(JDBCDataStoreFactory.USER.key, user)
        params.put(JDBCDataStoreFactory.FETCHSIZE.key, 100);
        params.put("database", database)
        params.put("schema", schema)
        params.put("host", host)
        params.put("port", port)
        params.put("user", user)
        params.put("passwd", password)
        def h2gisf = new H2GISDataStoreFactory()
        h2gisf.createDataStore(params)
    }

    /**
     * The H2GIS WorkspaceFactory
     */
    static class Factory extends WorkspaceFactory<H2GIS> {

        @Override
        Map getParametersFromString(String str) {
            Map params = [:]
            if (!str.contains("=") && str.endsWith(".mv.db")) {
                params.put("dbtype", "H2GIS")
                params.put("database", new File(str).absolutePath)
            } else {
                params = super.getParametersFromString(str)
            }
            params
        }

        @Override
        H2GIS create(String type, Map params) {
            if (type.equalsIgnoreCase('H2GIS')) {
                params['dbtype'] = 'H2GIS'
                if (params.containsKey('file')) {
                    params['database'] = params['file']
                }
                if (params['database'] instanceof File) {
                    params['database'] = (params['database'] as File).absolutePath
                }
                super.create(params)
            } else {
                null
            }
        }

        @Override
        H2GIS create(DataStore dataStore) {
            H2GIS h2GIS = null
            if (dataStore instanceof org.geotools.jdbc.JDBCDataStore) {
                def jdbcds = dataStore as org.geotools.jdbc.JDBCDataStore
                if (jdbcds.dataStoreFactory instanceof org.h2gis.geotools.H2GISDataStoreFactory ||
                        jdbcds.dataStoreFactory instanceof org.h2gis.geotools.H2GISJNDIDataStoreFactory) {
                    h2GIS = new H2GIS(dataStore)
                }
            }
            h2GIS
        }
    }
}
