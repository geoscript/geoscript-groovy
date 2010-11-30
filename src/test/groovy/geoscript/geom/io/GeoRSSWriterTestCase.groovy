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

import org.junit.Test
import static org.junit.Assert.assertEquals
import geoscript.geom.*

/**
 * The GeoRSSWriter UnitTest
 * @author Jared Erickson
 */
class GeoRSSWriterTestCase {

    @Test void writePoint() {
        GeoRSSWriter writer = new GeoRSSWriter()
        Point p = new Point(-71.92, 45.256)
        assertEquals "<georss:point>45.256 -71.92</georss:point>", writer.write(p)
    }

    @Test void writeLineString() {
        GeoRSSWriter writer = new GeoRSSWriter()
        LineString l = new LineString([-110.45,45.256], [-109.48,46.46], [-109.86,43.84])
        assertEquals "<georss:line>45.256 -110.45 46.46 -109.48 43.84 -109.86</georss:line>", writer.write(l)
    }

    @Test void writePolygon() {
        GeoRSSWriter writer = new GeoRSSWriter()
        Polygon p = new Polygon([-110.45,45.256], [-109.48,46.46], [-109.86,43.84], [-110.45,45.256])
        assertEquals "<georss:polygon>45.256 -110.45 46.46 -109.48 43.84 -109.86 45.256 -110.45</georss:polygon>", writer.write(p)
    }
}

