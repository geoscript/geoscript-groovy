package geoscript.layer.io

import geoscript.geom.Bounds
import geoscript.layer.Grid
import geoscript.layer.Pyramid
import geoscript.proj.Projection

/**
 * Read a Pyramid from an XML String
 * @author Jared Erickson
 */
class XmlPyramidReader implements PyramidReader {

    /**
     * Read a Pyramid from an XML String
     * @param str The XML String
     * @return A Pyramid
     */
    @Override
    Pyramid read(String str) {
        def xml = new XmlParser().parseText(str)
        Projection projection = new Projection(xml.proj.text())
        Bounds bounds = new Bounds(xml.bounds.minX.text() as double, xml.bounds.minY.text() as double,
                xml.bounds.maxX.text() as double, xml.bounds.maxY.text() as double, projection)
        List<Grid> grids = xml.grids.grid.collect { def xmlGrid ->
            new Grid(xmlGrid.z.text() as long, xmlGrid.width.text() as long, xmlGrid.height.text() as long,
                    xmlGrid.xres.text() as double, xmlGrid.yres.text() as double)
        }
        Pyramid.Origin origin = Pyramid.Origin.valueOf(xml.origin.text())
        int tileWidth = xml.tileSize.width.text() as int
        int tileHeight = xml.tileSize.height.text() as int
        new Pyramid(proj: projection, bounds: bounds, grids: grids, origin: origin, tileWidth: tileWidth, tileHeight: tileHeight)
    }
}
