package geoscript.feature.io

import geoscript.feature.Feature

/**
 * Write a Feature to a String.
 * @author Jared Erickson
 */
interface Writer {

    /**
     * Write a Feature to a String
     * @param feature The Feature
     * @return A String
     */
    String write(Feature feature)

}
