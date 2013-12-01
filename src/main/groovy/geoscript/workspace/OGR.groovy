package geoscript.workspace

import geoscript.feature.Schema
import geoscript.layer.Cursor
import geoscript.layer.Layer
import org.geotools.data.ogr.OGRDataStore
import org.geotools.data.ogr.jni.JniOGRDataStoreFactory
import org.geotools.data.simple.SimpleFeatureCollection
import org.geotools.data.store.ReTypingFeatureCollection

/**
 * A GDAL/OGR based Workspace requires a native installation of GDAL/OGR.
 * http://www.gdal.org/ogr/ogr_formats.html
 * @author Jared Erickson
 */
class OGR extends Workspace {

    /**
     * The driver string
     */
    private String driver

    /**
     * The dataset string
     */
    private String dataset

    /**
     * Create a new OGR Workspace
     * @param driver The driver
     * @param dataset The dataset
     */
    OGR(String driver, String dataset) {
        super(new JniOGRDataStoreFactory().createDataStore(["DriverName": driver, "DatasourceName": dataset]))
        this.driver = driver
        this.dataset = dataset
    }

    /**
     * Create a new OGR Workspace
     * @param dataset The dataset
     */
    OGR(String dataset) {
        super(new JniOGRDataStoreFactory().createDataStore(["DatasourceName": dataset]))
        this.dataset = dataset
    }

    /**
     * Get a Set of drivers that GDAL has been compiled to support.
     * @return A Set of drivers
     */
    static Set<String> getDrivers() {
        def factory =  new JniOGRDataStoreFactory()
        factory.getAvailableDrivers()
    }

    /**
     * Get the format
     * @return The workspace format name
     */
    String getFormat() {
        return "OGR"
    }

    /**
     * The String representation
     * @return A String representation
     */
    String toString() {
        return "OGR (" + (driver ? "Driver = ${driver}, " : "") + "Dataset = ${dataset})"
    }

    /**
     * Can OGR write to the Layer in place
     * @param layerName The Layer name
     * @return Whether OGR can write to the Layer in place
     */
    private boolean canWriteInPlace(String layerName) {
        ((OGRDataStore)ds).supportsInPlaceWrite(layerName)
    }

    /**
     * Create a Layer from a Schema
     * @param schema The Schema
     * @return A new Layer
     */
    @Override
    Layer create(Schema schema) {
        this.create(schema, canWriteInPlace(schema.name))
    }

    /**
     * Create a new Layer from a Schema
     * @param schema The Schema
     * @param write Whether to force the immediate creation of the Layer
     * @return The new Layer
     */
    Layer create(Schema schema, boolean write) {
        this.create([:], schema, write)
    }

    /**
     * Create a new Layer from a Schema
     * @param options OGR Layer creation options
     * <ul>
     *  <li>approximateFields = true or false</li>
     *  <li>options = A List of OGR options</li>
     * </ul>
     * @param schema The Schema
     * @param write Whether to force the immediate creation of the Layer
     * @return The new Layer
     */
    Layer create(Map options, Schema schema, boolean write) {
        if (write) {
            super.create(schema)
        } else {
            new OGRLayer(this, schema, options)
        }
    }

    /**
     * Create a Layer from a Cursor
     * @param options The OGR options
     * <ul>
     *  <li>approximateFields = true or false</li>
     *  <li>options = A List of OGR options</li>
     * </ul>
     * @param c The Cursor
     * @return The new Layer
     */
    Layer create(Map options = [:], Cursor c) {
        boolean approximateFields = options.get("approximateFields", true)
        List layerOptions = options.get("options", [])
        ((OGRDataStore)ds).createSchema(c.col as SimpleFeatureCollection, approximateFields, layerOptions as String[])
        // Because GeoJSON returns OGRGeoJSON
        if (names.size() == 1) {
            get(names[0])
        } else {
            get(c.col.schema.typeName)
        }
    }

    /**
     * Add a Layer to this Workspace
     * @param layer The Layer to add
     * @param name The name of the new Layer
     * @param chunk The number of Features to read from the Layer at a time
     * @return The new Layer
     */
    @Override
    Layer add(Layer layer, String name, int chunk=1000) {
        this.add([:], layer, name, chunk)
    }

    /**
     * Add an existing Layer to this Workspace
     * @param options The OGR options
     * <ul>
     *  <li>approximateFields = true or false</li>
     *  <li>options = A List of OGR options</li>
     * </ul>
     * @param layer The Layer to add
     * @param name The new Layer name
     * @param chunk The number of Features to read at one time
     * @return The new Layer
     */
    Layer add(Map options, Layer layer, String name, int chunk=1000) {
        def fc = new ReTypingFeatureCollection(layer.cursor.col,
                new Schema(name, layer.schema.fields, layer.schema.uri).featureType)
        this.create(options, new Cursor(fc))
    }

    /**
     * A Layer specifically for OGR Workspaces that remembers the
     * Schema and OGR Options.  The geoscript.layer.Writer calls write
     * to actually write Features.
     */
    private static class OGRLayer extends geoscript.layer.Layer {

        /**
         * The OGR Options
         */
        private Map options

        /**
         * Create a new OGRLayer
         * @param ogr The OGR Workspace
         * @param schema The Schema
         * @param options The OGR Options
         */
        OGRLayer(OGR ogr, Schema schema, Map options) {
            super(schema.name, schema)
            this.workspace = ogr
            this.options = options
        }

        /**
         * Write a Cursor of Features to the Layer
         * @param c The Cursor
         */
        void write(Cursor c) {
            Layer layer = this.workspace.create(options, c)
            this.fs = layer.fs
            this.name = layer.name
            this.schema = layer.schema
        }
    }
}
