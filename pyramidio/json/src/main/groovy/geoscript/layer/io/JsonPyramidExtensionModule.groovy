package geoscript.layer.io

import geoscript.layer.Pyramid

/**
 * A Groovy Extension Module for adding methods to the Pyramid class.
 */
class JsonPyramidExtensionModule {

    /**
     * Get this Pyramid as a JSON String
     * @return A JSON String
     */
    static String getJson(Pyramid pyramid) {
        new JsonPyramidWriter().write(pyramid)
    }

}
