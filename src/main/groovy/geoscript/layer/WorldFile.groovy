package geoscript.layer

import geoscript.geom.Bounds
import geoscript.geom.Point

import java.awt.geom.AffineTransform
import org.geotools.referencing.operation.matrix.AffineTransform2D
import org.geotools.data.WorldFileReader
import org.geotools.data.WorldFileWriter

/**
 * Read and write world files
 * @author Jared Erickson
 */
class WorldFile {

    /**
     * The GeoTools WorldFileReader
     */
    private WorldFileReader wfr

    /**
     * The File
     */
    private File file

    /**
     * Create a new WorldFile from the contents of an existing File
     * @param file The File
     */
    WorldFile(File file) {
        this.file = file
        wfr = new WorldFileReader(file)
    }


    /**
     * Write a new WorldFile
     * @param bounds The Bounds
     * @param size The size
     * @param file The output File
     * @return The new WorldFile
     */
    WorldFile(Bounds bounds, List size, File file) {
        double scx = bounds.width / size[0]
        double scy = -1 * bounds.height / size[1]
        def at = new AffineTransform(scx,0,0,scy, bounds.minX + scx / 2.0, bounds.maxY + scy / 2.0)
        def wfw = new WorldFileWriter(file, at)
        this.file = file
        wfr = new WorldFileReader(file)
    }

    /**
     * Get the pixel sizes
     * @return A List of x and y pixel sizes
     */
    List getPixelSize() {
        [wfr.XPixelSize, wfr.YPixelSize]
    }

    /**
     * Get a rotations values
     * @return A List of x and y rotation values
     */
    List getRotation() {
        [wfr.rotationX, wfr.rotationY]
    }

    /**
     * Get the upper left center coordinates as a Point
     * @return A Point of the x and y upper left center coordinates
     */
    Point getUlc() {
        new Point(wfr.getXULC(), wfr.getYULC())
    }

    /**
     * Get the File
     * @return The File
     */
    File getFile() {
        return file
    }

    /**
     * The string representation
     * @return The string representation
     */
    String toString() {
        "WorldFile: ${file}"
    }
}
