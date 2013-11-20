/*
 *  The MIT License
 * 
 *  Copyright 2010 Jared Erickson.
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 * 
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

package geoscript.geom.io

import geoscript.geom.*
import org.jdom.input.SAXBuilder
import org.jdom.Document
import org.jdom.Element
import org.jdom.Namespace
import java.util.regex.Pattern
import java.util.regex.Matcher

/**
 * A GeoRSS Simple Reader. See the <a href="http://www.georss.org/simple">GeoRSS spec</a> for more details.
 * <p><blockquote><pre>
 * GeoRSSReader reader = new GeoRSSReader()
 * {@link geoscript.geom.Point Point} p = reader.read("&lt;georss:point&gt;45.256 -71.92&lt;/georss:point&gt;")
 *
 * POINT (-71.92 45.256)
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class GeoRSSReader implements Reader {

    /**
     * The GeoRSS XML Namespace
     */
    final Namespace ns = Namespace.getNamespace("georss","http://www.georss.org/georss")

    /**
     * The GML XML Namespace
     */
    final Namespace gmlNs = Namespace.getNamespace("gml","http://www.opengis.net/gml")

    /**
     * Read a Geometry from a String
     * @param str The String
     * @return A Geometry
     */
    Geometry read(String str) {
        if (str) {
            str = str.trim()
            if(str.startsWith("<geo:")) {
                readW3C(str)
            } else if (str.startsWith("<georss:where")) {
                readGml(str)
            } else {
                readSimple(str)
            }
        }
    }

    /**
     * Read a Geometry from a Simple GeoRSS Xml String
     * @param str The Simple GeoRSS XML String
     * @return A Geometry or null
     */
    private Geometry readSimple(String str) {
        SAXBuilder builder = new SAXBuilder()
        Document document = builder.build(new StringReader(prepareXmlString(str)))
        Element root = document.rootElement
        String name = root.name
        if (name.equalsIgnoreCase("point")) {
            return getPoints(root.text)[0]
        }
        else if (name.equalsIgnoreCase("line")) {
            return new LineString(getPoints(root.text))
        }
        else if (name.equalsIgnoreCase("polygon")) {
            return new Polygon(new LinearRing(getPoints(root.text)))
        }
        else if (name.equalsIgnoreCase("box")) {
            List points = getPoints(root.text)
            return new Polygon(new LinearRing(
                    points[0],
                    new Point(points[1].x, points[0].y),
                    points[1],
                    new Point(points[0].x, points[1].y),
                    points[0]
            ))
        }
        else if (name.equalsIgnoreCase("circle")) {
            String[] values = root.text.split(" ")
            return new Point(Double.parseDouble(values[1]),
                    Double.parseDouble(values[0]))
                    .buffer(Double.parseDouble(values[2]))
        }
        else {
            return null
        }
    }

    /**
     * Read a Geometry from a GML GeoRSS Xml String
     * @param str The GML GeoRSS XML String
     * @return A Geometry or null
     */
    private Geometry readGml(String str) {
        def xml = new XmlParser(false, false).parseText(str)
        if(xml['gml:Point']) {
            String[] coords = xml['gml:Point']['gml:pos'].text().split(" ")
            new Point(coords[1] as double, coords[0] as double)
        } else if (xml['gml:LineString']) {
            new LineString(getPoints(xml['gml:LineString']['gml:posList'].text()))
        } else if (xml['gml:Polygon']) {
            new Polygon(new LinearRing(getPoints(xml['gml:Polygon']['gml:exterior']['gml:LinearRing']['gml:posList'].text())))
        } else {
            null
        }
    }

    /**
     * Read a Geometry from a W3C GeoRSS Xml String
     * @param str The W3C GeoRSS XML String
     * @return A Geometry or null
     */
    private Geometry readW3C(String str) {
        def xml = new XmlParser(false, false).parseText(str)
        double y = xml['geo:lat'].text() as double
        double x = xml['geo:long'].text() as double
        new Point(x, y)
    }

    /**
     * Get a List of Point from the String
     * @param str The space delimited GeoRSS List of coordinates
     * @return A List of Points
     */
    private List<Point> getPoints(String str) {
        def values = str.split(" ")
        int num = values.length / 2
        List points = []
        for (i in (0..<num)) {
            int k = i * 2
            double y = Double.parseDouble(values[k])
            double x = Double.parseDouble(values[k+1])
            points.add(new Point(x,y))
        }
        points
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
            String ns = "xmlns:${prefix}=\"http://www.georss.org/georss\""
            str = "${str.substring(0,i)} ${ns}${str.substring(i,str.length())}"
        }

        return str
    }
}

