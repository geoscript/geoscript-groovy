package geoscript.layer.io

import geoscript.layer.Pyramid
import geoscript.layer.TMS
import groovy.xml.MarkupBuilder

/**
 * Write a Pyramid to a GDAL MiniDriver XML File
 * @author Jared Erickson
 */
class GdalTmsPyramidWriter implements PyramidWriter {

    /**
     * Write a Pyramid to a String
     * @param pyramid The Pyramid
     * @return A String
     */

    @Override
    String write(Pyramid pyramid) {
        write([:], pyramid)
    }

    /**
     * Write a Pyramid to a String
     * @param options The named parameters
     * <ul>
     *     <li>serverUrl = The server url '${z}/${x}/${y}'</li>
     *     <li>imageFormat = The image format (png)</li>
     *     <li>numberOfBands = The number of bands (3)</li>
     * </ul>
     * @param pyramid The Pyramid
     * @return A String
     */
    String write(Map options, Pyramid pyramid) {
        String imageFormat = options.get("imageFormat", "png")
        String serverUrl = options.get("serverUrl", '${z}/${x}/${y}.' + imageFormat)
        StringWriter writer = new StringWriter()
        MarkupBuilder builder = new MarkupBuilder(writer)
        builder.GDAL_WMS {
            Service(name: "TMS") {
                ServerURL(serverUrl)
                SRS(pyramid.proj.id)
                ImageFormat(imageFormat)
            }
            DataWindow {
                UpperLeftX(pyramid.bounds.minX)
                UpperLeftY(pyramid.bounds.maxY)
                LowerRightX(pyramid.bounds.maxX)
                LowerRightY(pyramid.bounds.minY)
                TileLevel(pyramid.maxGrid.z)
                TileCountX(pyramid.minGrid.width)
                TileCountY(pyramid.minGrid.height)
                YOrigin(pyramid.origin in [Pyramid.Origin.TOP_LEFT, Pyramid.Origin.TOP_RIGHT] ? "top" : "bottom")
            }
            Projection(pyramid.proj.id)
            BlockSizeX(pyramid.tileWidth)
            BlockSizeY(pyramid.tileHeight)
            BandsCount(options.get("numberOfBands",3))
        }
        writer.toString()
    }

    /**
     * Write the TMS tile layer to a String.
     * @param options The optional named parameters
     * @param tms The TMS tile layer
     * @return An XML String
     */
    String write(Map options = [:], TMS tms) {
        String url
        if (tms.dir) {
            url = "file://${tms.dir.absolutePath}"
        } else {
            url = tms.url
        }
        write([
                serverUrl: "${url}/\${z}/\${x}/\${y}.${tms.imageType}",
                imageFormat: tms.imageType
        ], tms.pyramid)
    }
}
