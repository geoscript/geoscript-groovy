package geoscript.raster

import geoscript.proj.Projection
import geoscript.geom.Bounds
import geoscript.geom.Point
import geoscript.style.Style
import org.geotools.factory.Hints
import org.geotools.coverage.grid.AbstractGridCoverage
import org.opengis.coverage.grid.GridCoverageReader
import org.geotools.coverage.grid.io.AbstractGridFormat
import org.geotools.coverage.processing.DefaultProcessor

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
        this.style = new geoscript.style.Raster()
    }

    /**
     * Create a Raster from a GeoTools AbstractGridCoverage and an AbstractGridFormat.
     * @param coverage The GeoTools AbstractGridFormat
     * @param format The GeoTools AbstractGridFormat
     */
    Raster(AbstractGridCoverage coverage, AbstractGridFormat format) {
       this.coverage = coverage
       this.gridFormat = format
       this.style = new geoscript.style.Raster()
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

    /**
     * Crop this Raster
     * @param bounds The Bounds
     * @return A new Raster
     */
    Raster crop(Bounds bounds) {
        def processor = new DefaultProcessor()
        def params = processor.getOperation("CoverageCrop").parameters
        params.parameter("Source").value = coverage
        params.parameter("Envelope").value = new org.geotools.geometry.GeneralEnvelope(bounds.env)
        def cropped = processor.doOperation(params)
        new Raster(cropped, gridFormat)
    }

    /**
     * Reproject this Raster to another Projection creating a new Raster
     * @param proj The Projection
     * @return A new Raster
     */
    Raster reproject(Projection proj) {
        def processor = new DefaultProcessor()
        def params = processor.getOperation("Resample").parameters
        params.parameter("Source").value = coverage
        params.parameter("CoordinateReferenceSystem").value = proj.crs
        def reprojected = processor.doOperation(params)
        new Raster(reprojected, gridFormat)
    }

    /**
     * Write the Raster to the File
     * @param file The File
     */
    void write(File file) {
       gridFormat.getWriter(file).write(coverage, null)
    }

    /**
     * Get the value of the Raster at the given Location.
     * If the Raster contains multiple bands a Collection of values, one for
     * each band, will be returned.
     * @param point The Point where we want a value from the Raster
     * @return A value
     */
    def evaluate(Point point) {
        coverage.evaluate(new org.geotools.geometry.GeneralDirectPosition(point.x, point.y))
    }
}

