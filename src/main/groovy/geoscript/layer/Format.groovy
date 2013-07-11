package geoscript.layer

import geoscript.proj.Projection
import org.geotools.coverage.grid.io.AbstractGridFormat
import org.geotools.factory.Hints

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
     * @param source The source (usually a File)
     * @param proj The Projection
     * @return A Raster
     */
    Raster read(def source, Projection proj = null) {
        Hints hints = new Hints()
        if (proj) {
            hints.put(Hints.DEFAULT_COORDINATE_REFERENCE_SYSTEM, proj.crs)
        }
        def reader = gridFormat.getReader(source, hints)
        new Raster(reader.read(null))
    }
}
