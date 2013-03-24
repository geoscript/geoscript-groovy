package geoscript.raster

import geoscript.geom.Geometry
import geoscript.proj.Projection
import geoscript.geom.Bounds
import geoscript.geom.Point
import geoscript.style.RasterSymbolizer
import geoscript.style.Style
import org.geotools.coverage.grid.GridCoordinates2D
import org.geotools.coverage.grid.GridCoverage2D
import org.geotools.coverage.grid.GridEnvelope2D
import org.geotools.coverage.grid.GridGeometry2D
import org.geotools.coverage.processing.CoverageProcessor
import org.geotools.coverage.processing.OperationJAI
import org.geotools.geometry.DirectPosition2D
import org.geotools.process.raster.ContourProcess
import org.geotools.process.raster.PolygonExtractionProcess
import org.geotools.process.raster.RasterAsPointCollectionProcess
import org.geotools.process.raster.RasterZonalStatistics
import geoscript.layer.Layer
import geoscript.workspace.Memory
import geoscript.feature.Schema
import org.geotools.util.NumberRange
import org.jaitools.numeric.Range
import org.geotools.coverage.grid.GridCoverageFactory

import javax.media.jai.Interpolation
import java.awt.image.RenderedImage

/**
 * A Raster
 * @author Jared Erickson
 */
class Raster {

    /**
     * A GeoScript Raster wraps a GeoTools GridCoverage2D
     */
    GridCoverage2D coverage

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
        this.style = new RasterSymbolizer()
    }

    /**
     * Create a Raster from a GeoTools GridCoverage2D.
     * @param coverage The GeoTools GridCoverage2D
     */
    Raster(GridCoverage2D coverage) {
       this.coverage = coverage
       this.style = new RasterSymbolizer()
    }

    /**
     * Create a Raster from an Image and Bounds
     * @param image The image
     * @param bounds The Bounds
     */
    Raster(RenderedImage image, Bounds bounds) {
        GridCoverageFactory gridCoverageFactory = new GridCoverageFactory()
        this.coverage = gridCoverageFactory.create("Raster", image, bounds.env)
        this.style = new RasterSymbolizer()
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
        def grid = coverage.gridGeometry.gridRange2D
        [grid.width as int, grid.height as int]
    }

    /**
     * Get the number of columns
     * @return The number of columns
     */
    int getCols() {
        size[0]
    }

    /**
     * Get the number of rows
     * @return The number rows
     */
    int getRows() {
        size[1]
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
     * Dispose of this Raster
     */
    void dispose() {
        coverage.dispose(false)
    }

    /**
     * Get the underlying java.awt.image.Raster
     * @return The java.awt.image.Raster
     */
    java.awt.image.Raster getData() {
        coverage.renderedImage.data
    }

    /**
     * Get the RenderedImage
     * @return The RenderedImage
     */
    RenderedImage getImage() {
        coverage.renderedImage
    }

    /**
     * Get the value of the Raster at the given geographic Location.
     * If the Raster contains multiple bands a Collection of values, one for
     * each band, will be returned.
     * @param point The Point where we want a value from the Raster
     * @return A value
     */
    List eval(Point point) {
        coverage.evaluate(new DirectPosition2D(point.x, point.y))
    }

    /**
     * Get the value of the Raster at the given pixel Location.
     * If the Raster contains multiple bands a Collection of values, one for
     * each band, will be returned.
     * @param x The pixel x coordinate
     * @param y The pixel y coordinate
     * @return A value
     */
    List eval(int x, int y) {
        eval(getPoint(x,y))
    }

    /**
     * Get the value of the Raster at the given geographic Location or pixel
     * location.
     * If the Raster contains multiple bands a Collection of values, one for
     * each band, will be returned.
     * @param point The Point where we want a value from the Raster
     * @return A value
     */
    List getAt(def p) {
        if (p instanceof Point) {
            eval(p as Point)
        } else {
            eval(p[0] as int, p[1] as int)
        }
    }

    /**
     * Get a geographic Point from pixel coordinates
     * @param x The x pixel coordinate
     * @param y The y pixel coordinate
     * @return A geographic Point
     */
    Point getPoint(int x, int y) {
        GridGeometry2D gg = coverage.gridGeometry
        DirectPosition2D dp = gg.gridToWorld(new GridCoordinates2D(x,y))
        new Point(dp.x, dp.y)
    }

    /**
     * Get pixel coordinates from the geographic Point
     * @param p The geographic Point
     * @return A List of pixel coordinates
     */
    List getPixel(Point p) {
        GridGeometry2D gg = coverage.gridGeometry
        GridCoordinates2D gc = gg.worldToGrid(new DirectPosition2D(p.x, p.y))
        [gc.x, gc.y]
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
     * Reclassify this Raster with a List of ranges.
     * @param options Optional named parameters can include the band to reclassify and the noData value.
     * @param ranges The List of Ranges contains one or more Maps with min, max, and value keys.
     * @return A new Raster
     */
    Raster reclassify(Map options=[:], List ranges) {
        int band = options.get("band",0)
        double noData = options.get("noData",0)
        List<Range> rangeList = ranges.collect{r ->
            Range.create(r.get("min"), r.get("minIncluded", true), r.get("max"), r.get("maxIncluded", true))
        }
        List pixelValues = ranges.collect{r -> r.get("value")}
        def process = new org.geotools.process.raster.RangeLookupProcess()
        def cov = process.execute(this.coverage, band, rangeList, pixelValues as int[], noData, null)
        new Raster(cov)
    }

    /**
     * Calculate the min and max values for each band in this Raster.
     * @return A Map containing the min and max values
     */
    Map getExtrema() {
        def processor = new CoverageProcessor()
        def params = processor.getOperation("Extrema").parameters
        params.parameter("Source").value = this.coverage
        def result = processor.doOperation(params).getProperty("extrema")
        [
            min: result[0],
            max: result[1]
        ]
    }

    /**
     * Get a Histogram for this Raster
     * @param options Optional named parameters can include low, high, and numBins
     * @return A Histogram
     */
    Histogram getHistogram(Map options = [:]) {
        def low = options.get("low")
        def high = options.get("high")
        def numberOfBins = options.get("numBins")
        int numberOfBands = bands.size()

        def processor = new CoverageProcessor()
        def params = processor.getOperation("Histogram").parameters
        params.parameter("Source").value = this.coverage
        if (low) {
            if (!(low instanceof List)) {
                low = [low] * numberOfBands
                params.parameter("lowValue").value = low as double[]
            }
        }
        if (high) {
            if (!(high instanceof List)) {
                high = [high] * numberOfBands
                params.parameter("highValue").value = high as double[]
            }
        }
        if (numberOfBins) {
            if (!(numberOfBins instanceof List)) {
                numberOfBins = [numberOfBins] * numberOfBands
                params.parameter("numBins").value = numberOfBins as int[]
            }
        }
        def h = processor.doOperation(params)
        new Histogram(h.getProperty("histogram"))
    }

    /**
     * Resample this Raster
     * @param options Optional named parameters can include:
     * <ul>
     *     <li>bbox: A geographic Bounds</li>
     *     <li>rect: A List of 4 pixel coordinates representing a rectangle.</li>
     *     <li>size: A List resulting width and height</li>
     * </ul>
     * @return A new Raster
     */
    Raster resample(Map options = [:]) {
        // Options
        Bounds bbox = options.get("bbox")
        List rect = options.get("rect")
        List size = options.get("size")
        // Calculate bbox
        if (!bbox) {
            if (options.containsKey("rect")) {
                def (double dx, double dy) = pixelSize
                Bounds b = bounds
                bbox = new Bounds(
                    b.minX + rect[0] * dx,
                    b.minY + rect[1] * dy,
                    b.maxX + rect[2] * dx,
                    b.maxY + rect[3] * dy,
                    b.proj
                )
            } else {
                bbox = bounds
            }
        }
        // Calculate size
        if (!size) {
            if (!rect) {
                Bounds b = bounds
                int w = this.size[0] * bbox.width / b.width
                size = [w, (w * b.aspect) as int]
            } else {
                size = [rect[2], rect[3]]
            }
        }
        // Resample
        GridGeometry2D gg = new GridGeometry2D(new GridEnvelope2D(0,0,size[0],size[1]), bbox.env)
        def processor = new CoverageProcessor()
        def params = processor.getOperation("Resample").parameters
        params.parameter("Source").value = this.coverage
        params.parameter("CoordinateReferenceSystem").value = proj.crs
        params.parameter("GridGeometry").value = gg
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
     * Convert this Raster to a Layer of polygons.
     * @param options Optional named parameters may include:
     * <ul>
     * <li>band The band defaults to 0</li>
     * <li>insideEdges Whether to include the inside edges or not. Defaults to true</li>
     * <li>roi The Geometry region of interest.  Defaults to null.</li>
     * <li>noData The List of no data values.  Defaults to null.</li>
     * <li>range A List of range Maps with min, minIncluded, max, and maxIncluded keys.  Defaults to null.</li>
     * @return A Layer of polygons
     */
    Layer getPolygonLayer(Map options = [:]) {
        // Options
        int band = options.get("band",0)
        boolean insideEdges = options.get("insideEdges", true)
        Geometry roi = options.get("roi", null)
        def noData = options.get("noData", null)
        List ranges = options.get("ranges", null)
        // Prepare
        if (noData != null) {
            if (!(noData instanceof List)) {
                noData = [noData]
            }
            //noData = noData as Number[]
        }
        List rangeList = null
        if (ranges != null) {
            rangeList = ranges.collect{rng ->
                Range.create(rng.get("min"), rng.get("minIncluded", true), rng.get("max"), rng.get("maxIncluded", true))
            }
        }
        // Extract Polygons
        PolygonExtractionProcess process = new PolygonExtractionProcess()
        def fc = process.execute(this.coverage, band, insideEdges, roi?.g, noData, rangeList, null)
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
    Layer getPointLayer() {
        def process = new RasterAsPointCollectionProcess()
        def fc = process.execute(coverage)
        Schema s = new Schema(fc.schema)
        Schema schema =  new Schema("points", s.fields)
        Layer layer = new Memory().create(schema)
        layer.add(fc)
        layer
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
     * Add a constant value to this Raster
     * @param value The constant number
     * @return A new Raster
     */
    Raster add(double value) {
        add([value])
    }

    /**
     * Add a List of constant values to this Raster
     * @param values The List of constant numbers
     * @return A new Raster
     */
    Raster add(List<Double> values) {
        def processor = new CoverageProcessor()
        def params = processor.getOperation("AddConst").parameters
        params.parameter("Source").value = this.coverage
        params.parameter("constants").value = values as double[]
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
     * Add a constant value to this Raster
     * @param value The constant number
     * @return A new Raster
     */
    Raster plus(double value) {
        add(value)
    }

    /**
     * Add a List of constant values to this Raster
     * @param values The List of constant numbers
     * @return A new Raster
     */
    Raster plus(List<Double> values) {
        add(values)
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
     * Multiply this Raster by a List of constant values
     * @param values The list of constant values
     * @return A new Raster
     */
    Raster multiply(List<Double> values) {
        def processor = new CoverageProcessor()
        def params = processor.getOperation("MultiplyConst").parameters
        params.parameter("Source").value = this.coverage
        params.parameter("constants").value = values as double[]
        def newCoverage = processor.doOperation(params)
        new Raster(newCoverage)
    }

    /**
     * Multiply this Raster by a constant value
     * @param values The constant value
     * @return A new Raster
     */
    Raster multiply(double value) {
        multiply([value])
    }

    /**
     * Divide this Raster by another Raster
     * @param other The other Raster
     * @return A new Raster
     */
    Raster div(Raster other) {
        def processor = new CoverageProcessor()
        def params = processor.getOperation("Divide").parameters
        params.parameter("Source0").value = this.coverage
        params.parameter("Source1").value = other.coverage
        def newCoverage = processor.doOperation(params)
        new Raster(newCoverage)
    }

    /**
     * Divide this Raster by a constant value
     * @param value The constant value
     * @return A new Raster
     */
    Raster div(double value) {
        div([value])
    }

    /**
     * Divide this Raster by a List of constant values
     * @param values A List of constant values
     * @return A new Raster
     */
    Raster div(List<Double> values) {
        divide(values)
    }

    /**
     * Divide this Raster by another Raster
     * @param other The other Raster
     * @return A new Raster
     */
    Raster divide(Raster other) {
        div(other)
    }

    /**
     * Divide this Raster by a constant value
     * @param value The constant value
     * @return A new Raster
     */
    Raster divide(double value) {
        divide([value])
    }

    /**
     * Divide this Raster by a List of constant values
     * @param values A List of constant values
     * @return A new Raster
     */
    Raster divide(List<Double> values) {
        def processor = new CoverageProcessor()
        def params = processor.getOperation("DivideByConst").parameters
        params.parameter("Source").value = this.coverage
        params.parameter("constants").value = values as double[]
        def newCoverage = processor.doOperation(params)
        new Raster(newCoverage)
    }

    /**
     * Subtract this Raster from an other Raster
     * @param other The other Raster
     * @return A new Raster
     */
    Raster minus(Raster other) {
        def processor = new CoverageProcessor()
        def params = processor.getOperation("Subtract").parameters
        params.parameter("Source0").value = this.coverage
        params.parameter("Source1").value = other.coverage
        def newCoverage = processor.doOperation(params)
        new Raster(newCoverage)
    }

    /**
     * Subtract this Raster from a constant value
     * @param value The constant value
     * @return A new Raster
     */
    Raster minus(double value) {
        minus([value])
    }

    /**
     * Subtract this Raster from a List of constant values
     * @param values The List of constant values
     * @return A new Raster
     */
    Raster minus(List<Double> values) {
        def processor = new CoverageProcessor()
        def params = processor.getOperation("SubtractConst").parameters
        params.parameter("Source").value = this.coverage
        params.parameter("constants").value = values as double[]
        def newCoverage = processor.doOperation(params)
        new Raster(newCoverage)
    }

    /**
     * Subtract a constant value from this Raster
     * @param value The constant value
     * @return A new Raster
     */
    Raster minusFrom(double value) {
        minusFrom([value])
    }

    /**
     * Subtract a List of constant values from this Raster
     * @param values The List of constant values
     * @return A new Raster
     */
    Raster minusFrom(List<Double> values) {
        def processor = new CoverageProcessor()
        def params = processor.getOperation("SubtractFromConst").parameters
        params.parameter("Source").value = this.coverage
        params.parameter("constants").value = values as double[]
        def newCoverage = processor.doOperation(params)
        new Raster(newCoverage)
    }

    /**
     * Create a new Raster by inverting the values of this Raster
     * @return A new Raster
     */
    Raster invert() {
        def processor = new CoverageProcessor()
        def params = processor.getOperation("Invert").parameters
        params.parameter("Source").value = this.coverage
        def newCoverage = processor.doOperation(params)
        new Raster(newCoverage)
    }

    /**
     * Create a new Raster by inverting the values of this Raster.
     * Overrides the - operator
     * <p><blockquote><pre>
     * def invertedRaster = -raster
     * </pre></blockquote></p>
     * @return A new Raster
     */
    Raster negative() {
        invert()
    }

    static class Divide extends OperationJAI {

        private static final long serialVersionUID = 3559075474256896861L

        Divide() {
            super("Divide");
        }

        @Override
        protected NumberRange deriveRange(final NumberRange[] ranges, final OperationJAI.Parameters parameters) {
            if (ranges != null && ranges.length == 2){
                final NumberRange range0 = ranges[0]
                final NumberRange range1 = ranges[1]
                final double min0 = range0.getMinimum()
                final double min1 = range1.getMinimum()
                final double max0 = range0.getMaximum()
                final double max1 = range1.getMaximum()
                final double max = max0 != 0.0 ? max0 / max1 : 0.0
                final double min = min0 != 0.0 ? min0 / min1 : 0.0
                return NumberRange.create(min, max)
            }
            return null
        }
    }

    static class Subtract extends OperationJAI {

        private static final long serialVersionUID = -4029879625681129215L

        Subtract() {
            super("Subtract");
        }

        @Override
        protected NumberRange deriveRange(final NumberRange[] ranges, final OperationJAI.Parameters parameters) {
            if (ranges != null && ranges.length == 2){
                final NumberRange range0 = ranges[0]
                final NumberRange range1 = ranges[1]
                final double min0 = range0.getMinimum()
                final double min1 = range1.getMinimum()
                final double max0 = range0.getMaximum()
                final double max1 = range1.getMaximum()
                final double max = max0 - max1
                final double min = min0 - min1
                return NumberRange.create(min, max)
            }
            return null
        }
    }
}

