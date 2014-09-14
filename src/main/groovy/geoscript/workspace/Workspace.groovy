package geoscript.workspace

import geoscript.feature.Feature
import geoscript.feature.Field
import geoscript.feature.Schema
import geoscript.layer.Cursor
import geoscript.layer.Layer
import org.geotools.data.DataStore
import org.geotools.data.DataUtilities
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
        if (str.indexOf("=") == -1 || str.toLowerCase().startsWith("http")) {
            // Directory (Shapefile)
            if (str.endsWith(".shp")) {
                if (str.startsWith("file:/")) {
                    params.put("url", DataUtilities.fileToURL(DataUtilities.urlToFile(new URL(str)).getAbsoluteFile().getParentFile()))
                } else {
                    params.put("url", DataUtilities.fileToURL(new File(str).getAbsoluteFile().getParentFile()))
                }
            }
            // Properties
            else if (str.endsWith(".properties")) {
                String dir
                File f = new File(str)
                if (f.exists()) {
                    dir = f.absoluteFile.parentFile.absolutePath
                } else {
                    dir = f.absolutePath.substring(0,f.absolutePath.lastIndexOf(File.separator))
                }
                params.put("directory", dir)
            }
            // GeoPackage
            else if (str.endsWith(".gpkg")) {
                params.put("dbtype", "geopkg")
                params.put("database", new File(str).absolutePath)
            }
            // SpatiaLite
            else if (str.endsWith(".sqlite") || str.endsWith(".spatialite")) {
                params.put("dbtype", "spatialite")
                params.put("database", new File(str).absolutePath)
            }
            // H2
            else if (str.endsWith(".db")) {
                params.put("dbtype", "h2")
                params.put("database", new File(str).absolutePath)
            }
            // WFS
            else if (str.toLowerCase().startsWith("http") && str.toLowerCase().contains("service=wfs")
                    && str.toLowerCase().contains("request=getcapabilities")) {
                params.put("WFSDataStoreFactory:GET_CAPABILITIES_URL", str)
            }
            // Directory
            else if (new File(str).isDirectory()) {
                params.put("url", new File(str).toURL())
            } else {
                throw new IllegalArgumentException("Unknown Workspace parameter string: ${str}")
            }
        }
        else {
            str.split("[ ]+(?=([^\']*\'[^\']*\')*[^\']*\$)").each {
                def parts = it.split("=")
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
        return params
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
}
