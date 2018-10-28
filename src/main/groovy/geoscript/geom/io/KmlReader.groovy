package geoscript.geom.io

import geoscript.geom.Geometry
import geoscript.geom.GeometryCollection
import geoscript.geom.LineString
import geoscript.geom.LinearRing
import geoscript.geom.MultiLineString
import geoscript.geom.MultiPoint
import geoscript.geom.MultiPolygon
import geoscript.geom.Point
import geoscript.geom.Polygon
import org.geotools.kml.KMLConfiguration
import org.geotools.xsd.Parser

/**
 * Read a {@link geoscript.geom.Geometry Geometry} from a KML String.
 * <p><blockquote><pre>
 * KmlReader reader = new KmlReader()
 * {@link geoscript.geom.Point Point} point = reader.read("&lt;Point&gt;&lt;coordinates&gt;111.0,-47.0&lt;/coordinates&gt;&lt;/Point&gt;")
 *
 * POINT (111 -47)
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class KmlReader implements Reader {

    /**
     * Read a Geometry from a KML String
     * @param str The KML String
     * @return A Geometry
     */
    Geometry read(String str) {
        Parser parser = new Parser(new KMLConfiguration())
        Geometry.wrap(parser.parse(new StringReader(str)))
    }

    /**
     * Read a Geometry from an XML Node
     * @param node The XML Node
     * @return A Geometry or null
     */
    Geometry read(Node node) {
        Geometry geom = null
        String name = node.name()
        String ns = ""
        if (name.contains(":")) {
            String[] parts = name.split(":")
            ns = "${parts[0]}:"
            name = parts[1]
        }
        if (name.equalsIgnoreCase("Point")) {
            String text = node["${ns}coordinates"].text().replaceAll("\\s+", " ")
            geom = getPoints(text)[0]
        } else if (name.equalsIgnoreCase("LineString")) {
            String text = node["${ns}coordinates"].text().replaceAll("\\s+", " ")
            geom = new LineString(getPoints(text))
        } else if (name.equalsIgnoreCase("LinearRing")) {
            String text = node["${ns}coordinates"].text().replaceAll("\\s+", " ")
            geom = new LinearRing(getPoints(text))
        } else if (name.equalsIgnoreCase("Polygon")) {
            geom = new Polygon(read(node["${ns}outerBoundaryIs"]["${ns}LinearRing"][0]) as LinearRing,
                node["${ns}innerBoundaryIs"].collect{innerNode ->
                    read(innerNode["${ns}LinearRing"][0] as Node) as LinearRing
                }
            )
        } else if (name.equalsIgnoreCase("MultiGeometry")) {
            List<Geometry> geoms = node.children().collect{Node childNode ->
                read(childNode)
            }
            if (!(false in geoms.collect{g -> g instanceof Point})) {
                geom = new MultiPoint(geoms)
            }
            else if (!(false in geoms.collect{g -> g instanceof LineString})) {
                geom = new MultiLineString(geoms)
            }
            else if (!(false in geoms.collect{g -> g instanceof Polygon})) {
                geom = new MultiPolygon(geoms)
            }
            else {
                geom = new GeometryCollection(geoms)
            }
        }
        geom
    }

    /**
     * Get a List of Point from the String
     * @param str The space delimited KML List of coordinates
     * @return A List of Points
     */
    private List<Point> getPoints(String str) {
        str.split(" ").collect{
            String[] coords = it.split(",")
            new Point(coords[0] as double, coords[1] as double)
        }
    }

}