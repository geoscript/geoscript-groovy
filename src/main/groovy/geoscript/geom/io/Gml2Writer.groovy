package geoscript.geom.io

import geoscript.geom.*
import org.locationtech.jts.geom.Coordinate

/**
 * Write a Geoscript {@link geoscript.geom.Geometry Geometry} to a GML Version 2 String.
 * <p><blockquote><pre>
 * Gml2Writer writer = new Gml2Writer()
 * String gml = writer.write(new {@link geoscript.geom.Point Point}(111,-47))
 *
 * &lt;gml:Point&gt;&lt;gml:coordinates&gt;111.0,-47.0&lt;/gml:coordinates&gt;&lt;/gml:Point&gt;
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class Gml2Writer implements Writer {

    /**
     * Write the Geometry to GML
     * @param geom The Geometry
     * @return GML
     */
    String write(Geometry geom) {

        if (geom instanceof Point) {
            return "<gml:Point><gml:coordinates>${geom.x},${geom.y}</gml:coordinates></gml:Point>"
        }
        else if (geom instanceof LinearRing) {
            return "<gml:LinearRing><gml:coordinates>${getCoordinatesAsString(geom.coordinates)}</gml:coordinates></gml:LinearRing>"
        }
        else if (geom instanceof LineString) {
            return "<gml:LineString><gml:coordinates>${getCoordinatesAsString(geom.coordinates)}</gml:coordinates></gml:LineString>"
        }
        else if (geom instanceof Polygon) {
            return "<gml:Polygon><gml:outerBoundaryIs><gml:LinearRing><gml:coordinates>${getCoordinatesAsString(geom.exteriorRing.coordinates)}</gml:coordinates></gml:LinearRing></gml:outerBoundaryIs>${geom.interiorRings.collect{r-> "<gml:innerBoundaryIs><gml:LinearRing><gml:coordinates>" + getCoordinatesAsString(r.coordinates) + "</gml:coordinates></gml:LinearRing></gml:innerBoundaryIs>"}.join('')}</gml:Polygon>"
        }
        if (geom instanceof MultiPoint) {
            return "<gml:MultiPoint>${geom.geometries.collect{g->"<gml:pointMember>" + write(g) + "</gml:pointMember>"}.join('')}</gml:MultiPoint>"
        }
        else if (geom instanceof MultiLineString) {
            return """<gml:MultiLineString>${geom.geometries.collect{g->
                "<gml:lineStringMember><gml:LineString><gml:coordinates>${getCoordinatesAsString(g.coordinates)}</gml:coordinates></gml:LineString></gml:lineStringMember>"
                }.join('')}</gml:MultiLineString>"""
        }
        else if (geom instanceof MultiPolygon) {
            return """<gml:MultiPolygon>${geom.geometries.collect{g->
                "<gml:polygonMember>${write(g)}</gml:polygonMember>"
                }.join('')}</gml:MultiPolygon>"""
        }
        else {
            return "<gml:GeometryCollection>${geom.geometries.collect{g->"<gml:geometryMember>" + write(g) + "</gml:geometryMember>"}.join('')}</gml:GeometryCollection>"
        }
    }

    /**
     * Write an Array of Coordinates to a GML String
     * @param coords The Array of Coordinates
     * @return A GML String (x1,y1 x2,y2)
     */
    private String getCoordinatesAsString(Coordinate[] coords) {
        coords.collect{c -> "${c.x},${c.y}"}.join(" ")
    }

}

