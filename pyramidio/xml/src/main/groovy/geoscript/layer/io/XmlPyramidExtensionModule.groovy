package geoscript.layer.io

import geoscript.layer.Pyramid

/**
 * A Groovy Extension Module for adding methods to the Pyramid class.
 */
class XmlPyramidExtensionModule {

    /**
     * Get this Pyramid as a XML String
     * @return A XML String
     */
    static String getXml(Pyramid pyramid) {
        new XmlPyramidWriter().write(pyramid)
    }

}
