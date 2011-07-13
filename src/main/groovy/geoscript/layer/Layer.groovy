package geoscript.layer

import geoscript.feature.Schema
import geoscript.geom.*
import geoscript.proj.Projection
import geoscript.feature.*
import geoscript.workspace.*
import geoscript.filter.Filter
import geoscript.style.Style
import org.geotools.data.FeatureSource
import org.geotools.data.DefaultQuery
import org.geotools.data.Query
import org.geotools.data.Transaction
import org.geotools.data.FeatureStore
import org.geotools.data.FeatureReader
import org.geotools.data.DefaultTransaction
import org.geotools.feature.FeatureCollections
import org.geotools.feature.FeatureCollection
import org.geotools.feature.FeatureIterator
import org.opengis.feature.simple.SimpleFeatureType
import org.opengis.feature.simple.SimpleFeature
import org.opengis.referencing.crs.CoordinateReferenceSystem
import org.opengis.feature.type.AttributeDescriptor
import com.vividsolutions.jts.geom.Envelope
import net.opengis.wfs.WfsFactory
import org.geotools.wfs.v1_1.WFS
import org.geotools.wfs.v1_1.WFSConfiguration
import org.geotools.xml.Encoder
import org.opengis.filter.FilterFactory2
import org.json.*
import geoscript.geom.io.KmlWriter
import org.jdom.*
import org.jdom.output.*
import org.jdom.input.*
//import org.geotools.geojson.GeoJSONWriter

/**
 * A Layer is a source of spatial data
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
    private final static FilterFactory2 filterFactory = org.geotools.factory.CommonFactoryFinder.getFilterFactory2(org.geotools.factory.GeoTools.getDefaultHints())

    /**
     * Create a new Layer from a GeoTools FeatureSource
     * @param fs The GeoTools FeatureSource
     */
    Layer(FeatureSource<SimpleFeatureType, SimpleFeature> fs) {
        this.name = fs.name
        this.workspace = new Workspace(fs.dataStore)
        this.fs = fs
        this.schema = new Schema(fs.schema)
        this.projection = new Projection(fs.schema.coordinateReferenceSystem)
        setDefaultStyle(this.schema.geom.typ)
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
        this.projection = new Projection(fs.schema.coordinateReferenceSystem)
        setDefaultStyle(this.schema.geom.typ)
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
        this.projection = new Projection(fs.schema.coordinateReferenceSystem)
        setDefaultStyle(this.schema.geom.typ)
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
        this.projection = new Projection(fs.schema.coordinateReferenceSystem)
        setDefaultStyle(this.schema.geom.typ)
    }

    /**
     * Create a new Layer with a name
     * @param name The Layer's name
     * @param schema The Schema

     */
    Layer(String name, Schema schema) {
        this.workspace = new Memory()
        Layer layer = this.workspace.create(schema)
        this.name = name
        this.fs = layer.fs
        this.schema = new Schema(layer.fs.schema)
        this.projection = new Projection(fs.schema.coordinateReferenceSystem)
        setDefaultStyle(this.schema.geom.typ)
    }

    /**
     * Create a new Layer with a default name, Schema, and Memory Workspace
     */
    Layer() {
        this(newname(), new Schema("features", [new Field("geom","Geometry")]))
    }

    /**
     * Set the default style based on the geometry type
     * @param geometryType The geometry type
     * @return A default Style
     */
    private void setDefaultStyle(String geometryType) {
        if(!this.style) {
            this.style = Style.getDefaultStyleForGeometryType(geometryType)
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
        Envelope e = fs.getBounds(new DefaultQuery(getName(), f.filter))
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
     * Get a List of Features
     * @param filer The Filter or Filter String to limit the Features used to construct the bounds. Defaults to null.
     * @param transform The Closure used to modify the Features.  Defaults to null.
     * @return A List of Features
     */
    List<Feature> getFeatures(def filter = null, Closure transform = null) {
        List<Feature> features = []
        Cursor c = getCursor(filter)
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
     * Get a Cursor over the Features of the Layer.
     * @param filer The Filter or Filter String to limit the Features. Defaults to null.
     * @return A Cursor
     *
     */
    Cursor getCursor(def filter = null) {
        Filter f = (filter == null) ? Filter.PASS : new Filter(filter)
        DefaultQuery q = new DefaultQuery(getName(), f.filter)
        if (getProj()) {
            q.coordinateSystem = getProj().crs
        }
        FeatureReader r = fs.dataStore.getFeatureReader(q, Transaction.AUTO_COMMIT)
        return new Cursor(r, this)
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
     * @param fld The Field whose values will be udpated
     * @param value Either a static value or a Closure that takes
     * a Feature and return an Object
     * @param filter The Filter to limit the Features that will be updated
     */
    void update(Field fld, def value, def filter = null) {
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
     * @param o The Feature or List/Map of values
     */
    void add(def o) {
        // If it is a List of Features, then add it inside of a Transaction
        if (o instanceof List && o.size() > 0 && o.get(0) instanceof Feature) {
            Transaction t = new DefaultTransaction("addTransaction")
            try {
                FeatureStore<SimpleFeatureType, SimpleFeature> store = (FeatureStore)fs
                store.transaction = t
                o.each{f->
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
        // Otherwise its a Feature or a List of values
        else {
            Feature f
            if (o instanceof Feature) {
                f = o
                if (f.schema == null) {
                    f.schema = schema
                }
            }
            else {
                f = schema.feature(o)
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
     * @return The reprojected Layer
     */
    Layer reproject(Projection p, String newName = newname()) {
        Schema s = schema.reproject(p, newName)
        Layer l = workspace.create(s)
        DefaultQuery q = new DefaultQuery(getName(), Filter.PASS.filter)
        if (getProj() != null) {
            q.coordinateSystem = getProj().crs
        }
        q.coordinateSystemReproject = p.crs
        FeatureCollection fc = fs.getFeatures(q)
        FeatureIterator i = fc.features()
        while(i.hasNext()) {
           Feature f = new Feature(i.next())
           l.add(f)
        }
        i.close()
        l
    }

    /**
     * Filer the Layer.
     * @param filter A Filter or Filter String used to limit the number of Features returned in the new Layer
     * @param newName The name of the new Layer (defaults to a default new name)
     * @return A new Layer in the same workspace
     */
    Layer filter(def filter = null, String newName = newname()) {
        Filter f = (filter == null) ? Filter.PASS : new Filter(filter)
        Schema s = new Schema(newName, this.schema.fields)
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
     * Write the Layer as GML to an Outputstream
     * @param out The OutputStream (defaults to System.out)
     */
    void toGML(OutputStream out = System.out) {
        FeatureCollection features = fs.features
        def fc = WfsFactory.eINSTANCE.createFeatureCollectionType()
        fc.feature.add(features)

        Encoder e = new Encoder(new WFSConfiguration())
        URI uri = (fs.name.namespaceURI == null) ? new URI("http://geotools") : new URI(fs.name.namespaceURI)
        String prefix = "gt"
        e.namespaces.declarePrefix(prefix, uri.toString())
        e.indenting = true
        e.encode(fc, WFS.FeatureCollection, out)
    }

    /**
     * Write the Layer as GML to a File
     * @param file The File
     */
    String toGMLFile(File file) {
        FileOutputStream out = new FileOutputStream(file)
        toGML(out)
        out.close()
    }

    /**
     * Write the Layer as GML to a String
     * @param out A GML String
     */
    String toGMLString() {
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        toGML(out)
        out.toString()
    }

    /**
     * Write the Layer as GeoJSON to an OutputStream
     * @param out The OutputStream (defaults to System.out)
     */
    void toJSON(OutputStream out = System.out) {
        //FeatureCollection features = fs.features
        //GeoJSONWriter w = new GeoJSONWriter()
        //w.write(features, out)
        def writer = new geoscript.geom.io.GeoJSONWriter()
        JSONObject jsonObject = new JSONObject()
        jsonObject.put("type","FeatureCollection")
        JSONArray array = new JSONArray()
        Cursor cursor = getCursor()
        while(cursor.hasNext()) {
            Feature f = cursor.next()
            JSONObject obj = new JSONObject()
            obj.put("type","Feature")
            obj.put("geometry", new JSONObject(writer.write(f.geom)))
            JSONObject props = new JSONObject()
            f.attributes.each{k,v ->
                if (!(v instanceof Geometry)) {
                    props.put(k,v)
                }
            }
            obj.put("properties",props)
            array.put(obj)
        }
        cursor.close()
        jsonObject.put("features", array)
        Writer w = new java.io.OutputStreamWriter(out)
        jsonObject.write(w)
        w.flush()
        w.close()
    }

    /**
     * Write the Layer as GeoJSON to a File
     * @param file The File
     */
    String toJSONFile(File file) {
        FileOutputStream out = new FileOutputStream(file)
        toJSON(out)
        out.close()
    }

    /**
     * Write the Layer as GeoJSON to a String
     * @param out A GeoJSON String
     */
    String toJSONString() {
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        toJSON(out)
        out.toString()
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

        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat())
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
