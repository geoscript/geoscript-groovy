package geoscript.layer

import geoscript.geom.Bounds
import geoscript.proj.Projection
import org.geotools.coverage.grid.GridEnvelope2D
import org.geotools.coverage.grid.GridGeometry2D
import org.geotools.coverage.grid.io.AbstractGridFormat
import org.geotools.coverage.grid.io.GridFormatFinder
import org.geotools.coverage.grid.io.UnknownFormat
import org.geotools.factory.GeoTools
import org.geotools.factory.Hints
import org.opengis.coverage.grid.GridCoverageReader
import org.opengis.parameter.GeneralParameterValue
import org.opengis.parameter.ParameterValueGroup

import java.awt.*
import java.util.List

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
            // Check for ReadGridGeometry2D parameters
            if (options.containsKey("bounds")) {
                Bounds bounds = options.bounds
                List size = options.containsKey("size") ? options.size : [500,500]
                options.put("ReadGridGeometry2D", new GridGeometry2D(
                        new GridEnvelope2D(new Rectangle(size[0], size[1])),
                        bounds.env
                ))
                options.remove("bounds")
                options.remove("size")
            }
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
     * Whether the Format has a Raster by the given name
     * @param name The Raster name
     * @return Whether the Format has a Raster by the given name
     */
    boolean has(String name) {
        getNames().contains(name)
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
     * Get the Format that can read the given input stream
     * @param input The input stream (which is usually a File, but can be a URL, InputStream, or connection string)
     * @return A Format
     */
    static Format getFormat(Object input) {
        Format format = null
        // Try with FormatFactories
        for (FormatFactory formatFactory : FormatFactories.list()) {
            format = formatFactory.create(input)
            if (format != null) {
                break
            }
        }
        // Then try with GeoTools GridFormatFinder
        if (!format) {
            AbstractGridFormat gridFormat = null
            try {
                gridFormat = GridFormatFinder.findFormat(input)
            } catch(Exception ex) {
                // Move on
            }
            if (gridFormat && !(gridFormat instanceof UnknownFormat)) {
                format = new Format(gridFormat, input)
            }
        }
        format
    }
}
