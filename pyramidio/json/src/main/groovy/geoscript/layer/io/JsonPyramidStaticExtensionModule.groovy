package geoscript.layer.io

import geoscript.layer.Pyramid

/**
 * A Groovy Extension Module that adds static methods to the Pyramid class.
 * @author Jared Erickson
 */
class JsonPyramidStaticExtensionModule {

    /**
     * Create a Pyramid from a JSON String
     * @param json The JSON String
     * @return A Pyramid
     */
    static Pyramid fromJson(Pyramid pyramid, String json) {
        new JsonPyramidReader().read(json)
    }

}
