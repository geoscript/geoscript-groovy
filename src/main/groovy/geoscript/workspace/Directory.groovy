package geoscript.workspace

import java.net.URI
import java.io.File
import org.geotools.data.directory.DirectoryDataStore

/**
 * A Directory Workspace
 */
class Directory extends Workspace {

    /**
     * Create a Directory Workspace from a File directory
     */
    Directory(File dir) {
        super(new DirectoryDataStore(dir, new URI("http://geoscript.ogr")));
    }

    /**
     * Create a Directory Workspace from a File directory
     */
    Directory(String dir) {
        this(new File(dir))
    }

    /**
     * Get the format
     */
    String getFormat() {
        return "Directory"
    }

    /**
     * The string representation
     */
    String toString() {
        return "Directory[${ds.getInfo().getSource().getPath()}]"
    }

}