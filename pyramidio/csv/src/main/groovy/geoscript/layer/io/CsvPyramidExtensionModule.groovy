package geoscript.layer.io

import geoscript.layer.Pyramid

/**
 * A Groovy Extension Module for adding methods to the Pyramid class.
 */
class CsvPyramidExtensionModule {

    /**
     * Get this Pyramid as a CSV String
     * @return A CSV String
     */
    static String getCsv(Pyramid pyramid) {
        new CsvPyramidWriter().write(pyramid)
    }

}
