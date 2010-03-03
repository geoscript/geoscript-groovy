package geoscript.workspace

import geoscript.layer.Layer
import geoscript.geom.*
import geoscript.feature.Feature
import geoscript.feature.Field
import geoscript.feature.Schema
import org.geotools.data.DataStore
import org.geotools.data.memory.MemoryDataStore

/**
 * A Workspace is a container of Layers
 */
class Workspace {

    /**
     * The GeoTools DataStore
     */
    DataStore ds

    /**
     * Create a new Workspace wrapping a GeoTools DataStore
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
     */
    String getFormat() {
        ds.getClass().getName()
    }

    /**
     * Get a List of Layer names
     */
    List<String> getLayers() {
        ds.typeNames.collect{it.toString()}
    }

    /**
     * Get a Layer by name
     */
    Layer get(String name) {
        new Layer(this, ds.getFeatureSource(name))
    }

    /**
     * Create a Layer with a List of Fields
     */
    Layer create(String name, List<Field> fields = [new Field("geom","Geometry")]) {
        create(new Schema(name, fields))
    }

    /**
     * Create a Layer with a Schema
     */
    Layer create(Schema schema = new Schema([new Field("geom","Geometry")])) {
        ds.createSchema(schema.featureType)
        get(schema.name)
    }

    /**
     * Add a Layer to the Workspace
     */
    Layer add(Layer layer) {
        add(layer, layer.name)
    }

    /**
     * Add a Layer as a name to the Workspace
     */
    Layer add(Layer layer, String name) {
        List<Field> flds = layer.schema.fields.collect {
            if (it.isGeometry()) {
                return new Field(it.name, it.typ, layer.proj)
            }
            else {
                return new Field(it.name, it.typ)
            }
        }
        Layer l = create(name, flds)
        layer.features.each{l.add(it)}
        l
    }
}