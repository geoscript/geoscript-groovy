package geoscript.layer.io

import geoscript.layer.Pyramid

/**
 * A Groovy Extension Module that adds static methods to the Pyramid class.
 * @author Jared Erickson
 */
class XmlPyramidStaticExtensionModule {

    /**
     * Create a Pyramid from a XML String
     * @param xml The XML String
     * @return A Pyramid
     */
    static Pyramid fromXml(Pyramid pyramid, String xml) {
        new XmlPyramidReader().read(xml)
    }

}
