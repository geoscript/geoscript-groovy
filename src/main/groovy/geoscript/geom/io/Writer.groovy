package geoscript.geom.io

import geoscript.geom.Geometry

/**
 * Write a Geometry to a String
 * @author Jared Erickson
 */
interface Writer {

    /**
     * Write a Geometry to a String
     * @param g The Geometry
     * @return A String
     */
    String write(Geometry g);

}

