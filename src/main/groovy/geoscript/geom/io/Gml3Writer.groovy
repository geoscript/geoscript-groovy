package geoscript.geom.io

import geoscript.geom.*
import com.vividsolutions.jts.geom.Coordinate

/**
 * Write a Geoscript Geometry to a GML String.
 * <p><code>Gml3Writer writer = new Gml3Writer()</code></p>
 * <p><code>String gml = writer.write(new Point(111,-47))</code></p>
 * <p><code>&lt;gml:Point&gt;&lt;gml:pos&gt;111.0,-47.0&lt;/gml:pos&gt;&lt;/gml:Point&gt;</code></p>
 * @author Jared Erickson
 */
class Gml3Writer implements Writer{

    /**
     * Write the Geometry to GML
     * @param geom The Geometry
     * @return GML
     */
    String write(Geometry geom) {

        if (geom instanceof Point) {
            return "<gml:Point><gml:pos>${geom.x} ${geom.y}</gml:pos></gml:Point>"
        }
        else if (geom instanceof LinearRing) {
            return "<gml:LinearRing><gml:posList>${getCoordinatesAsString(geom.coordinates)}</gml:posList></gml:LinearRing>"
        }
        else if (geom instanceof LineString) {
            return "<gml:LineString><gml:posList>${getCoordinatesAsString(geom.coordinates)}</gml:posList></gml:LineString>"
        }
        else if (geom instanceof Polygon) {
            return "<gml:Polygon><gml:exterior><gml:LinearRing><gml:posList>${getCoordinatesAsString(geom.exteriorRing.coordinates)}</gml:posList></gml:LinearRing></gml:exterior>${geom.interiorRings.collect{r-> "<gml:interior><gml:LinearRing><gml:posList>" + getCoordinatesAsString(r.coordinates) + "</gml:posList></gml:LinearRing></gml:interior>"}.join()}</gml:Polygon>"
        }
        if (geom instanceof MultiPoint) {
            return "<gml:MultiPoint>${geom.geometries.collect{g->"<gml:pointMember>" + write(g) + "</gml:pointMember>"}.join('')}</gml:MultiPoint>"
        }
        else if (geom instanceof MultiLineString) {
            // <gml:MultiCurve><gml:curveMember><gml:LineString><gml:posList>1 2 3 4</gml:posList></gml:LineString></gml:curveMember><gml:curveMember><gml:LineString><gml:posList>5 6 7 8</gml:posList></gml:LineString></gml:curveMember></gml:MultiCurve>
            return """<gml:Curve><gml:segments>${geom.geometries.collect{g->
                "<gml:LineStringSegment interpolation=\"linear\"><gml:posList>${getCoordinatesAsString(g.coordinates)}</gml:posList></gml:LineStringSegment>"
            }.join('')}</gml:segments></gml:Curve>"""
        }
        else if (geom instanceof MultiPolygon) {
            return """<gml:MultiSurface>${geom.geometries.collect{g->
                "<gml:surfaceMember>${write(g)}</gml:surfaceMember>"
            }.join('')}</gml:MultiSurface>"""
        }
        else {
            //<gml:GeometryCollection><gml:geometryMember><gml:Point><gml:pos>100 0</gml:pos></gml:Point></gml:geometryMember><gml:geometryMember><gml:LineString><gml:posList>101 0 102 1</gml:posList></gml:LineString></gml:geometryMember></gml:GeometryCollection>
            return "<gml:MultiGeometry>${geom.geometries.collect{g->"<gml:geometryMember>" + write(g) + "</gml:geometryMember>"}.join('')}</gml:MultiGeometry>"
        }
    }

    /**
     * Write an Array of Coordinates to a GML String
     * @param coords The Array of Coordinates
     * @return A GML String (x1,y1 x2,y2)
     */
    private String getCoordinatesAsString(Coordinate[] coords) {
        coords.collect{c -> "${c.x} ${c.y}"}.join(" ")
    }

}

