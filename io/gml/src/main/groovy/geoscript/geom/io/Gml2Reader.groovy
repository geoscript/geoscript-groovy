package geoscript.geom.io

import geoscript.geom.*

/**
 * Read a {@link geoscript.geom.Geometry Geometry} from a GML Version 2 String.
 * <p><blockquote><pre>
 * Gml2Reader reader = new Gml2Reader()
 * {@link geoscript.geom.Point Point} p = reader.read("&lt;gml:Point&gt;&lt;gml:coordinates&gt;111.0,-47.0&lt;/gml:coordinates&gt;&lt;/gml:Point&gt;")
 *
 * POINT (111 -47)
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class Gml2Reader implements Reader {

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
            getPoints(xml["gml:coordinates"].text())[0]
        } else if (name.equalsIgnoreCase("gml:LineString")) {
            new LineString(getPoints(xml["gml:coordinates"].text()))
        } else if (name.equalsIgnoreCase("gml:LinearRing")) {
            new LinearRing(getPoints(xml["gml:coordinates"].text()))
        } else if (name.equalsIgnoreCase("gml:Polygon")) {
            LinearRing shell = new LinearRing(getPoints(xml["gml:outerBoundaryIs"]["gml:LinearRing"]["gml:coordinates"].text()))
            List<LinearRing> holes = xml["gml:innerBoundaryIs"].collect { inner ->
                new LinearRing(getPoints(inner["gml:LinearRing"]["gml:coordinates"].text()))
            }
            return new Polygon(shell, holes)
        } else if (name.equalsIgnoreCase("gml:MultiPoint")) {
            return new MultiPoint(xml["gml:pointMember"].collect { e ->
                readFromXmlNode(e["gml:Point"])
            })
        } else if (name.equalsIgnoreCase("gml:MultiLineString")) {
            return new MultiLineString(xml["gml:lineStringMember"].collect { e ->
                readFromXmlNode(e["gml:LineString"])
            })
        } else if (name.equalsIgnoreCase("gml:MultiPolygon")) {
            return new MultiPolygon(xml["gml:polygonMember"].collect { e ->
                readFromXmlNode(e["gml:Polygon"])
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
        str.split(" ").collect { s ->
            def parts = s.split(",")
            new Point(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]))
        }
    }
}

