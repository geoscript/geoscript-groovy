package geoscript.raster

import geoscript.proj.Projection
import geoscript.geom.Bounds
import geoscript.geom.Point
import geoscript.style.Style
import org.geotools.coverage.processing.CoverageProcessor
import org.geotools.coverage.grid.AbstractGridCoverage
import org.geotools.process.raster.ContourProcess
import org.geotools.process.raster.PolygonExtractionProcess
import org.geotools.process.raster.RasterAsPointCollectionProcess
import org.geotools.process.raster.RasterZonalStatistics
import geoscript.layer.Layer
import geoscript.workspace.Memory
import geoscript.feature.Schema
import org.jaitools.numeric.Range
import org.geotools.coverage.grid.GridCoverageFactory
import javax.media.jai.Interpolation

/**
 * The Raster
 * @author Jared Erickson
 */
class Raster {

    /**
     * A GeoScript Raster wraps a GeoTools AbstractGridCoverage
     */
    AbstractGridCoverage coverage

    /**
     * The Style
     */
    Style style

    /**
     * Create a new Raster from a List of List of float values
     * @param data A List of Lists of float values
     * @param bounds The geographic Bounds
     */
    Raster(List data, Bounds bounds) {
        def matrix = data.collect{datum ->
            datum.collect{
                it as float
            } as float[]
        } as float[][]
        def factory = new GridCoverageFactory()
        this.coverage = factory.create("Raster", matrix, bounds.env)
        this.style = new geoscript.style.Raster()
    }

    /**
     * Create a Raster from a GeoTools AbstractGridCoverage.
     * @param coverage The GeoTools AbstractGridCoverage
     */
    Raster(AbstractGridCoverage coverage) {
       this.coverage = coverage
       this.style = new geoscript.style.Raster()
    }

    /**
     * Get the Projection
     * @return The Projection
     */
    Projection getProj() {
        def crs = coverage.coordinateReferenceSystem
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
        def grid = coverage.gridGeometry.gridRange
        double minX = grid.getLow(0)
        double maxX = grid.getHigh(0)
        double minY = grid.getLow(1)
        double maxY = grid.getHigh(1)
        [maxX - minX, maxY - minY]
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
        [w,h]
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
        def processor = new CoverageProcessor()
        def params = processor.getOperation("CoverageCrop").parameters
        params.parameter("Source").value = coverage
        params.parameter("Envelope").value = new org.geotools.geometry.GeneralEnvelope(bounds.env)
        def newCoverage = processor.doOperation(params)
        new Raster(newCoverage)
    }

    /**
     * Scale this Raster
     * @param x The scale factor along the x axis
     * @param y The scale factor along the y axis
     * @param xTrans The x translation
     * @param yTrans The y translation
     * @param interpolation The interpolation method (bicubic, bicubic2, bilinear, nearest)
     * @return A new scaled Raster
     */
    Raster scale(float x, float y, float xTrans = 0, float yTrans = 0, String interpolation = "nearest") {
        int interp
        if (interpolation.equalsIgnoreCase("bicubic")) {
            interp = Interpolation.INTERP_BICUBIC
        } else if (interpolation.equalsIgnoreCase("bicubic2")) {
            interp = Interpolation.INTERP_BICUBIC_2
        } else if (interpolation.equalsIgnoreCase("bilinear")) {
            interp = Interpolation.INTERP_BILINEAR
        } else {
            interp = Interpolation.INTERP_NEAREST
        }
        def processor = new CoverageProcessor()
        def params = processor.getOperation("Scale").parameters
        params.parameter("Source").value = this.coverage
        params.parameter("xScale").value = x
        params.parameter("yScale").value = y
        params.parameter("xTrans").value = xTrans
        params.parameter("yTrans").value = yTrans
        params.parameter("Interpolation").value = Interpolation.getInstance(interp);
        def newCoverage = processor.doOperation(params)
        new Raster(newCoverage)
    }

    /**
     * Reproject this Raster to another Projection creating a new Raster
     * @param proj The Projection
     * @return A new Raster
     */
    Raster reproject(Projection proj) {
        def processor = new CoverageProcessor()
        def params = processor.getOperation("Resample").parameters
        params.parameter("Source").value = this.coverage
        params.parameter("CoordinateReferenceSystem").value = proj.crs
        def newCoverage = processor.doOperation(params)
        new Raster(newCoverage)
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
        def processor = new CoverageProcessor()
        def params = processor.getOperation("Add").parameters
        params.parameter("Source0").value = this.coverage
        params.parameter("Source1").value = other.coverage
        def newCoverage = processor.doOperation(params)
        new Raster(newCoverage)
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
        def processor = new CoverageProcessor()
        def params = processor.getOperation("Multiply").parameters
        params.parameter("Source0").value = this.coverage
        params.parameter("Source1").value = other.coverage
        def newCoverage = processor.doOperation(params)
        new Raster(newCoverage)
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

