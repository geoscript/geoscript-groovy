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
import groovy.xml.XmlParser

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
     * Read a Geometry from a String
     * @param str The String
     * @return A Geometry
     */
    Geometry read(String str) {
        if (str == null || str.trim().length() == 0 || !str.trim().startsWith("<")) return null
        str = str.trim()
        if (str.startsWith("<geo:")) {
            readW3C(str)
        } else if (str.startsWith("<georss:where")) {
            readGml(str)
        } else {
            readSimple(str)
        }
    }

    /**
     * Read a Geometry from a Simple GeoRSS Xml String
     * @param str The Simple GeoRSS XML String
     * @return A Geometry or null
     */
    private Geometry readSimple(String str) {
        def xml = new XmlParser(false, false).parseText(str)
        String name = xml.name().toString().toLowerCase()
        if (name.equals('georss:point')) {
            String[] coords = xml.text().split(" ")
            new Point(coords[1] as double, coords[0] as double)
        } else if (name.equals('georss:line')) {
            new LineString(getPoints(xml.text()))
        } else if (name.equals('georss:polygon')) {
            new Polygon(new LinearRing(getPoints(xml.text())))
        } else if (name.equals("georss:box")) {
            List points = getPoints(xml.text())
            new Polygon(new LinearRing(
                    points[0],
                    new Point(points[1].x, points[0].y),
                    points[1],
                    new Point(points[0].x, points[1].y),
                    points[0]
            ))
        } else if (name.equals("georss:circle")) {
            String[] values = xml.text().split(" ")
            Point pt = new Point(Double.parseDouble(values[1]), Double.parseDouble(values[0]))
            pt.buffer(Double.parseDouble(values[2]))
        }
    }

    /**
     * Read a Geometry from a GML GeoRSS Xml String
     * @param str The GML GeoRSS XML String
     * @return A Geometry or null
     */
    private Geometry readGml(String str) {
        def xml = new XmlParser(false, false).parseText(str)
        if (xml['gml:Point']) {
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
     * Read a Geometry from an XML Node
     * @param node The XML Node
     * @return A Geometry or null
     */
    Geometry read(Node node) {
        Geometry geom = null
        String name = node.name()
        if (name.equalsIgnoreCase("georss:where")) {
            String subName = node.children()[0].name()
            if (subName.equalsIgnoreCase("gml:Point")) {
                String[] xy = node["gml:Point"]["gml:pos"].text().replaceAll("\\s+", " ").split(" ")
                double x = xy[1] as double
                double y = xy[0] as double
                geom = new Point(x, y)
            } else if (subName.equalsIgnoreCase("gml:LineString")) {
                String text = node["gml:LineString"]["gml:posList"].text().replaceAll("\\s+", " ")
                geom = new LineString(getPoints(text))
            } else if (subName.equalsIgnoreCase("gml:Polygon")) {
                String text = node["gml:Polygon"]["gml:exterior"]["gml:LinearRing"]["gml:posList"].text().replaceAll("\\s+", " ")
                geom = new Polygon(new LinearRing(getPoints(text)))
            }
        } else if (name.equalsIgnoreCase("georss:point")) {
            String text = node.text().replaceAll("\\s+", " ")
            geom = getPoints(text)[0]
        } else if (name.equalsIgnoreCase("georss:line")) {
            String text = node.text().replaceAll("\\s+", " ")
            geom = new LineString(getPoints(text))
        } else if (name.equalsIgnoreCase("georss:polygon")) {
            String text = node.text().replaceAll("\\s+", " ")
            geom = new Polygon(new LinearRing(getPoints(text)))
        } else if (name.equalsIgnoreCase("georss:box")) {
            String text = node.text().replaceAll("\\s+", " ")
            List points = getPoints(text)
            geom = new Polygon(new LinearRing(
                    points[0],
                    new Point(points[1].x, points[0].y),
                    points[1],
                    new Point(points[0].x, points[1].y),
                    points[0]
            ))
        } else if (name.equalsIgnoreCase("georss:circle")) {
            String text = node.text().replaceAll("\\s+", " ")
            String[] parts = text.split(" ")
            geom = new Point(parts[0] as double, parts[1] as double).buffer(parts[2] as double)
        } else if (name.equalsIgnoreCase("geo:Point")) {
            double x = node["geo:long"].text() as double
            double y = node["geo:lat"].text() as double
            geom = new Point(x, y)
        }
        return geom
    }

    /**
     * Get a List of Point from the String
     * @param str The space delimited GeoRSS List of coordinates
     * @return A List of Points
     */
    private List<Point> getPoints(String str) {
        def values = str.trim().split(" ")
        int num = values.length / 2
        List points = []
        for (i in (0..<num)) {
            int k = i * 2
            double y = Double.parseDouble(values[k])
            double x = Double.parseDouble(values[k + 1])
            points.add(new Point(x, y))
        }
        points
    }
}

