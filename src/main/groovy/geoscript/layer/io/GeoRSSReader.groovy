package geoscript.layer.io

import geoscript.feature.Feature
import geoscript.feature.Schema
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
        Node root = parser.parse(input)
        String rootName = root.name().toString().toLowerCase()

        // Use the Feature GeoRSSReader
        def featureReader = new geoscript.feature.io.GeoRSSReader()

        // RSS
        if (rootName.equals("rss")) {
            root.channel.item.each { Node item ->
                // The data for a Feature
                Map datum = [:]
                // Read the item
                featureReader.read(item, datum, fieldMap)
                // Add the Feature
                data.add(datum)
            }
        }
        // ATOM
        else if (rootName.equals("feed")) {
            root.entry.each { Node entry ->
                Map datum = [:]
                // Read the entry
                featureReader.read(entry, datum, fieldMap)
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
}
