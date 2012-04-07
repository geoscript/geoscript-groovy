package geoscript.workspace

import java.net.URI
import java.io.File
import org.geotools.data.directory.DirectoryDataStore
import org.geotools.data.shapefile.ShapefileDataStoreFactory

/**
 * A Directory Workspace can contain one or more Shapefiles.
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
     * Get the format
     * @return The workspace format name
     */
    String getFormat() {
        return "Directory"
    }

    /**
     * The string representation
     * @return The string representation
     */
    String toString() {
        return "Directory[${new File(ds.info.source.path).absolutePath}]"
    }

}