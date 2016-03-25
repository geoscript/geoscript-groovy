package geoscript.workspace

import org.geotools.data.DataStore
import org.geotools.data.spatialite.SpatiaLiteDataStoreFactory
import org.geotools.jdbc.JDBCDataStore

/**
 * A SpatiaLite Workspace connects to a SpatiaLite database.
 * <p><blockquote><pre>
 * SpatiaLite spatialite = new SpatiaLite("db.sqlite", "databases")
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class SpatiaLite extends Database {

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
        this(name, new File(dir).absoluteFile)
    }

    /**
     * Create a new SpatiaLite Workspace from a GeoTools JDBCDataStore
     * @param ds The GeoTools JDBCDataStore
     */
    SpatiaLite(JDBCDataStore ds) {
        super(ds)
    }

    /**
     * Get the format
     * @return The workspace format name
     */
    String getFormat() {
        return "SpatiaLite"
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

    /**
     * The SpatiaLite WorkspaceFactory
     */
    static class Factory extends WorkspaceFactory<SpatiaLite> {

        @Override
        Map getParametersFromString(String str) {
            Map params = [:]
            if (!str.contains("=") && (str.endsWith(".sqlite") || str.endsWith(".spatialite"))) {
                params.put("dbtype", "spatialite")
                params.put("database", new File(str).absolutePath)
            } else {
                params = super.getParametersFromString(str)
            }
            params
        }

        @Override
        SpatiaLite create(String type, Map params) {
            if (type.equalsIgnoreCase('spatialite')) {
                params['dbtype'] = 'spatialite'
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
        SpatiaLite create(DataStore dataStore) {
            SpatiaLite spatialite = null
            if (dataStore instanceof org.geotools.jdbc.JDBCDataStore) {
                def jdbcds = dataStore as org.geotools.jdbc.JDBCDataStore
                if (jdbcds.dataStoreFactory instanceof org.geotools.data.spatialite.SpatiaLiteDataStoreFactory ||
                        jdbcds.dataStoreFactory instanceof org.geotools.data.spatialite.SpatiaLiteJNDIDataStoreFactory) {
                    spatialite = new SpatiaLite(dataStore)
                }
            }
            spatialite
        }
    }
}