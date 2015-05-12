package geoscript.layer.io

import geoscript.layer.Pyramid

/**
 * Write a Pyramid to a String.
 * @author Jared Erickson
 */
interface PyramidWriter {

    /**
     * Write a Pyramid to a String
     * @param pyramid The Pyramid
     * @return A String
     */
    String write(Pyramid pyramid)

}
