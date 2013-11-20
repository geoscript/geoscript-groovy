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
import com.vividsolutions.jts.geom.Coordinate

/**
 * A GeoRSS Simple Writer.  See the <a href="http://www.georss.org/simple">GeoRSS spec</a> for more details.
 * Only Points, LineStrings, and Polygon are supported.  Any other {@link geoscript.geom.Geometry Geometry} type
 * will return a null value.
 * <p><blockquote><pre>
 * GeoRSSWriter writer = new GeoRSSWriter()
 * String georss = writer.write(new {@link geoscript.geom.Point Point}(-71.92, 45.256))
 *
 * &lt;georss:point&gt;45.256 -71.92&lt;/georss:point&gt;
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class GeoRSSWriter implements Writer {

    /**
     * The type of encoding (simple, gml, w3c)
     */
    String type = "simple"

    /**
     * Write a Geometry to a String
     * @param g The Geometry
     * @return A String
     */
    String write(Geometry geom) {
        if (type.equalsIgnoreCase("simple")) {
            writeSimple(geom)
        } else if (type.equalsIgnoreCase("gml")) {
            writeGml(geom)
        } else if (type.equalsIgnoreCase("w3c")) {
            writeW3c(geom)
        } else {
            null
        }
    }

    /**
     * Write the Geometry to a GeoRSS Simple XML String
     * See http://georss.org/simple.html for more details.
     * @param geom The Geometry
     * @return A GeoRSS Simple XML String
     */
    private String writeSimple(Geometry geom) {
        if (geom instanceof Point) {
            return "<georss:point>${geom.y} ${geom.x}</georss:point>"
        }
        else if (geom instanceof LineString) {
            return "<georss:line>${getCoordinatesAsString(geom.coordinates)}</georss:line>"
        }
        else if (geom instanceof Polygon) {
            return "<georss:polygon>${getCoordinatesAsString(geom.exteriorRing.coordinates)}</georss:polygon>"
        }
        return null
    }

    /**
     * Write the Geometry to a GeoRSS GML XML String
     * See http://georss.org/gml.html for more details.
     * @param geom The Geometry
     * @return A GeoRSS GML XML String
     */
    private String writeGml(Geometry geom) {
        if (geom instanceof Point) {
            return "<georss:where><gml:Point><gml:pos>${geom.y} ${geom.x}</gml:pos></gml:Point></georss:where>"
        }
        else if (geom instanceof LineString) {
            return "<georss:where><gml:LineString><gml:posList>${getCoordinatesAsString(geom.coordinates)}</gml:posList></gml:LineString></georss:where>"
        }
        else if (geom instanceof Polygon) {
            return "<georss:where><gml:Polygon><gml:exterior><gml:LinearRing><gml:posList>${getCoordinatesAsString(geom.exteriorRing.coordinates)}</gml:posList></gml:LinearRing></gml:exterior></gml:Polygon></georss:where>"
        }
        return null
    }

    /**
     * Write the Geometry to a W3C encoded GeoRSS Xml String.
     * See http://georss.org/w3c.html for more details.
     * @param geom The Geometry.
     * @return A String or null
     */
    private String writeW3c(Geometry geom) {
        if (geom instanceof Point) {
            return "<geo:Point><geo:lat>${geom.y}</geo:lat><geo:long>${geom.x}</geo:long></geo:Point>"
        }
        return null
    }

    /**
     * Write an Array of Coordinates to a GML String
     * @param coords The Array of Coordinates
     * @return A GML String (x1,y1 x2,y2)
     */
    private String getCoordinatesAsString(Coordinate[] coords) {
        coords.collect{c -> "${c.y} ${c.x}"}.join(" ")
    }
}

