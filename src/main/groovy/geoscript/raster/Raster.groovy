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
import geoscript.layer.Layer
import org.geotools.process.raster.gs.ContourProcess
import geoscript.workspace.Memory
import geoscript.feature.Schema
import org.geotools.process.raster.gs.PolygonExtractionProcess
import org.geotools.process.raster.gs.RasterAsPointCollectionProcess
import org.geotools.process.raster.gs.AddCoveragesProcess
import org.geotools.process.raster.gs.MultiplyCoveragesProcess
import org.jaitools.numeric.Range
import org.geotools.process.raster.gs.ScaleCoverage
import org.geotools.coverage.grid.GridCoverageFactory
import org.geotools.process.raster.gs.RasterZonalStatistics

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
    GridCoverageReader reader

    /**
     * The Style
     */
    Style style

    /**
     * Create a new Raster from a List of List of float values
     * @param data A List of Lists of float values
     * @param bounds The geographic Bounds
     * @param format The GeoTools AbstractGridFormat
     */
    Raster(List data, Bounds bounds, AbstractGridFormat format) {
        def matrix = data.collect{datum ->
            datum.collect{
                it as float
            } as float[]
        } as float[][]
        def factory = new GridCoverageFactory()
        this.coverage = factory.create("Raster", matrix, bounds.env)
        this.gridFormat = format
        this.style = new geoscript.style.Raster()
    }

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
     * @param coverage The GeoTools AbstractGridCoverage
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
     * Scale this Raster
     * @param x The scale factor along the x axis
     * @param y The scale factor along the y axis
     * @return A new scaled Raster
     */
    Raster scale(double x, double y) {
        def process = new ScaleCoverage()
        def grid = process.execute(coverage, x, y, 0, 0, null)
        new Raster(grid, gridFormat)
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

    /**
     * Add this Raster with another Raster
     * @param other Another a Raster
     * @return A new Raster
     */
    Raster add(Raster other) {
        def process = new AddCoveragesProcess()
        def cov = process.execute(this.coverage, other.coverage, null)
        new Raster(cov, gridFormat)
    }

    /**
     * Add this Raster with another Raster
     * <p><code>def r3 = r1 + r2</code></p>
     * @param other Another a Raster
     * @return A new Raster
     */
    Raster plus(Raster other) {
        add(other)
    }

    /**
     * Multiple this Raster with another Raster
     * <p><code>def r3 = r1 * r2</code></p>
     * @param other Another a Raster
     * @return A new Raster
     */
    Raster multiply(Raster other) {
        def process = new MultiplyCoveragesProcess()
        def cov = process.execute(this.coverage, other.coverage, null)
        new Raster(cov, gridFormat)
    }

    /**
     * Create contours
     * @param band The Raster band
     * @param intervalOrLevels The contour interval or a List of levels
     * @param simplify Whether to simplify the contours
     * @param smooth Whether to smooth the contours
     * @return A Layer
     */
    Layer contours(int band, def intervalOrLevels, boolean simplify = true, boolean smooth = true, Bounds bounds = null) {
        def levels = null
        def interval = null
        if (intervalOrLevels instanceof Collection) {
            levels = intervalOrLevels as double[]
        } else {
            interval = intervalOrLevels as double
        }
        def fc = ContourProcess.process(coverage, band, levels, interval, simplify, smooth, bounds.geometry.g, null)
        Schema s = new Schema(fc.schema)
        Schema schema =  new Schema("contours", s.fields)
        Layer layer = new Memory().create(schema)
        layer.add(fc)
        layer
    }

    /**
     * Calculate the zonal statistics of this Raster
     * @param band The band
     * @param zones A Layer of polygons representing the zones
     * @param classification An optional Raster whose values are used as classes
     * @return A Layer with statistics (count, min, max, sum, avg, stddev, and optionally classification)
     */
    Layer zonalStatistics(int band, Layer zones, Raster classification = null) {
        def calculator = new RasterZonalStatistics()
        def fc = calculator.execute(this.coverage, band, zones.fs.features, classification?.coverage)
        new Layer("${zones.name}ZonalStatistics", fc)
    }

    /**
     * Extract Polygons.
     * @param band The band
     * @param insideEdges Whether to include the inside edges or not
     * @param bounds The geographic Bounds
     * @param noData The List of no data values
     * @param range A List of range Maps with min, minIncluded, max, and maxIncluded keys
     * @return A Layer
     */
    Layer toPolygons(int band, boolean insideEdges = true, Bounds bounds = null, List noData = null, List ranges = null) {
        if (noData != null) {
            noData = noData as Number[]
        }
        List rangeList = null
        if (ranges != null) {
            rangeList = ranges.collect{rng ->
                Range.create(rng.get("min"), rng.get("minIncluded", true), rng.get("max"), rng.get("maxIncluded", true))
            }
        }
        PolygonExtractionProcess process = new PolygonExtractionProcess()
        def fc = process.execute(coverage, band, insideEdges, bounds.geometry.g, noData, rangeList, null)
        Schema s = new Schema(fc.schema)
        Schema schema =  new Schema("polygons", s.fields)
        Layer layer = new Memory().create(schema)
        layer.add(fc)
        layer
    }

    /**
     * Convert this Raster into a Layer of Points
     * @return A Layer
     */
    Layer toPoints() {
        def process = new RasterAsPointCollectionProcess()
        def fc = process.execute(coverage)
        Schema s = new Schema(fc.schema)
        Schema schema =  new Schema("points", s.fields)
        Layer layer = new Memory().create(schema)
        layer.add(fc)
        layer
    }
}

