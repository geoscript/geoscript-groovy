package geoscript.layer

import geoscript.workspace.Directory

/**
 * A Shapefile Layer.
 * <p>You can create a Shapefile Layer by passing the .shp file:</p>
 * <code>Shapefile shp = new Shapefile('states.shp')</code>
 */
class Shapefile extends Layer {

    /**
     * Create a Shapefile Layer from a File
     * @param file The Shapefile file (*.shp)
     */
    Shapefile(File file) {
        super(create(file.absoluteFile))
    }

    /**
     * Create a Shapefile Layer from a File
     * @param file The Shapefile file (*.shp)
     */
    Shapefile(String file) {
        this(new File(file))
    }

    /**
     * Get the Shapefile's File
     * @return The Shapefile's File
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