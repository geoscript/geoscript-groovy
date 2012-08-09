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
 * String georss = writer.write(new {@geoscript.geom.Point Point}(-71.92, 45.256))
 * &lt;georss:point&gt;45.256 -71.92&lt;/georss:point&gt;
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class GeoRSSWriter implements Writer {

    /**
     * Write a Geometry to a String
     * @param g The Geometry
     * @return A String
     */
    String write(Geometry geom) {
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
     * Write an Array of Coordinates to a GML String
     * @param coords The Array of Coordinates
     * @return A GML String (x1,y1 x2,y2)
     */
    private String getCoordinatesAsString(Coordinate[] coords) {
        coords.collect{c -> "${c.y} ${c.x}"}.join(" ")
    }
}

