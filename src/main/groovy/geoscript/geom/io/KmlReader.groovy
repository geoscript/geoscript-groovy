package geoscript.geom.io


import geoscript.geom.*
import org.jdom.input.SAXBuilder
import org.jdom.Document
import org.jdom.Element

/**
 * Read a Geometry from a KML String.
 * <p><code>KmlReader reader= new KmlReader()</code></p>
 * <p><code>Point point = reader.read("&lt;Point&gt;&lt;coordinates&gt;111.0,-47.0&lt;/coordinates&gt;&lt;/Point&gt;")</code></p>
 * <p><code>POINT (111 -47)</code></p>
 * @author Jared Erickson
 */
class KmlReader {
	
    /**
     * Read a Geometry from a KML String
     * @param str The KML String
     * @return A Geometry
     */
    Geometry read(String str) {

        SAXBuilder builder = new SAXBuilder()
        Document document = builder.build(new StringReader(str))
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
            return getPoints(element.getChild("coordinates").text)[0]
        }
        else if (name.equalsIgnoreCase("LineString")) {
            return new LineString(getPoints(element.getChild("coordinates").text))
        }
        else if (name.equalsIgnoreCase("LinearRing")) {
            return new LinearRing(getPoints(element.getChild("coordinates").text))
        }
        else if (name.equalsIgnoreCase("Polygon")) {
            LinearRing shell = new LinearRing(getPoints(element.getChild("outerBoundaryIs").getChild("LinearRing").getChild("coordinates").text))
            List<LinearRing> holes = element.getChildren("innerBoundaryIs").collect{e ->
                new LinearRing(getPoints(e.getChild("LinearRing").getChild("coordinates").text))
            }
            return new Polygon(shell, holes)
        }
        else if (name.equalsIgnoreCase("MultiGeometry")) {
            List<Geometry> geoms = element.getChildren().collect{e->read(e)}
            if (!(false in geoms.collect{g -> g instanceof Point})) {
                return new MultiPoint(geoms)
            }
            else if (!(false in geoms.collect{g -> g instanceof LineString})) {
                return new MultiLineString(geoms)
            }
            else if (!(false in geoms.collect{g -> g instanceof Polygon})) {
                return new MultiPolygon(geoms)
            }
            else {
                return new GeometryCollection(geoms)
            }
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

}

