package geoscript.geom.io

import geoscript.geom.*
import com.vividsolutions.jts.geom.Coordinate

/**
 * Write a Geoscript Geometry to a KML String.
 * <p><code>KmlWriter writer = new KmlWriter()</code></p>
 * <p><code>String kml = writer.write(new Point(111,-47)</code></p>
 * <p><code>&lt;Point&gt;&lt;coordinates&gt;111.0,-47.0&lt;/coordinates&gt;&lt;/Point&gt;</code></p>
 * @author Jared Erickson
 */
class KmlWriter implements Writer {

    /**
     * Write the Geometry to KML
     * @param geom The Geometry
     * @return KML
     */
    String write(Geometry geom) {

        if (geom instanceof Point) {
            return "<Point><coordinates>${geom.x},${geom.y}</coordinates></Point>"
        }
        else if (geom instanceof LinearRing) {
            return "<LinearRing><coordinates>${getCoordinatesAsString(geom.coordinates)}</coordinates></LinearRing>"
        }
        else if (geom instanceof LineString) {
            return "<LineString><coordinates>${getCoordinatesAsString(geom.coordinates)}</coordinates></LineString>"
        }
        else if (geom instanceof Polygon) {
            return "<Polygon><outerBoundaryIs><LinearRing><coordinates>${getCoordinatesAsString(geom.exteriorRing.coordinates)}</coordinates></LinearRing></outerBoundaryIs>${geom.interiorRings.collect{r-> "<innerBoundaryIs><LinearRing><coordinates>" + getCoordinatesAsString(r.coordinates) + "</coordinates></LinearRing></innerBoundaryIs>"}.join('')}</Polygon>"
        }
        if (geom instanceof MultiPoint) {
            return "<MultiGeometry>${geom.geometries.collect{g->write(g)}.join('')}</MultiGeometry>"
        }
        else if (geom instanceof MultiLineString) {
            return "<MultiGeometry>${geom.geometries.collect{g->write(g)}.join('')}</MultiGeometry>"
        }
        else if (geom instanceof MultiPolygon) {
            return "<MultiGeometry>${geom.geometries.collect{g->write(g)}.join('')}</MultiGeometry>"
        }
        else {
            return "<MultiGeometry>${geom.geometries.collect{g->write(g)}.join('')}</MultiGeometry>"
        }
    }

    /**
     * Write an Array of Coordinates to a KML String
     * @param coords The Array of Coordinates
     * @return A KML String (x1,y1 x2,y2)
     */
    private String getCoordinatesAsString(Coordinate[] coords) {
        coords.collect{c -> "${c.x},${c.y}"}.join(" ")
    }

}

