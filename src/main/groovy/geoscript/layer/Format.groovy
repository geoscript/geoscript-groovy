package geoscript.layer

import geoscript.proj.Projection
import org.geotools.coverage.grid.io.AbstractGridFormat
import org.geotools.coverage.grid.io.GridFormatFinder
import org.geotools.coverage.grid.io.UnknownFormat
import org.geotools.coverage.io.netcdf.NetCDFFormat
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
import org.opengis.coverage.grid.GridCoverageReader
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
     * The stream which is usually a File
     */
    protected def stream

    /**
     * Create a new Format wrapping an AbstractGridFormat
     * @param gridFormat The GeoTools AbstractGridFormat
     */
    @Deprecated
    Format(AbstractGridFormat gridFormat) {
        this.gridFormat = gridFormat
    }

    /**
     * Create a new Format wrapping an AbstractGridFormat
     * @param gridFormat The GeoTools AbstractGridFormat
     * @param stream The input or output stream
     */
    Format(AbstractGridFormat gridFormat, def stream) {
        this.gridFormat = gridFormat
        this.stream = stream
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
    @Deprecated
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
     * Write the Raster to the destination object (usually a File)
     * @param raster The Raster to write
     */
    void write(Map options = [:], Raster raster) {
        def writer = gridFormat.getWriter(stream)
        try {
            ParameterValueGroup params = writer.format.writeParameters
            options.each{k,v ->
                params.parameter(k).setValue(v)
            }
            def gpv = options.collect{k,v ->
                params.parameter(k)
            }
            writer.write(raster.coverage, gpv as GeneralParameterValue[])
        } finally {
            writer.dispose()
        }
    }

    /**
     * Read a Raster from the source (usually a File)
     * @param options Optional named parameters that are turned into an array
     * of GeoTools GeneralParameterValues
     * @param source The source (usually a File)
     * @return A Raster
     */
    @Deprecated
    Raster read(Map options = [:], def source) {
        this.read(options, source, GeoTools.getDefaultHints())
    }

    /**
     * Read a Raster from the stream (usually a File)
     * @param options Optional named parameters that are turned into an array
     * of GeoTools GeneralParameterValues
     * @return A Raster
     */
    Raster read(Map options = [:]) {
        this.read(options, GeoTools.getDefaultHints())
    }

    /**
     * Read a Raster from the stream (usually a File)
     * @param options Optional named parameters that are turned into an array
     * of GeoTools GeneralParameterValues
     * @param name The name of the Raster to read
     * @return A Raster
     */
    Raster read(Map options = [:], String name) {
        this.read(options, name, GeoTools.getDefaultHints())
    }

    /**
     * Read a Raster from the source (usually a File)
     * @param options Optional named parameters that are turned into an array
     * of GeoTools GeneralParameterValues
     * @param source The source (usually a File)
     * @param proj The Projection
     * @return A Raster
     */
    @Deprecated
    Raster read(Map options = [:], def source, Projection proj) {
        // Create Hints
        Hints hints = GeoTools.getDefaultHints()
        if (proj) {
            hints.put(Hints.DEFAULT_COORDINATE_REFERENCE_SYSTEM, proj.crs)
        }
        this.read(options, source, hints)
    }

    /**
     * Read a Raster from the stream (usually a File)
     * @param options Optional named parameters that are turned into an array
     * of GeoTools GeneralParameterValues
     * @param proj The Projection
     * @return A Raster
     */
    Raster read(Map options = [:], Projection proj) {
        this.read(options, "", proj)
    }

    /**
     * Read a Raster from the stream (usually a File)
     * @param options Optional named parameters that are turned into an array
     * of GeoTools GeneralParameterValues
     * @param name The name of the Raster
     * @param proj The Projection
     * @return A Raster
     */
    Raster read(Map options = [:], String name, Projection proj) {
        // Create Hints
        Hints hints = GeoTools.getDefaultHints()
        if (proj) {
            hints.put(Hints.DEFAULT_COORDINATE_REFERENCE_SYSTEM, proj.crs)
        }
        this.read(options, name, hints)
    }

    /**
     * Read a Raster from the source (usually a File)
     * @param options Optional named parameters that are turned into an array
     * of GeoTools GeneralParameterValues
     * @param source The source (usually a File)
     * @param hints GeoTools Hints
     * @return A Raster
     */
    @Deprecated
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
     * Read a Raster from the stream (usually a File)
     * @param options Optional named parameters that are turned into an array
     * of GeoTools GeneralParameterValues
     * @param hints GeoTools Hints
     * @return A Raster
     */
    Raster read(Map options = [:],  Hints hints) {
        this.read(options, "", hints)
    }

    /**
     * Read a Raster from the stream (usually a File)
     * @param options Optional named parameters that are turned into an array
     * of GeoTools GeneralParameterValues
     * @param name The name of the Raster
     * @param hints GeoTools Hints
     * @return A Raster
     */
    Raster read(Map options = [:], String name, Hints hints) {
        Raster raster = null
        // Create Reader
        def reader = gridFormat.getReader(stream, hints)
        try {
            // Create GeneralParameterValues
            ParameterValueGroup params = reader.format.readParameters
            options.each{k,v ->
                params.parameter(k).setValue(v)
            }
            def gpv = options.collect{k,v ->
                params.parameter(k)
            }
            // Read the Raster
            if (name) {
                raster = new Raster(reader.read(name, gpv as GeneralParameterValue[]), this)
            } else {
                raster = new Raster(reader.read(gpv as GeneralParameterValue[]), this)
            }
        } finally {
            reader.dispose()
        }
        raster
    }

    /**
     * Get the list of the raster names
     * @return A List of the raster names
     */
    List<String> getNames() {
        List names = []
        GridCoverageReader reader = this.gridFormat.getReader(stream)
        try {
            names.addAll(reader.gridCoverageNames)
        } finally {
            reader.dispose()
        }
        names
    }

    /**
     * Get metadata
     * @param name The optional Raster name
     * @return A Map of metadata
     */
    Map getMetadata(String name = null) {
        Map metadata = [:]
        GridCoverageReader reader = this.gridFormat.getReader(stream)
        try {
            reader.metadataNames.each { String metadataName ->
                metadata[metadataName] = name ?
                        reader.getMetadataValue(name, metadataName) : reader.getMetadataValue(metadataName)
            }
        } finally {
            reader.dispose()
        }
        metadata
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
                return new GeoTIFF(file)
            } else if (format instanceof ArcGridFormat) {
                return new ArcGrid(file)
            } else if (format instanceof GrassCoverageFormat) {
                return new Grass(file)
            } else if (format instanceof GTopo30Format) {
                return new GTopo30(file)
            } else if (format instanceof ImagePyramidFormat) {
                return new ImagePyramid(file)
            } else if (format instanceof ImageMosaicFormat) {
                return new Mosaic(file)
            } else if (format instanceof MrSIDFormat) {
                return new MrSID(file)
            } else if (format instanceof WorldImageFormat) {
                return new WorldImage(file)
            } else if (format instanceof NetCDFFormat) {
                return new NetCDF(file)
            } else {
                return new Format(format, file)
            }
        } else {
            String ext = file.name.substring(file.name.lastIndexOf(".") + 1).toLowerCase()
            if (ext in ["tif"]) {
                return new GeoTIFF(file)
            } else if (ext in ["png","jpg","jpeg","gif"]) {
                return new WorldImage(file)
            } else if (ext in ["arx"]) {
                return new Grass(file)
            } else if (ext in ["sid"]) {
                return new MrSID(file)
            } else if (ext in ["asc"]) {
                return new ArcGrid(file)
            } else if (ext in ["nc"]) {
                return new NetCDF(file)
            } else {
                return null
            }
        }
    }
}
