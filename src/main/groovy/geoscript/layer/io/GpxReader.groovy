package geoscript.layer.io

import geoscript.feature.Feature
import geoscript.feature.Field
import geoscript.feature.Schema
import geoscript.geom.Point
import geoscript.layer.Layer
import geoscript.proj.Projection
import geoscript.workspace.Memory
import geoscript.workspace.Workspace
import groovy.xml.XmlParser
import java.nio.charset.Charset

/**
 * Read a Layer from a GPX document.
 * @author Jared Erickson
 */
class GpxReader implements Reader {

    /**
     * The Type of data to read from the GPX document
     */
    Type type = Type.WayPointsRoutesTracks

    /**
     * GPX Data Types
     */
    static enum Type {
        WayPoints,
        Routes,
        Tracks,
        WayPointsRoutesTracks,
        RoutePoints,
        TrackPoints
    }

    /**
     * Read a GeoScript Layer from an InputStream
     * @param input An InputStream
     * @return A GeoScript Layer
     */
    @Override
    Layer read(InputStream input) {
        read([:], input)
    }

    /**
     * Read a GeoScript Layer from an InputStream
     * @param input An InputStream
     * @return A GeoScript Layer
     */
    Layer read(Map options, InputStream input) {

        // Default parameters
        Workspace workspace = options.get("workspace", new Memory())
        Projection proj = options.get("projection", new Projection("EPSG:4326"))
        String layerName = options.get("name", "gpx")

        // Determine geometry type based on the type we are reading
        String geometryType
        if (type == Type.WayPointsRoutesTracks) {
            geometryType = "Geometry"
        } else if (type == Type.WayPoints || type == Type.RoutePoints || type == Type.TrackPoints) {
            geometryType = "Point"
        } else if (type == Type.Routes) {
            geometryType = "LineString"
        } else if (type == Type.Tracks) {
            geometryType = "MultiLineString"
        }
        Map fieldMap = [geom: new Field("geom", geometryType, "EPSG:4326")]
        List data = []

        geoscript.feature.io.GpxReader featureReader = new geoscript.feature.io.GpxReader()

        XmlParser parser = new XmlParser(false, false)
        Node root = parser.parse(input)

        // Read way points
        if (type == Type.WayPoints || type == Type.WayPointsRoutesTracks) {
            root.wpt.each { Node wpt ->
                Map datum = [:]
                featureReader.read(wpt, datum, fieldMap)
                data.add(datum)
            }
        }

        // Read routes as lines
        if (type == Type.Routes || type == Type.WayPointsRoutesTracks) {
            root.rte.each { Node rte ->
                Map datum = [:]
                featureReader.read(rte, datum, fieldMap)
                data.add(datum)
            }
        }

        // Read tracks as line
        if (type == Type.Tracks || type == Type.WayPointsRoutesTracks) {
            root.trk.each { Node trk ->
                Map datum = [:]
                featureReader.read(trk, datum, fieldMap)
                data.add(datum)
            }
        }

        // Read routes as points
        if (type == Type.RoutePoints) {
            fieldMap["route_fid"] = new Field("route_fid", "Integer")
            fieldMap["route_point_id"] = new Field("route_point_id", "Integer")
            int routeIndex = 0
            root.rte.each { Node rte ->
                routeIndex++
                Map baseDatum = [:]
                rte.children().each { Node rteChild ->
                    if (!rteChild.name().equals("rtept")) {
                        String name = rteChild.name()
                        String text = rteChild.text()
                        if (!fieldMap.containsKey(name)) {
                            fieldMap[name] = new Field(name, "String")
                        }
                        baseDatum[name] = text
                    }
                }
                int routePointIndex = 0
                rte.rtept.each {Node rtept ->
                    routePointIndex++
                    Map datum = [route_fid: routeIndex, route_point_id: routePointIndex]
                    datum.putAll(baseDatum)
                    datum["geom"] = new Point(rtept.attribute("lon") as double, rtept.attribute("lat") as double)
                    rtept.children().each {Node n ->
                        String name = n.name()
                        String text = n.text()
                        if (!fieldMap.containsKey(name)) {
                            fieldMap[name] = new Field(name, "String")
                        }
                        datum[name] = text
                    }
                    data.add(datum)
                }
            }
        }

        // Read tracks as points
        if (type == Type.TrackPoints) {
            fieldMap["track_fid"] = new Field("track_fid", "Integer")
            fieldMap["track_seg_id"] = new Field("track_seg_id", "Integer")
            fieldMap["track_seg_point_id"] = new Field("track_seg_point_id", "Integer")
            int trackIndex = 0
            root.trk.each { Node trk ->
                trackIndex++
                Map baseDatum = [:]
                trk.children().each { Node trkChild ->
                    if (!trkChild.name().equals("trkseg")) {
                        String name = trkChild.name()
                        String text = trkChild.text()
                        if (!fieldMap.containsKey(name)) {
                            fieldMap[name] = new Field(name, "String")
                        }
                        baseDatum[name] = text
                    }
                }
                int trackSegIndex = 0
                trk.trkseg.each { Node trkseg ->
                    trackSegIndex++
                    int trackPointIndex = 0
                    trkseg.trkpt.each { Node trkpt ->
                        trackPointIndex++
                        Map datum = [:]
                        datum.putAll(baseDatum)
                        datum["track_fid"] = trackIndex
                        datum["track_seg_id"] = trackSegIndex
                        datum["track_seg_point_id"] = trackPointIndex
                        datum["geom"] = new Point(trkpt.attribute("lon") as double, trkpt.attribute("lat") as double)
                        trkpt.children().each {Node n ->
                            String name = n.name()
                            String text = n.text()
                            if (!fieldMap.containsKey(name)) {
                                fieldMap[name] = new Field(name, "String")
                            }
                            datum[name] = text
                        }
                        data.add(datum)
                    }
                }
            }
        }

        // Write the GeoRSS data to a Layer
        Schema schema = new Schema(layerName, fieldMap.values()).reproject(proj)
        Layer layer = workspace.create(schema)
        layer.withWriter { writer ->
            data.each { datum ->
                Feature f = schema.feature(datum)
                writer.add(f)
            }
        }

        layer
    }

    /**
     * Read a GeoScript Layer from a File
     * @param file A File
     * @return A GeoScript Layer
     */
    @Override
    Layer read(File file) {
        read([:], file)
    }

    /**
     * Read a GeoScript Layer from a File
     * @param file A File
     * @return A GeoScript Layer
     */
    Layer read(Map options, File file) {
        InputStream input = new FileInputStream(file)
        Layer layer = read(options, input)
        input.close()
        layer
    }

    /**
     * Read a GeoScript Layer from a String
     * @param str A String
     * @return A GeoScript Layer
     */
    @Override
    Layer read(String str) {
        read([:], str)
    }

    /**
     * Read a GeoScript Layer from a String
     * @param str A String
     * @return A GeoScript Layer
     */
    Layer read(Map options, String str) {
        InputStream input = new ByteArrayInputStream(str.getBytes(Charset.forName("UTF-8")))
        Layer layer = read(options, input)
        input.close()
        layer
    }
}
