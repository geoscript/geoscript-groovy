package geoscript.layer.io

import geoscript.layer.Pyramid

/**
 * A Groovy Extension Module that adds static methods to the Pyramid class.
 * @author Jared Erickson
 */
class StaticPyramidExtensionModule {

    /**
     * Create a Pyramid from a CSV String
     * @param csv The CSV String
     * @return A Pyramid
     */
    static Pyramid fromCsv(Pyramid pyramid, String csv) {
        new CsvPyramidReader().read(csv)
    }

    /**
     * Create a Pyramid from an XML String
     * @param xml The XML String
     * @return A Pyramid
     */
    static Pyramid fromXml(Pyramid pyramid, String xml) {
        new XmlPyramidReader().read(xml)
    }

    /**
     * Create a Pyramid from a JSON String
     * @param json The JSON String
     * @return A Pyramid
     */
    static Pyramid fromJson(Pyramid pyramid, String json) {
        new JsonPyramidReader().read(json)
    }
}
