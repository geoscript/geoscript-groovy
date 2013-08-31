package geoscript.layer.io

import geoscript.feature.Feature
import geoscript.feature.Schema
import geoscript.layer.Layer
import geoscript.proj.Projection
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
     * The default Projection is EPSG:4326
     */
    private Projection defaultProj = new Projection("EPSG:4326")

    /**
     * Read a GeoScript Layer from a KML InputStream
     * @param options The optional named parameters:
     * <ul>
     *     <li>workspace: The Workspace used to create the Layer (defaults to Memory)</li>
     *     <li>projection: The Projection assigned to the Layer (defaults to EPSG:4326)</li>
     *     <li>name: The name of the Layer (defaults to kml)</li>
     * </ul>
     * @param input A KML InputStream
     * @return A GeoScript Layer
     */
    Layer read(Map options = [:], InputStream input) {
        // Default parameters
        Workspace workspace = options.get("workspace", new Memory())
        Projection proj = options.get("projection", defaultProj)
        String name = options.get("name", "kml")
        List subFields = options.get("subFields",["Geometry","name","description"])
        // Parse the KML
        Parser parser = new Parser(new KMLConfiguration());
        SimpleFeature f = parser.parse(input);
        // Collect placemarks
        List<SimpleFeature> placemarks = []
        collectPlaceMarks(placemarks, f);
        // Create the Schema and Layer
        Schema schema = new Schema(placemarks.size() > 0 ? placemarks[0].featureType : f.featureType)
                .reproject(proj, name).includeFields(subFields, name)
        Layer layer = workspace.create(schema)
        // Add Features
        placemarks.each{SimpleFeature placemark ->
            layer.add(new Feature(placemark))
        }
        layer
    }

    /**
     * Read a GeoScript Layer from a KML File
     * @param options The optional named parameters:
     * <ul>
     *     <li>workspace: The Workspace used to create the Layer (defaults to Memory)</li>
     *     <li>projection: The Projection assigned to the Layer (defaults to EPSG:4326)</li>
     *     <li>name: The name of the Layer (defaults to kml)</li>
     * </ul>
     * @param file A KML File
     * @return A GeoScript Layer
     */
    Layer read(Map options = [:], File file) {
         read(options, new FileInputStream(file))
    }

    /**
     * Read a GeoScript Layer from a String
     * @param options The optional named parameters:
     * <ul>
     *     <li>workspace: The Workspace used to create the Layer (defaults to Memory)</li>
     *     <li>projection: The Projection assigned to the Layer (defaults to EPSG:4326)</li>
     *     <li>name: The name of the Layer (defaults to kml)</li>
     * </ul>
     * @param str A KML String
     * @return A GeoScript Layer
     */
    Layer read(Map options = [:], String str) {
        read(options, new ByteArrayInputStream(str.getBytes("UTF-8")))
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
