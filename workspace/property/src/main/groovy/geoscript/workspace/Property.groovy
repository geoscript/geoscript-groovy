package geoscript.workspace

import geoscript.layer.Layer
import org.geotools.data.DataStore
import org.geotools.data.property.PropertyDataStore
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
     * Create a new Property Workspace
     * @param directory The File directory
     */
    Property(String directory) {
        this(new File(directory).absoluteFile)
    }

    /**
     * Create a new Property Workspace from a GeoTools PropertyDataStore
     * @param ds The GeoTools PropertyDataStore
     */
    Property(PropertyDataStore ds) {
        super(ds)
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
     * Get a Layer by name
     * @param The Layer name
     * @return A Layer
     */
    @Override
    Layer get(String name) {
        if (name.endsWith(".properties")) {
            super.get(name.substring(0,name.lastIndexOf(".properties")))
        } else {
            super.get(name)
        }
    }

    /**
     * Get the File
     * @return The File
     */
    File getFile() {
        new File(ds.info.source.path)
    }

    /**
     * Remove a Layer by name from this Workspace
     * @param name The Layer name
     */
    @Override
    void remove(String name) {
        File file = new File(this.getFile(), name.endsWith(".properties") ? name : "${name}.properties")
        if (file.exists()) {
            file.delete()
        }
    }

    /**
     * Get the string representation
     * @return The string representation
     */
    @Override
    String toString() {
        return "Property[${getFile().absolutePath}]"
    }

    /**
     * The Property WorkspaceFactory
     */
    static class Factory extends WorkspaceFactory<Property> {

        @Override
        Map getParametersFromString(String str) {
            Map params = [:]
            if (!str.contains("=") && str.endsWith(".properties")) {
                String dir
                File f = new File(str)
                if (f.exists()) {
                    dir = f.absoluteFile.parentFile.absolutePath
                } else {
                    dir = f.absolutePath.substring(0,f.absolutePath.lastIndexOf(File.separator))
                }
                params.put("directory", dir)
            } else {
                params = super.getParametersFromString(str)
            }
            params
        }

        @Override
        Property create(DataStore dataStore) {
            Property property = null
            if (dataStore instanceof org.geotools.data.property.PropertyDataStore) {
                property = new Property(dataStore)
            }
            property
        }
    }
}
