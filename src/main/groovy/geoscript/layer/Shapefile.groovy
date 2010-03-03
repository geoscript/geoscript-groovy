package geoscript.layer

import geoscript.workspace.Directory

/**
 * A Shapefile Layer
 */
class Shapefile extends Layer {

    /**
     * Create a Shapefile Layer from a File
     */
    Shapefile(File file) {
        super(create(file))
    }

    /**
     * Create a Shapefile Layer from a File
     */
    Shapefile(String file) {
        super(create(file))
    }

    /**
     * Get the Shapefile's File
     */
    File getFile() {
        new File(fs.dataStore.info.source.toURL().file)
    }

    /**
     * Create a Shapefile Layer form a File
     */
    private static Layer create(String file) {
        File f = new File(file)
        create(f)
    }

    /**
     * Create a Shapefile Layer form a File
     */
    private static Layer create(File file) {
        String fileName = file.name
        String name = fileName.substring(0, fileName.lastIndexOf('.'))
        return new Layer(name, new Directory(file.canonicalFile.parent))
    }

}