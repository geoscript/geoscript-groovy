package geoscript.workspace

import geoscript.layer.Layer
import org.geotools.data.directory.DirectoryDataStore
import org.geotools.data.shapefile.ShapefileDataStore
import org.geotools.data.shapefile.ShapefileDataStoreFactory

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
     * The string representation
     * @return The string representation
     */
    String toString() {
        return "Directory[${new File(ds.info.source.path).absolutePath}]"
    }

}