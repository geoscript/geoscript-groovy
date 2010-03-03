package geoscript.feature

import org.opengis.feature.simple.SimpleFeature
import org.geotools.feature.simple.SimpleFeatureBuilder
import com.vividsolutions.jts.geom.Geometry as JtsGeometry
import geoscript.geom.*

/**
 * A Feature
 */
class Feature {

    /**
     * The wrapped GeoTools SimpleFeature
     */
    SimpleFeature f

    /**
     * The Schema
     */
    Schema schema

    /**
     * Create a new Feature by wrapping a GeoTools SimpleFeature
     */
    Feature(SimpleFeature f) {
        this.f = f
        this.schema = new Schema(this.f.featureType)
    }

    /**
     * Create a new Feature with a Map of attributes, an id, and a Schema
     */
    Feature(Map attributes, String id, Schema schema) {
        this(buildFeature(attributes, id, schema))
    }

    /**
     * Create a new Feature with a List of values, an id, and a Schema.  The
     * List of values must be in the same order as the Schema's fields
     */
    Feature(List attributes, String id, Schema schema) {
        this(buildFeature(attributes, id, schema))
    }

    /**
     * Create a new Feature with a Map of Attributes and an Id.  The Schema is
     * inferred from the attribute values
     */
    Feature(Map attributes, String id) {
        this(buildFeature(attributes, id))
    }

    /**
     * Get the Feature's ID
     */
    String getId() {
        f.identifier.toString()
    }

    /**
     * Get the Feature's Geometry
     */
    Geometry getGeom() {
        Geometry.wrap((JtsGeometry) f.defaultGeometry)
    }

    /**
     * Set the Feature's Geometry
     */
    void setGeom(Geometry geom) {
        f.defaultGeometry = geom.g
    }

    /**
     * Get a value by Field name
     */
    Object get(String name) {
        Field fld = schema.field(name)
        Object obj = f.getAttribute(name)
        if (obj instanceof JtsGeometry) {
            return Geometry.wrap((JtsGeometry)obj)
        }
        else {
            return obj
        }
    }

    /**
     * Set a value for a Field
     */
    void set(String name, Object value) {
        Field fld = schema.field(name)
        f.setAttribute(name, (value instanceof Geometry) ? ((Geometry)value).g : value)
    }

    /**
     * Get a Map of all attributes
     */
    Map getAttributes() {
        Map atts = [:]
        schema.fields.each{fld ->
            String name = fld.name
            Object value = f.getAttribute(name)
            atts[name] = (value instanceof JtsGeometry) ? Geometry.wrap((JtsGeometry) value) : value
        }
        atts
    }

    /**
     * The string representation
     */
    String toString() {
        String atts = schema.fields.collect{fld -> "${fld.name}: ${get(fld.name)}"}.join(", ")
        String id = (id.startsWith(schema.name)) ? id : "${schema.name}.${id}"
        "${id} ${atts}"
    }

    /**
     * Build a SimpleFeature using the Map of data, the ID, and the Schema
     */
    private static SimpleFeature buildFeature(Map attributes, String id, Schema schema) {
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(schema.featureType)
        attributes.each{
            if (it instanceof Geometry) {
                featureBuilder.set(it.key, it.value.g)
            }
            else {
                featureBuilder.set(it.key, it.value);
            }
        }
        featureBuilder.buildFeature(id)
    }

    /**
     * Build a SimpleFeature using the List of data, the ID, and the Schema
     */
    private static SimpleFeature buildFeature(List attributes, String id, Schema schema) {
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(schema.featureType)
        attributes.each{
            if (it instanceof Geometry) {
                featureBuilder.add(it.g)
            }
            else {
                featureBuilder.add(it);
            }
        }
        featureBuilder.buildFeature(id)
    }

    /**
     * Build a SimpleFeature using the Map of data and the ID.  The Schema is inferred
     * from the values of the Map
     */
    private static SimpleFeature buildFeature(Map attributes, String id) {
        List<Field> fields = attributes.collect{at ->
            String name = at.key.toString()
            Object value = at.value
            String type = Schema.lookUpBinding(value.class.name)
            new Field(name, type)
        }
        Schema schema = new Schema("feature", fields)
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(schema.featureType)
        attributes.each{at -> featureBuilder.set(at.key.toString(), (at.value instanceof Geometry) ? ((Geometry)at.value).g : at.value)}
        featureBuilder.buildFeature(id)
    }

}