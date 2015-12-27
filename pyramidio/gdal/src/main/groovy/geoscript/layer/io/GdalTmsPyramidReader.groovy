package geoscript.layer.io

import geoscript.geom.Bounds
import geoscript.layer.Grid
import geoscript.layer.Pyramid
import geoscript.layer.TMS
import geoscript.proj.Projection

/**
 * Read a Pyramid from a GDAL MiniDriver XML File
 * @author Jared Erickson
 */
class GdalTmsPyramidReader implements PyramidReader {

    /**
     * Read a Pyramid from an XML String
     * @param str An XML String
     * @return A Pyramid
     */
    @Override
    Pyramid read(String str) {
        readTms(str).pyramid
    }

    /**
     * Read a TMS Tile Layer from an XML String
     * @param str An XML String
     * @return A TMS Tile Layer
     */
    TMS readTms(String str) {
        def xml = new XmlParser().parseText(str)
        String url = xml.Service.ServerURL.text()
        int s = url.indexOf('${z}')
        if (s > -1) {
            url = url.substring(0, s)
        }
        String imageType = xml.Service?.ImageFormat?.text() ?: "png"
        Projection projection = new Projection(xml.Projection.text())
        Bounds bounds = new Bounds(
                xml.DataWindow.UpperLeftX.text() as double,
                xml.DataWindow.LowerRightY.text() as double,
                xml.DataWindow.LowerRightX.text() as double,
                xml.DataWindow.UpperLeftY.text() as double,
                projection
        )
        int levels = Integer.parseInt(xml.DataWindow.TileLevel.text())
        Pyramid.Origin origin = xml.DataWindow.YOrigin.text().equalsIgnoreCase("top") ?
                Pyramid.Origin.TOP_LEFT : Pyramid.Origin.BOTTOM_LEFT
        int tileWidth = xml.BlockSizeX.text() as int
        int tileHeight = xml.BlockSizeY.text() as int
        int tileCountX = xml.DataWindow.TileCountX.text() as int
        int tileCountY = xml.DataWindow.TileCountY.text() as int
        double res = (bounds.width  / (tileWidth  * tileCountX))
        List<Grid> grids = (0..levels).collect { int z ->
            int col
            int row
            double zRes
            if (projection.id.equalsIgnoreCase("EPSG:3857")) {
                int n = Math.pow(2, z)
                col = n
                row = n
                zRes = 156412.0 / n
            } else {
                col = Math.pow(2, z + (tileCountX - 1))
                row = Math.pow(2, z + (tileCountY - 1))
                zRes =  res / Math.pow(2, z)
            }
            new Grid(z, col, row, zRes, zRes)
        }
        Pyramid p = new Pyramid(proj: projection, bounds: bounds, grids: grids, origin: origin, tileWidth: tileWidth, tileHeight: tileHeight)
        new TMS("tms", imageType, url, p)
    }

}
