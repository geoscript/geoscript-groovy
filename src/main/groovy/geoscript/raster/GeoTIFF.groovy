package geoscript.raster

import geoscript.geom.Bounds
import geoscript.proj.Projection
import org.geotools.gce.geotiff.GeoTiffFormat
import org.geotools.gce.geotiff.GeoTiffReader
import org.geotools.coverage.grid.io.imageio.IIOMetadataDumper
import org.geotools.coverage.grid.AbstractGridCoverage

/**
 * A GeoTIFF Raster.
 * <p>You can create a GeoTIFF Raster from a File and an optional Projection.</p>
 * <code>GeoTIFF tiff = new GeoTIFF(new File("alki.tif"))</code>
 * @author Jared Erickson
 */
class GeoTIFF extends Raster {

    /**
     * Create a new GeoTIFF
     * @param file The GeoTIFF File
     * @param proj The optional Projection
     */
    GeoTIFF(File file, Projection proj = null) {
        super(new GeoTiffFormat(), file, proj)
    }

    /**
     * Create a new GeoTIFF
     * @param coverage The GeoTools AbstractGridCoverage
     * @param proj The optional Projection
     */
    GeoTIFF(AbstractGridCoverage coverage) {
        super(coverage, new GeoTiffFormat())
    }

    /**
     * Create a new GeoTIFF from a List of data values
     * @param data The List of data values
     * @param bounds The geographic Bounds
     */
    GeoTIFF(List data, Bounds bounds) {
        super(data, bounds, new GeoTiffFormat())
    }

    /**
     * Get the pixel size
     * @return A List of pixel sizes (w,h)
     */
    @Override
    List getPixelSize() {
        def md = (reader as GeoTiffReader).metadata
        if (md.hasPixelScales()) {
            def ps = md.modelPixelScales
            def values = ps.values
            return values
        } else {
            return super.getPixelSize()
        }
    }

    /**
     * Dump metadata to standard output
     */
    void dump() {
        def md = (reader as GeoTiffReader).metadata
        new IIOMetadataDumper(md.rootNode).metadata.split("\n").each {s ->
            println(s)
        }
    }
}
