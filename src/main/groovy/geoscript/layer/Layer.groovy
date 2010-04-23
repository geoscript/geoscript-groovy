package geoscript.layer

import geoscript.feature.Schema
import geoscript.geom.*
import geoscript.proj.Projection
import geoscript.feature.*
import geoscript.workspace.*
import geoscript.filter.Filter
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
import org.json.*
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
     * The internal counter for new Layer names
     */
    private static int id = 0

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
    }

    /**
     * Create a new Layer with a default name, Schema, and Memory Workspace
     */
    Layer() {
        this(newname(), new Schema("features", [new Field("geom","Geometry")]))
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
                    def filterFactory = org.geotools.factory.CommonFactoryFinder.getFilterFactory2(org.geotools.factory.GeoTools.getDefaultHints())
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
        }
    }

    /**
     * Add a Feature to the Layer
     * @param o The Feature or List/Map of values
     */
    void add(def o) {
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

    /**
     * Add a Feature to the Layer
     * @param o The Feature or List/Map of values
     */
    void plus(def o) {
        add(o)
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
        URI uri = (fs.name.namespaceURI == null) ? new URI("http://geotools") : fs.name.namespaceURI
        String prefix = "gt"
        e.namespaces.declarePrefix(prefix, uri.toString())
        e.indenting = true
        e.encode(fc, WFS.FeatureCollection, out)
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
     * Generate a new name
     * @return A new Layer name
     */
    static String newname() {
        id += 1
        "layer_${id}".toString()
    }
}
