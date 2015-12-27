package geoscript.layer.io

import geoscript.layer.Pyramid

/**
 * A Groovy Extension Module that adds static methods to the Pyramid class.
 * @author Jared Erickson
 */
class CsvPyramidStaticExtensionModule {

    /**
     * Create a Pyramid from a CSV String
     * @param csv The CSV String
     * @return A Pyramid
     */
    static Pyramid fromCsv(Pyramid pyramid, String csv) {
        new CsvPyramidReader().read(csv)
    }

}
