package geoscript.feature.io

import geoscript.feature.Feature
import geoscript.feature.Field
import geoscript.feature.Schema
import geoscript.geom.LineString
import geoscript.geom.MultiLineString
import geoscript.geom.Point

/**
 * Read a Feature from a GPX document
 * @author Jared Erickson
 */
class GpxReader implements Reader {

    /**
     * Read a Feature from a String.
     * @param str The String
     * @return A Feature
     */
    @Override
    Feature read(String str) {
        XmlParser parser = new XmlParser(false, false)
        read(parser.parse(new StringReader(str)))
    }

    /**
     * Read a Feature from an XML Node
     * @param node The XML Node
     * @return A Feature
     */
    Feature read(Node node) {
        // Collect data and fields
        Map datum = [:]
        Map fieldMap = [:]
        read(node, datum, fieldMap)
        // Create Schema and then Feature
        Schema schema = new Schema("gpx", fieldMap.values())
        schema.feature(datum)
    }

    /**
     * Read an XML Node and put the values in the datum Map and the fields in the fieldMap.
     * This method is useful for reading in a group of Features.
     * @param node The XML Node
     * @param datum The Map for the data
     * @param fieldMap The Map for the Fields
     */
    void read(Node node, Map datum, Map fieldMap) {
        // Get the root node's name
        String rootName = node.name().toString().toLowerCase()
        // WayPoints
        if (rootName.equalsIgnoreCase("wpt")) {
            if (!fieldMap.containsKey("geom")) {
                fieldMap["geom"] = new Field("geom", "Point", "EPSG:4326")
            }
            datum["geom"] = new Point(node.attribute("lon") as double, node.attribute("lat") as double)
            node.children().each { Node child ->
                if (child.name().toString().equals("extensions")) {
                   addExtensions(child, fieldMap, datum)
                } else {
                    String key = child.name()
                    String value = child.text()
                    if (!fieldMap.containsKey(key)) {
                        fieldMap[key] = new Field(key, "String")
                    }
                    datum[key] = value
                }
            }
        }
        // Routes
        else if (rootName.equalsIgnoreCase("rte")) {
            if (!fieldMap.containsKey("geom")) {
                fieldMap["geom"] = new Field("geom", "LineString", "EPSG:4326")
            }
            List points = []
            node.children().each { Node child ->
                if (child.name().toString().equals("extensions")) {
                    addExtensions(child, fieldMap, datum)
                } else {
                    String key = child.name()
                    String value = child.text()
                    if (key.equalsIgnoreCase("rtept")) {
                        points.add(new Point(child.attribute("lon") as double, child.attribute("lat") as double))
                    } else {
                        if (!fieldMap.containsKey(key)) {
                            fieldMap[key] = new Field(key, "String")
                        }
                    }
                    datum[key] = value
                }
            }
            datum["geom"] = new LineString(points)
        }
        // Tracks
        else if (rootName.equalsIgnoreCase("trk")) {
            if (!fieldMap.containsKey("geom")) {
                fieldMap["geom"] = new Field("geom", "MultiLineString", "EPSG:4326")
            }
            List lines = []
            node.children().each { Node child ->
                if (child.name().toString().equals("extensions")) {
                    addExtensions(child, fieldMap, datum)
                } else {
                    String key = child.name()
                    String value = child.text()
                    if (key.equalsIgnoreCase("trkseg")) {
                        List points = []
                        child.trkpt.each { Node trkpt ->
                            points.add(new Point(trkpt.attribute("lon") as double, trkpt.attribute("lat") as double))
                        }
                        lines.add(new LineString(points))
                    } else {
                        if (!fieldMap.containsKey(key)) {
                            fieldMap[key] = new Field(key, "String")
                        }
                    }
                    datum[key] = value
                }
            }
            datum["geom"] = new MultiLineString(lines)
        }
    }

    /**
     * Add values from the extensions XML element
     * @param node The Node representing the extensions element
     * @param fieldMap The field map
     * @param datum The datum
     */
    private void addExtensions(Node node, Map fieldMap, Map datum) {
        node.children().each { Node child ->
            // Transform my:element into my_element
            String name = child.name().replaceAll(":", "_")
            // Get the value as XML blob if complex or just the text value
            Object value
            if (child.children().size() > 0 && !(child.children()[0] instanceof String)) {
                value = getXml(child)
            } else {
                value = child.value()[0]
            }
            if (!fieldMap.containsKey(name)) {
                fieldMap[name] = new Field(name, "String")
            }
            datum[name] = value
        }
    }

    /**
     * Get an XML String from the children of the XML Node
     * @param node The XML Node
     * @return
     */
    private String getXml(Node node) {
        StringWriter w = new StringWriter()
        XmlNodePrinter nodePrinter = new XmlNodePrinter(new PrintWriter(w), "")
        nodePrinter.preserveWhitespace = false
        // Don't include the Node itself, only it's children
        node.children().each {
            nodePrinter.print(it)
        }
        // Remote new lines
        w.toString().replaceAll("[\r\n]+", "")
    }
}