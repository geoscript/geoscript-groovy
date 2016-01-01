package geoscript.workspace

import geoscript.layer.Layer
import org.geotools.data.DataStore
import org.geotools.data.DataUtilities
import org.geotools.data.directory.DirectoryDataStore
import org.geotools.data.shapefile.ShapefileDataStore
import org.geotools.data.shapefile.ShapefileDataStoreFactory
import org.geotools.data.shapefile.files.ShpFileType

/**
 * A Directory Workspace can contain one or more Shapefiles.
 * <p><blockquote><pre>
 * Directory dir = new Directory("shapefiles")
 * Layer layer = dir.get("states")
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class Directory extends Workspace {

    /**
     * Create a Directory Workspace from a File directory
     * @param dir The File directory
     */
    Directory(File dir) {
        super(new DirectoryDataStore(dir, new ShapefileDataStoreFactory.ShpFileStoreFactory(new ShapefileDataStoreFactory(),[:])));
    }

    /**
     * Create a Directory Workspace from a File directory
     * @param dir The File directory as a String
     */
    Directory(String dir) {
        this(new File(dir))
    }

    /**
     * Create a Directory Workspace from a GeoTools DirectoryDataStore
     * @param ds The GeoTools DirectoryDataStore
     */
    Directory(DirectoryDataStore ds) {
        super(ds)
    }

    /**
     * Create a Directory Workspace from a GeoTools ShapefileDataStore
     * @param ds The GeoTools ShapefileDataStore
     */
    Directory(ShapefileDataStore ds) {
        super(ds)
    }

    /**
     * Get the format
     * @return The workspace format name
     */
    String getFormat() {
        return "Directory"
    }

    /**
     * Get a Layer by name
     * @param The Layer name
     * @return A Layer
     */
    @Override
    Layer get(String name) {
        if (name.endsWith(".shp")) {
            super.get(name.substring(0,name.lastIndexOf(".shp")))
        } else {
            super.get(name)
        }
    }

    /**
     * Get the File or Directory
     * @return The File or Directory
     */
    File getFile() {
        if (ds instanceof ShapefileDataStore) {
            new File(ds.shpFiles.get(ShpFileType.SHP))
        } else {
            new File(ds.info.source.path)
        }
    }

    /**
     * Remove a Layer by name from this Workspace
     * @param name The Layer name
     */
    @Override
    void remove(String name) {
        ds.removeSchema(name)
        ds.cache.refreshCacheContents()
    }

    /**
     * The string representation
     * @return The string representation
     */
    String toString() {
        "Directory[${getFile().absolutePath}]"
    }

    /**
     * The Directory WorkspaceFactory
     */
    static class Factory extends WorkspaceFactory<Directory> {

        @Override
        Map getParametersFromString(String str) {
            Map params = [:]
            if (!str.contains("=") && str.endsWith(".shp")) {
                if (str.startsWith("file:/")) {
                    params.put("url", DataUtilities.fileToURL(DataUtilities.urlToFile(new URL(str)).getAbsoluteFile().getParentFile()))
                } else {
                    params.put("url", DataUtilities.fileToURL(new File(str).getAbsoluteFile().getParentFile()))
                }
            } else if (!str.contains("=") && new File(str).isDirectory()) {
                params.put("url", new File(str).toURL())
            } else {
                params = super.getParametersFromString(str)
            }
            params
        }

        @Override
        Directory create(DataStore dataStore) {
            if (dataStore != null && (
                    dataStore instanceof org.geotools.data.directory.DirectoryDataStore ||
                    dataStore instanceof org.geotools.data.shapefile.ShapefileDataStore)) {
                new Directory(dataStore)
            } else {
                null
            }
        }
    }

}