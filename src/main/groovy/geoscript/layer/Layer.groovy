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
import org.geotools.feature.FeatureCollections
import org.opengis.feature.simple.SimpleFeatureType
import org.opengis.feature.simple.SimpleFeature
import org.opengis.referencing.crs.CoordinateReferenceSystem
import com.vividsolutions.jts.geom.Envelope
import org.geotools.data.FeatureReader
import org.geotools.feature.FeatureCollection
import org.geotools.feature.FeatureIterator
import net.opengis.wfs.WfsFactory
import org.geotools.wfs.v1_1.WFS
import org.geotools.wfs.v1_1.WFSConfiguration
import org.geotools.xml.Encoder
//import org.geotools.geojson.GeoJSONWriter

/**
 * A Layer
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
    Projection proj

    /**
     * The internal counter for new Layer names
     */
    private static int id = 0

    /**
     * Create a new Layer from an existing Layer
     */
    Layer(Layer layer) {
        this.name = layer.name
        this.workspace = layer.workspace
        this.fs = layer.fs
        this.schema = layer.schema
        this.proj = layer.proj
    }

    /**
     * Create a new Layer from a name, Workspace, FeatureSource, and Schema
     */
    Layer(String name, Workspace workspace, FeatureSource<SimpleFeatureType, SimpleFeature> fs, Schema schema) {
        this.name = name
        this.workspace = workspace
        this.fs = fs
        this.schema = schema
    }

    /**
     * Create a new Layer from a Workspace and FeatureSource
     */
    Layer(Workspace workspace, FeatureSource<SimpleFeatureType, SimpleFeature> fs) {
        this(fs.name.localPart, workspace, fs, new Schema(fs.schema))
    }

    /**
     * Create a new Layer from a name and Workspace
     */
    Layer(String name, Workspace workspace) {
        Layer layer = workspace.get(name)
        this.name = name
        this.workspace = workspace
        this.fs = layer.fs
        this.schema = new Schema(layer.fs.schema)
    }

    /**
     * Create a new Layer with a name
     */
    Layer(String name, Schema schema) {
        this.workspace = new Memory()
        Layer layer = this.workspace.create(schema)
        this.name = name
        this.fs = layer.fs
        this.schema = new Schema(layer.fs.schema)
    }

    /**
     * Create a new Layer
     */
    Layer() {
        this(newname(), new Schema("features", [new Field("geom","Geometry")]))
    }

    /**
     * Get the Workspace format
     */
    String getFormat() {
        workspace.format
    }

    /**
     * Get the Layer's name
     */
    String getName() {
        fs.name.localPart
    }

    /**
     * Get the Layer's Projection
     */
    Projection getProj() {
        if (proj != null) {
            return proj
        }
        else {
            CoordinateReferenceSystem crs = fs.schema.coordinateReferenceSystem
            return new Projection(crs)
        }
    }

    /**
     * Set the Layer's Projection.
     * @param value The value can either be a Projection or a String
     */
    void setProj(def value) {
        proj = new Projection(value)
    }

    /**
     * Count the number of Features in the layer
     */
    int count(def filter = null) {
        Filter f = (filter == null) ? Filter.PASS : new Filter(filter)
        int count = fs.getCount(new DefaultQuery(getName(), f.filter))
        if (count == -1) {
            count = 0
            // count manually
            features.each{count++}
        }
        return count

    }

    /**
     * Get the Bounds of the Feature in the Layer
     */
    Bounds bounds(def filter = null) {
        Filter f = (filter == null) ? Filter.PASS : new Filter(filter)
        Envelope e = fs.getBounds(new DefaultQuery(getName(), f.filter))
        return new Bounds(e)
    }

    /**
     * Get a List of Features
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
     * Get a Cursor over the Feature of the Layer
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
     */
    void delete(def filter = null) {
        Filter f = (filter == null) ? Filter.PASS : new Filter(filter)
        fs.removeFeatures(f.filter)
    }

    /**
     * Add a Feature to the Layer
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
     * Reproject the Layer
     */
    Layer reproject(Projection prj, String newName = newname()) {
        Projection p = new Projection(prj)
        Schema s = schema.reproject(p, newName)
        Layer l = workspace.create(s)
        DefaultQuery q = new DefaultQuery(getName(), Filter.PASS.filter)
        if (getProj() != null) {
            q.coordinateSystem = getProj().crs
        }
        q.coordinateSystemReproject = prj.crs
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
     * Filer the layer
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
     */
    void toJSON(OutputStream out = System.out) {
        FeatureCollection features = fs.features
        //GeoJSONWriter w = new GeoJSONWriter()
        //w.write(features, out)
    }

    /**
     * Generate a new name
     */
    static String newname() {
        id += 1
        "layer_${id}".toString()
    }
}
