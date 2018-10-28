package geoscript.layer.io

import geoscript.feature.Schema
import geoscript.layer.Cursor
import geoscript.layer.Layer
import geoscript.proj.Projection
import geoscript.workspace.Workspace
import geoscript.workspace.Memory
import net.opengis.wfs.FeatureCollectionType
import org.geotools.feature.FeatureCollection
import org.geotools.xsd.Parser
import org.geotools.wfs.v1_0.WFSConfiguration_1_0 as WFS10
import org.geotools.wfs.v1_1.WFSConfiguration as WFS11
import org.geotools.wfs.v2_0.WFSConfiguration as WFS20

/**
 * Read a {@link geoscript.layer.Layer Layer} from a GML InputStream, File, or String.
 * <p><blockquote><pre>
 * String gml = """<wfs:FeatureCollection>...</wfs:FeatureCollection>"""
 * GmlReader reader = new GmlReader()
 * Layer layer = reader.read(gml)
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class GmlReader implements Reader {

    /**
     * Read a GeoScript Layer from an InputStream
     * @param options The optional named parameters:
     * <ul>
     *     <li>workspace: The Workspace used to create the Layer (defaults to Memory)</li>
     *     <li>projection: The Projection assigned to the Layer (defaults to null)</li>
     *     <li>name: The name of the Layer (defaults to gml)</li>
     *     <li>version: The GML/WFS version (2, 3, or 3.2, defaults to 2)</li>
     * </ul>
     * @param input An InputStream
     * @return A GeoScript Layer
     */
    Layer read(Map options = [:], InputStream input) {
        read(options, input, options.get("version", 2) as double)
    }

    /**
     * Read a GeoScript Layer from an InputStream
     * @param options The optional named parameters:
     * <ul>
     *     <li>workspace: The Workspace used to create the Layer (defaults to Memory)</li>
     *     <li>projection: The Projection assigned to the Layer (defaults to null)</li>
     *     <li>name: The name of the Layer (defaults to gml)</li>
     * </ul>
     * @param input An InputStream
     * @param version The GML/WFS version (2, 3, or 3.2)
     * @return A GeoScript Layer
     */
    Layer read(Map options = [:], InputStream input, double version) {
        // Default parameters
        Workspace workspace = options.get("workspace", new Memory())
        Projection proj = options.get("projection")
        String name = options.get("name", "gml")
        // Parse the GML
        def parser = new Parser(getGmlConfig(version))
        def fct = parser.parse(input) as FeatureCollectionType
        def fc = fct.getFeature().get(0) as FeatureCollection
        /*fc.feature.each{f -> ws.ds.addFeatures(f)}
        List<Layer> layers = ws.layers
        if (layers.size() > 1) {
            return layers
        } else {
            return layers[0]
        }*/
        // Create Schema and Layer
        Schema schema = new Schema(fc.schema).reproject(proj, name)
        Layer layer = workspace.create(schema)
        layer.add(new Cursor(fc))
        layer


    }

    /**
     * Read a GeoScript Layer from a File
     * @param options The optional named parameters:
     * <ul>
     *     <li>workspace: The Workspace used to create the Layer (defaults to Memory)</li>
     *     <li>projection: The Projection assigned to the Layer (defaults to null)</li>
     *     <li>name: The name of the Layer (defaults to gml)</li>
     *     <li>version: The GML/WFS version (2, 3, or 3.2, defaults to 2)</li>
     * </ul>
     * @param file A File
     * @return A GeoScript Layer
     */
    Layer read(Map options = [:], File file) {
        read(options, file, options.get("version", 2) as double)
    }

    /**
     * Read a GeoScript Layer from a File
     * @param options The optional named parameters:
     * <ul>
     *     <li>workspace: The Workspace used to create the Layer (defaults to Memory)</li>
     *     <li>projection: The Projection assigned to the Layer (defaults to null)</li>
     *     <li>name: The name of the Layer (defaults to gml)</li>
     * </ul>
     * @param file A File
     * @param version The GML/WFS version (2, 3, or 3.2)
     * @return A GeoScript Layer
     */
    Layer read(Map options = [:], File file, double version) {
        read(options, new FileInputStream(file), version)
    }

    /**
     * Read a GeoScript Layer from a String
     * @param options The optional named parameters:
     * <ul>
     *     <li>workspace: The Workspace used to create the Layer (defaults to Memory)</li>
     *     <li>projection: The Projection assigned to the Layer (defaults to null)</li>
     *     <li>name: The name of the Layer (defaults to gml)</li>
     *     <li>version: The GML/WFS version (2, 3, or 3.2, defaults to 2)</li>
     * </ul>
     * @param str A String
     * @return A GeoScript Layer
     */
    Layer read(Map options = [:], String str) {
        read(options, str, options.get("version", 2) as double)
    }

    /**
     * Read a GeoScript Layer from a String
     * @param options The optional named parameters:
     * <ul>
     *     <li>workspace: The Workspace used to create the Layer (defaults to Memory)</li>
     *     <li>projection: The Projection assigned to the Layer (defaults to null)</li>
     *     <li>name: The name of the Layer (defaults to gml)</li>
     * </ul>
     * @param str A String
     * @param version The GML/WFS version (2, 3, or 3.2)
     * @return A GeoScript Layer
     */
    Layer read(Map options = [:], String str, double version) {
        read(options, new ByteArrayInputStream(str.getBytes("UTF-8")), version)
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
