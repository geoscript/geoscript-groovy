package geoscript.workspace

import geoscript.feature.Field
import geoscript.feature.Schema
import geoscript.layer.Layer
import org.geotools.data.DataStore
import org.geotools.feature.FeatureIterator
import org.geotools.feature.FeatureCollection
import org.opengis.feature.simple.SimpleFeatureType
import org.geotools.data.collection.ListFeatureCollection

/**
 * A Workspace is a container of Layers.
 * @author Jared Erickson
 */
class Workspace {

    /**
     * The GeoTools DataStore
     */
    DataStore ds

    /**
     * Create a new Workspace wrapping a GeoTools DataStore
     * @param The GeoTools DataStore
     */
    Workspace(DataStore ds) {
        this.ds = ds
    }

    /**
     * Create a new Workspace with an in Memory Workspace
     */
    Workspace() {
        this(new Memory().ds)
    }

    /**
     * Get the format
     * @return The Workspace format name
     */
    String getFormat() {
        ds.getClass().getName()
    }

    /**
     * Get a List of Layer names
     * @return A List of Layer names
     */
    List<String> getLayers() {
        ds.typeNames.collect{it.toString()}
    }

    /**
     * Get a Layer by name
     * @param The Layer name
     * @return A Layer
     */
    Layer get(String name) {
        new Layer(this, ds.getFeatureSource(name))
    }
    
    
    /**
     * Another way to get a Layer by name.
     * <p><code>Layer layer = workspace["hospitals"]</code><p>
     * @param The Layer name
     * @return A Layer
     */
    Layer getAt(String name) {
        get(name)
    }

    /**
     * Create a Layer with a List of Fields
     * @param name The new Layer name
     * @param fields A List of Fields (defaults to a "geom", "Geometry" Field)
     * @return A new Layer
     */
    Layer create(String name, List<Field> fields = [new Field("geom","Geometry")]) {
        create(new Schema(name, fields))
    }

    /**
     * Create a Layer with a Schema
     * @param schema The Schema (defaults to a Schema with a single Geometry Field
     * named "geom"
     * @return A new Layer
     */
    Layer create(Schema schema = new Schema([new Field("geom","Geometry")])) {
        ds.createSchema(schema.featureType)
        get(schema.name)
    }

    /**
     * Add a Layer to the Workspace
     * @param layer The Layer to add
     * @return The newly added Layer
     */
    Layer add(Layer layer) {
        add(layer, layer.name)
    }

    /**
     * Add a Layer as a name to the Workspace
     * @param layer The Layer to add
     * @param name The new name of the Layer
     * @param chunk The number of Features to add in one batch
     * @return The newly added Layer
     */
    Layer add(Layer layer, String name, int chunk=1000) {
        List<Field> flds = layer.schema.fields.collect {
            if (it.isGeometry()) {
                return new Field(it.name, it.typ, layer.proj)
            }
            else {
                return new Field(it.name, it.typ)
            }
        }
        Layer l = create(name, flds)
        FeatureIterator it = layer.fs.getFeatures().features()
        try {
            while(true) {
                def features = readFeatures(it, layer.fs.schema, chunk)
                if (features.isEmpty()) break
                l.fs.addFeatures(features)
            }
        }
        finally {
            it.close()
        }
        l
    }

    /**
     * Read Features from a FeatureIterator in batches
     * @param it The FeatureIterator
     * @param type The SimpleFeatureType
     * @param chunk The number of Features to read in one batch
     * @return A FeatureCollection
     */
    private FeatureCollection readFeatures(FeatureIterator it, SimpleFeatureType type, int chunk) {
        int i = 0
        def features = new ListFeatureCollection(type)
        while (it.hasNext() && i < chunk) {
            features.add(it.next())
            i++
        }
        features
    }

    /**
     * Closes the Workspace by disposing of any resources.
     */
    void close() {
        ds.dispose()
    }
}
