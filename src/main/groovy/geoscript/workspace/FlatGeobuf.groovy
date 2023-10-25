package geoscript.workspace

import geoscript.layer.Layer
import geoscript.style.DirectoryStyleRepository
import geoscript.style.Style
import geoscript.style.StyleRepository
import org.geotools.api.data.DataStore
import org.geotools.data.flatgeobuf.FlatGeobufDataStoreFactory
import org.geotools.data.flatgeobuf.FlatGeobufDirectoryDataStore
import org.geotools.util.URLs

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
     * Create a new FlatGeobuf Workspace from a FlatGeobufDirectoryDataStore
     * @param ds The FlatGeobufDirectoryDataStore
     */
    FlatGeobuf(FlatGeobufDirectoryDataStore ds) {
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

    @Override
    Style getStyle(Layer layer, String name = "") {
        StyleRepository styleRepository = new DirectoryStyleRepository(this.directory)
        styleRepository.getStyleForLayer(layer.name, name ?: layer.name) ?: super.getStyle(layer, name)
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
        params.put("url", URLs.fileToUrl(directory))
        FlatGeobufDataStoreFactory factory = new FlatGeobufDataStoreFactory()
        factory.createDataStore(params)
    }

    /**
     * The FlatGeobuf WorkspaceFactory
     */
    static class Factory extends WorkspaceFactory<FlatGeobuf> {

        @Override
        Map getParametersFromString(String str) {
            Map params = [:]
            if (!str.contains("=") && str.endsWith(".fgb")) {
                File file = new File(str).absoluteFile
                if (!file.isDirectory()) {
                    file = file.parentFile
                }
                params.put("type", "flatgeobuf")
                params.put("url", URLs.fileToUrl(file))
            } else {
                params = super.getParametersFromString(str)
            }
            params
        }

        @Override
        FlatGeobuf create(String type) {
            if (!type.contains("=") && type.endsWith(".fgb")) {
                File file = new File(type)
                new FlatGeobuf(file.isDirectory() ? file : file.getParentFile())
            } else {
                null
            }
        }

        @Override
        FlatGeobuf create(String type, Map params) {
            if (type.equalsIgnoreCase('flatgeobuf') && params.containsKey('file')) {
                File file = params.get('file') instanceof File ? params.get('file') : new File(params.get('file'))
                if (!file.isDirectory()) {
                    file = file.parentFile
                }
                new FlatGeobuf(file)
            } else if (params.containsKey('url') && params["url"]?.toString().endsWith(".fgb")) {
                File file = params.get('url') instanceof File ? params.get('url') : new File(params.get('url'))
                if (!file.isDirectory()) {
                    file = file.parentFile
                }
                new FlatGeobuf(file)
            } else {
                null
            }
        }

        @Override
        FlatGeobuf create(DataStore dataStore) {
            if (dataStore != null && dataStore instanceof org.geotools.data.flatgeobuf.FlatGeobufDirectoryDataStore) {
                new FlatGeobuf(dataStore)
            } else {
                null
            }
        }
    }
}
