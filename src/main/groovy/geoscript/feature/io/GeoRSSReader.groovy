package geoscript.feature.io

import geoscript.feature.Feature
import geoscript.feature.Field
import geoscript.feature.Schema
import geoscript.geom.Point

/**
 * Read a Feature from a GeoRSS String
 * @author Jared Erickson
 */
class GeoRSSReader implements Reader {

    /**
     * Read a Feature from a String.
     * @param str The String
     * @return A Feature
     */
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
        Schema schema = new Schema("georss", fieldMap.values())
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

        def geometryReader = new geoscript.geom.io.GeoRSSReader()

        // RSS
        if (rootName.equals("item")) {
            Node item = node
            // Handle w3c geometry with parent geo:Point element
            if (item["geo:lat"] && item["geo:long"]) {
                fieldMap["geom"] = new Field("geom", "Point")
                datum["geom"] = new Point(item["geo:long"].text() as double, item["geo:lat"].text() as double)
            }
            item.each { Node child ->
                // Transform my:element into my_element
                String name = child.name().replaceAll(":", "_")
                // Remember original name for duplicate elements
                String originalName = name
                // Add attributes as Fields
                child.attributes().each { k, v ->
                    String fieldName = "${name}_${k}"
                    String originalFieldName = fieldName
                    // Support duplicate element_attribute names
                    int i = 1
                    while (datum.containsKey(fieldName)) {
                        fieldName = "${originalFieldName}${i}"
                        i++
                    }
                    fieldMap.put(fieldName, new Field(fieldName, "String"))
                    datum[fieldName] = v
                }
                // Get the value as XML blob if complex or just the text value
                Object value
                if (child.children().size() > 0 && !(child.children()[0] instanceof String)) {
                    value = getXml(child)
                } else {
                    value = child.value()[0]
                }
                // Get the field type
                String type = getFieldType(child)
                // Support duplicate elements
                int i = 1
                while (datum.containsKey(name)) {
                    name = "${originalName}${i}"
                    i++
                }
                // If it's geometry, then parse the geometry
                if (isGeometryType(type)) {
                    name = "geom"
                    value = geometryReader.read(child)
                }
                // Add to the schema if necessary
                if (!fieldMap.containsKey(name)) {
                    fieldMap.put(name, new Field(name, type))
                }
                // Build of the Feature's data
                datum[name] = value
            }
        }
        // ATOM
        else if (rootName.equals("entry")) {
            Node entry = node
            entry.each { Node child ->
                // Transform my:element into my_element
                String name = child.name().replaceAll(":", "_")
                // Remember original name for duplicate elements
                String originalName = name
                // Add attributes as Fields
                child.attributes().each { k, v ->
                    String fieldName = "${name}_${k}"
                    String originalFieldName = fieldName
                    // Support duplicate element_attribute names
                    int i = 1
                    while (datum.containsKey(fieldName)) {
                        fieldName = "${originalFieldName}${i}"
                        i++
                    }
                    fieldMap.put(fieldName, new Field(fieldName, "String"))
                    datum[fieldName] = v
                }
                // Get the value as XML blob if complex or just the text value
                Object value
                if (child.children().size() > 0 && !(child.children()[0] instanceof String)) {
                    value = getXml(child)
                } else {
                    value = child.value()[0]
                }
                // Get the field type
                String type = getFieldType(child)
                // Support duplicate elements
                int i = 1
                while (datum.containsKey(name)) {
                    name = "${originalName}${i}"
                    i++
                }
                // If it's geometry, then parse the geometry
                if (isGeometryType(type)) {
                    name = "geom"
                    value = geometryReader.read(child)
                }
                // Add to the schema if necessary
                if (!fieldMap.containsKey(name)) {
                    fieldMap.put(name, new Field(name, type))
                }
                // Build of the Feature's data
                datum[name] = value
            }
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

    /**
     * Is the Field type a Geometry?
     * @param type The Field type
     * @return Whether the Field type is a geometry or not
     */
    private boolean isGeometryType(String type) {
        type in ["Point", "LineString", "Polygon"]
    }

    /**
     * Get Field type from the XML Node
     * @param node The XML Node
     * @return A Field Type
     */
    private String getFieldType(Node node) {
        String name = node.name()
        String type = "String"
        if (name.equalsIgnoreCase("georss:where")) {
            String subName = node.children()[0].name()
            if (subName.equalsIgnoreCase("gml:Point")) {
                type = "Point"
            } else if (subName.equalsIgnoreCase("gml:LineString")) {
                type = "LineString"
            } else if (subName.equalsIgnoreCase("gml:Polygon")) {
                type = "Polygon"
            }
        } else if (name.equalsIgnoreCase("georss:point")) {
            type = "Point"
        } else if (name.equalsIgnoreCase("georss:line")) {
            type = "LineString"
        } else if (name.equalsIgnoreCase("georss:polygon")) {
            type = "Polygon"
        } else if (name.equalsIgnoreCase("georss:box")) {
            type = "Polygon"
        } else if (name.equalsIgnoreCase("geo:Point")) {
            type = "Point"
        } else if (name.equalsIgnoreCase("updated") || name.equalsIgnoreCase("pubDate")) {
            "Date"
        }
        type
    }
}
