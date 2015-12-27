package geoscript.geom.io

import geoscript.geom.Geometry
import geoscript.geom.LineString
import geoscript.geom.MultiLineString
import geoscript.geom.Point

/**
 * Write a Geometry to a GPX document
 * @author Jared Erickson
 */
class GpxWriter implements Writer {

    /**
     * Write a Geometry to a String
     * @param geom The Geometry
     * @return A String
     */
    @Override
    String write(Geometry geom) {
        if (geom instanceof Point) {
            Point pt = geom as Point
            return "<wpt lat='${pt.y}' lon='${pt.x}'/>"
        } else if (geom instanceof LineString) {
            LineString line = geom as LineString
            return "<rte>${line.points.collect { '<rtept lat=\'' + it.y + '\' lon=\'' + it.x + '\' />' }.join('')}</rte>"
        } else if (geom instanceof MultiLineString) {
            MultiLineString multiLine = geom as MultiLineString
            return "<trk>${multiLine.geometries.collect { '<trkseg>' + it.points.collect { '<trkpt lat=\'' + it.y + '\' lon=\'' + it.x + '\'/>' }.join('') + '</trkseg>' }.join('')}</trk>"
        } else {
            return null
        }
    }

    /**
     * Build a GeoRSS Geometry using a Groovy MarkupBuilder
     * @param options The named parameters
     * @param builder The MarkupBuilder Node
     * @param geom The Geometry
     */
    void write(Map options = [:], def builder, Geometry geom) {
        if (geom instanceof Point) {
            Point pt = geom as Point
            builder.wpt(lat: pt.y, lon: pt.x)
        } else if (geom instanceof LineString) {
            LineString line = geom as LineString
            builder.rte {
                line.points.each { Point pt ->
                    builder.rtept(lat: pt.y, lon: pt.x)
                }
            }
        } else if (geom instanceof MultiLineString) {
            MultiLineString multiLine = geom as MultiLineString
            builder.trk {
                multiLine.geometries.each { LineString line ->
                    builder.trkseg {
                        line.points.each { Point pt ->
                            builder.trkpt(lat: pt.y, lon: pt.x)
                        }
                    }
                }
            }
        }
    }
}
