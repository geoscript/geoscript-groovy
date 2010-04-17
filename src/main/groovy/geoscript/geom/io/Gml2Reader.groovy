package geoscript.geom.io

import geoscript.geom.*
import org.jdom.input.SAXBuilder
import org.jdom.Document
import org.jdom.Element
import org.jdom.Namespace
import java.util.regex.Pattern
import java.util.regex.Pattern
import java.util.regex.Matcher

/**
 * Read a Geometry from a GML Version 2 String.
 * <p><code>Gml2Reader reader = new Gml2Reader()</code></p>
 * <p><code>Point p = reader.read("&lt;gml:Point&gt;&lt;gml:coordinates&gt;111.0,-47.0&lt;/gml:coordinates&gt;&lt;/gml:Point&gt;")</code></p>
 * <p><code>POINT (111 -47)</code></p>
 * @author Jared Erickson
 */
class Gml2Reader implements Reader {

    /**
     * The GML XML Namespace
     */
    final Namespace ns = Namespace.getNamespace("gml","http://www.opengis.net/gml")

    /**
     * Read a Geometry from a GML Version 2 String
     * @param str The GML String
     */
    Geometry read(String str) {
        SAXBuilder builder = new SAXBuilder()
        Document document = builder.build(new StringReader(prepareXmlString(str)))
        Element root = document.rootElement
        read(root)
    }

    /**
     * Get a Geometry from a JDOM Element
     * @param The JDOM Element
     * @return A Geometry
     */
    private Geometry read(Element element) {

        String name = element.name

        if (name.equalsIgnoreCase("Point")) {
            return getPoints(element.getChild("coordinates",ns).text)[0]
        }
        else if (name.equalsIgnoreCase("LineString")) {
            return new LineString(getPoints(element.getChild("coordinates",ns).text))
        }
        else if (name.equalsIgnoreCase("LinearRing")) {
            return new LinearRing(getPoints(element.getChild("coordinates",ns).text))
        }
        else if (name.equalsIgnoreCase("Polygon")) {
            LinearRing shell = read(element.getChild("outerBoundaryIs",ns).getChild("LinearRing",ns))
            List<LinearRing> holes = element.getChildren("innerBoundaryIs",ns).collect{e->read(e.getChild("LinearRing",ns))}
            return new Polygon(shell, holes)
        }
        else if (name.equalsIgnoreCase("MultiPoint")) {
            return new MultiPoint(element.getChildren("pointMember",ns).collect{e->read(e.getChild("Point",ns))})
        }
        else if (name.equalsIgnoreCase("MultiLineString")) {
            return new MultiLineString(element.getChildren("lineStringMember",ns).collect{e->read(e.getChild("LineString",ns))})
        }
        else if (name.equalsIgnoreCase("MultiPolygon")) {
            return new MultiPolygon(element.getChildren("polygonMember",ns).collect{e->read(e.getChild("Polygon",ns))})
        }
        else if (name.equalsIgnoreCase("GeometryCollection")) {
            return new GeometryCollection(element.getChildren("geometryMember",ns).collect{e->read(e.getChildren()[0])})
        }
    }

    /**
     * Get a List of Points from a space delimited list of comma delimited coordinates
     * @param str Our space delimited list of comma delimited coordinates
     * @return A List of Points
     */
    private List<Point> getPoints(String str) {
        str.split(" ").collect{s ->
            def parts = s.split(",")
            new Point(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]))
        }
    }

    /**
     * Insert an XML namespace if the user left it out
     * @param str The XML String
     * @return An XML String with an XML namespace to make JDOM happy
     */
    private String prepareXmlString(String str) {
        int s = str.indexOf('<') + 1
        int e = str.indexOf(':', s)
        String prefix = str.substring(s,e)

        String rx = "xmlns:${prefix}=\".*\""
        Pattern p = Pattern.compile(rx)
        Matcher m = p.matcher(str)
        boolean present = m.find()

        if (!present) {
            int i = str.indexOf('>')
            String ns = "xmlns:${prefix}=\"http://www.opengis.net/gml\""
            str = "${str.substring(0,i)} ${ns}${str.substring(i,str.length())}"
        }

        return str
    }
}

