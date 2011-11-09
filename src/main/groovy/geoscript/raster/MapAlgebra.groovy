package geoscript.raster

import org.jaitools.jiffle.JiffleBuilder
import java.awt.image.RenderedImage
import org.geotools.coverage.grid.GridCoverageFactory
import geoscript.geom.Bounds
import org.geotools.coverage.grid.GridCoverage2D

/**
 * The MapAlgebra uses Jiffle to perform Map Algebra for Rasters.
 * @author Jared Erickson
 */
class MapAlgebra {

    Raster calculate(String script, Map<String, Raster> inputRasters, String outputName = "dest", List size = [500,500], Bounds bounds = null) {
        JiffleBuilder builder = new JiffleBuilder()
        builder.script(script)
        inputRasters.each {input ->
            builder.source(input.key, (input.value as Raster).coverage.renderedImage)
        }
        if (!bounds) {
            if (inputRasters != null && inputRasters.size() > 0) {
                inputRasters.each{input ->
                    if (bounds == null) {
                        bounds = input.value.bounds
                    }
                    else {
                        bounds.expand(input.value.bounds)
                    }
                }
            } else {
                bounds = new Bounds(0, 0, size[0], size[1], "EPSG:4326")
            }
        }
        builder.dest(outputName, size[0] as int, size[1] as int)
        builder.run()
        RenderedImage image = builder.getImage(outputName)
        GridCoverageFactory gridCoverageFactory = new GridCoverageFactory()
        GridCoverage2D grid = gridCoverageFactory.create(outputName, image, bounds.env)
        Raster raster = new GeoTIFF(grid)
        return raster
    }
}
