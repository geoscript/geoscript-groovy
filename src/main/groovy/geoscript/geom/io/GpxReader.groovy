package geoscript.geom.io

import geoscript.geom.Geometry
import geoscript.geom.LineString
import geoscript.geom.MultiLineString
import geoscript.geom.Point

/**
 * Read a Geometry from a GPX String
 * @author Jared Erickson
 */
class GpxReader implements Reader {

    /**
     * Read a Geometry from a String
     * @param str The String
     * @return A Geometry
     */
    @Override
    Geometry read(String str) {
        if (str == null || str.trim().length() == 0 || !str.trim().startsWith("<")) return null
        str = str.trim()
        def xml = new XmlParser(false, false).parseText(str)
        read(xml)
    }

    /**
     * Read a Geometry from an XML Node
     * @param node The XML Node
     * @return A Geometry or null
     */
    Geometry read(Node node) {
        Geometry geom = null
        String name = node.name()
        if (name.equals("wpt")) {
            geom = new Point(node.attribute("lon") as double, node.attribute("lat") as double)
        } else if (name.equals("rte")) {
            geom = new LineString(node.rtept.collect{Node rtept ->
                new Point(rtept.attribute("lon") as double, rtept.attribute("lat") as double)
            })
        } else if (name.equals("trk")) {
            geom = new MultiLineString(node.trkseg.collect{Node trkseg ->
                new LineString(trkseg.trkpt.collect{Node trkpt ->
                    new Point(trkpt.attribute("lon") as double, trkpt.attribute("lat") as double)
                })
            })
        }
        geom
    }
}
