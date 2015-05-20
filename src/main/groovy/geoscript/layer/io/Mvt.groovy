package geoscript.layer.io

import geoscript.feature.Feature
import geoscript.feature.Field
import geoscript.feature.Schema
import geoscript.geom.Geometry
import geoscript.geom.io.WkbReader
import geoscript.layer.Layer
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

import java.nio.ByteBuffer
import java.util.zip.DeflaterInputStream
import java.util.zip.InflaterInputStream

/**
 * A MapNik Vector Tile Reader and Writer
 * @author Jared Erickson
 */
class Mvt {

    /**
     * The MVT File signature
     */
    static final byte[] MVT_SIGNATURE = [-119 as byte, 77 as byte, 86 as byte, 84 as byte]

    /**
     * Write the Layer to File
     * @param layer The Layer
     * @param file The File
     */
    static void write(Layer layer, File file) {
        OutputStream out = new FileOutputStream(file)
        write(layer, out)
        out.close()
    }

    /**
     * Write the Layer to an OutputStream
     * @param layer The Layer
     * @param out The OutputStream
     */
    static void write(Layer layer, OutputStream out) {

        // Get the number of features
        int numberOfFeatures = layer.count

        // Create a ByteBuffer for each Feature
        ByteBuffer[] featureBuffers = new ByteBuffer[numberOfFeatures]

        // Remember the total number of bytes for all features
        int totalFeatureByteCount = 4

        // Write each Feature to a ByteBuffer
        int index = 0
        layer.eachFeature { Feature f ->
            Geometry geometry = f.geom
            byte[] geomBytes = geometry.wkbBytes
            Map attributes = f.attributes
            attributes.remove(f.schema.geom.name)
            String jsonStr = new JsonBuilder(attributes).toString()
            byte[] jsonBytes = jsonStr.bytes
            int numberOfBytes = 4 + geomBytes.length + 4 + jsonBytes.length
            totalFeatureByteCount += numberOfBytes
            featureBuffers[index] = ByteBuffer.allocate(numberOfBytes)
            featureBuffers[index].putInt(geomBytes.length)
            featureBuffers[index].put(geomBytes)
            featureBuffers[index].putInt(jsonBytes.length)
            featureBuffers[index].put(jsonBytes)
            index++
        }

        // Create a single ByteBuffer for all Features
        ByteBuffer combinedfeatureBuffer = ByteBuffer.allocate(totalFeatureByteCount)
        combinedfeatureBuffer.putInt(numberOfFeatures)
        featureBuffers.each { ByteBuffer b ->
            b.flip()
            combinedfeatureBuffer.put(b)
        }

        // Then extract the byte array
        byte[] featureBytes = new byte[totalFeatureByteCount]
        combinedfeatureBuffer.flip()
        combinedfeatureBuffer.get(featureBytes)

        // Zlib compress the feature body
        byte[] compressedBytes = new DeflaterInputStream(new ByteArrayInputStream(featureBytes)).bytes

        // Create the final byte array
        int finalLength = MVT_SIGNATURE.length + 4 + compressedBytes.length
        ByteBuffer  buffer = ByteBuffer.allocate(finalLength)
        buffer.put(MVT_SIGNATURE)
        buffer.putInt(compressedBytes.length)
        buffer.put(compressedBytes)
        buffer.flip()

        byte[] finalBytes = new byte[finalLength]
        buffer.get(finalBytes)
        out.write(finalBytes)
    }

    /**
     * Read a Layer from a MVT encoded URL
     * @param url the URL
     * @return A Layer
     */
    static Layer read(URL url) {
        InputStream input = url.openStream()
        Layer layer = read(input)
        input.close()
        layer
    }

    /**
     * Read a Layer from a MVT encoded File
     * @param file The File
     * @return A Layer
     */
    static Layer read(File file) {
        InputStream input = new FileInputStream(file)
        Layer layer = read(input)
        input.close()
        layer
    }

    /**
     * Read a Layer from a MVT encoded InputStream
     * @param inputStream The InputStream
     * @return A Layer
     */
    static Layer read(InputStream inputStream) {
        // Get bytes from the InputStream and wrap in 
        // a ByteBuffer
        byte[] bytes = inputStream.bytes
        ByteBuffer buffer = ByteBuffer.wrap(bytes)

        // Extract header signature
        byte[] header = new byte[4]
        buffer.get(header)
        if (!Arrays.equals(MVT_SIGNATURE, header)) {
            throw new IllegalArgumentException("Incorrect file signature!")
        }

        // Extract the body length
        int length = buffer.getInt()
        byte[] bodyBytes = new byte[length]
        buffer.get(bodyBytes)

        // Decompress the zlip encoded features
        byte[] inflatedBytes = new InflaterInputStream(new ByteArrayInputStream(bodyBytes)).bytes
        ByteBuffer bodyBuffer = ByteBuffer.wrap(inflatedBytes)

        // Get the feature count
        int count = bodyBuffer.getInt()

        // Get the features
        List data = []
        JsonSlurper jsonSlurper = new JsonSlurper()
        WkbReader wkbReader = new WkbReader()
        (0..<count).each { int i ->
            int wkbLength = bodyBuffer.getInt()
            byte[] wkb = new byte[wkbLength]
            bodyBuffer.get(wkb)
            Geometry geom = wkbReader.read(wkb)
            int attributesLength = bodyBuffer.getInt()
            byte[] attributes = new byte[attributesLength]
            bodyBuffer.get(attributes)
            String jsonStr = new String(attributes)
            Map json = jsonSlurper.parseText(jsonStr)
            Map datum = [:]
            datum.geom = geom
            json.each{ String key, Object value ->
                datum[key] = value
            }
            data.add(datum)
        }

        // Create a List of Fields from the first feature
        List fields = []
        Map datum = data[0]
        datum.each { String key, Object value ->
            Field fld = new Field(key, value ? value.class.name : "String", value instanceof Geometry ? "EPSG:3857" : null)
            fields.add(fld)
        }

        // Create a Schema
        Schema schema = new Schema("layer", fields)
        // Create a Layer
        Layer layer = new Layer("layer", schema)
        // Add each Feature to the Layer
        data.each { Map map ->
            layer.add(map)
        }
        layer
    }
}
