package geoscript.workspace

import org.geotools.data.DataStore
import org.geotools.data.geobuf.GeobufDirectoryDataStore
import org.geotools.data.geobuf.GeobufDataStoreFactory

/**
 * A Geobuf Workspace.
 * @author Jared Erickson
 */
class Geobuf extends Workspace {

    /**
     * The directory of pbf files
     */
    private File directory

    /**
     * The maximum precision
     */
    private int precision

    /**
     * The supported geometry coordinate dimension
     */
    private int dimension

    /**
     * Create a new Geobuf Workspace
     * @param options Optional named parameters:
     * <ul>
     *     <li> precision = The maximum precision (defaults to 6) </li>
     *     <li> dimension = The supported geometry coordinates dimension (defaults to 2) </li>
     * </ul>
     * @param directory The directory of pbf files
     */
    Geobuf(Map options = [:], File directory) {
        this(createDataStore(directory, options.get("precision", 6), options.get("dimension", 2)))
        this.directory = directory
        this.precision = precision
        this.dimension = dimension
    }

    /**
     * Create a new Geobuf Workspace from a GeobufDirectoryDataStore
     * @param ds The GeobufDirectoryDataStore
     */
    Geobuf(GeobufDirectoryDataStore ds) {
        super(ds)
    }

    /**
     * Get the format
     * @return The workspace format name
     */
    @Override
    String getFormat() {
        return "Geobuf"
    }

    private static DataStore createDataStore(File directory, int precision, int dimension) {
        Map params = new java.util.HashMap()
        params.put("file", directory.absolutePath)
        params.put("precision", precision)
        params.put("dimension", dimension)
        GeobufDataStoreFactory factory = new GeobufDataStoreFactory()
        factory.createDataStore(params)
    }

    /**
     * The Geobuf WorkspaceFactory
     */
    static class Factory extends WorkspaceFactory<Geobuf> {

        @Override
        Map getParametersFromString(String str) {
            Map params = [:]
            if (!str.contains("=") && str.endsWith(".pbf")) {
                params.put("file", new File(str).absolutePath)
            } else {
                params = super.getParametersFromString(str)
            }
            params
        }

        @Override
        Geobuf create(DataStore dataStore) {
            if (dataStore != null && dataStore instanceof org.geotools.data.geobuf.GeobufDirectoryDataStore) {
                new Geobuf(dataStore)
            } else {
                null
            }
        }
    }
}
