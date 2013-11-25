package geoscript.layer.io

import geoscript.feature.Feature
import geoscript.feature.Field
import geoscript.feature.Schema
import geoscript.geom.*
import geoscript.layer.Layer
import geoscript.proj.Projection
import geoscript.workspace.Memory
import geoscript.workspace.Workspace

import java.nio.charset.Charset

/**
 * Read a Layer from a GeoRSS document
 * @author Jared Erickson
 */
class GeoRSSReader implements Reader {

    /**
     * Read a Layer from a GeoRSS File
     * @param options The optional named parameters:
     * <ul>
     *     <li>workspace: The Workspace used to create the Layer (defaults to Memory)</li>
     *     <li>projection: The Projection assigned to the Layer (defaults to null)</li>
     *     <li>name: The name of the Layer (defaults to geojson)</li>
     * </ul>
     * @param file The GeoRSS File
     * @return A Layer
     */
    Layer read(Map options = [:], File file) {
        InputStream input = new FileInputStream(file)
        Layer layer = read(options, input)
        input.close()
        layer
    }

    /**
     * Read a Layer from a GeoRSS String
     * @param options The optional named parameters:
     * <ul>
     *     <li>workspace: The Workspace used to create the Layer (defaults to Memory)</li>
     *     <li>projection: The Projection assigned to the Layer (defaults to null)</li>
     *     <li>name: The name of the Layer (defaults to geojson)</li>
     * </ul>
     * @param str The GeoRSS String
     * @return A Layer
     */
    Layer read(Map options = [:], String str) {
        InputStream input = new ByteArrayInputStream(str.getBytes(Charset.forName("UTF-8")))
        Layer layer = read(options, input)
        input.close()
        layer
    }

    /**
     * Read a Layer from a GeoRSS InputStream
     * @param options The optional named parameters:
     * <ul>
     *     <li>workspace: The Workspace used to create the Layer (defaults to Memory)</li>
     *     <li>projection: The Projection assigned to the Layer (defaults to null)</li>
     *     <li>name: The name of the Layer (defaults to geojson)</li>
     * </ul>
     * @param input The InputStream
     * @return A Layer
     */
    Layer read(Map options = [:], InputStream input) {

        // Default parameters
        Workspace workspace = options.get("workspace", new Memory())
        Projection proj = options.get("projection")
        String layerName = options.get("name", "georss")

        // Set up a Map for collecting Fields by name and a List of the data (which is stored in Maps)
        Map fieldMap = [:]
        List data = []

        // Parse the GeoRSS
        XmlParser parser = new XmlParser(false, false)
        def root = parser.parse(input)
        String rootName = root.name().toString().toLowerCase()

        // RSS
        if (rootName.equals("rss")) {
            root.channel.item.each { item ->
                // The data for a Feature
                Map datum = [:]
                // Handle w3c geometry with parent geo:Point element
                if (item["geo:lat"] && item["geo:long"]) {
                    fieldMap["geom"] = new Field("geom", "Point")
                    datum["geom"] = new Point(item["geo:long"].text() as double, item["geo:lat"].text() as double)
                }
                item.each { child ->
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
                        value = parseGeometry(child)
                    }
                    // Add to the schema if necessary
                    if (!fieldMap.containsKey(name)) {
                        fieldMap.put(name, new Field(name, type))
                    }
                    // Build of the Feature's data
                    datum[name] = value
                }
                // Add the Feature
                data.add(datum)
            }
        }
        // ATOM
        else if (rootName.equals("feed")) {
            root.entry.each { entry ->
                Map datum = [:]
                entry.each { child ->
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
                        value = parseGeometry(child)
                    }
                    // Add to the schema if necessary
                    if (!fieldMap.containsKey(name)) {
                        fieldMap.put(name, new Field(name, type))
                    }
                    // Build of the Feature's data
                    datum[name] = value
                }
                // Add the Feature
                data.add(datum)
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
     * Get an XML String from the children of the XML Node
     * @param node The XML Node
     * @return
     */
    private String getXml(def node) {
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
     * Parse the XML element and construct a Geometry
     * @param element The XML element
     * @return A Geometry or null
     */
    private Geometry parseGeometry(def element) {
        Geometry geom = null
        String name = element.name()
        if (name.equalsIgnoreCase("georss:where")) {
            String subName = element.children()[0].name()
            if (subName.equalsIgnoreCase("gml:Point")) {
                String[] xy = element["gml:Point"]["gml:pos"].text().replaceAll("\\s+", " ").split(" ")
                double x = xy[1] as double
                double y = xy[0] as double
                geom = new Point(x, y)
            } else if (subName.equalsIgnoreCase("gml:LineString")) {
                String text = element["gml:LineString"]["gml:posList"].text().replaceAll("\\s+", " ")
                geom = new LineString(getPoints(text))
            } else if (subName.equalsIgnoreCase("gml:Polygon")) {
                String text = element["gml:Polygon"]["gml:exterior"]["gml:LinearRing"]["gml:posList"].text().replaceAll("\\s+", " ")
                geom = new Polygon(new LinearRing(getPoints(text)))
            }
        } else if (name.equalsIgnoreCase("georss:point")) {
            String text = element.text().replaceAll("\\s+", " ")
            geom = getPoints(text)[0]
        } else if (name.equalsIgnoreCase("georss:line")) {
            String text = element.text().replaceAll("\\s+", " ")
            geom = new LineString(getPoints(text))
        } else if (name.equalsIgnoreCase("georss:polygon")) {
            String text = element.text().replaceAll("\\s+", " ")
            geom = new Polygon(new LinearRing(getPoints(text)))
        } else if (name.equalsIgnoreCase("georss:box")) {
            String text = element.text().replaceAll("\\s+", " ")
            List points = getPoints(text)
            geom = new Polygon(new LinearRing(
                    points[0],
                    new Point(points[1].x, points[0].y),
                    points[1],
                    new Point(points[0].x, points[1].y),
                    points[0]
            ))
        } else if (name.equalsIgnoreCase("geo:Point")) {
            double x = element["geo:Point"]["geo:long"].text() as double
            double y = element["geo:Point"]["geo:lat"].text() as double
            geom = new Point(x, y)
        }
        return geom
    }

    /**
     * Clean up a String by removing extra white space
     * @param str The str
     * @return A new String with the extra white space removed
     */
    private String cleanUpString(String str) {
        str.replaceAll("\\s+", " ")
    }

    /**
     * Get a List of Point from the String
     * @param str The space delimited GeoRSS List of coordinates
     * @return A List of Points
     */
    private List<Point> getPoints(String str) {
        def values = str.split(" ")
        int num = values.length / 2
        List points = []
        for (i in (0..<num)) {
            int k = i * 2
            double y = Double.parseDouble(values[k])
            double x = Double.parseDouble(values[k + 1])
            points.add(new Point(x, y))
        }
        points
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
     * Get Field type from the name of the Element and the XML Element
     * @param name The name of the Element
     * @param element The XML Element
     * @return A Field Type
     */
    private String getFieldType(def element) {
        String name = element.name()
        String type = "String"
        if (name.equalsIgnoreCase("georss:where")) {
            String subName = element.children()[0].name()
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
