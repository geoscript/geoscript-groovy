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
        super(create(file.absoluteFile))
    }

    /**
     * Create a Shapefile Layer from a File
     */
    Shapefile(String file) {
        this(new File(file))
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
    private static Layer create(File file) {
        String fileName = file.name
        String name = fileName.substring(0, fileName.lastIndexOf('.'))
        return new Layer(name, new Directory(file.parentFile))
    }

}