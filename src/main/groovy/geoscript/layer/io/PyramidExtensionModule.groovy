package geoscript.layer.io

import geoscript.layer.Pyramid

/**
 * A Groovy Extension Module for adding methods to the Pyramid class.
 */
class PyramidExtensionModule {

    /**
     * Get this Pyramid as a CSV String
     * @return A CSV String
     */
    static String getCsv(Pyramid pyramid) {
        new CsvPyramidWriter().write(pyramid)
    }

    /**
     * Get this Pyramid as a XML String
     * @return A XML String
     */
    static String getXml(Pyramid pyramid) {
        new XmlPyramidWriter().write(pyramid)
    }

    /**
     * Get this JSON as a JSON String
     * @return A CSV String
     */
    static String getJson(Pyramid pyramid) {
        new JsonPyramidWriter().write(pyramid)
    }

}
