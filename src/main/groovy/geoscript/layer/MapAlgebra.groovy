package geoscript.layer

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
     * Calculate a new Raster by performing the map algebra specified by the Jiffle/MapCalc script.
     * @param script The Jiffle/MapCalc script.  This can be a File, a URL, or a String
     * @param rasters A Map of the input Rasters and their names.
     * @param outputName The output Raster name.
     * @param size The size of the output Raster
     * @param bounds The geographic Bounds of the output Raster
     * @return A new Raster
     */
    Raster calculate(Map options = [:], def script, Map<String, Raster> rasters) {
        // Options
        String outputName = options.get("outputName", "dest")
        List size = options.get("size",null)
        Bounds bounds = options.get("bounds",null)
        // Script can be a File, URL, or String
        if (script instanceof File) {
            script = script.text
        } else if (script instanceof URL) {
            script = script.text
        }
        JiffleBuilder builder = new JiffleBuilder()
        builder.script(script)
        rasters.each {input ->
            builder.source(input.key, (input.value as Raster).coverage.renderedImage)
        }
        if (size == null || size.size() == 0) {
           int width = 0
           int height = 0
           if (rasters != null && rasters.size() > 0) {
               rasters.each{input ->
                  Raster r = input.value
                  List s = r.size
                  width = Math.max(width, s[0])
                  height = Math.max(height, s[1])
               }
               size = [width,height]
           } else {
               size = [500,500]
           }
        }
        if (!bounds) {
            if (rasters != null && rasters.size() > 0) {
                rasters.each{input ->
                    Raster r = input.value
                    if (bounds == null) {
                        bounds = r.bounds
                    }
                    else {
                        bounds.expand(r.bounds)
                    }
                }
            } else {
                bounds = new Bounds(0, 0, size[0], size[1], "EPSG:4326")
            }
        }
        if (!bounds.proj) {
            bounds = new Bounds(bounds.minX, bounds.minX, bounds.maxX, bounds.maxY, "EPSG:4326")
        }
        builder.dest(outputName, size[0] as int, size[1] as int)
        builder.run()
        RenderedImage image = builder.getImage(outputName)
        GridCoverageFactory gridCoverageFactory = new GridCoverageFactory()
        GridCoverage2D grid = gridCoverageFactory.create(outputName, image, bounds.env)
        Raster raster = new Raster(grid)
        return raster
    }
}
