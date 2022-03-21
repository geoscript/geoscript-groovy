package geoscript.workspace

import geoscript.layer.Layer
import geoscript.style.DatabaseStyleRepository
import geoscript.style.Style
import geoscript.style.StyleRepository
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

    @Override
    Style getStyle(Layer layer, String name = "") {
        StyleRepository styleRepository = DatabaseStyleRepository.forSqlite(getSql())
        styleRepository.getStyleForLayer(layer.name, name ?: layer.name) ?: super.getStyle(layer, name)
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

    /**
     * The GeoPackage WorkspaceFactory
     */
    static class Factory extends WorkspaceFactory<GeoPackage> {

        @Override
        Map getParametersFromString(String str) {
            Map params = [:]
            if (!str.contains("=") && str.endsWith(".gpkg")) {
                params.put("dbtype", "geopkg")
                params.put("database", new File(str).absolutePath)
            } else {
                params = super.getParametersFromString(str)
            }
            params
        }

        @Override
        GeoPackage create(String type, Map params) {
            if (type.equalsIgnoreCase('geopkg') || type.equalsIgnoreCase("geopackage")) {
                params['dbtype'] = 'geopkg'
                if (params.containsKey('file')) {
                    params['database'] = params['file']
                }
                super.create(params)
            } else {
                null
            }
        }

        @Override
        GeoPackage create(DataStore dataStore) {
            GeoPackage geopackage = null
            if (dataStore instanceof org.geotools.jdbc.JDBCDataStore) {
                def jdbcds = dataStore as org.geotools.jdbc.JDBCDataStore
                if (jdbcds.dataStoreFactory instanceof org.geotools.geopkg.GeoPkgDataStoreFactory) {
                    geopackage = new GeoPackage(dataStore)
                }
            }
            geopackage
        }
    }
}
