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

import static org.junit.Assert.assertNull

/**
 * The GeoRSSReader UnitTest
 * @author Jared Erickson
 */
class GeoRSSReaderTestCase {

    @Test void readPoint() {
        GeoRSSReader reader = new GeoRSSReader()
        // Simple
        String str = "<georss:point>45.256 -71.92</georss:point>"
        Point p = reader.read(str)
        assertEquals new Point(-71.92, 45.256).wkt, p.wkt
        // W3C
        str = "<geo:Point><geo:lat>45.256</geo:lat><geo:long>-71.92</geo:long></geo:Point>"
        p = reader.read(str)
        assertEquals new Point(-71.92, 45.256).wkt, p.wkt
        // GML
        str = "<georss:where><gml:Point><gml:pos>45.256 -71.92</gml:pos></gml:Point></georss:where>"
        p = reader.read(str)
        assertEquals new Point(-71.92, 45.256).wkt, p.wkt
    }

    @Test void readLineString() {
        GeoRSSReader reader = new GeoRSSReader()
        // Simple
        String str = "<georss:line>45.256 -110.45 46.46 -109.48 43.84 -109.86</georss:line>"
        LineString l = reader.read(str)
        assertEquals new LineString([-110.45,45.256], [-109.48,46.46], [-109.86,43.84]).wkt, l.wkt
        // GML
        str = "<georss:where><gml:LineString><gml:posList>45.256 -110.45 46.46 -109.48 43.84 -109.86</gml:posList></gml:LineString></georss:where>"
        l = reader.read(str)
        assertEquals new LineString([-110.45,45.256], [-109.48,46.46], [-109.86,43.84]).wkt, l.wkt
    }

    @Test void readPolygon() {
        GeoRSSReader reader = new GeoRSSReader()
        // Simple
        String str = "<georss:polygon>45.256 -110.45 46.46 -109.48 43.84 -109.86 45.256 -110.45</georss:polygon>"
        Polygon p = reader.read(str)
        assertEquals new Polygon([-110.45,45.256], [-109.48,46.46], [-109.86,43.84], [-110.45,45.256]).wkt, p.wkt
        // GML
        str = "<georss:where><gml:Polygon><gml:exterior><gml:LinearRing><gml:posList>45.256 -110.45 46.46 -109.48 43.84 -109.86 45.256 -110.45</gml:posList></gml:LinearRing></gml:exterior></gml:Polygon></georss:where>"
        p = reader.read(str)
        assertEquals new Polygon([-110.45,45.256], [-109.48,46.46], [-109.86,43.84], [-110.45,45.256]).wkt, p.wkt
    }

    @Test void readBox() {
        GeoRSSReader reader = new GeoRSSReader()
        String str = "<georss:box>42.943 -71.032 43.039 -69.856</georss:box>"
        Polygon p = reader.read(str)
        assertEquals new Polygon(
            [-71.032, 42.943],
            [-69.856, 42.943],
            [-69.856, 43.039],
            [-71.032, 43.039],
            [-71.032, 42.943]
        ).wkt, p.wkt
    }

    @Test void readCircle() {
        GeoRSSReader reader = new GeoRSSReader()
        String str = "<georss:circle>42.943 -71.032 500</georss:circle>"
        Polygon p = reader.read(str)
        assertEquals new Point(-71.032, 42.943).buffer(500).wkt, p.wkt
    }
}

