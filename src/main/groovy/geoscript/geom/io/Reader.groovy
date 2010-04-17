package geoscript.geom.io

import geoscript.geom.Geometry

/**
 * Read a Geometry from a String
 * @author Jared Erickson
 */
interface Reader {

    /**
     * Read a Geometry from a String
     * @param str The String
     * @return A Geometry
     */
    Geometry read(String str);

}

