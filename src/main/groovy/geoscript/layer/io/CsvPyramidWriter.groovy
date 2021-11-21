package geoscript.layer.io

import geoscript.layer.Grid
import geoscript.layer.Pyramid

import java.text.DecimalFormat

/**
 * Write a Pyramid to a CSV String.
 * @author Jared Erickson
 */
class CsvPyramidWriter implements PyramidWriter {

    /**
     * Write a Pyramid to a CSV String
     * @param pyramid The Pyramid
     * @return A CSV String
     */
    @Override
    String write(Pyramid pyramid) {
        String NEW_LINE = System.getProperty("line.separator")
        StringBuilder builder = new StringBuilder()
        builder.append(pyramid.proj.id).append(NEW_LINE)
        builder.append(pyramid.bounds.minX).append(",").append(pyramid.bounds.minY).append(",")
        builder.append(pyramid.bounds.maxX).append(",").append(pyramid.bounds.maxY).append(",")
        builder.append(pyramid.bounds.proj ? pyramid.bounds.proj.id : pyramid.proj.id).append(NEW_LINE)
        builder.append(pyramid.origin).append(NEW_LINE)
        builder.append(pyramid.tileWidth).append(",").append(pyramid.tileHeight).append(NEW_LINE)
        pyramid.grids.each { Grid g ->
            builder.append(g.z).append(",")
            builder.append(g.width).append(",")
            builder.append(g.height).append(",")
            builder.append(g.xResolution).append(",")
            builder.append(g.yResolution).append(NEW_LINE)
        }
        builder.toString()
    }
}
