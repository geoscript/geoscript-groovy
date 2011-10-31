package geoscript.layer.io

import geoscript.layer.Layer
import geoscript.workspace.Workspace
import geoscript.workspace.Memory
import org.geotools.xml.Parser
import org.geotools.wfs.v1_0.WFSConfiguration as WFS10
import org.geotools.wfs.v1_1.WFSConfiguration as WFS11
import org.geotools.wfs.v2_0.WFSConfiguration as WFS20

/**
 * Read a GeoScript Layer from a GeoJSON InputStream, File, or String.
 * @author Jared Erickson
 */
class GmlReader implements Reader {

    /**
     * Read a GeoScript Layer from an InputStream
     * @param input An InputStream
     * @return A GeoScript Layer
     */
    Layer read(InputStream input) {
        read(input, 2)
    }

    /**
     * Read a GeoScript Layer from an InputStream
     * @param input An InputStream
     * @param version The GML/WFS version (2, 3, or 3.2)
     * @return A GeoScript Layer
     */
    Layer read(InputStream input, double version) {
        def parser = new Parser(getGmlConfig(version))
        def fc = parser.parse(input)
        Workspace ws = new Memory()
        fc.feature.each{f -> ws.ds.addFeatures(f)}
        List<Layer> layers = ws.layers.collect {name -> ws.get(name)}
        if (layers.size() > 1) {
            return layers
        } else {
            return layers[0]
        }
    }

    /**
     * Read a GeoScript Layer from a File
     * @param file A File
     * @return A GeoScript Layer
     */
    Layer read(File file) {
        read(file, 2)
    }

    /**
     * Read a GeoScript Layer from a File
     * @param file A File
     * @param version The GML/WFS version (2, 3, or 3.2)
     * @return A GeoScript Layer
     */
    Layer read(File file, double version) {
        read(new FileInputStream(file), version)
    }

    /**
     * Read a GeoScript Layer from a String
     * @param str A String
     * @return A GeoScript Layer
     */
    Layer read(String str) {
        read(str, 2)
    }

    /**
     * Read a GeoScript Layer from a String
     * @param str A String
     * @param version The GML/WFS version (2, 3, or 3.2)
     * @return A GeoScript Layer
     */
    Layer read(String str, double version) {
        read(new ByteArrayInputStream(str.getBytes("UTF-8")), version)
    }

    /**
     * Get the correct GML Configuration based on the version
     * @param version The version number (2, 3, or 3.2)
     * @return  A GeoTools GML Configuration
     */
    private def getGmlConfig(double version) {
        if (version == 2) {
            return new WFS10()
        } else if (version == 3) {
            return new WFS11()
        } else /*if (version == 3.2)*/ {
            return new WFS20()
        }
    }
}
