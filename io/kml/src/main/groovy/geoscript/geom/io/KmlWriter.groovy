package geoscript.geom.io

import com.vividsolutions.jts.geom.Coordinate
import geoscript.geom.*

/**
 * Write a Geoscript {@link geoscript.geom.Geometry Geometry} to a KML String.
 * <p><blockquote><pre>
 * KmlWriter writer = new KmlWriter()
 * String kml = writer.write(new {@link geoscript.geom.Point Point}(111,-47)
 *
 * &lt;Point&gt;&lt;coordinates&gt;111.0,-47.0&lt;/coordinates&gt;&lt;/Point&gt;
 * </pre></blockquote></p>
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

    /**
     * Build a KML Geometry using a Groovy MarkupBuilder
     * @param options The named parameters
     * <ul>
     *      <li>namespace = The KML namespace prefix (defaults to blank)</li>
     * </ul>
     * @param builder The MarkupBuilder Node
     * @param geom The Geometry
     */
    void write(Map options = [:], def builder, Geometry geom) {
        String namespace = options.get("namespace","")
        String ns = namespace.isEmpty() ? "" : "${namespace}:"
        if (geom instanceof Point) {
            builder."${ns}Point" {
                builder."${ns}coordinates" "${geom.x},${geom.y}"
            }
        } else if (geom instanceof LinearRing) {
            builder."${ns}LinearRing" {
                builder."${ns}coordinates" {mkp.yield(getCoordinatesAsString(geom.coordinates))}
            }
        } else if (geom instanceof LineString) {
            builder."${ns}LineString" {
                builder."${ns}coordinates" {mkp.yield(getCoordinatesAsString(geom.coordinates))}
            }
        } else if (geom instanceof Polygon) {
            Polygon poly = geom as Polygon
            builder."${ns}Polygon" {
                builder."${ns}outerBoundaryIs" {
                    write builder, new LinearRing(poly.exteriorRing.points), namespace: namespace
                }
                poly.interiorRings.each { ring ->
                    builder."${ns}innerBoundaryIs" {
                        write builder, new LinearRing(ring.points), namespace: namespace
                    }
                }
            }
        } else if (geom instanceof GeometryCollection) {
            builder."${ns}MultiGeometry" {
                geom.geometries.each {
                    write builder, it, namespace: namespace
                }
            }
        }
    }
}

