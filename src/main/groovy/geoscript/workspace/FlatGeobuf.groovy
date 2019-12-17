package geoscript.workspace

import org.geotools.data.DataStore
import org.geotools.data.flatgeobuf.FlatgeobufDataStoreFactory
import org.geotools.data.flatgeobuf.FlatgeobufDirectoryDataStore

/**
 * A FlatGeobuf Workspace.
 * @author Jared Erickson
 */
class FlatGeobuf extends Workspace {

    /**
     * The directory of fgb files
     */
    private File directory
    
    /**
     * Create a new FlatGeobuf Workspace
     * @param directory The directory of fgb files
     */
    FlatGeobuf(File directory) {
        this(createDataStore(directory))
        this.directory = directory
    }

    /**
     * Create a new FlatGeobuf Workspace from a FlatgeobufDirectoryDataStore
     * @param ds The FlatgeobufDirectoryDataStore
     */
    FlatGeobuf(FlatgeobufDirectoryDataStore ds) {
        super(ds)
    }

    /**
     * Get the format
     * @return The workspace format name
     */
    @Override
    String getFormat() {
        return "FlatGeobuf"
    }

    /**
     * Remove a Layer by name from this Workspace
     * @param name The Layer name
     */
    @Override
    void remove(String name) {
        File file = new File(directory, name.endsWith(".fgb") ? name : "${name}.fgb")
        if (file.exists()) {
            file.delete()
        }
    }

    private static DataStore createDataStore(File directory) {
        Map params = new java.util.HashMap()
        params.put("flatgeobuf-file", directory.absolutePath)
        FlatgeobufDataStoreFactory factory = new FlatgeobufDataStoreFactory()
        factory.createDataStore(params)
    }

    /**
     * The FlatGeobuf WorkspaceFactory
     */
    static class Factory extends WorkspaceFactory<FlatGeobuf> {

        @Override
        Map getParametersFromString(String str) {
            println "FlatGeobuf.getParametersFromString"
            Map params = [:]
            if (!str.contains("=") && str.endsWith(".fgb")) {
                File file = new File(str).absoluteFile
                if (!file.isDirectory()) {
                    file = file.parentFile
                }
                params.put("flatgeobuf-file", file.absolutePath)
            } else {
                params = super.getParametersFromString(str)
            }
            println "   ${params}"
            params
        }

        @Override
        FlatGeobuf create(String type, Map params) {
            if (type.equalsIgnoreCase('flatgeobuf') && params.containsKey('file')) {
                File file = params.get('file') instanceof File ? params.get('file') : new File(params.get('file'))
                if (!file.isDirectory()) {
                    file = file.parentFile
                }
                super.create(['flatgeobuf-file': file])
            } else {
                null
            }
        }

        @Override
        FlatGeobuf create(DataStore dataStore) {
            if (dataStore != null && dataStore instanceof org.geotools.data.flatgeobuf.FlatgeobufDirectoryDataStore) {
                new FlatGeobuf(dataStore)
            } else {
                null
            }
        }
    }
}
