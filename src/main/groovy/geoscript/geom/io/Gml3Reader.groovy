package geoscript.geom.io

import geoscript.geom.*
import groovy.xml.XmlParser

/**
 * Read a {@link geoscript.geom.Geometry Geometry} from a GML Version 3 String.
 * <p><blockquote><pre>
 * Gml3Reader reader = new Gml3Reader()
 * {@link geoscript.geom.Point Point} p = reader.read("&lt;gml:Point&gt;&lt;gml:pos&gt;111.0,-47.0&lt;/gml:pos&gt;&lt;/gml:Point&gt;")
 *
 * POINT (111 -47)
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class Gml3Reader implements Reader {

    /**
     * Read a Geometry from a GML Version 2 String
     * @param str The GML String
     */
    Geometry read(String str) {
        if (str == null || str.trim().length() == 0 || !str.trim().startsWith("<")) return null
        def xml = new XmlParser(false, false).parseText(str)
        readFromXmlNode(xml)
    }

    /**
     * Read a Geometry from an XML Node
     * @param xml The XML Node
     * @return A Geometry or null
     */
    private Geometry readFromXmlNode(Node xml) {
        String name = xml.name().toString()
        if (name.equalsIgnoreCase("gml:Point")) {
            getPoints(xml["gml:pos"].text())[0]
        } else if (name.equalsIgnoreCase("gml:LineString")) {
            new LineString(getPoints(xml["gml:posList"].text()))
        } else if (name.equalsIgnoreCase("gml:LinearRing")) {
            new LinearRing(getPoints(xml["gml:posList"].text()))
        } else if (name.equalsIgnoreCase("gml:Polygon")) {
            LinearRing shell = new LinearRing(getPoints(xml["gml:exterior"]["gml:LinearRing"]["gml:posList"].text()))
            List<LinearRing> holes = xml["gml:interior"].collect { inner ->
                new LinearRing(getPoints(inner["gml:LinearRing"]["gml:posList"].text()))
            }
            return new Polygon(shell, holes)
        } else if (name.equalsIgnoreCase("gml:MultiPoint")) {
            return new MultiPoint(xml["gml:pointMember"].collect { e ->
                readFromXmlNode(e["gml:Point"])
            })
        } else if (name.equalsIgnoreCase("gml:Curve")) {
            return new MultiLineString(xml["gml:segments"]["gml:LineStringSegment"].collect { e ->
                new LineString(getPoints(e["gml:posList"].text()))
            })
        } else if (name.equalsIgnoreCase("gml:MultiCurve")) {
            return new MultiLineString(xml["gml:curveMember"].collect { e ->
                readFromXmlNode(e["gml:LineString"])
            })
        } else if (name.equalsIgnoreCase("gml:MultiSurface")) {
            return new MultiPolygon(xml["gml:surfaceMember"].collect { e ->
                readFromXmlNode(e["gml:Polygon"])
            })
        } else if (name.equalsIgnoreCase("gml:MultiGeometry")) {
            return new GeometryCollection(xml["gml:geometryMember"].collect { e ->
                readFromXmlNode(e.children()[0])
            })
        } else if (name.equalsIgnoreCase("gml:GeometryCollection")) {
            return new GeometryCollection(xml["gml:geometryMember"].collect { e ->
                readFromXmlNode(e.children()[0])
            })
        }
    }

    /**
     * Get a List of Points from a space delimited list of comma delimited coordinates
     * @param str Our space delimited list of comma delimited coordinates
     * @return A List of Points
     */
    private List<Point> getPoints(String str) {
        List coordinates = str.split(" ")
        (1..coordinates.size()).step(2).collect { i ->
            new Point(Double.parseDouble(coordinates[i - 1]), Double.parseDouble(coordinates[i]))
        }
    }
}

