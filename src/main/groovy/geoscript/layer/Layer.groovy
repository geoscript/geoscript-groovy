package geoscript.layer

import geoscript.feature.Schema
import geoscript.filter.Expression
import geoscript.geom.*
import geoscript.proj.Projection
import geoscript.feature.*
import geoscript.workspace.*
import geoscript.filter.Filter
import geoscript.style.Style
import geoscript.style.Symbolizer
import org.geotools.data.FeatureSource
import org.geotools.data.DefaultQuery
import org.geotools.data.Query
import org.geotools.data.Transaction
import org.geotools.data.FeatureStore
import org.geotools.data.DefaultTransaction
import org.geotools.data.transform.Definition
import org.geotools.data.transform.TransformFactory
import org.geotools.feature.FeatureCollections
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
import geoscript.geom.io.KmlWriter
import org.jdom.*
import org.jdom.output.*
import org.jdom.input.*
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
        this(newname(), new Schema("features", [new Field("geom","Geometry")]))
    }

    /**
     * Create a new Layer with the given name, a simple Schema with just a Geometry Field in the Memory Workspace
     */
    Layer(String name) {
        this(name, new Schema("features", [new Field("geom","Geometry")]))
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
            if (this instanceof Shapefile || this.format.equalsIgnoreCase("Directory")) {
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
                    def reader = new geoscript.style.io.SLDReader()
                    this.style = reader.read(f)
                }
                // Check for CSS but only if the style is still falsey
                if (!this.style) {
                    f = new File(dir,"${fileName}.css")
                    if (f.exists()) {
                        def reader = new geoscript.style.io.CSSReader()
                        this.style = reader.read(f)
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
        Filter f = (filter == null) ? Filter.PASS : new Filter(filter)
        int count = fs.getCount(new DefaultQuery(getName(), f.filter))
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
        Filter f = (filter == null) ? Filter.PASS : new Filter(filter)
        def query = new DefaultQuery(getName(), f.filter)
        Envelope e = fs.getBounds(query)
        if (!e) {
            e = fs.getFeatures(query).bounds
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
        Cursor c = getCursor(filter)
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
        List results = []
        Cursor c = getCursor(filter)
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
     * @return A List of Features
     */
    List<Feature> getFeatures(def filter = null, Closure transform = null, List sort = null) {
        List<Feature> features = []
        Cursor c = getCursor(filter, sort)
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
     * </ul>
     * @return A Cursor
     */
    Cursor getCursor(Map options) {
        getCursor(options.get("filter", null), options.get("sort", null),
            options.get("max",-1), options.get("start", -1),options.get("fields", null),
            options.get("sourceProj", null), options.get("destProj", null))
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
     * @return A Cursor
     */
    Cursor getCursor(def filter = null, List sort = null, int max = -1, int start = -1, List fields = null, def sourceProj = null, def destProj = null) {
        Map cursorOptions = [:]
        Filter f = (filter == null) ? Filter.PASS : new Filter(filter)
        DefaultQuery q = new DefaultQuery(getName(), f.filter)
        if (fields != null && fields.size() > 0) {
            q.propertyNames = ((fields[0] instanceof Field) ? fields*.name : fields) as String[]
        }
        if (max > -1) {
            q.maxFeatures = max
        }
        if (start > -1) {
            if (fs.queryCapabilities.offsetSupported) {
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
            q.coordinateSystemReproject = new Projection(destProj).crs
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
     * </ul>
     * @return
     */
    Feature first(Map options = [:]) {
        def filter = options.get("filter")
        def sort = options.get("sort")
        if (sort != null && !(sort instanceof List)) {
            sort = [sort]
        }
        Cursor c = getCursor(filter: filter, sort: sort)
        Feature f = c.next()
        c.close()
        f
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
            Transaction t = new DefaultTransaction("addTransaction")
            try {
                FeatureStore<SimpleFeatureType, SimpleFeature> store = (FeatureStore)fs
                store.transaction = t
                o.each{f->
                    if (f instanceof java.util.Map) {
                        f = this.schema.feature(f as java.util.Map)
                    }
                    if (f.schema == null) {
                        f.schema = schema
                    } else if (f.schema != this.schema) {
                        f = this.schema.feature(f.attributes)
                    }
                    FeatureCollection fc = FeatureCollections.newCollection()
                    fc.add(f.f)
                    store.addFeatures(fc)
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
        // Else if it is a FeatureCollection
        else if (o instanceof FeatureCollection || o instanceof Cursor) {
            Transaction t = new DefaultTransaction("addTransaction")
            try {
                FeatureStore<SimpleFeatureType, SimpleFeature> store = (FeatureStore)fs
                store.transaction = t
                int chunk = 1000
                Cursor c = o instanceof FeatureCollection ? new Cursor(o) : o as Cursor
                while(true) {
                    def features = readFeatures(c, this.schema, chunk)
                    if (features.isEmpty()) break
                    store.addFeatures(features)
                    if (features.size() < chunk) break
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
            FeatureCollection fc = FeatureCollections.newCollection()
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
            def idFilter = filterFactory.createFidFilter()
            modifiedFeatures.keySet().each{id-> idFilter.addFid(id)}
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
        Query q = new Query(getName(), Filter.PASS.filter)
        if (getProj() != null) {
            q.coordinateSystem = getProj().crs
        } else {
            q.coordinateSystem = sourceProjection.crs
        }
        q.coordinateSystemReproject = projectedLayer.proj.crs
        FeatureCollection fc = fs.getFeatures(q)
        Cursor c = new Cursor(fc)
        while(true) {
            def features = readFeatures(c, projectedLayer.schema, chunk)
            if (features.isEmpty()) break
            projectedLayer.fs.addFeatures(features)
            if (features.size() < chunk) break
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
    private FeatureCollection readFeatures(Cursor cursor, Schema schema, int chunk) {
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
        DefaultQuery q = new DefaultQuery(getName(), f.filter)
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
        def query = new DefaultQuery(this.name)
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
     * The GML Layer Writer
     */
    private static final GmlWriter gmlWriter = new GmlWriter()

    /**
     * Write the Layer as GML to an Outputstream
     * @param out The OutputStream (defaults to System.out)
     */
    void toGML(OutputStream out = System.out) {
        gmlWriter.write(this, out)
    }

    /**
     * Write the Layer as GML to a File
     * @param file The File
     */
    void toGMLFile(File file) {
        gmlWriter.write(this, file)
    }

    /**
     * Write the Layer as GML to a String
     * @param out A GML String
     */
    String toGMLString() {
        gmlWriter.write(this)
    }

    /**
     * The GeoJSON Layer Writer
     */
    private final static GeoJSONWriter geoJSONWriter = new GeoJSONWriter()

    /**
     * Write the Layer as GeoJSON to an OutputStream
     * @param out The OutputStream (defaults to System.out)
     */
    void toJSON(OutputStream out = System.out) {
        geoJSONWriter.write(this, out)
    }

    /**
     * Write the Layer as GeoJSON to a File
     * @param file The File
     */
    void toJSONFile(File file) {
        geoJSONWriter.write(this, file)
    }

    /**
     * Write the Layer as GeoJSON to a String
     * @param out A GeoJSON String
     */
    String toJSONString() {
        geoJSONWriter.write(this)
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

        String geometryType = schema.geom.typ.toLowerCase()
        KmlWriter kmlWriter = new KmlWriter()

        SAXBuilder builder = new SAXBuilder()
        Namespace ns = Namespace.getNamespace("kml","http://www.opengis.net/kml/2.2")
        Document doc = new Document()
        Element kmlElem = new Element("kml",ns)
        doc.setRootElement(kmlElem)
        Element docElem = new Element("Document",ns)
        kmlElem.addContent(docElem)
        Element folderElem = new Element("Folder",ns)
        docElem.addContent(folderElem)
        folderElem.addContent(new Element("name",ns).setText(name))
        Element schemaElem = new Element("Schema",ns)
        schemaElem.setAttribute("name", name, ns)
        schemaElem.setAttribute("id", name, ns)
        folderElem.addContent(schemaElem)
        schema.fields.each {fld ->
            if (!fld.isGeometry()) {
                schemaElem.addContent(new Element("SimpleField", ns).setAttribute("name",fld.name, ns).setAttribute("type", fld.typ, ns))
            }
        }
        features.each {f ->
            Element placeMarkElem = new Element("Placemark", ns)
            placeMarkElem.addContent(new Element("name", ns).setText(nameClosure.call(f))) //f.get("STATE_NAME")
            if (descriptionClosure != null) {
                placeMarkElem.addContent(new Element("description", ns).setText(descriptionClosure.call(f)))
            }
            folderElem.addContent(placeMarkElem)
            Element styleElem = new Element("Style", ns)
            if (geometryType.endsWith("point") ) {
                styleElem.addContent(new Element("IconStyle",ns).addContent(new Element("color",ns).setText("ff0000ff")))
            }
            else {
                styleElem.addContent(new Element("LineStyle",ns).addContent(new Element("color",ns).setText("ff0000ff")))
                if (geometryType.endsWith("polygon")) {
                    styleElem.addContent(new Element("PolyStyle",ns).addContent(new Element("fill",ns).setText("0")))
                }
            }
            placeMarkElem.addContent(styleElem)
            Element extendedDataElem = new Element("ExtendedData",ns)
            placeMarkElem.addContent(extendedDataElem)
            Element schemaDataElem = new Element("SchemaData",ns).setAttribute("schemaUrl","#" + name,ns)
            extendedDataElem.addContent(schemaDataElem)
            schema.fields.each{fld ->
                if (!fld.isGeometry()) {
                    schemaDataElem.addContent(new Element("SimpleData",ns).setAttribute("name",fld.name,ns).setText(f.get(fld.name) as String))
                }
            }
            // add geometry
            String kml = kmlWriter.write(f.geom)
            Element geomElem = builder.build(new StringReader(kml)).rootElement.detach()
            addNamespace(geomElem,ns)
            placeMarkElem.addContent(geomElem)

        }

        XMLOutputter outputter = new XMLOutputter(org.jdom.output.Format.getPrettyFormat())
        outputter.output(doc, out)
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
     * Add a Namespace to the JDOM Element recursively.  This
     * is needed by the toKML method.
     * @param element The JDOM Element
     * @param ns The JDOM Namespace
     */
    protected void addNamespace(Element element, Namespace ns) {
        element.setNamespace(ns)
        element.children.each{e -> addNamespace(e, ns)}
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
}
