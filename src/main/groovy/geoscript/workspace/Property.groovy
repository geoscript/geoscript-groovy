package geoscript.workspace

import org.geotools.data.DataStore
import org.geotools.data.property.PropertyDataStoreFactory

/**
 * A Workspace based on a directory of java style property files.
 * <p><blockquote><pre>
 * Property property = new Property("files")
 * Layer layer = property.get("points")
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class Property extends Workspace {

    /**
     * Create a new Property Workspace
     * @param directory The File directory
     */
    Property(File directory) {
        super(createDataStore(directory))
    }

    /**
     * Create a new Property Workspace with a directory
     * @param directory The directory
     */
    private static DataStore createDataStore(File directory) {
        Map params = new java.util.HashMap()
        params.put("directory", directory.absolutePath)
        PropertyDataStoreFactory factory = new PropertyDataStoreFactory()
        factory.createDataStore(params)
    }

    /**
     * Get the format
     * @return The workspace format name
     */
    @Override
    String getFormat() {
        return "Property"
    }

    /**
     * Get the string representation
     * @return The string representation
     */
    @Override
    String toString() {
        return "Property[${new File(ds.info.source.path).absolutePath}]"
    }
}
