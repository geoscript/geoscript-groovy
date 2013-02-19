package geoscript.layer.io

import geoscript.layer.Layer
import geoscript.workspace.Workspace
import geoscript.workspace.Memory
import org.geotools.xml.Parser
import org.geotools.kml.KML
import org.geotools.kml.KMLConfiguration
import org.opengis.feature.simple.SimpleFeature

/**
 * Read a GeoScript Layer from a KML InputStream, File, or String.
 * @author Jared Erickson
 */
class KmlReader implements Reader {

    /**
     * Read a GeoScript Layer from a KML InputStream
     * @param input A KML InputStream
     * @return A GeoScript Layer
     */
    Layer read(InputStream input) {
        Parser parser = new Parser(new KMLConfiguration());
        SimpleFeature f = parser.parse(input);
        List placemarks = []
        collectPlaceMarks(placemarks, f);
        Workspace ws = new Memory()
        placemarks.each{placemark -> ws.ds.addFeatures(placemark)}
        ws.get(ws.layers[0].name)
    }

    /**
     * Read a GeoScript Layer from a KML File
     * @param file A KML File
     * @return A GeoScript Layer
     */
    Layer read(File file) {
         read(new FileInputStream(file))
    }

    /**
     * Read a GeoScript Layer from a String
     * @param str A KML String
     * @return A GeoScript Layer
     */
    Layer read(String str) {
        read(new ByteArrayInputStream(str.getBytes("UTF-8")))
    }

    private void collectPlaceMarks(List<SimpleFeature> features, SimpleFeature feature) {
        if (feature.getAttribute("Feature")) {
            feature.getAttribute("Feature").each { f ->
                collectPlaceMarks(features, f)
            }
        } else {
            features.add(feature)
        }
    }

}
