package geoscript.workspace

import geoscript.GeoScript
import geoscript.feature.Feature
import geoscript.feature.Field
import geoscript.feature.Schema
import geoscript.layer.Cursor
import geoscript.layer.Layer
import org.geotools.data.DataStore
import org.geotools.feature.FeatureCollection
import org.geotools.data.collection.ListFeatureCollection
import org.geotools.data.DataStoreFinder

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
        if (ds == null) {
            throw new IllegalArgumentException("Can't find Workspace!")
        }
        this.ds = ds
    }

    /**
     * Create a new Workspace with an in Memory Workspace
     */
    Workspace() {
        this(new Memory().ds)
    }

    /**
     * Create a new Workspace from a Map of parameters.
     * @param params The Map of parameters
     */
    Workspace(Map params) {
        this(DataStoreFinder.getDataStore(params))
    }

    /**
     * Create a new Workspace from a parameter string.  The parameter string is space delimited collection of key=value
     * parameters.  If the key or value contains spaces they must be single quoted.
     * @param paramString The parameter string.
     */
    Workspace(String paramString) {
        this(getParametersFromString(paramString))
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
    List<String> getNames() {
        ds.typeNames.collect{it.toString()}
    }

    /**
     * Get a List of Layers
     * @return A List of Layers
     */
    List<Layer> getLayers() {
        getNames().collect{name -> get(name)}
    }

    /**
     * Whether the Workspace has a Layer by the given name
     * @param name The Layer name
     * @return Whether the Workspace has a Layer by the given name
     */
    boolean has(String name) {
        getNames().contains(name)
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
        l.withWriter {geoscript.layer.Writer writer ->
            Cursor c = layer.getCursor()
            while(true) {
                def features = readFeatures(c, l.schema, chunk)
                if (features.isEmpty()) {
                    break
                }
                new Cursor(features).each{Feature f->
                    writer.add(f)
                }
                if (features.size() < chunk) {
                    break
                }
            }
        }
        l
    }

    /**
     * Read Features from a Cursor in Batches.
     * @param cursor The Cursor
     * @param schema The output Schema
     * @param chunk The number of Features to be read
     * @return A GeoTools FeatureCollection
     */
    protected FeatureCollection readFeatures(Cursor cursor, Schema schema, int chunk) {
        int i = 0
        def features = new ListFeatureCollection(schema.featureType)
        while(cursor.hasNext() && i < chunk) {
            Feature f = cursor.next()
            if (f.schema == null) {
                f.schema = schema
            } else if (f.schema != schema) {
                f = schema.feature(f.attributes)
            }
            features.add(f.f)
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

    /**
     * Get a Map from a parameter string: "dbtype=h2 database=roads.db"
     * @param str The parameter string is a space delimited collection of key=value parameters.  Use single
     * quotes around key or values with internal spaces.
     * @return A Map of parameters
     */
    private static Map getParametersFromString(String str) {
        Map params = [:]
        for (WorkspaceFactory workspaceFactory : WorkspaceFactories.list()) {
            params = workspaceFactory.getParametersFromString(str)
            if (!params.isEmpty()) {
                break
            }
        }
        if (params.isEmpty()) {
            throw new IllegalArgumentException("Unknown Workspace parameter string: ${str}")
        }
        params
    }

    /**
     * Get a List of available GeoTools workspaces (aka DataStores)
     * @return A List of available GeoTools workspace
     */
    static List getWorkspaceNames() {
        DataStoreFinder.availableDataStores.collect{ds ->
            ds.displayName
        }
    }

    /**
     * Get the list of connection parameters for the given workspace
     * @param name The workspace name
     * @return A List of parameters which are represented as a Map with key, type, required keys
     */
    static List getWorkspaceParameters(String name) {
        def ds = DataStoreFinder.availableDataStores.find{ds ->
            if (ds.displayName.equalsIgnoreCase(name)) {
                return ds
            }
        }
        ds.parametersInfo.collect{param ->
            [key: param.name, type: param.type.name, required: param.required]
        }
    }

    /**
     * Get a Workspace from a parameter string
     * @param paramString The parameter string
     * @return A Workspace or null
     */
    static Workspace getWorkspace(String paramString) {
        Workspace w = null
        // Look in WorkspaceFactories first
        for (WorkspaceFactory workspaceFactory : WorkspaceFactories.list()) {
            w = workspaceFactory.create(paramString)
            if (w != null) {
                break
            }
        }
        // Then try unregistered GeoTools DataStores
        if (w == null) {
            w = getWorkspace(getParameters(paramString))
        }
        if (w == null) {
            throw new IllegalArgumentException("Unknown Workspace parameter string: ${paramString}")
        }
        w
    }

    /**
     * Get a connection map from a connection string
     * @param str The connection string
     * @return A connection map
     */
    static Map getParameters(String str) {
        Map params = [:]
        str.split("[ ]+(?=([^\']*\'[^\']*\')*[^\']*\$)").each {
            def parts = it.split("=")
            if (parts.size() > 1) {
                def key = parts[0].trim()
                if ((key.startsWith("'") && key.endsWith("'")) ||
                        (key.startsWith("\"") && key.endsWith("\""))) {
                    key = key.substring(1, key.length() - 1)
                }
                def value = parts[1].trim()
                if ((value.startsWith("'") && value.endsWith("'")) ||
                        (value.startsWith("\"") && value.endsWith("\""))) {
                    value = value.substring(1, value.length() - 1)
                }
                if (key.equalsIgnoreCase("url")) {
                    value = new File(value).absoluteFile.toURL()
                }
                params.put(key, value)
            }
        }
        params
    }

    /**
     * Get a Workspace from a connection parameter Map
     * @param params The Map of connection parameters
     * @return A Workspace or null
     */
    static Workspace getWorkspace(Map params) {
        Workspace w = null
        // Look in WorkspaceFactories first
        for (WorkspaceFactory workspaceFactory : WorkspaceFactories.list()) {
            w = workspaceFactory.create(params)
            if (w != null) {
                break
            }
        }
        // Then try unregistered GeoTools DataStores
        if (!w) {
            w = wrap(DataStoreFinder.getDataStore(params))
        }
        w
    }

    /**
     * Use a Workspace within the Closure.  The Workspace will
     * be closed.
     * @param paramString The param string
     * @param closure The Closure that gets the opened Workspace
     */
    static void withWorkspace(String paramString, Closure closure) {
        withWorkspace(Workspace.getWorkspace(paramString), closure)
    }

    /**
     * Use a Workspace within the Closure.  The Workspace will
     * be closed.
     * @param params The parameter Map
     * @param closure The Closure that gets the opened Workspace
     */
    static void withWorkspace(Map params, Closure closure) {
        withWorkspace(Workspace.getWorkspace(params), closure)
    }

    /**
     * Use a Workspace within the Closure.  The Workspace will
     * be closed.
     * @param workspace The Workspace
     * @param closure The Closure that gets the Workspace
     */
    static void withWorkspace(Workspace workspace, Closure closure) {
        try {
            closure.call(workspace)
        } finally {
            workspace.close()
        }
    }

    /**
     * Wrap a GeoTools DataStore in the appropriate GeoScript Workspace
     * @param ds The GeoTools DataStore
     * @return A GeoScript Workspace or null
     */
    static Workspace wrap(DataStore ds) {
        if (ds == null) {
            null
        } else {
            Workspace w
            for (WorkspaceFactory workspaceFactory : WorkspaceFactories.list()) {
                w = workspaceFactory.create(ds)
                if (w != null) {
                    break
                }
            }
            if (!w) {
                w = new Workspace(ds)
            }
            w
        }
    }

}
