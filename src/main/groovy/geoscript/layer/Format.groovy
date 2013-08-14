package geoscript.layer

import geoscript.proj.Projection
import org.geotools.coverage.grid.io.AbstractGridFormat
import org.geotools.coverage.grid.io.GridFormatFinder
import org.geotools.coverage.grid.io.UnknownFormat
import org.geotools.coverageio.gdal.mrsid.MrSIDFormat
import org.geotools.factory.GeoTools
import org.geotools.factory.Hints
import org.geotools.gce.arcgrid.ArcGridFormat
import org.geotools.gce.geotiff.GeoTiffFormat
import org.geotools.gce.grassraster.format.GrassCoverageFormat
import org.geotools.gce.gtopo30.GTopo30Format
import org.geotools.gce.image.WorldImageFormat
import org.geotools.gce.imagemosaic.ImageMosaicFormat
import org.geotools.gce.imagepyramid.ImagePyramidFormat
import org.opengis.parameter.GeneralParameterValue
import org.opengis.parameter.ParameterValueGroup

/**
 * A Raster Format can read and write Rasters
 * @author Jared Erickson
 */
class Format {

    /**
     * The GeoTools AbstractGridFormat
     */
    AbstractGridFormat gridFormat

    /**
     * Create a new Format wrapping an AbstractGridFormat
     * @param gridFormat The GeoTools AbstractGridFormat
     */
    Format(AbstractGridFormat gridFormat) {
        this.gridFormat = gridFormat
    }

    /**
     * Get the format name
     * @return The format name
     */
    String getName() {
        gridFormat.name
    }

    /**
     * Write the Raster to the destination object (usually a File)
     * @param raster The Raster to write
     * @param destination The destination object (usually a File)
     */
    void write(Map options = [:], Raster raster, def destination) {
        def writer = gridFormat.getWriter(destination)
        ParameterValueGroup params = writer.format.writeParameters
        options.each{k,v ->
            params.parameter(k).setValue(v)
        }
        def gpv = options.collect{k,v ->
            params.parameter(k)
        }
        writer.write(raster.coverage, gpv as GeneralParameterValue[])
    }

    /**
     * Read a Raster from the source (usually a File)
     * @param options Optional named parameters that are turned into an array
     * of GeoTools GeneralParameterValues
     * @param source The source (usually a File)
     * @return A Raster
     */
    Raster read(Map options = [:], def source) {
        this.read(options, source, GeoTools.getDefaultHints())
    }

    /**
     * Read a Raster from the source (usually a File)
     * @param options Optional named parameters that are turned into an array
     * of GeoTools GeneralParameterValues
     * @param source The source (usually a File)
     * @param proj The Projection
     * @return A Raster
     */
    Raster read(Map options = [:], def source, Projection proj) {
        // Create Hints
        Hints hints = GeoTools.getDefaultHints()
        if (proj) {
            hints.put(Hints.DEFAULT_COORDINATE_REFERENCE_SYSTEM, proj.crs)
        }
        this.read(options, source, hints)
    }

    /**
     * Read a Raster from the source (usually a File)
     * @param options Optional named parameters that are turned into an array
     * of GeoTools GeneralParameterValues
     * @param source The source (usually a File)
     * @param hints GeoTools Hints
     * @return A Raster
     */
    Raster read(Map options = [:], def source, Hints hints) {
        // Create Reader
        def reader = gridFormat.getReader(source, hints)
        // Create GeneralParameterValues
        ParameterValueGroup params = reader.format.readParameters
        options.each{k,v ->
            params.parameter(k).setValue(v)
        }
        def gpv = options.collect{k,v ->
            params.parameter(k)
        }
        // Read the Raster
        new Raster(reader.read(gpv as GeneralParameterValue[]), this)
    }

    /**
     * Get a String representation of this Format
     * @return The Format's name
     */
    String toString() {
        name
    }

    /**
     * Get the Format that can read the given File
     * @param file The File
     * @return A Format
     */
    static Format getFormat(File file) {
        if (file.exists()) {
            AbstractGridFormat format = GridFormatFinder.findFormat(file);
            if (format == null || format instanceof UnknownFormat) {
                return null
            }
            if (format instanceof GeoTiffFormat) {
                return new GeoTIFF()
            } else if (format instanceof ArcGridFormat) {
                return new ArcGrid()
            } else if (format instanceof GrassCoverageFormat) {
                return new Grass()
            } else if (format instanceof GTopo30Format) {
                return new GTopo30()
            } else if (format instanceof ImagePyramidFormat) {
                return new ImagePyramid()
            } else if (format instanceof ImageMosaicFormat) {
                return new Mosaic()
            } else if (format instanceof MrSIDFormat) {
                return new MrSID()
            } else if (format instanceof WorldImageFormat) {
                return new WorldImage()
            } else {
                return new Format(format)
            }
        } else {
            String ext = file.name.substring(file.name.lastIndexOf(".") + 1).toLowerCase()
            if (ext in ["tif"]) {
                return new GeoTIFF()
            } else if (ext in ["png","jpg","jpeg","gif"]) {
                return new WorldImage()
            } else if (ext in ["arx"]) {
                return new Grass()
            } else if (ext in ["sid"]) {
                return new MrSID()
            } else if (ext in ["asc"]) {
                return new ArcGrid()
            } else {
                return null
            }
        }
    }
}
