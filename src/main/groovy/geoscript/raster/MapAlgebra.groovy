package geoscript.raster

import geoscript.geom.Bounds
import java.awt.image.RenderedImage
import org.jaitools.jiffle.JiffleBuilder
import org.geotools.coverage.grid.GridCoverage2D
import org.geotools.coverage.grid.GridCoverageFactory

/**
 * The MapAlgebra uses Jiffle to perform Map Algebra for Rasters.
 * @author Jared Erickson
 */
class MapAlgebra {

    /**
     * Calculate a new Raster by peforming the map algebra specified by the Jiffle/MapCalc script.
     * @param script The Jiffle/MapCalc script.  This can be a File, a URL, or a String
     * @param inputRasters A Map of the input Rasters and their names.
     * @param outputName The output Raster name.
     * @param size The size of the output Raster
     * @param bounds The geographic Bounds of the output Raster
     * @return A new Raster
     */
    Raster calculate(def script, Map<String, Raster> inputRasters, String outputName = "dest", List size = [500,500], Bounds bounds = null) {
        if (script instanceof File) {
            script = script.text
        } else if (script instanceof URL) {
            script = script.text
        }
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
