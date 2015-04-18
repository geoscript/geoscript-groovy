package geoscript.layer

import geoscript.feature.Schema
import geoscript.filter.Expression
import geoscript.geom.*
import geoscript.index.Quadtree
import geoscript.index.STRtree
import geoscript.index.SpatialIndex
import geoscript.layer.io.GeobufWriter
import geoscript.proj.Projection
import geoscript.feature.*
import geoscript.workspace.*
import geoscript.filter.Filter
import geoscript.style.Style
import geoscript.style.Symbolizer
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil
import org.geotools.data.FeatureSource
import org.geotools.data.Query
import org.geotools.data.Transaction
import org.geotools.data.FeatureStore
import org.geotools.data.DefaultTransaction
import org.geotools.data.transform.Definition
import org.geotools.data.transform.TransformFactory
import org.geotools.factory.Hints
import org.geotools.feature.DefaultFeatureCollection
import org.geotools.feature.FeatureCollection
import org.geotools.feature.FeatureIterator
import org.geotools.process.vector.VectorToRasterProcess
import org.opengis.filter.sort.SortOrder
import org.opengis.feature.simple.SimpleFeatureType
import org.opengis.feature.simple.SimpleFeature
import org.opengis.referencing.crs.CoordinateReferenceSystem
import org.opengis.feature.type.AttributeDescriptor
import com.vividsolutions.jts.geom.Envelope
import org.opengis.filter.FilterFactory2
import geoscript.layer.io.GmlWriter
import geoscript.layer.io.GeoJSONWriter
import org.geotools.data.collection.ListFeatureCollection

import java.awt.Dimension

/**
 * A Layer is a source of spatial data that contains a collection of Features.  Most often Layers are accessed from
 * a {@link geoscript.workspace.Workspace Workspace} but you can create an in memory Layer by simply passing a name
 * and a {@link geoscript.feature.Schema Schema}:
 * <p><blockquote><pre>
 * Schema schema = new Schema("facilities", [new Field("geom","Point", "EPSG:2927"), new Field("name","string"), new Field("address","string")])
 * Layer layer = new Layer("facilities", schema)
 * </pre></blockquote></p>
 * If, all you want to store in a Layer is {@link geoscript.geom.Geometry Geometry}, you can just pass a layer name:
 * <p><blockquote><pre>
 * Layer layer = new Layer("points")
 * layer.add([new Point(0,0)])
 * layer.add([new Point(1,1)])
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class Layer {

    /**
     * The name
     */
    String name

    /**
     * The Workspace
     */
    Workspace workspace

    /**
     * The GeoTools FeatureSource
     */
    FeatureSource<SimpleFeatureType, SimpleFeature> fs

    /**
     * The Schema
     */
    Schema schema

    /**
     * The Projection
     */
    private Projection projection

    /**
     * The Style
     */
    Style style

    /**
     * The internal counter for new Layer names
     */
    private static int id = 0

    /**
     * The FilterFactory2 for creating Filters
     */
    protected final static FilterFactory2 filterFactory = org.geotools.factory.CommonFactoryFinder.getFilterFactory2(org.geotools.factory.GeoTools.getDefaultHints())

    /**
     * Create a new Layer from a GeoTools FeatureSource
     * @param fs The GeoTools FeatureSource
     */
    Layer(FeatureSource<SimpleFeatureType, SimpleFeature> fs) {
        this.name = fs.name
        this.workspace = new Workspace(fs.dataStore)
        this.fs = fs
        this.schema = new Schema(fs.schema)
        if (fs.schema.coordinateReferenceSystem != null) {
            this.projection = new Projection(fs.schema.coordinateReferenceSystem)
        }
        setDefaultSymbolizer(this.schema.geom?.typ)
    }

    /**
     * Create a new Layer from an existing Layer
     * @param layer Another Layer
     */
    Layer(Layer layer) {
        this.name = layer.name
        this.workspace = layer.workspace
        this.fs = layer.fs
        this.schema = layer.schema
        if (fs.schema.coordinateReferenceSystem != null) {
            this.projection = new Projection(fs.schema.coordinateReferenceSystem)
        }
        if (layer.style) {
            this.style = layer.style
        } else {
            setDefaultSymbolizer(this.schema.geom?.typ)
        }
    }

    /**
     * Create a new Layer from a name, Workspace, FeatureSource, and Schema
     * @param name The Layer's name
     * @param workspace The Workspace
     * @param fs The GeoTools FeatureSource
     * @param schema The GeoScript Schema
     */
    Layer(String name, Workspace workspace, FeatureSource<SimpleFeatureType, SimpleFeature> fs, Schema schema) {
        this.name = name
        this.workspace = workspace
        this.fs = fs
        this.schema = schema
        if (fs.schema.coordinateReferenceSystem != null) {
            this.projection = new Projection(fs.schema.coordinateReferenceSystem)
        }
        setDefaultSymbolizer(this.schema.geom?.typ)
    }

    /**
     * Create a new Layer from a Workspace and FeatureSource
     * @param workspace The Workspace
     * @param fs The GeoTools FeatureSource
     */
    Layer(Workspace workspace, FeatureSource<SimpleFeatureType, SimpleFeature> fs) {
        this(fs.name.localPart, workspace, fs, new Schema(fs.schema))
    }

    /**
     * Create a new Layer from a name and Workspace
     * @param name The Layer's name
     * @param workspace The Workspace
     */
    Layer(String name, Workspace workspace) {
        Layer layer = workspace.get(name)
        this.name = name
        this.workspace = workspace
        this.fs = layer.fs
        this.schema = new Schema(layer.fs.schema)
        if (fs.schema.coordinateReferenceSystem != null) {
            this.projection = new Projection(fs.schema.coordinateReferenceSystem)
        }
        setDefaultSymbolizer(this.schema.geom?.typ)
    }

    /**
     * Create a new Layer with a name in the Memory Workspace
     * @param name The Layer's name
     * @param schema The Schema
     */
    Layer(String name, Schema schema) {
        this.workspace = new Memory()
        Layer layer = this.workspace.create(schema)
        this.name = name
        this.fs = layer.fs
        this.schema = new Schema(layer.fs.schema)
        if (fs.schema.coordinateReferenceSystem != null) {
            this.projection = new Projection(fs.schema.coordinateReferenceSystem)
        }
        setDefaultSymbolizer(this.schema.geom?.typ)
    }

    /**
     * Create a new Layer with a default name, Schema in the Memory Workspace
     */
    Layer() {
        this(newname())
    }

    /**
     * Create a new Layer with the given name, a simple Schema with just a Geometry Field in the Memory Workspace
     */
    Layer(String name) {
        this(name, new Schema(name, [new Field("geom","Geometry")]))
    }

    /**
     * Create a new Layer from a GeoTools FeatureCollection.
     * @param fc The GeoTools FeatureCollection
     */
    Layer(FeatureCollection fc) {
        this(fc.schema.name.localPart, fc)
    }

    /**
     * Create a new Layer from a GeoTools FeatureCollection
     * @param name The name of the new Layer
     * @param fc The GeoTools FeatureCollection
     */
    Layer(String name, FeatureCollection fc) {
        this(createLayerFromFeatureCollection(name, fc))
    }

    /**
     * Create a Layer from a name and FeatureCollection
     * @param name The name of the new Layer
     * @param fc The FeatureCollection
     * @return A new Layer
     */
    private static Layer createLayerFromFeatureCollection(String name, FeatureCollection fc) {
        Schema s = new Schema(fc.schema)
        Schema schema =  new Schema(name, s.fields, fc.schema.name.namespaceURI ?: "http://geoscript.org/feature")
        Layer layer = new Memory().create(schema)
        layer.add(fc)
        layer
    }

    /**
     * Set the default Symbolizer based on the geometry type
     * @param geometryType The geometry type
     * @return A default Symbolizer
     */
    protected void setDefaultSymbolizer(String geometryType) {
        if(!this.style) {
            if (this instanceof Shapefile || this.format.equalsIgnoreCase("Directory")
                    || this.fs instanceof org.geotools.data.directory.DirectoryFeatureStore) {
                def dir
                def fileName
                if (this instanceof Shapefile) {
                    def shp = this as Shapefile
                    fileName = shp.file.name.substring(0, shp.file.name.lastIndexOf(".shp"))
                    dir = shp.file.parentFile
                } else {
                    dir = this.workspace.ds.info.source.path
                    fileName = this.name
                }
                // Check for SLD
                def f = new File(dir,"${fileName}.sld")
                if (f.exists()) {
                    try {
                        def reader = new geoscript.style.io.SLDReader()
                        this.style = reader.read(f)
                    } catch(Exception ignore) {
                    }
                }
                // Check for CSS but only if the style is still falsey
                if (!this.style) {
                    f = new File(dir,"${fileName}.css")
                    if (f.exists()) {
                        try {
                            def reader = new geoscript.style.io.CSSReader()
                            this.style = reader.read(f)
                        } catch (Exception ignore) {
                        }
                    }
                }
            }
            // If the Layer isn't a Shapefile or if the Shapefile didn't
            // have a companion SLD or CSS file
            if (!this.style) {
                this.style = Symbolizer.getDefault(geometryType ?: "geometry")
            }
        }
    }

    /**
     * Get the Workspace format
     * @return The format identifying the workspace
     */
    String getFormat() {
        workspace.format
    }

    /**
     * Get the Layer's name
     * @return The Layer's name
     */
    String getName() {
        fs.name.localPart
    }

    /**
     * Get the Layer's Projection
     * @return The Layer's Projection 
     */
    Projection getProj() {
        if (this.projection == null || this.projection.crs == null) {
            CoordinateReferenceSystem crs = fs.schema.coordinateReferenceSystem
            if (crs != null) {
                this.projection = new Projection(crs)
            }
        }
        this.projection
    }

    /**
     * Set the Layer's Projection.
     * @param value The value can either be a Projection or a String
     */
    void setProj(def value) {
        if (value instanceof String) {
            this.projection = new Projection(value as String)
        }
        else if (value instanceof Projection) {
            this.projection = new Projection(value as Projection)
        }
        else if (value instanceof CoordinateReferenceSystem) {
            this.projection = new Projection(value as CoordinateReferenceSystem)
        }
    }

    /**
     * Count the number of Features in the layer
     * @param filer The Filter or Filter String to limit the number of Features counted.  Defaults to null.
     * @return The number of Features in the Layer
     */
    int count(def filter = null) {
        count([filter: filter])
    }

    /**
     * Count the number of Features using named parameters
     * @param options The named parameters
     * <ul>
     *     <li>filter = A Filter or CQL String</li>
     *     <li>params = A Map of parameters based to the query</li>
     * </ul>
     * @return The number of Features in the Layer
     */
    int count(Map options) {
        def filter = options.get("filter")
        def params = options.get("params")
        Filter f = (filter == null) ? Filter.PASS : new Filter(filter)
        Query q = new Query(getName(), f.filter)
        if (params != null) {
            q.hints = new Hints(Hints.VIRTUAL_TABLE_PARAMETERS, params)
        }
        int count = fs.getCount(q)
        if (count == -1) {
            count = 0
            // count manually
            getFeatures(f).each{count++}
        }
        return count
    }

    /**
     * Count the number of Features in the layer
     * @return The number of Features in the Layer
     */
    int getCount() {
        count();
    }

    /**
     * Get the Bounds of the Features in the Layer
     * @param filer The Filter or Filter String to limit the Features used to construct the bounds. Defaults to null.
     * @return The Bounds of the Features in the Layer
     */
    Bounds bounds(def filter = null) {
        bounds([filter: filter])
    }

    /**
     * Get the Bounds of Layer using named parameters
     * @param options The named parameters
     * <ul>
     *     <li>filter = A Filter or a CQL String</li>
     *     <li>params = A Map of parameters to plugin into a query</li>
     * </ul>
     * @return The Bounds
     */
    Bounds bounds(Map options) {
        def filter = options.get("filter")
        Map params = options.get("params")
        Filter f = (filter == null) ? Filter.PASS : new Filter(filter)
        Query q = new Query(getName(), f.filter)
        if (params != null) {
            q.hints = new Hints(Hints.VIRTUAL_TABLE_PARAMETERS, params)
        }
        Envelope e = fs.getBounds(q)
        if (!e) {
            e = fs.getFeatures(q).bounds
        }
        return new Bounds(e)
    }

    /**
     * Get the Bounds of the Features in the Layer
     * @return The Bounds of the Features in the Layer
     */
    Bounds getBounds() {
        bounds()
    }

    /**
     * Call the Closure for each Feature optionally filtered by the Filter
     * @param filter The Filter which is optional
     * @param closure A Closure which takes a Feature
     */
    void eachFeature(def filter = null, Closure closure) {
        eachFeature([filter: filter], closure)
    }

    /**
     * Call the Closure for each Feature.
     * @param options The named parameters
     * * <ul>
     *  <li>filter = The Filter or Filter String to limit the Features. Defaults to null.</li>
     *  <li>sort = A List of Lists that define the sort order [[Field or Field name, "ASC" or "DESC"],...]. Not all Layers
     *  support sorting!</li>
     *  <li>max= The maximum number of Features to include in the Cursor</li>
     *  <li>start = The index of the record to start the cursor at.  Together with maxFeatures this simulates paging.
     *      Not all Layers support the start index and paging!
     *  </li>
     *  <li>fields = A List of Fields or Field names to include.  Used to select only a subset of Fields.</li>
     *  <li>params = A Map of parameters to plug into the query.</li>
     * </ul>
     * @param closure The Closure which takes a Feature
     */
    void eachFeature(Map options, Closure closure) {
        Cursor c = getCursor(options)
        try {
            while(c.hasNext()) {
                Feature f = c.next()
                closure.call(f)
            }
        } finally {
            c.close()
        }
    }

    /**
     * Collect values from the Features of a Layer
     * @param filter The Filter which is optional
     * @param closure A Closure which takes a Feature and returns a value
     */
    List collectFromFeature(def filter = null, Closure closure) {
        collectFromFeature([filter: filter], closure)
    }

    /**
     * Collect values from the Features of a Layer
     * @param options The named parameters
     * * <ul>
     *  <li>filter = The Filter or Filter String to limit the Features. Defaults to null.</li>
     *  <li>sort = A List of Lists that define the sort order [[Field or Field name, "ASC" or "DESC"],...]. Not all Layers
     *  support sorting!</li>
     *  <li>max= The maximum number of Features to include in the Cursor</li>
     *  <li>start = The index of the record to start the cursor at.  Together with maxFeatures this simulates paging.
     *      Not all Layers support the start index and paging!</li>
     *  <li>fields = A List of Fields or Field names to include.  Used to select only a subset of Fields.</li>
     *  <li>params = A Map of parameters to plug into the query.</li>
     * </ul>
     * @param closure The Closure which takes a Feature and returns a value
     * @return
     */
    List collectFromFeature(Map options, Closure closure) {
        List results = []
        Cursor c = getCursor(options)
        try {
            while(c.hasNext()) {
                Feature f = c.next()
                results.add(closure.call(f))
            }
        } finally {
            c.close()
        }
        results
    }

    /**
     * Get a List of Features
     * @param filer The Filter or Filter String to limit the Features used to construct the bounds. Defaults to null.
     * @param transform The Closure used to modify the Features.  Defaults to null.
     * @param sort A List of Lists that define the sort order [[Field or Field name, "ASC" or "DESC"],...]. Not all Layers
     * support sorting!
     * @param params A Map of parameters to plug into the query.
     * @return A List of Features
     */
    List<Feature> getFeatures(def filter = null, Closure transform = null, List sort = null, Map params = null) {
        getFeatures([filter:filter, transform: transform, sort:sort, params:params])
    }

    /**
     * Get a List of Features
     * @param options The named parameters
     * <ul>
     *  <li>transform = The Closure used to modify the Features takes a Feature and returns a Feature</li>
     *  <li>filter = The Filter or Filter String to limit the Features. Defaults to null.</li>
     *  <li>sort = A List of Lists that define the sort order [[Field or Field name, "ASC" or "DESC"],...]. Not all Layers
     *  support sorting!</li>
     *  <li>max= The maximum number of Features to include in the Cursor</li>
     *  <li>start = The index of the record to start the cursor at.  Together with maxFeatures this simulates paging.
     * Not all Layers support the start index and paging!</li>
     *  <li>fields = A List of Fields or Field names to include.  Used to select only a subset of Fields.</li>
     *  <li>params = A Map of parameters to plug into the query.</li>
     * </ul>
     * @return A List of Features
     */
    List<Feature> getFeatures(Map options) {
        Closure transform = options.get("transform")
        List<Feature> features = []
        Cursor c = getCursor(options)
        while(c.hasNext()) {
            Feature f = c.next()
            def result = null
            if (transform != null) {
                result = transform.call(f)
            }
            if (result != null && result instanceof Feature) {
                features.add(result)
            }
            else {
                features.add(f)
            }
        }
        c.close()
        return features
    }

    /**
     * Get a Cursor over the Features of the Layer using named parameters.
     * @param options.  The Map of named parameters can include:
     * <ul>
     *  <li>filter = The Filter or Filter String to limit the Features. Defaults to null.</li>
     *  <li>sort = A List of Lists that define the sort order [[Field or Field name, "ASC" or "DESC"],...]. Not all Layers
     *  support sorting!</li>
     *  <li>max= The maximum number of Features to include in the Cursor</li>
     *  <li>start = The index of the record to start the cursor at.  Together with maxFeatures this simulates paging.
     * Not all Layers support the start index and paging!</li>
     *  <li>fields = A List of Fields or Field names to include.  Used to select only a subset of Fields.</li>
     *  <li>params = A Map of parameters to plug into the query.</li>
     * </ul>
     * @return A Cursor
     */
    Cursor getCursor(Map options) {
        getCursor(options.get("filter", null), options.get("sort", null),
            options.get("max",-1), options.get("start", -1),options.get("fields", null),
            options.get("sourceProj", null), options.get("destProj", null), options.get("params", null))
    }

    /**
     * Get a Cursor over the Features of the Layer.
     * @param filter The Filter or Filter String to limit the Features. Defaults to null.
     * @param sort A List of Lists [[Field or Field name, "ASC" or "DESC"],...] or a List of Strings ["name DESC", "price ASC"]
     * that define the sort order. Not all Layers support sorting!
     * @param max The maximum number of Features to include in the Cursor
     * @param start The zero based index of the record to start the cursor at.  Together with maxFeatures this simulates paging.
     * Not all Layers support the start index and paging!
     * @param fields A List of Fields or Field names to include.  Used to select only a subset of Fields.
     * @param params A Map of parameters to plug into the query.
     * @return A Cursor
     */
    Cursor getCursor(def filter = null, List sort = null, int max = -1, int start = -1, List fields = null, def sourceProj = null, def destProj = null, def params = null) {
        Map cursorOptions = [:]
        Filter f = (filter == null) ? Filter.PASS : new Filter(filter)
        Query q = new Query(getName(), f.filter)
        if (params != null) {
            q.hints = new Hints(Hints.VIRTUAL_TABLE_PARAMETERS, params)
        }
        if (fields != null && fields.size() > 0) {
            q.propertyNames = ((fields[0] instanceof Field) ? fields*.name : fields) as String[]
        }
        if (max > -1 && !(fs.dataStore instanceof org.geotools.data.memory.MemoryDataStore)) {
            q.maxFeatures = max
        }
        if (start > -1) {
            if (fs.queryCapabilities.offsetSupported && !(fs.dataStore instanceof org.geotools.data.memory.MemoryDataStore)) {
                q.startIndex = start
            } else {
                cursorOptions.start = start
                cursorOptions.max = max
                // Reset max features because the we will
                // be using the MaxFeaturesIterator in Cursor
                // and it needs all of the Features to simulate paging
                q.maxFeatures = Integer.MAX_VALUE
            }
        }
        // Set source Projection
        if (getProj()) {
            q.coordinateSystem = getProj().crs
        } else if (sourceProj) {
            q.coordinateSystem = new Projection(sourceProj).crs
        }
        // Set destination Projection
        if (destProj) {
            if (getProj()) {
                q.coordinateSystemReproject = new Projection(destProj).crs
            } else if (sourceProj) {
                cursorOptions["sourceProj"] = new Projection(sourceProj)
                cursorOptions["destProj"] = new Projection(destProj)
            }
        }
        // Add sorting to the Query
        if (sort != null && sort.size() > 0) {
            // Create a list of SortBy's
            List sortBy = sort.collect{s ->
                String sortName
                String sortDirection
                // ["name","ASC"] or ["name"] or [nameField,"ASC"]
                if (s instanceof List) {
                    sortName = s[0] instanceof Field ? s[0].name : s[0]
                    sortDirection = s.size() > 1 ? s[1] : "ASC"
                }
                // "name ASC", "name"
                else {
                    List values = s.toString().split(" ") as List
                    if (values.last().toUpperCase() in ["ASC","DESC"]) {
                        sortDirection = values.pop().toUpperCase()
                        sortName = values.join(" ")
                    } else {
                        sortName = values.join(" ")
                        sortDirection = "ASC"
                    }
                }
                filterFactory.sort(sortName, SortOrder.valueOf(sortDirection))
            }
            // Turn it into an array
            def sortByArray = sortBy as org.geotools.filter.SortByImpl[]
            // Only apply it if the FeatureSource supports it.
            // Don't throw an Exception
            if (fs.queryCapabilities.supportsSorting(sortByArray)) {
                q.sortBy = sortByArray
            } else {
                cursorOptions.sort = sortByArray
            }
        }
        def col = fs.getFeatures(q)
        return new Cursor(cursorOptions, col, this)
    }

    /**
     * Get the first Feature that matches
     * @param options Named parameters
     * <ul>
     *     <li>filter = A geoscript.filter.Filter or CQL String</li>
     *     <li>sort = A String (FIELD ASC | DESC) or List of Strings</li>
     *     <li>params = A Map of parameters to plugin into the query</li>
     * </ul>
     * @return
     */
    Feature first(Map options = [:]) {
        def filter = options.get("filter")
        def sort = options.get("sort")
        if (sort != null && !(sort instanceof List)) {
            sort = [sort]
        }
        Map params = options.get("params")
        Cursor c = getCursor(filter: filter, sort: sort, params: params)
        Feature f = null
        if (c.hasNext()) {
            f = c.next()
            c.close()
        }
        f
    }

    /**
     * Get a Writer for this Layer
     * @param options The named parameters
     * <ul>
     *   <li>batch: The number of features to write at one time (defaults to 1000)</li>
     *   <li>transaction: The type of transaction: null, auto or autocommit, or default.  The default value
     *      depends on the type of Layer.
     *   </li>
     * </ul>
     * @return A Writer
     */
    Writer getWriter(Map options = [:]) {
        new Writer(options, this)
    }

    /**
     * Add Features to a Writer within a Closure that takes a Writer ready to adding Features.
     * @param options The named parameters
     * <ul>
     *   <li>batch: The number of features to write at one time (defaults to 1000)</li>
     *   <li>transaction: The type of transaction: null, auto or autocommit, or default.  The default value
     *      depends on the type of Layer.
     *   </li>
     * </ul>
     * @param c A Closure which takes one parameter, a Writer
     */
    void withWriter(Map options = [:], Closure c) {
        Writer w = getWriter(options)
        try {
            c.call(w)
        } finally {
            w.close()
        }
    }

    /**
     * Delete Features from the Layer
     * @param filer The Filter or Filter String to limit the Features to delete. Defaults to null.
     */
    void delete(def filter = null) {
        Filter f = (filter == null) ? Filter.PASS : new Filter(filter)
        fs.removeFeatures(f.filter)
    }

    /**
     * Update the values of a Field
     * @param fld The Field whose values will be updated
     * @param value Either a static value, a Closure that takes
     * a Feature and return an Object, an Expression, or a Groovy Script (if isScript is true)
     * @param filter The Filter to limit the Features that will be updated
     * @param isScript A flag for whether the value is a Groovy script or not (defaults to false).
     * If the value is a script, it can access the Feature as variable f and the counter as
     * variable c.
     */
    void update(Field fld, def value, def filter = null, boolean isScript = false) {
        Filter f = (filter == null) ? Filter.PASS : new Filter(filter)
        Transaction t = new DefaultTransaction("calculateTransaction")
        try {
            FeatureStore<SimpleFeatureType, SimpleFeature> store = (FeatureStore)fs
            store.transaction = t
            AttributeDescriptor ad = schema.featureType.getDescriptor(fld.name)
            if (value instanceof Closure) {
                Cursor c = getCursor(f)
                while(c.hasNext()) {
                    Feature feature = c.next()
                    def idFilter = filterFactory.id(java.util.Collections.singleton(feature.f.identifier))
                    store.modifyFeatures(ad, value.call(feature), idFilter)
                }
                c.close()
            }
            else if (value instanceof Expression) {
                Expression expression = value as Expression
                Cursor c = getCursor(f)
                while(c.hasNext()) {
                    Feature feature = c.next()
                    def idFilter = filterFactory.id(java.util.Collections.singleton(feature.f.identifier))
                    store.modifyFeatures(ad, expression.evaluate(feature), idFilter)
                }
                c.close()
            }
            else if (isScript) {
                int counter = 0
                Cursor c = getCursor(f)
                while(c.hasNext()) {
                    Feature feature = c.next()
                    def idFilter = filterFactory.id(java.util.Collections.singleton(feature.f.identifier))
                    Binding binding = new Binding()
                    binding.setVariable("f", feature)
                    binding.setVariable("c", counter)
                    GroovyShell shell = new GroovyShell(binding)
                    store.modifyFeatures(ad.name, shell.evaluate(value), idFilter)
                    counter++
                }
                c.close()
            }
            else {
                store.modifyFeatures(ad, value, f.filter)
            }
            
            t.commit()
        }
        catch (Exception e) {
            e.printStackTrace()
            t.rollback()
        }
        finally {
            t.close()
            fs.transaction = Transaction.AUTO_COMMIT
        }
    }

    /**
     * Add a Feature to the Layer
     * @param o The Feature, the List of Features, or a List/Map of values
     */
    void add(def o) {
        // If it is a List of Features, then add it inside of a Transaction
        if (o instanceof List && o.size() > 0 && (o.get(0) instanceof Feature || o.get(0) instanceof java.util.Map)) {
            withWriter {Writer w ->
                o.each{f->
                    if (f instanceof java.util.Map) {
                        f = this.schema.feature(f as java.util.Map)
                    }
                    if (f.schema == null) {
                        f.schema = schema
                    } else if (f.schema != this.schema) {
                        f = this.schema.feature(f.attributes)
                    }
                    w.add(f)
                }
            }
        }
        // Else if it is a FeatureCollection
        else if (o instanceof FeatureCollection || o instanceof Cursor) {
            withWriter{Writer w ->
                int chunk = 1000
                Cursor c = o instanceof FeatureCollection ? new Cursor(o) : o as Cursor
                while(true) {
                    def features = readFeatures(c, this.schema, chunk)
                    if (features.isEmpty()) break
                    new Cursor(features).each{Feature f ->
                        w.add(f)
                    }
                    if (features.size() < chunk) break
                }
            }
        }
        // Otherwise its a Feature or a List of values
        else {
            Feature f
            if (o instanceof Feature) {
                f = o
                if (f.schema == null) {
                    f.schema = this.schema
                } else if (f.schema != this.schema) {
                    f = this.schema.feature(o.attributes)
                }
            }
            else {
                f = this.schema.feature(o)
            }
            FeatureCollection fc = new DefaultFeatureCollection()
            fc.add(f.f)
            fs.addFeatures(fc)
        }
    }

    /**
     * Add a Feature to the Layer
     * @param o The Feature or List/Map of values
     */
    void plus(def o) {
        add(o)
    }

    /**
     * A Map of modified Features by ID
     */
    private Map modifiedFeatures

    /**
     * Add the Feature to a List of modified Features
     * @param feature The modified Feature
     * @param name The modified field name.
     */
    void queueModified(Feature feature, String name) {
        if (!modifiedFeatures) {
            modifiedFeatures = new HashMap()
        }
        String id = feature.id
        if (!modifiedFeatures.containsKey(id)) {
            modifiedFeatures[id] = [names: []]
        }
        modifiedFeatures[id].feature = feature
        modifiedFeatures[id].names.add(name)
    }

    /**
     * Update all modified Features whose values where changed with the Feature.set(field,value) method.
     */
    void update() {
        if (modifiedFeatures != null && !modifiedFeatures.isEmpty()) {
            def idFilter = filterFactory.id(*modifiedFeatures.keySet().collect{id-> filterFactory.featureId(id)})
            def results = fs.dataStore.getFeatureWriter(name, idFilter, Transaction.AUTO_COMMIT)
            try {
                while(results.hasNext()) {
                    def feat = results.next()
                    String id = feat.identifier
                    modifiedFeatures[id].names.each { name ->
                        feat.setAttribute(name, modifiedFeatures[id].feature.f.getAttribute(name))
                    }
                    results.write()
                    modifiedFeatures.remove(modifiedFeatures[id])
                }
            }
            finally {
                results.close()
            }
            modifiedFeatures.clear()
        }
    }

    /**
     * Reproject the Layer
     * @param p The Projection
     * @param newName The new name (defaults to a default new name)
     * @param chunk The number of Features to reproject in one batch
     * @param sourceProjection The optional default source Projection if the Layer doesn't have a Projection defined
     * @return The reprojected Layer
     */
    Layer reproject(Projection p, String newName = newname(), int chunk=1000, Projection sourceProjection = new Projection("EPSG:4326")) {
        Schema s = schema.reproject(p, newName)
        Layer l = workspace.create(s)
        reproject(l, chunk, sourceProjection)
    }

    /**
     * Reproject the Layer to another Layer in the given Workspace
     * @param p The Projection
     * @param outputWorkspace The output Workspace
     * @param newName The name of the new Layer
     * @param chunk The number of Features to reproject in one batch
     * @param sourceProjection The optional default source Projection if the Layer doesn't have a Projection defined
     * @return The reprojected Layer
     */
    Layer reproject(Projection p, Workspace outputWorkspace, String newName, int chunk=1000, Projection sourceProjection = new Projection("EPSG:4326")) {
        Schema s = schema.reproject(p, newName)
        Layer l = outputWorkspace.create(s)
        reproject(l, chunk, sourceProjection)
    }

    /**
     * Reproject this Layer to another Layer that already exists.
     * @param projectedLayer The already created projected Layer
     * @param chunk The number of Features to reproject in one batch
     * @param sourceProjection The optional default source Projection if the Layer doesn't have a Projection defined
     * @return The projected Layer
     */
    Layer reproject(Layer projectedLayer, int chunk = 1000, Projection sourceProjection = new Projection("EPSG:4326")) {
        Cursor c = this.getCursor(sourceProj: sourceProjection, destProj: projectedLayer.proj)
        projectedLayer.withWriter{ w ->
            while(true) {
                def features = readFeatures(c, projectedLayer.schema, chunk)
                if (features.isEmpty()) break
                new Cursor(features).each{f ->
                    w.add(f)
                }
                if (features.size() < chunk) break
            }
        }
        projectedLayer
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
     * Filer the Layer.
     * @param filter A Filter or Filter String used to limit the number of Features returned in the new Layer
     * @param newName The name of the new Layer (defaults to a default new name)
     * @return A new Layer in the same workspace
     */
    Layer filter(def filter = null, String newName = newname()) {
        Filter f = (filter == null) ? Filter.PASS : new Filter(filter)
        Schema s = new Schema(newName, this.schema.fields, this.schema.uri)
        Layer l = this.workspace.create(s)
        Query q = new Query(getName(), f.filter)
        FeatureCollection fc = this.fs.getFeatures(q)
        FeatureIterator i = fc.features()
        while(i.hasNext()) {
            Feature feature = new Feature(i.next())
            l.add(feature)
        }
        i.close()
        l
    }

    /**
     * Calculates the minimum and maximum values for an attribute of the Layer.
     * @param field The Field
     * @param low The low/minimum value
     * @param high The high/maximum value
     * @return A Map with mininimum (min) and maximum (max) values
     */
    Map minmax(def field, def low = null, def high = null) {
        String attr = field instanceof Field ? field.name : field
        List filters = []
        if (low != null) {
            filters += "${attr} >= ${low}"
        }
        if (high != null) {
            filters += "${attr} <= ${high}"
        }
        Filter filter = filters.size() > 0 ? new Filter(filters.join(" AND ")) : Filter.PASS
        def query = new Query(this.name)
        query.filter = filter.filter
        def min = null
        def max = null
        def fit = fs.getFeatures(query).features()
        try {
            while (fit.hasNext()) {
                def f = fit.next()
                def val = f.getAttribute(attr)
                min = (min == null || val < min) ? val : min
                max = (max == null || val > max) ? val : max
            }
        } finally {
            fit.close()
        }
        [min: min, max:max]
    }

    /**
     * Calculate a histogram of values for an attribute of the Layer.
     * @param field The Field or field name
     * @param classes The number of classes
     * @return A List of Lists for each class
     */
    List histogram(def field, int classes = 10) {

        // Calculate the high and low values
        def minMax = minmax(field)
        double low = minMax.min
        double high = minMax.max

        // Calculate the range
        double range = high - low
        float dx = range/(classes as float)

        // See the list of values with zeros
        List values = [0] * classes

        // Query features from the Layer
        String attr = field instanceof Field ? field.name : field
        Filter filter = new Filter("${attr} BETWEEN ${low} AND ${high}")
        def fit = fs.getFeatures(filter.filter).features()
        try {
            while (fit.hasNext()) {
                def f = fit.next()
                def val = f.getAttribute(attr)
                int index = ((val-low)/((float)range) * classes) as int
                values[Math.min(classes - 1, index)] += 1
            }
        } finally {
            fit.close()
        }
        List keys = (0..classes).collect{x -> round2(low + x * dx)}.sort()
        List vals = (1..keys.size() - 1).collect{i ->
            [keys[i-1], keys[i]]
        }
        if (vals[vals.size() - 1][1] != high) {
            vals[vals.size() - 1][1] = high
        }
        return vals
    }

    protected double round2(double num) {
        Math.round(num * 100) / 100
    }

    /**
     * Create a List of interpolated values for a Field
     * @param field The Field of Field name
     * @param classes The number of classes
     * @param method The interpolation method: linear, exp(onential), log(arithmic)
     * @return A List of values
     */
    List interpolate(def field, int classes = 10, String method="linear") {

        // Calculate the high and low values
        def minMax = minmax(field)
        double min = minMax.min
        double max = minMax.max
        double delta = max - min

        Closure fx
        if (method.equalsIgnoreCase("linear")) {
            fx = {x -> delta * x}
        } else if (method.equalsIgnoreCase("exp") || method.equalsIgnoreCase("exponential")) {
            fx = {x -> Math.exp(x * Math.log(1+delta)) - 1}
        } else if (method.equalsIgnoreCase("log") || method.equalsIgnoreCase("logarithmic")) {
            fx = {x -> delta * Math.log(x+1) / Math.log(2)}
        } else {
            throw new IllegalArgumentException("Interpolation method '${method}' is not supported!")
        }

        Closure fy = {x -> min + fx(x)}
        (0..classes).collect{x ->
            fy(x/(float)classes)
        }
    }

    /**
     * Convert this Layer into a Raster
     * @param field The numeric Field or Field name from which to get values
     * @param gridSize The grid size (width and height)
     * @param bounds The Bounds of the Raster
     * @param name The name of the Raster
     * @return A Raster
     */
    Raster getRaster(def field, List gridSize, Bounds bounds, String rasterName) {
        def dim = new Dimension(gridSize[0] as int, gridSize[1] as int)
        def fld =  filterFactory.property(field instanceof Field ? field.name : field)
        def cov = VectorToRasterProcess.process(fs.features, fld, dim, bounds.env, rasterName, null)
        def tif = new Raster(cov)
        return tif
    }

    /**
     * Transform this Layer into a Layer with a new name using a Map
     * of definitions.
     * @param name The new name
     * @param definitions A Map of Definitions (key=Field name, value=Expression)
     * @return A new transformed Layer
     */
    Layer transform(String name, Map definitions) {
        List defs = []
        definitions.each{k,v ->
            def e = v instanceof Expression ? v as Expression : Expression.fromCQL(v)
            def d = new Definition(k, e.expr)
            defs.add(d)
        }
        def tfs = TransformFactory.transform(this.fs, name, defs)
        new Layer(tfs)
    }

    /**
     * Dissolve the Features of a Layer by a Field.
     * @param options A Map of options that can include outLayer, outWorkspace, idFieldName, and countFieldName
     * @param layer The input Layer
     * @param field The Field
     * @return The output Layer
     */
    Layer dissolve(Map options = [:], Field field) {

        String idFieldName = options.get("idFieldName","id")
        String countFieldName = options.get("countFieldName","count")

        String outLayerName = options.get("outLayer", "${this.name}_${field.name}_dissolve")
        Workspace outWorkspace = options.get("outWorkspace", new Memory())
        Layer outLayer = outWorkspace.create(outLayerName, [
            new Field(idFieldName, "int"),
            new Field(countFieldName, "int"),
            new Field(field.name, field.typ),
            new Field(this.schema.geom)
        ])

        Map<Object, Geometry> values = [:]
        this.eachFeature { f->
            Object value = f.get(field.name)
            if (!values.containsKey(value)) {
                values.put(value, [geom: f.geom, count: 1])
            } else {
                Map v = values.get(value)
                v.geom = v.geom.union(f.geom)
                v.count = v.count + 1
                values.put(value, v)
            }
        }

        String geomFieldName = outLayer.schema.geom.name
        outLayer.withWriter{w ->
            values.eachWithIndex { value, i ->
                Map v = [:]
                v[idFieldName] = i
                v[field.name] = value.key
                v[countFieldName] = value.value.count
                v[geomFieldName] = value.value.geom
                Feature f = outLayer.schema.feature(v)
                w.add(f)
            }
        }

        outLayer
    }

    /**
     * Dissolve intersecting Features of a Layer.
     * @param options A Map of options that can include outLayer, outWorkspace, idFieldName, and countFieldName
     * <ul>
     *     <li>outLayer = The output Layer's name</li>
     *     <li>outWorkspace = The output Workspace</li>
     *     <li>idFieldName = The ID Field name (id)</li>
     *     <li>countyFieldName = The count Field name (count)</li>
     * </ul>
     * @param layer The input Layer
     * @param field The Field
     * @return The output Layer
     */
    Layer dissolve(Map options = [:]) {

        String idFieldName = options.get("idFieldName","id")
        String countFieldName = options.get("countFieldName","count")

        String outLayerName = options.get("outLayer", "${this.name}_dissolve")
        Workspace outWorkspace = options.get("outWorkspace", new Memory())
        Layer outLayer = outWorkspace.create(outLayerName, [
            new Field(idFieldName, "int"),
            new Field(countFieldName, "int"),
            new Field(this.schema.geom)
        ])

        Quadtree index = new Quadtree()
        this.eachFeature { f->
            Geometry unionGeom = f.geom
            int count = 1
            index.query(unionGeom.bounds).each { Map v ->
                Geometry g = v.geom
                if(unionGeom.intersects(g)) {
                    index.remove(g.bounds, v)
                    unionGeom = unionGeom.union(g)
                    count = v.count + 1
                }
            }
            index.insert(unionGeom.bounds, [geom: unionGeom, count: count])
        }

        String geomFieldName = outLayer.schema.geom.name
        int i = 0
        outLayer.withWriter{w ->
            index.queryAll().each { Map v ->
                Map values = [:]
                values[idFieldName] = i
                values[countFieldName] = v.count
                values[geomFieldName] = v.geom
                Feature f = outLayer.schema.feature(v)
                w.add(f)
                i++
            }
        }

        outLayer
    }

    /**
     * Merge this Layer with another Layer to create an output Layer
     * @param options A Map of options that can include outLayer or outWorkspace
     * <ul>
     *     <li>outLayer = The output Layer's name</li>
     *     <li>outWorkspace = The output Workspace</li>
     *     <li>postfixAll = Whether to add a postfix to all field names</li>
     *     <li>includeDuplicates = Whether to include duplicate fields</li>
     *     <li>maxFieldNameLength = The maximum Field name length</li>
     * </ul>
     * @param otherLayer The other layer
     * @return The merged Layer
     */
    Layer merge(Map options = [:], Layer otherLayer) {

        String outLayerName = options.get("outLayer", "${this.name}_${otherLayer.name}")
        Workspace outWorkspace = options.get("outWorkspace", new Memory())
        Map schemaAndFields = this.schema.addSchema(otherLayer.schema, outLayerName,
            postfixAll: options.get("postfixAll",false),
            includeDuplicates: options.get("includeDuplicates",true),
            maxFieldNameLength: outWorkspace instanceof Directory ? 10 : -1)
        Layer outLayer = outWorkspace.create(schemaAndFields.schema)

        this.eachFeature{ f ->
            Map attributes = [:]
            Map fieldMap = schemaAndFields.fields[0]
            f.attributes.each {String k, Object v ->
                if (fieldMap.containsKey(k)) {
                    attributes[fieldMap[k]] = v
                }
            }
            outLayer.add(attributes)
        }

        outLayer.withWriter{w ->
            otherLayer.eachFeature{ f->
                Map attributes = [:]
                Map fieldMap = schemaAndFields.fields[1]
                f.attributes.each {String k, Object v ->
                    // Always set the Geometry
                    if (v instanceof Geometry) {
                        attributes[outLayer.schema.geom.name] = v
                    }
                    // Set value if present in the field map
                    else if (fieldMap.containsKey(k)) {
                        attributes[fieldMap[k]] = v
                    }
                    // Set the value if field is present in output Layer
                    else if (outLayer.schema.has(k)) {
                        attributes[k] = v
                    }
                }
                w.add(outLayer.schema.feature(attributes))
            }
        }

        outLayer
    }

    /**
     * Split this Layer into sub Layers based on values taken from the Field. The new Layers
     * are created in the given Workspace.
     * @param field The Field that contains the values that will be used to split the Layer
     * @param workspace The Workspace where the new Layers will be created
     */
    void split(Field field, Workspace workspace) {

        // Get unique values
        Set values = []
        this.eachFeature{ f ->
            values.add(f.get(field))
        }

        // The quote character for creating a CQL Filter
        String quote = field.typ.equalsIgnoreCase("String") ? "'" : ""

        // For each unique value create a Layer and add Features
        values.each{ v ->
            Layer outLayer = workspace.create("${this.name}_${v.toString().replaceAll(' ','_')}", this.schema.fields)
            Filter filter = new Filter("${field.name} = ${quote}${v}${quote}")
            this.getFeatures(filter).each{ f->
                outLayer.add(f)
            }
        }
    }

    /**
     * Split this Layer into multiple Layers based on the Features from the split Layer.
     * @param splitLayer The split Layer whose Features are used to split this Layer into multiple Layers
     * @param field The Field from the split Layer used to name the new Layers
     * @param workspace The Workspace where the new Layers are created
     */
    void split(Layer splitLayer, Field field, Workspace workspace) {

        // Put all of the Features in the input Layer in a spatial index
        SpatialIndex index = new STRtree()
        this.eachFeature { f ->
            index.insert(f.bounds, f)
        }

        // Iterate through all of the Features in the input Layer
        splitLayer.eachFeature { f ->
            // Create the new output Layer
            Layer outLayer = workspace.create("${this.name}_${f.get(field).toString().replaceAll(' ','_')}", this.schema.fields)
            outLayer.withWriter{w ->
                // See if the Feature intersects with the Bounds of any Feature in the spatial index
                index.query(f.bounds).each { layerFeature ->
                    // Make sure it actually intersects the Geometry of a Feature in the spatial index
                    if (f.geom.intersects(layerFeature.geom)) {
                        // Clip the geometry from the input Layer
                        Geometry intersection = layerFeature.geom.intersection(f.geom)
                        // Create a new Feature and add if to the clipped Layer
                        Map values = layerFeature.attributes
                        values[outLayer.schema.geom.name] = intersection
                        w.add(outLayer.schema.feature(values))
                    }
                }
            }
        }
    }

    /**
     * Buffer all of the Features in the this Layer.
     * @param options The Map of options which can include outWorkspace and outLayer
     * <ul>
     *     <li>outLayer = The output Layer's name</li>
     *     <li>outWorkspace = The output Workspace</li>
     *     <li>quadrantSegments = The number of quadrant segments</li>
     *     <li>capStyle = The end cap style</li>
     *     <li>singleSided = Whether the buffer should be single sided or not</li>
     * </ul>
     * @param distance The buffer distance
     * @return A Layer with the buffered Features
     */
    Layer buffer(Map options = [:], double distance) {
        buffer(options, new Expression(distance))
    }

    /**
     * Buffer all of the Features in the this Layer.
     * @param options The Map of options which can include outWorkspace and outLayer
     * <ul>
     *     <li>outLayer = The output Layer's name</li>
     *     <li>outWorkspace = The output Workspace</li>
     *     <li>quadrantSegments = The number of quadrant segments</li>
     *     <li>capStyle = The end cap style</li>
     *     <li>singleSided = Whether the buffer should be single sided or not</li>
     * </ul>
     * @param distance An Expression that represents the buffer distance (can be a Literal, Function, or Property)
     * @return A Layer with the buffered Features
     */
    Layer buffer(Map options = [:], Expression distance) {

        String outLayerName = options.get("outLayer", "${this.name}_buffer")
        Workspace outWorkspace = options.get("outWorkspace", new Memory())
        Schema schema = this.schema.changeGeometryType("Polygon", outLayerName)
        Layer outLayer = outWorkspace.create(schema)

        int quadrantSegments = options.get("quadrantSegments", 8)
        int capStyle = options.get("capStyle", Geometry.CAP_ROUND)
        boolean singleSided = options.get("singleSided", false)

        outLayer.withWriter{w ->
            this.eachFeature {Feature f ->
                Map values = [:]
                f.attributes.each{k,v ->
                    if (v instanceof geoscript.geom.Geometry) {
                        double d = distance.evaluate(f) as double
                        Geometry b = singleSided ?
                            v.buffer(d, quadrantSegments, capStyle) :
                            v.singleSidedBuffer(d, quadrantSegments, capStyle)
                        values[k] = b
                    } else {
                        values[k] = v
                    }
                }
                w.add(outLayer.schema.feature(values))
            }
        }

        outLayer
    }

    /**
     * Write the Layer as GML to an Outputstream
     * @param out The OutputStream (defaults to System.out)
     */
    void toGML(OutputStream out = System.out) {
        GmlWriter gmlWriter = new GmlWriter()
        gmlWriter.write(this, out)
    }

    /**
     * Write the Layer as GML to a File
     * @param file The File
     */
    void toGMLFile(File file) {
        GmlWriter gmlWriter = new GmlWriter()
        gmlWriter.write(this, file)
    }

    /**
     * Write the Layer as GML to a String
     * @param out A GML String
     */
    String toGMLString() {
        GmlWriter gmlWriter = new GmlWriter()
        gmlWriter.write(this)
    }

    /**
     * Write the Layer as GeoJSON to an OutputStream
     * @param out The OutputStream (defaults to System.out)
     */
    void toJSON(OutputStream out = System.out) {
        GeoJSONWriter geoJSONWriter = new GeoJSONWriter()
        geoJSONWriter.write(this, out)
    }

    /**
     * Write the Layer as GeoJSON to a File
     * @param file The File
     */
    void toJSONFile(File file) {
        GeoJSONWriter geoJSONWriter = new GeoJSONWriter()
        geoJSONWriter.write(this, file)
    }

    /**
     * Write the Layer as GeoJSON to a String
     * @param out A GeoJSON String
     */
    String toJSONString() {
        GeoJSONWriter geoJSONWriter = new GeoJSONWriter()
        geoJSONWriter.write(this)
    }

    /**
     * Write the Layer as Geobuf to an OutputStream
     * @param out The OutputStream (defaults to System.out)
     */
    void toGeobuf(OutputStream out = System.out) {
        GeobufWriter writer = new GeobufWriter()
        writer.write(this, out)
    }

    /**
     * Write the Layer as Geobuf to a File
     * @param file The File
     */
    void toGeobufFile(File file) {
        GeobufWriter writer = new GeobufWriter()
        writer.write(this, file)
    }

    /**
     * Write the Layer as Geobuf to a String
     * @param out A Geobuf Hex String
     */
    String toGeobufString() {
        GeobufWriter writer = new GeobufWriter()
        writer.write(this)
    }

    /**
     * Write the Layer as Geobuf to a byte array
     * @param out A Geobuf byte array
     */
    byte[] toGeobufBytes() {
        GeobufWriter writer = new GeobufWriter()
        writer.writeBytes(this)
    }

    /**
     * Write the Layer as KML to an OutputStream.
     * @param out The OutputStream (defaults to System.out)
     * @param nameClosure A Closure that takes a Feature and returns a value
     * used as the Placemark's name.  Default to the Feature's ID
     * @param descriptionClosure A Closure that takes a Feature and returns a value
     * used as the Placemark's description. Defaults to null which means no description
     * is created
     */
    void toKML(OutputStream out = System.out, Closure nameClosure = {f -> f.id}, Closure descriptionClosure = null) {
        def xml
        def markupBuilder = new StreamingMarkupBuilder()
        def featureWriter = new geoscript.feature.io.KmlWriter()
        xml = markupBuilder.bind { builder ->
            mkp.xmlDeclaration()
            mkp.declareNamespace([kml: "http://www.opengis.net/kml/2.2"])
            kml.kml {
                kml.Document {
                    kml.Folder {
                        kml.name name
                        kml.Schema ("kml:name": name, "kml:id": name) {
                            schema.fields.each {fld ->
                                if (!fld.isGeometry()) {
                                    kml.SimpleField("kml:name": fld.name, "kml:type": fld.typ)
                                }
                            }
                        }
                        eachFeature {f ->
                            featureWriter.write builder, f, namespace: "kml", name: nameClosure, description: descriptionClosure
                        }
                    }
                }
            }
        }

        XmlUtil.serialize(xml, out)
    }

    /**
     * Write the Layer as KML to a String.
     * @param nameClosure A Closure that takes a Feature and returns a value
     * used as the Placemark's name.  Default to the Feature's ID
     * @param descriptionClosure A Closure that takes a Feature and returns a value
     * used as the Placemark's description. Defaults to null which means no description
     * is created
     */
    String toKMLString(Closure nameClosure = {f -> f.id}, Closure descriptionClosure = null) {
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        toKML(out, nameClosure, descriptionClosure)
        out.toString()
    }

    /**
     * Write the Layer as KML to a File.
     * @param file The File we are writing
     * @param nameClosure A Closure that takes a Feature and returns a value
     * used as the Placemark's name.  Default to the Feature's ID
     * @param descriptionClosure A Closure that takes a Feature and returns a value
     * used as the Placemark's description. Defaults to null which means no description
     * is created
     */
    void toKMLFile(File file, Closure nameClosure = {f -> f.id}, Closure descriptionClosure = null) {
        FileOutputStream out = new FileOutputStream(file)
        toKML(out, nameClosure, descriptionClosure)
        out.close()
    }

    /**
     * Generate a new name
     * @return A new Layer name
     */
    static String newname() {
        id += 1
        "layer_${id}".toString()
    }

    /**
     * The string representation
     * @return The string representation
     */
    String toString() {
        name
    }

    /**
     * Clip this Layer by another Layer.
     * @param options Named parameters
     * <ul>
     *     <li>outLayer = The name of the output Layer (defaults to this.layer.name_clipLayer.name_clipped)</li>
     *     <li>outWorkspace = The output Workspace (defaults to the Memory Workspace)</li>
     * </ul>
     * @param clipLayer The clip Layer
     * @return The clipped Layer
     */
    Layer clip(Map options = [:], Layer clipLayer) {

        // Get or create the output/clipped Layer
        def clippedLayerName = options.get("outLayer", "${this.name}_${clipLayer.name}_clipped")
        Workspace workspace = options.get("outWorkspace", new Memory())
        Layer clippedLayer = workspace.create(clippedLayerName as String, this.schema.fields)

        // Put all of the Features in the Clip Layer in a spatial index
        SpatialIndex index = new STRtree()
        clipLayer.eachFeature { f ->
            index.insert(f.bounds, f)
        }

        // Iterate through all of the Features in the input Layer
        clippedLayer.withWriter { w ->
            this.eachFeature(Filter.intersects(clipLayer.bounds.geometry), { f ->
                // See if the Feature intersects with the Bounds of any Feature in the spatial index
                index.query(f.bounds).each { clipFeature ->
                    // Make sure it actually intersects the Geometry of a Feature in the spatial index
                    if (f.geom.intersects(clipFeature.geom)) {
                        // Clip the geometry from the input Layer
                        Geometry intersection = f.geom.intersection(clipFeature.geom)
                        // Create a new Feature and add it to the clipped Layer
                        Map values = f.attributes
                        values[f.schema.geom.name] = intersection
                        w.add(clippedLayer.schema.feature(values))
                    }
                }
            })
        }
        clippedLayer
    }

    /**
     * Union this Layer with another Layer.
     * @param options Named parameters
     * <ul>
     *     <li>outLayer = The name of the output Layer (defaults to this.layer.name_layer2.name_union)</li>
     *     <li>outWorkspace = The output Workspace (defaults to the Memory Workspace)</li>
     *     <li>postfixAll = Whether to postfix all field names when combinging schemas (defaults to false)</li>
     *     <li>includeDuplicates = Whether to include duplicate field names (defaults to true)</li>
     *     <li>maxFieldNameLength = The maximum field name length (defaults to 10 if output Workspace is Directory, otherwise there is no limit)</li>
     * </ul>
     * @param layer2 The second Layer
     * @return The unioned Layer
     */
    Layer union(Map options = [:], Layer layer2) {

        // Get the output Layer
        String outLayerName = options.get("outLayer", "${this.name}_${layer2.name}_union")
        Workspace workspace = options.get("outWorkspace", new Memory())
        Map schemaAndFields = this.schema.addSchema(layer2.schema, outLayerName as String,
                postfixAll: options.get("postfixAll",false),
                includeDuplicates: options.get("includeDuplicates",true),
                maxFieldNameLength: workspace instanceof Directory ? 10 : -1)
        Layer outLayer = workspace.create(schemaAndFields.schema)

        // Add all Features from the first Layer into spatial index
        // Entries in the spatial index are Maps with geom, feature1, and feature2 values
        Quadtree index = new Quadtree()
        this.eachFeature { f ->
            Map features = [geom: f.geom, feature1: f, feature2: null]
            index.insert(features.geom.bounds, features)
        }

        // Go through each Feature in the second Layer
        layer2.eachFeature { f ->
            Geometry geom = f.geom
            // Check the spatial index to see if this Feature intersects anything
            index.query(f.geom.bounds).each { features ->
                // Make sure the Geometries actually intersect
                if(geom.intersects(features.geom)) {
                    // Remove the original Feaure
                    index.remove(features.geom.bounds, features)
                    // Calculate the intersection and difference from both sides
                    Geometry intersection = features.geom.intersection(geom)
                    Geometry difference1 = features.geom.difference(geom)
                    Geometry difference2 = geom.difference(features.geom)
                    // Store the second difference because more Features may intersect it
                    geom = difference2

                    // Insert the first difference and the intersection into the spatial index
                    index.insert(intersection.bounds, [geom: intersection, feature1: features.feature1, feature2: f])
                    index.insert(difference1.bounds, [geom: difference1, feature1: features.feature1, feature2: null])
                }
            }
            // Finally, insert geometry from the second Layer
            index.insert(geom.bounds, [geom: geom, feature1: null, feature2: f])
        }

        // Put all Features in the spatial index into the output Layer
        Schema schema = outLayer.schema
        outLayer.withWriter{w ->
            index.queryAll().each { features ->
                Geometry geom = features.geom
                Feature f1 = features.feature1
                Feature f2 = features.feature2
                Map attributes = [:]
                attributes[schema.geom.name] = geom
                if (f1) {
                    Map fieldMap = schemaAndFields.fields[0]
                    f1.attributes.each {String k, Object v ->
                        if (!k.equalsIgnoreCase(this.schema.geom.name) && fieldMap.containsKey(k)) {
                            attributes[fieldMap[k]] = v
                        }
                    }
                }
                if (f2) {
                    Map fieldMap = schemaAndFields.fields[1]
                    f2.attributes.each {String k, Object v ->
                        if (!k.equalsIgnoreCase(layer2.schema.geom.name) && fieldMap.containsKey(k)) {
                            attributes[fieldMap[k]] = v
                        }
                    }
                }
                Feature f = schema.feature(attributes)
                w.add(f)
            }
        }

        outLayer
    }

    /**
     * Intersect this Layer with another Layer.
     * @param options Named parameters
     * <ul>
     *     <li>outLayer = The name of the output Layer (defaults to this.layer.name_layer2.name_intersection)</li>
     *     <li>outWorkspace = The output Workspace (defaults to the Memory Workspace)</li>
     *     <li>postfixAll = Whether to postfix all field names when combinging schemas (defaults to false)</li>
     *     <li>includeDuplicates = Whether to include duplicate field names (defaults to true)</li>
     *     <li>maxFieldNameLength = The maximum field name length (defaults to 10 if output Workspace is Directory, otherwise there is no limit)</li>
     * </ul>
     * @param layer2 The second Layer
     * @return The output Layer
     */
    Layer intersection(Map options = [:], Layer layer2) {

        // Get the output Layer
        String outLayerName = options.get("outLayer", "${this.name}_${layer2.name}_intersection")
        Workspace workspace = options.get("outWorkspace", new Memory())
        Map schemaAndFields = this.schema.addSchema(layer2.schema, outLayerName as String,
                postfixAll: options.get("postfixAll",false),
                includeDuplicates: options.get("includeDuplicates",true),
                maxFieldNameLength: workspace instanceof Directory ? 10 : -1)
        Layer outLayer = workspace.create(schemaAndFields.schema)

        // Add all Features from the first Layer into spatial index
        // Entries in the spatial index are Maps with geom, feature1, and feature2 values
        Quadtree index = new Quadtree()
        this.eachFeature { f ->
            Map features = [geom: f.geom, feature1: f, feature2: null]
            index.insert(features.geom.bounds, features)
        }

        // Go through each Feature in the second Layer and check for intersections
        layer2.eachFeature { f ->
            // First, check the spatial index
            index.query(f.geom.bounds).each { features ->
                // Make sure the geometries actually intersect
                if(f.geom.intersects(features.geom)) {
                    // Calculate and insert the intersection
                    Geometry intersection = features.geom.intersection(f.geom)
                    index.insert(intersection.bounds, [geom: intersection, feature1: features.feature1, feature2: f])
                }
            }
        }

        // Only add features from the spatial index that have features from Layer 1 and Layer2
        Schema schema = outLayer.schema
        outLayer.withWriter{w ->
            index.queryAll().each { features ->
                Geometry geom = features.geom
                Feature f1 = features.feature1
                Feature f2 = features.feature2
                if (f1 != null && f2 != null) {
                    Map attributes = [(schema.geom.name): geom]
                    Map fieldMap1 = schemaAndFields.fields[0]
                    f1.attributes.each {String k, Object v ->
                        if (!k.equalsIgnoreCase(this.schema.geom.name) && fieldMap1.containsKey(k)) {
                            attributes[fieldMap1[k]] = v
                        }
                    }
                    Map fieldMap2 = schemaAndFields.fields[1]
                    f2.attributes.each {String k, Object v ->
                        if (!k.equalsIgnoreCase(layer2.schema.geom.name) && fieldMap2.containsKey(k)) {
                            attributes[fieldMap2[k]] = v
                        }
                    }
                    Feature f = schema.feature(attributes)
                    w.add(f)
                }
            }
        }

        outLayer
    }

    /**
     * Erase this Layer with another Layer.
     * @param options Named parameters
     * <ul>
     *     <li>outLayer = The name of the output Layer (defaults to this.layer.name_layer2.name_erase)</li>
     *     <li>outWorkspace = The output Workspace (defaults to the Memory Workspace)</li>
     * </ul>
     * @param layer2 The second Layer
     * @return The output Layer
     */
    Layer erase(Map options = [:], Layer layer2) {

        // Get the output Layer
        String outLayerName = options.get("outLayer", "${this.name}_${layer2.name}_erase")
        Workspace workspace = options.get("outWorkspace", new Memory())
        Schema schema = new Schema(outLayerName as String, this.schema.fields)
        Layer outLayer = workspace.create(schema)

        // Add each Feature from the first Layer to a spatial index
        Quadtree index = new Quadtree()
        this.eachFeature { f ->
            index.insert(f.geom.bounds, f)
        }

        // Go through each Feature from the second Layer see if
        // it intersects with any Feature from the first layer
        layer2.eachFeature { f2 ->
            // First check the spatial index
            index.query(f2.geom.bounds).each { f1 ->
                // Then make sure the geometries actually intersect
                if(f1.geom.intersects(f2.geom)) {
                    // Remove the original Feature
                    index.remove(f1.geom.bounds, f1)
                    // Calculate the difference
                    Geometry difference = f1.geom.difference(f2.geom)
                    f1.geom = difference
                    // Insert the Feature with the new Geometry
                    index.insert(difference.bounds, f1)
                }
            }
        }

        // Add all Features in the spatial index to the output Layer
        outLayer.withWriter{w ->
            index.queryAll().each{f ->
                w.add(f)
            }
        }

        outLayer
    }


    /**
     * Calculate the identity between this Layer and another Layer.
     * @param options Named parameters
     * <ul>
     *     <li>outLayer = The name of the output Layer (defaults to this.layer.name_layer2.name_identity)</li>
     *     <li>outWorkspace = The output Workspace (defaults to the Memory Workspace)</li>
     *     <li>postfixAll = Whether to postfix all field names when combinging schemas (defaults to false)</li>
     *     <li>includeDuplicates = Whether to include duplicate field names (defaults to true)</li>
     *     <li>maxFieldNameLength = The maximum field name length (defaults to 10 if output Workspace is Directory, otherwise there is no limit)</li>
     * </ul>
     * @param layer2 The second Layer
     * @return The output Layer
     */
    Layer identity(Map options = [:], Layer layer2) {

        // Get the output Layer
        String outLayerName = options.get("outLayer", "${this.name}_${layer2.name}_identity")
        Workspace workspace = options.get("outWorkspace", new Memory())
        Map schemaAndFields = this.schema.addSchema(layer2.schema, outLayerName as String,
                postfixAll: options.get("postfixAll",false),
                includeDuplicates: options.get("includeDuplicates",true),
                maxFieldNameLength: workspace instanceof Directory ? 10 : -1)
        Layer outLayer = workspace.create(schemaAndFields.schema)

        // Add all Features from the first Layer into spatial index
        // Entries in the spatial index are Maps with geom, feature1, and feature2 values
        Quadtree index = new Quadtree()
        this.eachFeature { f ->
            Map features = [geom: f.geom, feature1: f, feature2: null]
            index.insert(features.geom.bounds, features)
        }

        // Go through each Feature in the second Layer and check for intersections
        layer2.eachFeature { f ->
            // First, check the spatial index
            index.query(f.geom.bounds).each { features ->
                // Then make sure the geometries actually intersect
                if(f.geom.intersects(features.geom)) {
                    // Remove the original Feature
                    index.remove(features.geom.bounds, features)
                    // Calculate the intersection the difference
                    Geometry intersection = features.geom.intersection(f.geom)
                    Geometry difference = features.geom.difference(f.geom)

                    // Insert the intersection and difference back into the spatial index
                    index.insert(intersection.bounds, [geom: intersection, feature1: features.feature1, feature2: f])
                    index.insert(difference.bounds, [geom: difference, feature1: features.feature1, feature2: null])
                }
            }
        }

        // Put all Features in the spatial index into the output Layer
        Schema schema = outLayer.schema
        outLayer.withWriter{w ->
            index.queryAll().each { features ->
                Geometry geom = features.geom
                Feature f1 = features.feature1
                Feature f2 = features.feature2
                Map attributes = [(schema.geom.name): geom]
                if (f1) {
                    Map fieldMap = schemaAndFields.fields[0]
                    f1.attributes.each {String k, Object v ->
                        if (!k.equalsIgnoreCase(this.schema.geom.name) && fieldMap.containsKey(k)) {
                            attributes[fieldMap[k]] = v
                        }
                    }
                }
                if (f2) {
                    Map fieldMap = schemaAndFields.fields[1]
                    f2.attributes.each {String k, Object v ->
                        if (!k.equalsIgnoreCase(layer2.schema.geom.name) && fieldMap.containsKey(k)) {
                            attributes[fieldMap[k]] = v
                        }
                    }
                }
                Feature f = schema.feature(attributes)
                w.add(f)
            }
        }

        outLayer
    }

    /**
     * Calculate the update between this Layer and another Layer.
     * @param options Named parameters
     * <ul>
     *     <li>outLayer = The name of the output Layer (defaults to this.layer.name_layer2.name_update)</li>
     *     <li>outWorkspace = The output Workspace (defaults to the Memory Workspace)</li>
     * </ul>
     * @param layer2 The second Layer
     * @return The output Layer
     */
    Layer update(Map options = [:], Layer layer2) {

        // Get the output Layer
        String outLayerName = options.get("outLayer", "${this.name}_${layer2.name}_update")
        Workspace workspace = options.get("outWorkspace", new Memory())
        Schema schema = new Schema(outLayerName as String, this.schema.fields)
        Layer outLayer = workspace.create(schema)

        // Add each Feature from the second layer to a spatial index
        Quadtree index = new Quadtree()
        layer2.eachFeature { f ->
            Map features = [geom: f.geom, feature1: null, feature2: f]
            index.insert(features.geom.bounds, features)
        }

        // Then go through each Feature from the first Layer and check
        // for intersections
        this.eachFeature { f ->
            // Remember the Geometry, since there can be multiple intersecting features
            Geometry geom = f.geom
            // First check the spatial index
            index.query(geom.bounds).each { features ->
                // Then make sure the geometries actually intersect
                if(geom.intersects(features.geom)) {
                    // Calculate the difference
                    geom = geom.difference(features.geom)
                }
            }
            // Finally, insert the geometry into the spatial index
            index.insert(geom.bounds, [geom: geom, feature1: f, feature2: null])
        }

        // Add all Features in the spatial index to the output Layer
        outLayer.withWriter{w ->
            index.queryAll().each { features ->
                Geometry geom = features.geom
                Feature f1 = features.feature1
                Map attributes = [(outLayer.schema.geom.name): geom]
                if (f1) {
                    f1.attributes.each {String k, Object v ->
                        if (!k.equalsIgnoreCase(this.schema.geom.name)) {
                            attributes[k] = v
                        }
                    }
                }
                Feature f = schema.feature(attributes)
                w.add(f)
            }
        }

        outLayer
    }

    /**
     * Calculate the symmetric difference between this Layer and another Layer.
     * @param options Named parameters
     * <ul>
     *     <li>outLayer = The name of the output Layer (defaults to this.layer.name_layer2.name_symdifference)</li>
     *     <li>outWorkspace = The output Workspace (defaults to the Memory Workspace)</li>
     *     <li>postfixAll = Whether to postfix all field names when combinging schemas (defaults to false)</li>
     *     <li>includeDuplicates = Whether to include duplicate field names (defaults to true)</li>
     *     <li>maxFieldNameLength = The maximum field name length (defaults to 10 if output Workspace is Directory, otherwise there is no limit)</li>
     * </ul>
     * @param layer2 The second Layer
     * @return The output Layer
     */
    Layer symDifference(Map options = [:], Layer layer2) {

        // Get the output Layer
        def outLayerName = options.get("outLayer", "${this.name}_${layer2.name}_symdifference")
        Workspace workspace = options.get("outWorkspace", new Memory())
        Map schemaAndFields = this.schema.addSchema(layer2.schema, outLayerName as String,
                postfixAll: options.get("postfixAll",false),
                includeDuplicates: options.get("includeDuplicates",true),
                maxFieldNameLength: workspace instanceof Directory ? 10 : -1)
        Layer outLayer = workspace.create(schemaAndFields.schema)

        // Add each Feature from the first Layer to a spatial index
        Quadtree index = new Quadtree()
        this.eachFeature { f ->
            Map features = [geom: f.geom, feature1: f, feature2: null]
            index.insert(features.geom.bounds, features)
        }

        // Check each feature in the second Layer for intersections
        layer2.eachFeature { f ->
            Geometry geom = f.geom
            // First check the spatial index
            index.query(f.geom.bounds).each { features ->
                // Then check to make sure the geometries actually intersect
                if(geom.intersects(features.geom)) {
                    // Remove the previous entry in the spatial index
                    index.remove(features.geom.bounds, features)
                    // Calculate the differences
                    Geometry difference1 = features.geom.difference(geom)
                    Geometry difference2 = geom.difference(features.geom)
                    // Keep the second difference around in case other Geometries
                    // intersect with it
                    geom = difference2
                    // Insert first difference back into the spatial index
                    index.insert(difference1.bounds, [geom: difference1, feature1: features.feature1, feature2: null])
                }
            }
            // Finally, insert the geometry from the second Layer into the spatial index
            index.insert(geom.bounds, [geom: geom, feature1: null, feature2: f])
        }

        // Write each Feature in the spatial index into the output Layer
        Schema schema = outLayer.schema
        outLayer.withWriter{w ->
            index.queryAll().each { features ->
                Geometry geom = features.geom
                Feature f1 = features.feature1
                Feature f2 = features.feature2
                Map attributes = [(schema.geom.name): geom]
                if (f1) {
                    Map fieldMap = schemaAndFields.fields[0]
                    f1.attributes.each {String k, Object v ->
                        if (!k.equalsIgnoreCase(this.schema.geom.name) && fieldMap.containsKey(k)) {
                            attributes[fieldMap[k]] = v
                        }
                    }
                }
                if (f2) {
                    Map fieldMap = schemaAndFields.fields[1]
                    f2.attributes.each {String k, Object v ->
                        if (!k.equalsIgnoreCase(layer2.schema.geom.name) && fieldMap.containsKey(k)) {
                            attributes[fieldMap[k]] = v
                        }
                    }
                }
                Feature f = schema.feature(attributes)
                w.add(f)
            }
        }

        outLayer
    }
}
