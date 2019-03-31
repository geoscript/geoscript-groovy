package geoscript.workspace

import geoscript.feature.Schema
import geoscript.layer.Cursor
import geoscript.layer.Layer
import org.geotools.data.DataStore
import org.geotools.data.ogr.OGRDataStore

/**
 * A SpatiaLite Workspace connects to a SpatiaLite database.
 * <p><blockquote><pre>
 * SpatiaLite spatialite = new SpatiaLite("db.sqlite")
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class SpatiaLite extends OGR {

    SpatiaLite(File file) {
        this(file.absolutePath)
    }

    SpatiaLite(String fileName) {
        super("SQLite", fileName)
    }

    protected SpatiaLite(OGRDataStore dataStore) {
        super(dataStore)
    }

    @Override
    Layer create(Map options, Schema schema, boolean write) {
        (options.get("options",[]) as List).add("SPATIALITE=YES")
        super.create(options, schema, write)
    }

    @Override
    Layer create(Map options = [:], Cursor c) {
        (options.get("options",[]) as List).add("SPATIALITE=YES")
        super.create(options, c)
    }

    @Override
    Layer add(Map options, Layer layer) {
        (options.get("options",[]) as List).add("SPATIALITE=YES")
        super.add(options, layer)
    }

    @Override
    Layer add(Map options, Layer layer, String name, int chunk=1000) {
        (options.get("options",[]) as List).add("SPATIALITE=YES")
        super.add(options, layer, name, chunk)
    }

    /**
     * Get the format
     * @return The workspace format name
     */
    String getFormat() {
        return "SpatiaLite"
    }

    /**
     * The String representation
     * @return A String representation
     */
    @Override
    String toString() {
        "SpatiaLite(${dataset})"
    }

    /**
     * The SpatiaLite WorkspaceFactory
     */
    static class Factory extends WorkspaceFactory<SpatiaLite> {

        @Override
        Map getParametersFromString(String str) {
            Map params = [:]
            if (!str.contains("=") && (str.endsWith(".sqlite") || str.endsWith(".spatialite"))) {
                params.put("DriverName", "SQLite")
                params.put("DatasourceName", new File(str).absolutePath)
            } else {
                params = super.getParametersFromString(str)
            }
            params
        }

        @Override
        SpatiaLite create(String type, Map params) {
            if (type.equalsIgnoreCase('spatialite')) {
                params.put("DriverName", "SQLite")
                if (params.containsKey('file')) {
                    Object file = params["file"]
                    params.put("DatasourceName", file instanceof File ? file.absolutePath: new File(file.toString()).absolutePath)
                }
                if (params['database'] instanceof File) {
                    params['DatasourceName'] = (params['database'] as File).absolutePath
                }
                super.create(params)
            } else {
                null
            }
        }

        @Override
        SpatiaLite create(DataStore dataStore) {
            SpatiaLite spatialite = null
            if (dataStore instanceof org.geotools.data.ogr.OGRDataStore) {
                def ogrDataSource = dataStore as org.geotools.data.ogr.OGRDataStore
                if (ogrDataSource.@ogrDriver.equalsIgnoreCase("SQLite")) {
                    spatialite = new SpatiaLite(ogrDataSource)
                }
            }
            spatialite
        }
    }
}