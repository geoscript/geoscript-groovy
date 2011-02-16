package geoscript.raster

import geoscript.proj.Projection
import geoscript.geom.Bounds
import geoscript.style.Style
import geoscript.style.RasterSymbolizer
import org.geotools.factory.Hints
import org.geotools.coverage.grid.AbstractGridCoverage
import org.opengis.coverage.grid.GridCoverageReader
import org.geotools.coverage.grid.io.AbstractGridFormat

/**
 * The Raster base class
 * @author Jared Erickson
 */
class Raster {

    /**
     * A GeoScript Raster wraps a GeoTools AbstractGridCoverage
     */
    AbstractGridCoverage coverage

    /**
     * The GeoTools AbstractGridFormat
     */
    AbstractGridFormat gridFormat

    /**
     * The GeoTools GridCoverageReader
     */
    protected GridCoverageReader reader

    /**
     * The Style
     */
    Style style

    /**
     * Create a new Raster using a given Format to read the File.
     * @param format The GeoTools AbstractGridFormat
     * @param file The File
     * @param proj The optional Projection (null by default)
     */
    Raster(AbstractGridFormat format, File file, Projection proj = null) {
        Hints hints = new Hints()
        if (proj) {
            hints.put(Hints.DEFAULT_COORDINATE_REFERENCE_SYSTEM, proj.crs)
        }
        this.gridFormat = format
        this.reader = gridFormat.getReader(file, hints)
        this.coverage = reader.read(null)
        style = new Style(new RasterSymbolizer())
    }

    /**
     * Create a Raster from a GeoTools AbstractGridCoverage and an AbstractGridFormat.
     * @param coverage The GeoTools AbstractGridFormat
     * @param format The GeoTools AbstractGridFormat
     */
    Raster(AbstractGridCoverage coverage, AbstractGridFormat format) {
       this.coverage = coverage
       this.gridFormat = format
       style = new Style(new RasterSymbolizer())
    }

    /**
     * Get the format name
     * @return The format name
     */
    String getFormat() {
        gridFormat.getName()
    }

    /**
     * Get the Projection
     * @return The Projection
     */
    Projection getProj() {
        def crs = coverage.coordinateReferenceSystem2D
        if (crs) {
            new Projection(crs)
        } else {
            null
        }
    }

    /**
     * Get the Bounds
     * @return The Bounds
     */
    Bounds getBounds() {
        def env = coverage.envelope
        def crs = env.coordinateReferenceSystem
        if (crs == null) {
            crs = getProj()
        }
        def l = env.lowerCorner.coordinate
        def u = env.upperCorner.coordinate
        new Bounds(l[0], l[1], u[0], u[1], new Projection(crs))
    }

    /**
     * Get the size
     * @return The size [w,h]
     */
    List getSize() {
        def grid = coverage.gridGeometry.gridRange2D
        [grid.width, grid.height]
    }

    /**
     * Get the List of Bands
     * @return The List of Bands
     */
    List<Band> getBands() {
        (0..<coverage.numSampleDimensions).collect{i ->
            new Band(coverage.getSampleDimension(i))
        }
    }

    /**
     * Get the block size
     * @return The block size [w,h]
     */
    List getBlockSize() {
        def (int w, int h) = coverage.optimalDataBlockSizes
    }

    /**
     * Get the pixel size
     * @return The pixel size [w,h]
     */
    List getPixelSize() {
        def b = getBounds()
        def s = getSize()
        [b.width / s[0], b.height / s[1]]
    }

    /**
     * Render the Raster in a simple GUI
     */
    void render() {
        coverage.show()
    }
}

