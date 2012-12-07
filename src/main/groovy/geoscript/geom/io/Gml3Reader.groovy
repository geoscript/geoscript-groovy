package geoscript.geom.io

import geoscript.geom.*
import org.jdom.input.SAXBuilder
import org.jdom.Document
import org.jdom.Element
import org.jdom.Namespace
import java.util.regex.Pattern
import java.util.regex.Matcher

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
class Gml3Reader implements Reader{

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
        readElement(root)
    }

    /**
     * Get a Geometry from a JDOM Element
     * @param The JDOM Element
     * @return A Geometry
     */
    private Geometry readElement(Element element) {

        String name = element.name

        if (name.equalsIgnoreCase("Point")) {
            return getPoints(element.getChild("pos",ns).text)[0]
        }
        else if (name.equalsIgnoreCase("LineString")) {
            return new LineString(getPoints(element.getChild("posList",ns).text))
        }
        else if (name.equalsIgnoreCase("LinearRing")) {
            return new LinearRing(getPoints(element.getChild("posList",ns).text))
        }
        else if (name.equalsIgnoreCase("Polygon")) {
            LinearRing shell = readElement(element.getChild("exterior",ns).getChild("LinearRing",ns)) as LinearRing
            List<LinearRing> holes = element.getChildren("interior",ns).collect{e->readElement(e.getChild("LinearRing",ns)) as LinearRing}
            return new Polygon(shell, holes)
        }
        else if (name.equalsIgnoreCase("MultiPoint")) {
            return new MultiPoint(element.getChildren("pointMember",ns).collect{e->readElement(e.getChild("Point",ns))})
        }
        else if (name.equalsIgnoreCase("Curve")) {
            return new MultiLineString(element.getChild("segments",ns).getChildren("LineStringSegment",ns).collect{e->
                    new LineString(getPoints(e.getChild("posList",ns).text))
            })
        }
        else if (name.equalsIgnoreCase("MultiCurve")) {
            return new MultiLineString(element.getChildren("curveMember",ns).collect{e->readElement(e.getChild("LineString",ns))})
        }
        else if (name.equalsIgnoreCase("MultiSurface")) {
            return new MultiPolygon(element.getChildren("surfaceMember",ns).collect{e->readElement(e.getChild("Polygon",ns))})
        }
        else if (name.equalsIgnoreCase("MultiGeometry")) {
            return new GeometryCollection(element.getChildren("geometryMember",ns).collect{e->readElement(e.getChildren()[0])})
        }
        else if (name.equalsIgnoreCase("GeometryCollection")) {
            return new GeometryCollection(element.getChildren("geometryMember",ns).collect{e->readElement(e.getChildren()[0])})
        }
        else {
            return null
        }
    }

    /**
     * Get a List of Points from a space delimited list of comma delimited coordinates
     * @param str Our space delimited list of comma delimited coordinates
     * @return A List of Points
     */
    private List<Point> getPoints(String str) {
        List coordinates = str.split(" ")
        (1..coordinates.size()).step(2).collect{i->
            new Point(Double.parseDouble(coordinates[i-1]), Double.parseDouble(coordinates[i]))
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

