package geoscript.layer

import geoscript.GeoScript
import geoscript.workspace.Directory
import org.geotools.data.DataUtilities
import org.geotools.data.shapefile.files.ShpFileType
import org.geotools.data.shapefile.ShapefileDumper

/**
 * A Shapefile Layer.
 * <p>You can create a Shapefile Layer by passing the .shp file:</p>
 * <p><blockquote><pre>
 * Shapefile shp = new Shapefile('states.shp')
 * </pre></blockquote></p>
 * @author Jared Erickson
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
        DataUtilities.urlToFile(new URL(fs.dataStore.shpFiles.get(ShpFileType.SHP)))
    }

    /**
     * Create a zip file with all of the Shapefile's files
     * @param options The optional named parameters
     * <ul>
     *     <li> zipFile = The optional zip File which defaults to a zip file in the same directory as the Shapefile</li>
     * </ul>
     * @return The zip File
     */
    File zip(Map options = [:]) {
        File dir = this.file.absoluteFile.parentFile
        File zipFile = options.get("zipFile", new File(dir, "${this.name}.zip"))
        GeoScript.zip(["shp", "dbf", "shx", "prj", "qix", "fix"].collect { String ext ->
            new File(dir, "${this.name}.${ext}")
        }, zipFile)
    }

    /**
     * Unzip the zip file and return a Shapefile if possible
     * @param options The optional named parameters
     * <ul>
     *     <li>dir = The directory in which the zip file is unzipped</li>
     * </ul>
     * @param file The zip File or file name
     * @return A Shapefile or null
     */
    static Shapefile unzip(Map options = [:], def file) {
        File zipFile = file instanceof File ? file as File : new File(file)
        String name = options.get("name", ".shp")
        File dir = options.get("dir", new File(zipFile.absoluteFile.parentFile, zipFile.name.substring(0, zipFile.name.lastIndexOf(".zip"))))
        GeoScript.unzip(zipFile, dir)
        File shpFile = dir.listFiles().find{ File f -> f.name.endsWith(name)}
        shpFile != null ? new Shapefile(shpFile) : null
    }

    /**
     * Dump the Layer which may contain more than one Geometry type into a Directory of Shapefiles
     * @param options The optional named parameters:
     * <ul>
     *     <li> maxShapeSize = The maximum shp size </li>
     *     <li> maxDbfSize = The maximum dbf size </li>
     * </ul>
     * @param dir The File where the Shapefiles will be written
     * @param layer The Layer to turn into Shapefiles
     * @return A Directory Workspace
     */
    static Directory dump(Map options = [:], File dir, Layer layer) {
        dump(options, dir, layer.cursor)
    }

    /**
     * Dump the Cursor which may contain more than one Geometry type into a Directory of Shapefiles
     * @param options The optional named parameters:
     * <ul>
     *     <li> maxShapeSize = The maximum shp size </li>
     *     <li> maxDbfSize = The maximum dbf size </li>
     * </ul>
     * @param dir The File where the Shapefiles will be written
     * @param cursor The Cursor to turn into Shapefiles
     * @return A Directory Workspace
     */
    static Directory dump(Map options = [:], File dir, Cursor cursor) {
        ShapefileDumper dumper = new ShapefileDumper(dir)
        if (options.maxShapeSize) {
            dumper.maxShapeSize = options.maxShapeSize
        }
        if (options.maxDbfSize) {
            dumper.maxDbfSize = options.maxDbfSize
        }
        dumper.dump(cursor.col)
        new Directory(dir)
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