package geoscript.layer.io

import geoscript.layer.Grid
import geoscript.layer.Pyramid
import groovy.xml.MarkupBuilder

/**
 * Write a Pyramid to an XML String.
 * @author Jared Erickson
 */
class XmlPyramidWriter implements PyramidWriter {

    /**
     * Write a Pyramid to an XML String.
     * @param pyramid The Pyramid
     * @return A Pyramid
     */
    @Override
    String write(Pyramid pyramid) {
        StringWriter writer = new StringWriter()
        MarkupBuilder builder = new MarkupBuilder(writer)
        builder.pyramid {
            proj pyramid.proj
            bounds {
                minX pyramid.bounds.minX
                minY pyramid.bounds.minY
                maxX pyramid.bounds.maxX
                maxY pyramid.bounds.maxY
            }
            origin pyramid.origin
            tileSize {
                width pyramid.tileWidth
                height pyramid.tileHeight
            }
            grids {
                pyramid.grids.each { Grid g ->
                    builder.grid {
                        z g.z
                        width g.width
                        height g.height
                        xres g.xResolution
                        yres g.yResolution
                    }
                }
            }
        }
        writer.toString()
    }
}
